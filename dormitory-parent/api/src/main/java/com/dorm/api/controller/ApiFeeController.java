/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.result.R;
import com.dorm.system.entity.DormFee;
import com.dorm.system.entity.DormStudent;
import com.dorm.system.mapper.DormFeeMapper;
import com.dorm.system.mapper.DormStudentMapper;
import com.dorm.system.vo.FeeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 学生端 - 水电费查询接口
 * <p>路径前缀：/api/fee</p>
 */
@RestController
@RequestMapping("/api/fee")
@RequiredArgsConstructor
public class ApiFeeController {

    private final DormFeeMapper dormFeeMapper;
    private final DormStudentMapper dormStudentMapper;

    /**
     * 查询当前学生房间的水电费
     */
    @GetMapping("/list")
    public R<List<FeeVO>> list(@RequestParam(required = false) String month) {
        Long userId = StpUtil.getLoginIdAsLong();
        // 根据系统用户ID查询学生信息
        DormStudent student = dormStudentMapper.selectOne(
                new LambdaQueryWrapper<DormStudent>()
                        .eq(DormStudent::getUserId, userId)
        );
        if (student == null || student.getRoomId() == null) {
            return R.ok(List.of());
        }

        // 查询房间的水电费
        LambdaQueryWrapper<DormFee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DormFee::getRoomId, student.getRoomId());
        if (month != null) {
            wrapper.eq(DormFee::getMonth, month);
        }
        wrapper.orderByDesc(DormFee::getMonth);

        List<DormFee> feeList = dormFeeMapper.selectList(wrapper);
        List<FeeVO> voList = feeList.stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return R.ok(voList);
    }

    /**
     * Entity 转 VO
     */
    private FeeVO toVO(DormFee fee) {
        FeeVO vo = new FeeVO();
        vo.setId(fee.getId());
        vo.setBuildingId(fee.getBuildingId());
        vo.setRoomId(fee.getRoomId());
        vo.setFeeType(fee.getFeeType());
        vo.setFeeTypeDesc(fee.getFeeType() == 1 ? "水费" : "电费");
        vo.setAmount(fee.getAmount());
        vo.setMonth(fee.getMonth());
        vo.setPreviousReading(fee.getPreviousReading());
        vo.setCurrentReading(fee.getCurrentReading());
        vo.setStatus(fee.getStatus());
        vo.setStatusDesc(fee.getStatus() == 1 ? "未缴费" : "已缴费");
        vo.setPayTime(fee.getPayTime());
        vo.setCreateTime(fee.getCreateTime());
        return vo;
    }
}
