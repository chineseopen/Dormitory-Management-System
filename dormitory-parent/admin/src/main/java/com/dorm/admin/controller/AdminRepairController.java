/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.result.R;
import com.dorm.system.dto.RepairHandleDTO;
import com.dorm.system.dto.RepairPageDTO;
import com.dorm.system.service.DormRepairService;
import com.dorm.system.vo.RepairVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端 - 报修管理接口
 * <p>路径前缀：/admin/repair</p>
 */
@RestController
@RequestMapping("/admin/repair")
@RequiredArgsConstructor
public class AdminRepairController {

    private final DormRepairService dormRepairService;

    /**
     * 分页查询报修列表
     */
    @SaCheckPermission("repair:list")
    @GetMapping("/page")
    public R<Page<RepairVO>> page(RepairPageDTO dto) {
        return R.ok(dormRepairService.pageRepairs(dto));
    }

    /**
     * 处理报修
     */
    @SaCheckPermission("repair:handle")
    @PutMapping("/handle")
    public R<Void> handle(@Validated @RequestBody RepairHandleDTO dto) {
        dormRepairService.handleRepair(dto);
        return R.ok();
    }

    /**
     * 批量删除报修记录
     */
    @SaCheckPermission("repair:close")
    @DeleteMapping("/batch")
    public R<Void> deleteBatch(@RequestBody List<Long> ids) {
        dormRepairService.batchDeleteRepairs(ids);
        return R.ok();
    }
}