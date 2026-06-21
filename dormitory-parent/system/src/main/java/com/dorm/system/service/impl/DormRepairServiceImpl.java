/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dorm.common.enums.RepairStatusEnum;
import com.dorm.common.exception.BusinessException;
import com.dorm.system.dto.RepairHandleDTO;
import com.dorm.system.dto.RepairPageDTO;
import com.dorm.system.entity.DormBuilding;
import com.dorm.system.entity.DormRepair;
import com.dorm.system.entity.DormRoom;
import com.dorm.system.entity.DormStudent;
import com.dorm.system.mapper.DormBuildingMapper;
import com.dorm.system.mapper.DormRepairMapper;
import com.dorm.system.mapper.DormRoomMapper;
import com.dorm.system.mapper.DormStudentMapper;
import com.dorm.system.service.DormRepairService;
import com.dorm.system.vo.RepairVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 报修 Service 实现
 *
 * <p>核心业务：
 * <ol>
 *   <li>学生提交报修 —— 默认待处理状态</li>
 *   <li>管理员处理报修 —— 更新状态和维修结果</li>
 *   <li>报修分页查询 —— 支持按楼栋/状态/学生筛选</li>
 * </ol>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class DormRepairServiceImpl extends ServiceImpl<DormRepairMapper, DormRepair> implements DormRepairService {

    private final DormRepairMapper dormRepairMapper;
    private final DormStudentMapper dormStudentMapper;
    private final DormBuildingMapper dormBuildingMapper;
    private final DormRoomMapper dormRoomMapper;

    /**
     * 分页查询报修列表
     */
    @Override
    public Page<RepairVO> pageRepairs(RepairPageDTO dto) {
        Page<DormRepair> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<DormRepair> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dto.getBuildingId() != null, DormRepair::getBuildingId, dto.getBuildingId())
               .eq(dto.getStatus() != null, DormRepair::getStatus, dto.getStatus())
               .eq(dto.getStudentId() != null, DormRepair::getStudentId, dto.getStudentId())
               .orderByDesc(DormRepair::getCreateTime);

        Page<DormRepair> resultPage = dormRepairMapper.selectPage(page, wrapper);

        Page<RepairVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<RepairVO> voList = resultPage.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 学生提交报修
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitRepair(DormRepair repair) {
        Long userId = StpUtil.getLoginIdAsLong();
        DormStudent student = dormStudentMapper.selectOne(
                new LambdaQueryWrapper<DormStudent>()
                        .eq(DormStudent::getUserId, userId)
        );
        if (student == null) {
            throw new BusinessException("学生信息不存在");
        }
        repair.setStudentId(student.getId());
        repair.setBuildingId(student.getBuildingId());
        repair.setRoomId(student.getRoomId());
        repair.setStatus(1); // 待处理
        dormRepairMapper.insert(repair);
    }

    /**
     * 处理报修
     *
     * <p>更新报修状态，记录维修人员和维修时间</p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleRepair(RepairHandleDTO dto) {
        DormRepair repair = dormRepairMapper.selectById(dto.getRepairId());
        if (repair == null) {
            throw new BusinessException("报修记录不存在");
        }

        // 校验状态流转合法性：待处理(1)→处理中(2)→已完成(3)→已关闭(4)，不可回退
        Integer oldStatus = repair.getStatus();
        Integer newStatus = dto.getStatus();
        if (newStatus <= oldStatus) {
            throw new BusinessException("报修状态不可回退");
        }

        repair.setStatus(newStatus);
        repair.setResult(dto.getResult());
        repair.setRepairTime(LocalDateTime.now());
        // 记录当前操作人为维修人员
        repair.setRepairUserId(StpUtil.getLoginIdAsLong());

        dormRepairMapper.updateById(repair);
    }

    /**
     * 查询学生的报修记录
     */
    @Override
    public Page<RepairVO> pageStudentRepairs(Long studentId, Long pageNum, Long pageSize) {
        Page<DormRepair> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<DormRepair> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DormRepair::getStudentId, studentId)
               .orderByDesc(DormRepair::getCreateTime);

        Page<DormRepair> resultPage = dormRepairMapper.selectPage(page, wrapper);

        Page<RepairVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<RepairVO> voList = resultPage.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 批量删除报修记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteRepairs(List<Long> ids) {
        dormRepairMapper.deleteBatchIds(ids);
    }

    /**
     * 查询待处理报修数量
     */
    @Override
    public Long getPendingCount() {
        return dormRepairMapper.selectCount(
                new LambdaQueryWrapper<DormRepair>()
                        .eq(DormRepair::getStatus, 1)
        );
    }

    /**
     * Entity 转 VO
     */
    private RepairVO toVO(DormRepair repair) {
        RepairVO vo = new RepairVO();
        vo.setId(repair.getId());
        vo.setStudentId(repair.getStudentId());
        vo.setBuildingId(repair.getBuildingId());
        vo.setRoomId(repair.getRoomId());
        vo.setTitle(repair.getTitle());
        vo.setContent(repair.getContent());
        vo.setImages(repair.getImages());
        vo.setStatus(repair.getStatus());
        vo.setStatusDesc(RepairStatusEnum.getByCode(repair.getStatus()) != null
                ? RepairStatusEnum.getByCode(repair.getStatus()).getDesc() : "");
        vo.setRepairUserId(repair.getRepairUserId());
        vo.setRepairTime(repair.getRepairTime());
        vo.setResult(repair.getResult());
        vo.setCreateTime(repair.getCreateTime());

        // 查询学生姓名
        if (repair.getStudentId() != null) {
            DormStudent student = dormStudentMapper.selectById(repair.getStudentId());
            if (student != null) {
                vo.setStudentName(student.getName());
            }
        }

        // 查询楼栋名称
        if (repair.getBuildingId() != null) {
            DormBuilding building = dormBuildingMapper.selectById(repair.getBuildingId());
            if (building != null) {
                vo.setBuildingName(building.getName());
            }
        }

        // 查询房间号
        if (repair.getRoomId() != null) {
            DormRoom room = dormRoomMapper.selectById(repair.getRoomId());
            if (room != null) {
                vo.setRoomNumber(room.getRoomNumber());
            }
        }
        return vo;
    }
}