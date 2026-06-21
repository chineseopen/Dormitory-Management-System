/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dorm.common.exception.BusinessException;
import com.dorm.system.dto.RoomChangePageDTO;
import com.dorm.system.entity.DormBed;
import com.dorm.system.entity.DormBuilding;
import com.dorm.system.entity.DormRoom;
import com.dorm.system.entity.DormRoomChange;
import com.dorm.system.entity.DormStudent;
import com.dorm.system.entity.SysUser;
import com.dorm.system.mapper.DormBedMapper;
import com.dorm.system.mapper.DormBuildingMapper;
import com.dorm.system.mapper.DormRoomChangeMapper;
import com.dorm.system.mapper.DormRoomMapper;
import com.dorm.system.mapper.DormStudentMapper;
import com.dorm.system.mapper.SysUserMapper;
import com.dorm.system.service.DormRoomChangeService;
import com.dorm.system.vo.RoomChangeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 调宿记录 Service 实现
 */
@Service
@RequiredArgsConstructor
public class DormRoomChangeServiceImpl extends ServiceImpl<DormRoomChangeMapper, DormRoomChange> implements DormRoomChangeService {

    private final DormRoomChangeMapper dormRoomChangeMapper;
    private final DormStudentMapper dormStudentMapper;
    private final DormBuildingMapper dormBuildingMapper;
    private final DormRoomMapper dormRoomMapper;
    private final DormBedMapper dormBedMapper;
    private final SysUserMapper sysUserMapper;

    /**
     * 分页查询调宿记录
     */
    @Override
    public Page<RoomChangeVO> pageRoomChanges(RoomChangePageDTO dto) {
        Page<DormRoomChange> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<DormRoomChange> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dto.getStatus() != null, DormRoomChange::getStatus, dto.getStatus())
               .orderByDesc(DormRoomChange::getCreateTime);

        // 学生姓名/学号需要关联查询
        if (StrUtil.isNotBlank(dto.getStudentName()) || StrUtil.isNotBlank(dto.getStudentNo())) {
            LambdaQueryWrapper<DormStudent> studentWrapper = new LambdaQueryWrapper<>();
            studentWrapper.like(StrUtil.isNotBlank(dto.getStudentName()), DormStudent::getName, dto.getStudentName())
                          .like(StrUtil.isNotBlank(dto.getStudentNo()), DormStudent::getStudentNo, dto.getStudentNo());
            List<DormStudent> students = dormStudentMapper.selectList(studentWrapper);
            if (students.isEmpty()) {
                Page<RoomChangeVO> emptyPage = new Page<>(dto.getPageNum(), dto.getPageSize(), 0);
                emptyPage.setRecords(Collections.emptyList());
                return emptyPage;
            }
            List<Long> studentIds = students.stream().map(DormStudent::getId).collect(Collectors.toList());
            wrapper.in(DormRoomChange::getStudentId, studentIds);
        }

        Page<DormRoomChange> resultPage = dormRoomChangeMapper.selectPage(page, wrapper);

        Page<RoomChangeVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<RoomChangeVO> voList = resultPage.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 审核调宿申请
     *
     * <p>审核流程：
     * <ol>
     *   <li>校验调宿记录是否存在且为待审核状态</li>
     *   <li>审核通过：释放原床位 → 校验新床位 → 占用新床位 → 更新房间人数 → 更新学生住宿信息</li>
     *   <li>审核拒绝：仅更新审核状态</li>
     *   <li>记录审核人、审核时间、审核备注</li>
     * </ol>
     * </p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditRoomChange(Long id, Integer status, String remark) {
        DormRoomChange roomChange = dormRoomChangeMapper.selectById(id);
        if (roomChange == null) {
            throw new BusinessException("调宿记录不存在");
        }
        if (roomChange.getStatus() != 1) {
            throw new BusinessException("该申请已审核，不能重复操作");
        }
        if (status != 2 && status != 3) {
            throw new BusinessException("审核状态不合法");
        }

        // 审核通过，执行实际换房
        if (status == 2) {
            doChangeRoom(roomChange);
        }

        // 更新审核信息
        roomChange.setStatus(status);
        roomChange.setAuditUserId(StpUtil.getLoginIdAsLong());
        roomChange.setAuditTime(LocalDateTime.now());
        roomChange.setAuditRemark(remark);
        dormRoomChangeMapper.updateById(roomChange);
    }

    /**
     * 执行实际换房操作
     */
    private void doChangeRoom(DormRoomChange roomChange) {
        Long studentId = roomChange.getStudentId();
        DormStudent student = dormStudentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        if (student.getStatus() != 1) {
            throw new BusinessException("该学生未入住，无法调换宿舍");
        }

        // 释放原床位
        if (roomChange.getOldBedId() != null) {
            DormBed oldBed = dormBedMapper.selectById(roomChange.getOldBedId());
            if (oldBed != null) {
                oldBed.setStatus(1);
                oldBed.setStudentId(null);
                dormBedMapper.updateById(oldBed);
            }
        }

        // 更新原房间人数
        if (roomChange.getOldRoomId() != null) {
            DormRoom oldRoom = dormRoomMapper.selectById(roomChange.getOldRoomId());
            if (oldRoom != null) {
                oldRoom.setCurrentCount(Math.max(0, oldRoom.getCurrentCount() - 1));
                oldRoom.setStatus(1);
                dormRoomMapper.updateById(oldRoom);
            }
        }

        // 校验新床位是否空闲
        DormBed newBed = dormBedMapper.selectById(roomChange.getNewBedId());
        if (newBed == null || newBed.getStatus() != 1) {
            throw new BusinessException("新床位不可用");
        }

        // 占用新床位
        newBed.setStatus(2);
        newBed.setStudentId(studentId);
        dormBedMapper.updateById(newBed);

        // 更新新房间人数
        DormRoom newRoom = dormRoomMapper.selectById(roomChange.getNewRoomId());
        if (newRoom != null) {
            newRoom.setCurrentCount(newRoom.getCurrentCount() + 1);
            if (newRoom.getCurrentCount() >= newRoom.getCapacity()) {
                newRoom.setStatus(2);
            }
            dormRoomMapper.updateById(newRoom);
        }

        // 更新学生住宿信息
        student.setBuildingId(roomChange.getNewBuildingId());
        student.setRoomId(roomChange.getNewRoomId());
        student.setBedId(roomChange.getNewBedId());
        dormStudentMapper.updateById(student);
    }

    /**
     * Entity 转 VO
     */
    private RoomChangeVO toVO(DormRoomChange roomChange) {
        RoomChangeVO vo = new RoomChangeVO();
        vo.setId(roomChange.getId());
        vo.setStudentId(roomChange.getStudentId());
        vo.setOldBuildingId(roomChange.getOldBuildingId());
        vo.setOldRoomId(roomChange.getOldRoomId());
        vo.setOldBedId(roomChange.getOldBedId());
        vo.setNewBuildingId(roomChange.getNewBuildingId());
        vo.setNewRoomId(roomChange.getNewRoomId());
        vo.setNewBedId(roomChange.getNewBedId());
        vo.setReason(roomChange.getReason());
        vo.setStatus(roomChange.getStatus());
        vo.setStatusDesc(switch (roomChange.getStatus()) {
            case 1 -> "待审核";
            case 2 -> "已通过";
            case 3 -> "已拒绝";
            default -> "";
        });
        vo.setAuditUserId(roomChange.getAuditUserId());
        vo.setAuditTime(roomChange.getAuditTime());
        vo.setAuditRemark(roomChange.getAuditRemark());
        vo.setCreateTime(roomChange.getCreateTime());

        // 查询学生信息
        DormStudent student = dormStudentMapper.selectById(roomChange.getStudentId());
        if (student != null) {
            vo.setStudentName(student.getName());
            vo.setStudentNo(student.getStudentNo());
        }

        // 查询原楼栋/房间/床位信息
        fillRoomInfo(vo, roomChange.getOldBuildingId(), roomChange.getOldRoomId(), roomChange.getOldBedId(), true);
        // 查询新楼栋/房间/床位信息
        fillRoomInfo(vo, roomChange.getNewBuildingId(), roomChange.getNewRoomId(), roomChange.getNewBedId(), false);

        // 查询审核人信息
        if (roomChange.getAuditUserId() != null) {
            SysUser auditUser = sysUserMapper.selectById(roomChange.getAuditUserId());
            if (auditUser != null) {
                vo.setAuditUserName(auditUser.getNickname());
            }
        }

        return vo;
    }

    /**
     * 填充楼栋/房间/床位信息
     *
     * @param vo         VO对象
     * @param buildingId 楼栋ID
     * @param roomId     房间ID
     * @param bedId      床位ID
     * @param isOld      是否为原信息
     */
    private void fillRoomInfo(RoomChangeVO vo, Long buildingId, Long roomId, Long bedId, boolean isOld) {
        if (buildingId != null) {
            DormBuilding building = dormBuildingMapper.selectById(buildingId);
            if (building != null) {
                if (isOld) {
                    vo.setOldBuildingName(building.getName());
                } else {
                    vo.setNewBuildingName(building.getName());
                }
            }
        }

        if (roomId != null) {
            DormRoom room = dormRoomMapper.selectById(roomId);
            if (room != null) {
                if (isOld) {
                    vo.setOldRoomNumber(room.getRoomNumber());
                } else {
                    vo.setNewRoomNumber(room.getRoomNumber());
                }
            }
        }

        if (bedId != null) {
            DormBed bed = dormBedMapper.selectById(bedId);
            if (bed != null) {
                String bedNo = bed.getBedNumber() != null ? String.valueOf(bed.getBedNumber()) : "";
                if (isOld) {
                    vo.setOldBedNo(bedNo);
                } else {
                    vo.setNewBedNo(bedNo);
                }
            }
        }
    }
}