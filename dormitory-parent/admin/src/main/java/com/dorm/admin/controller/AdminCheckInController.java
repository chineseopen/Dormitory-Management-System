/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.result.R;
import com.dorm.system.dto.CheckInPageDTO;
import com.dorm.system.service.DormCheckInService;
import com.dorm.system.vo.CheckInVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端 - 入住记录接口
 * <p>路径前缀：/admin/checkin</p>
 */
@RestController
@RequestMapping("/admin/checkin")
@RequiredArgsConstructor
public class AdminCheckInController {

    private final DormCheckInService dormCheckInService;

    /**
     * 分页查询入住记录
     */
    @SaCheckPermission("student:checkin")
    @GetMapping("/page")
    public R<Page<CheckInVO>> page(CheckInPageDTO dto) {
        return R.ok(dormCheckInService.pageCheckIns(dto));
    }
}