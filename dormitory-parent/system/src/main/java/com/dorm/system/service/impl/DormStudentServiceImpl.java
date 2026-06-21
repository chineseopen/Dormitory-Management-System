/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dorm.common.enums.BuildingTypeEnum;
import com.dorm.common.enums.GenderEnum;
import com.dorm.common.exception.BusinessException;
import com.dorm.common.result.ResultCode;
import com.dorm.system.dto.CheckInDTO;
import com.dorm.system.dto.RegisterDTO;
import com.dorm.system.dto.RoomChangeDTO;
import com.dorm.system.dto.StudentPageDTO;
import com.dorm.system.entity.*;
import com.dorm.system.mapper.*;
import com.dorm.system.service.DormStudentService;
import com.dorm.system.vo.LoginVO;
import com.dorm.system.vo.StudentVO;
import com.dorm.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学生 Service 实现
 *
 * <p>核心业务：
 * <ol>
 *   <li>学生注册 —— 创建系统账号 + 学生信息</li>
 *   <li>办理入住 —— 校验楼栋类型 + 床位占用 + 更新房间人数</li>
 *   <li>办理退宿 —— 释放床位 + 更新房间人数</li>
 *   <li>学生分页查询 —— 支持按学号/姓名/楼栋/状态筛选</li>
 * </ol>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class DormStudentServiceImpl extends ServiceImpl<DormStudentMapper, DormStudent> implements DormStudentService {

    private final DormStudentMapper dormStudentMapper;
    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final DormBuildingMapper dormBuildingMapper;
    private final DormRoomMapper dormRoomMapper;
    private final DormBedMapper dormBedMapper;
    private final DormCheckInMapper dormCheckInMapper;
    private final DormRoomChangeMapper dormRoomChangeMapper;

    /**
     * 学生注册
     *
     * <p>注册流程：
     * <ol>
     *   <li>校验用户名唯一性</li>
     *   <li>校验学号唯一性</li>
     *   <li>创建系统用户（密码BCrypt加密）</li>
     *   <li>创建学生信息</li>
     *   <li>Sa-Token 执行登录</li>
     * </ol>
     * </p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO register(RegisterDTO dto) {
        // 1. 校验用户名唯一性
        Long userCount = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, dto.getUsername())
        );
        if (userCount > 0) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        // 2. 校验学号唯一性
        Long studentCount = dormStudentMapper.selectCount(
                new LambdaQueryWrapper<DormStudent>()
                        .eq(DormStudent::getStudentNo, dto.getStudentNo())
        );
        if (studentCount > 0) {
            throw new BusinessException(ResultCode.STUDENT_NO_EXISTS);
        }

        // 3. 创建系统用户
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setNickname(dto.getName());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender());
        user.setStatus(1);
        sysUserMapper.insert(user);

        // 4. 分配学生角色
        SysRole studentRole = sysRoleMapper.selectOne(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getCode, "student")
        );
        if (studentRole != null) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(studentRole.getId());
            sysUserRoleMapper.insert(userRole);
        }

        // 5. 创建学生信息
        DormStudent student = new DormStudent();
        student.setUserId(user.getId());
        student.setStudentNo(dto.getStudentNo());
        student.setName(dto.getName());
        student.setGender(dto.getGender());
        student.setPhone(dto.getPhone());
        student.setStatus(0); // 未入住
        dormStudentMapper.insert(student);

        // 6. Sa-Token 执行登录
        StpUtil.login(user.getId());
        return studentLogin(student.getId());
    }

    /**
     * 学生登录
     */
    @Override
    public LoginVO studentLogin(Long studentId) {
        DormStudent student = dormStudentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }

        LoginVO vo = new LoginVO();
        vo.setToken(StpUtil.getTokenValue());

        UserVO userVO = new UserVO();
        userVO.setId(student.getId());
        userVO.setUsername(student.getStudentNo());
        userVO.setNickname(student.getName());
        userVO.setGender(student.getGender());
        userVO.setPhone(student.getPhone());
        vo.setUser(userVO);
        vo.setRoles(Collections.singletonList("student"));
        return vo;
    }

    /**
     * 分页查询学生列表
     */
    @Override
    public Page<StudentVO> pageStudents(StudentPageDTO dto) {
        Page<DormStudent> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<DormStudent> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(dto.getStudentNo()), DormStudent::getStudentNo, dto.getStudentNo())
               .like(StrUtil.isNotBlank(dto.getName()), DormStudent::getName, dto.getName())
               .eq(dto.getBuildingId() != null, DormStudent::getBuildingId, dto.getBuildingId())
               .eq(dto.getStatus() != null, DormStudent::getStatus, dto.getStatus())
               .orderByDesc(DormStudent::getCreateTime);

        Page<DormStudent> resultPage = dormStudentMapper.selectPage(page, wrapper);

        Page<StudentVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<StudentVO> voList = resultPage.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 办理入住
     *
     * <p>入住流程：
     * <ol>
     *   <li>校验学生状态（不能重复入住）</li>
     *   <li>校验楼栋类型与学生性别匹配</li>
     *   <li>校验床位是否空闲</li>
     *   <li>更新床位状态为已占用</li>
     *   <li>更新房间入住人数，满员则更新房间状态</li>
     *   <li>更新学生住宿信息</li>
     *   <li>创建入住记录</li>
     * </ol>
     * </p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkIn(CheckInDTO dto) {
        DormStudent student = dormStudentMapper.selectById(dto.getStudentId());
        if (student == null) {
            throw new BusinessException("学生不存在");
        }

        // 校验学生是否已入住
        if (student.getStatus() == 1) {
            throw new BusinessException("该学生已入住，不能重复办理");
        }

        // 校验楼栋类型与学生性别匹配
        DormBuilding building = dormBuildingMapper.selectById(dto.getBuildingId());
        if (building != null && student.getGender() != null) {
            if (building.getType() != student.getGender()) {
                throw new BusinessException(ResultCode.BUILDING_TYPE_MISMATCH);
            }
        }

        // 校验床位是否空闲
        DormBed bed = dormBedMapper.selectById(dto.getBedId());
        if (bed == null || bed.getStatus() != 1) {
            throw new BusinessException(ResultCode.BED_OCCUPIED);
        }

        // 更新床位状态为已占用
        bed.setStatus(2);
        bed.setStudentId(student.getId());
        dormBedMapper.updateById(bed);

        // 更新房间入住人数
        DormRoom room = dormRoomMapper.selectById(dto.getRoomId());
        if (room != null) {
            room.setCurrentCount(room.getCurrentCount() + 1);
            // 满员则更新房间状态
            if (room.getCurrentCount() >= room.getCapacity()) {
                room.setStatus(2); // 已满
            }
            dormRoomMapper.updateById(room);
        }

        // 更新学生住宿信息
        student.setBuildingId(dto.getBuildingId());
        student.setRoomId(dto.getRoomId());
        student.setBedId(dto.getBedId());
        student.setCheckInDate(LocalDate.now());
        student.setStatus(1); // 在住
        dormStudentMapper.updateById(student);

        // 创建入住记录
        DormCheckIn checkIn = new DormCheckIn();
        checkIn.setStudentId(student.getId());
        checkIn.setBuildingId(dto.getBuildingId());
        checkIn.setRoomId(dto.getRoomId());
        checkIn.setBedId(dto.getBedId());
        checkIn.setCheckInDate(LocalDate.now());
        checkIn.setStatus(1); // 在住
        checkIn.setRemark(dto.getRemark());
        dormCheckInMapper.insert(checkIn);
    }

    /**
     * 办理退宿
     *
     * <p>退宿流程：
     * <ol>
     *   <li>释放床位</li>
     *   <li>更新房间入住人数和状态</li>
     *   <li>更新学生状态</li>
     *   <li>更新入住记录</li>
     * </ol>
     * </p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkOut(Long studentId) {
        DormStudent student = dormStudentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        if (student.getStatus() != 1) {
            throw new BusinessException("该学生未入住，无法退宿");
        }

        // 释放床位
        if (student.getBedId() != null) {
            DormBed bed = dormBedMapper.selectById(student.getBedId());
            if (bed != null) {
                bed.setStatus(1); // 空闲
                bed.setStudentId(null);
                dormBedMapper.updateById(bed);
            }
        }

        // 更新房间入住人数
        if (student.getRoomId() != null) {
            DormRoom room = dormRoomMapper.selectById(student.getRoomId());
            if (room != null) {
                room.setCurrentCount(Math.max(0, room.getCurrentCount() - 1));
                room.setStatus(1); // 空闲
                dormRoomMapper.updateById(room);
            }
        }

        // 更新学生状态
        student.setStatus(2); // 退宿
        student.setBuildingId(null);
        student.setRoomId(null);
        student.setBedId(null);
        dormStudentMapper.updateById(student);

        // 更新入住记录
        DormCheckIn checkIn = dormCheckInMapper.selectOne(
                new LambdaQueryWrapper<DormCheckIn>()
                        .eq(DormCheckIn::getStudentId, studentId)
                        .eq(DormCheckIn::getStatus, 1)
        );
        if (checkIn != null) {
            checkIn.setStatus(2); // 已退宿
            checkIn.setCheckOutDate(LocalDate.now());
            dormCheckInMapper.updateById(checkIn);
        }
    }

    /**
     * 获取学生个人信息
     */
    @Override
    public StudentVO getStudentInfo(Long studentId) {
        DormStudent student = dormStudentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        return toVO(student);
    }

    /**
     * 新增学生
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addStudent(DormStudent student) {
        // 校验学号唯一性
        Long count = dormStudentMapper.selectCount(
                new LambdaQueryWrapper<DormStudent>()
                        .eq(DormStudent::getStudentNo, student.getStudentNo())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.STUDENT_NO_EXISTS);
        }
        // 新增学生默认为未入住状态
        if (student.getStatus() == null) {
            student.setStatus(0);
        }
        dormStudentMapper.insert(student);
    }

    /**
     * 编辑学生
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStudent(DormStudent student) {
        DormStudent exist = dormStudentMapper.selectById(student.getId());
        if (exist == null) {
            throw new BusinessException("学生不存在");
        }
        dormStudentMapper.updateById(student);
    }

    /**
     * 调换宿舍
     *
     * <p>换房流程：
     * <ol>
     *   <li>校验学生是否在住</li>
     *   <li>释放原床位</li>
     *   <li>更新原房间人数</li>
     *   <li>校验新床位是否空闲</li>
     *   <li>占用新床位</li>
     *   <li>更新新房间人数</li>
     *   <li>更新学生住宿信息</li>
     * </ol>
     * </p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeRoom(RoomChangeDTO dto) {
        DormStudent student = dormStudentMapper.selectById(dto.getStudentId());
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        if (student.getStatus() != 1) {
            throw new BusinessException("该学生未入住，无法调换宿舍");
        }

        // 释放原床位
        if (student.getBedId() != null) {
            DormBed oldBed = dormBedMapper.selectById(student.getBedId());
            if (oldBed != null) {
                oldBed.setStatus(1);
                oldBed.setStudentId(null);
                dormBedMapper.updateById(oldBed);
            }
        }

        // 更新原房间人数
        if (student.getRoomId() != null) {
            DormRoom oldRoom = dormRoomMapper.selectById(student.getRoomId());
            if (oldRoom != null) {
                oldRoom.setCurrentCount(Math.max(0, oldRoom.getCurrentCount() - 1));
                oldRoom.setStatus(1);
                dormRoomMapper.updateById(oldRoom);
            }
        }

        // 校验新床位是否空闲
        DormBed newBed = dormBedMapper.selectById(dto.getNewBedId());
        if (newBed == null || newBed.getStatus() != 1) {
            throw new BusinessException("新床位不可用");
        }

        // 占用新床位
        newBed.setStatus(2);
        newBed.setStudentId(student.getId());
        dormBedMapper.updateById(newBed);

        // 更新新房间人数
        DormRoom newRoom = dormRoomMapper.selectById(dto.getNewRoomId());
        if (newRoom != null) {
            newRoom.setCurrentCount(newRoom.getCurrentCount() + 1);
            if (newRoom.getCurrentCount() >= newRoom.getCapacity()) {
                newRoom.setStatus(2);
            }
            dormRoomMapper.updateById(newRoom);
        }

        // 保存原住宿信息（用于记录调宿）
        Long oldBuildingId = student.getBuildingId();
        Long oldRoomId = student.getRoomId();
        Long oldBedId = student.getBedId();

        // 更新学生住宿信息
        student.setBuildingId(dto.getNewBuildingId());
        student.setRoomId(dto.getNewRoomId());
        student.setBedId(dto.getNewBedId());
        dormStudentMapper.updateById(student);

        // 记录调宿信息
        DormRoomChange roomChange = new DormRoomChange();
        roomChange.setStudentId(student.getId());
        roomChange.setOldBuildingId(oldBuildingId);
        roomChange.setOldRoomId(oldRoomId);
        roomChange.setOldBedId(oldBedId);
        roomChange.setNewBuildingId(dto.getNewBuildingId());
        roomChange.setNewRoomId(dto.getNewRoomId());
        roomChange.setNewBedId(dto.getNewBedId());
        roomChange.setReason(dto.getReason());
        roomChange.setStatus(2); // 已通过（管理员直接调宿）
        roomChange.setAuditUserId(StpUtil.getLoginIdAsLong());
        roomChange.setAuditTime(LocalDateTime.now());
        dormRoomChangeMapper.insert(roomChange);
    }

    /**
     * 批量删除学生
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteStudents(List<Long> ids) {
        // 在住学生不允许删除
        Long count = dormStudentMapper.selectCount(
                new LambdaQueryWrapper<DormStudent>()
                        .in(DormStudent::getId, ids)
                        .eq(DormStudent::getStatus, 1)
        );
        if (count > 0) {
            throw new BusinessException("在住学生不允许删除，请先办理退宿");
        }
        dormStudentMapper.deleteBatchIds(ids);
    }

    /**
     * Entity 转 VO
     */
    private StudentVO toVO(DormStudent student) {
        StudentVO vo = new StudentVO();
        vo.setId(student.getId());
        vo.setUserId(student.getUserId());
        vo.setStudentNo(student.getStudentNo());
        vo.setName(student.getName());
        vo.setGender(student.getGender());
        vo.setGenderDesc(GenderEnum.getByCode(student.getGender()) != null
                ? GenderEnum.getByCode(student.getGender()).getDesc() : "");
        vo.setCollege(student.getCollege());
        vo.setMajor(student.getMajor());
        vo.setClassName(student.getClassName());
        vo.setPhone(student.getPhone());
        vo.setEmail(student.getEmail());
        vo.setBuildingId(student.getBuildingId());
        vo.setRoomId(student.getRoomId());
        vo.setBedId(student.getBedId());
        vo.setCheckInDate(student.getCheckInDate());
        vo.setStatus(student.getStatus());
        // 状态描述
        String statusDesc = switch (student.getStatus()) {
            case 0 -> "未入住";
            case 1 -> "在住";
            case 2 -> "退宿";
            case 3 -> "休学";
            default -> "";
        };
        vo.setStatusDesc(statusDesc);
        vo.setCreateTime(student.getCreateTime());

        // 查询楼栋名称
        if (student.getBuildingId() != null) {
            DormBuilding building = dormBuildingMapper.selectById(student.getBuildingId());
            if (building != null) {
                vo.setBuildingName(building.getName());
            }
        }

        // 查询房间号
        if (student.getRoomId() != null) {
            DormRoom room = dormRoomMapper.selectById(student.getRoomId());
            if (room != null) {
                vo.setRoomNumber(room.getRoomNumber());
            }
        }
        return vo;
    }
}