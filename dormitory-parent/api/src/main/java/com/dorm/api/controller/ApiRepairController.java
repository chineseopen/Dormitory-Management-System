/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.result.R;
import com.dorm.system.entity.DormRepair;
import com.dorm.system.entity.DormStudent;
import com.dorm.system.mapper.DormStudentMapper;
import com.dorm.system.service.DormRepairService;
import com.dorm.system.vo.RepairVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 学生端 - 报修接口
 * <p>路径前缀：/api/repair</p>
 */
@RestController
@RequestMapping("/api/repair")
@RequiredArgsConstructor
public class ApiRepairController {

    private final DormRepairService dormRepairService;
    private final DormStudentMapper dormStudentMapper;

    /**
     * 提交报修
     */
    @PostMapping
    public R<Void> submit(@Validated @RequestBody DormRepair repair) {
        dormRepairService.submitRepair(repair);
        return R.ok();
    }

    /**
     * 查询我的报修记录
     */
    @GetMapping("/page")
    public R<Page<RepairVO>> myPage(@RequestParam(defaultValue = "1") Long pageNum,
                                    @RequestParam(defaultValue = "10") Long pageSize) {
        Long userId = StpUtil.getLoginIdAsLong();
        // 根据系统用户ID查询学生ID
        DormStudent student = dormStudentMapper.selectOne(
                new LambdaQueryWrapper<DormStudent>()
                        .eq(DormStudent::getUserId, userId)
        );
        if (student == null) {
            return R.fail("学生信息不存在");
        }
        return R.ok(dormRepairService.pageStudentRepairs(student.getId(), pageNum, pageSize));
    }
}
