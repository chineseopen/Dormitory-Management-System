/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dorm.system.dto.CheckInPageDTO;
import com.dorm.system.entity.DormBed;
import com.dorm.system.entity.DormBuilding;
import com.dorm.system.entity.DormCheckIn;
import com.dorm.system.entity.DormRoom;
import com.dorm.system.entity.DormStudent;
import com.dorm.system.mapper.DormBedMapper;
import com.dorm.system.mapper.DormBuildingMapper;
import com.dorm.system.mapper.DormCheckInMapper;
import com.dorm.system.mapper.DormRoomMapper;
import com.dorm.system.mapper.DormStudentMapper;
import com.dorm.system.service.DormCheckInService;
import com.dorm.system.vo.CheckInVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 入住记录 Service 实现
 */
@Service
@RequiredArgsConstructor
public class DormCheckInServiceImpl extends ServiceImpl<DormCheckInMapper, DormCheckIn> implements DormCheckInService {

    private final DormCheckInMapper dormCheckInMapper;
    private final DormStudentMapper dormStudentMapper;
    private final DormBuildingMapper dormBuildingMapper;
    private final DormRoomMapper dormRoomMapper;
    private final DormBedMapper dormBedMapper;

    /**
     * 分页查询入住记录
     */
    @Override
    public Page<CheckInVO> pageCheckIns(CheckInPageDTO dto) {
        Page<DormCheckIn> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<DormCheckIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dto.getBuildingId() != null, DormCheckIn::getBuildingId, dto.getBuildingId())
               .eq(dto.getStatus() != null, DormCheckIn::getStatus, dto.getStatus())
               .orderByDesc(DormCheckIn::getCreateTime);

        // 学生姓名/学号需要关联查询
        if (StrUtil.isNotBlank(dto.getStudentName()) || StrUtil.isNotBlank(dto.getStudentNo())) {
            LambdaQueryWrapper<DormStudent> studentWrapper = new LambdaQueryWrapper<>();
            studentWrapper.like(StrUtil.isNotBlank(dto.getStudentName()), DormStudent::getName, dto.getStudentName())
                          .like(StrUtil.isNotBlank(dto.getStudentNo()), DormStudent::getStudentNo, dto.getStudentNo());
            List<DormStudent> students = dormStudentMapper.selectList(studentWrapper);
            if (students.isEmpty()) {
                Page<CheckInVO> emptyPage = new Page<>(dto.getPageNum(), dto.getPageSize(), 0);
                emptyPage.setRecords(Collections.emptyList());
                return emptyPage;
            }
            List<Long> studentIds = students.stream().map(DormStudent::getId).collect(Collectors.toList());
            wrapper.in(DormCheckIn::getStudentId, studentIds);
        }

        Page<DormCheckIn> resultPage = dormCheckInMapper.selectPage(page, wrapper);

        Page<CheckInVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<CheckInVO> voList = resultPage.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * Entity 转 VO
     */
    private CheckInVO toVO(DormCheckIn checkIn) {
        CheckInVO vo = new CheckInVO();
        vo.setId(checkIn.getId());
        vo.setStudentId(checkIn.getStudentId());
        vo.setBuildingId(checkIn.getBuildingId());
        vo.setRoomId(checkIn.getRoomId());
        vo.setBedId(checkIn.getBedId());
        vo.setCheckInDate(checkIn.getCheckInDate());
        vo.setCheckOutDate(checkIn.getCheckOutDate());
        vo.setStatus(checkIn.getStatus());
        vo.setStatusDesc(checkIn.getStatus() == 1 ? "在住" : "已退宿");
        vo.setRemark(checkIn.getRemark());
        vo.setCreateTime(checkIn.getCreateTime());

        // 查询学生信息
        DormStudent student = dormStudentMapper.selectById(checkIn.getStudentId());
        if (student != null) {
            vo.setStudentName(student.getName());
            vo.setStudentNo(student.getStudentNo());
        }

        // 查询楼栋名称
        if (checkIn.getBuildingId() != null) {
            DormBuilding building = dormBuildingMapper.selectById(checkIn.getBuildingId());
            if (building != null) {
                vo.setBuildingName(building.getName());
            }
        }

        // 查询房间号
        if (checkIn.getRoomId() != null) {
            DormRoom room = dormRoomMapper.selectById(checkIn.getRoomId());
            if (room != null) {
                vo.setRoomNumber(room.getRoomNumber());
            }
        }

        // 查询床位号
        if (checkIn.getBedId() != null) {
            DormBed bed = dormBedMapper.selectById(checkIn.getBedId());
            if (bed != null) {
                vo.setBedNo(bed.getBedNumber() != null ? String.valueOf(bed.getBedNumber()) : "");
            }
        }

        return vo;
    }
}
