/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.result.R;
import com.dorm.system.dto.VisitorPageDTO;
import com.dorm.system.entity.DormVisitor;
import com.dorm.system.service.DormVisitorService;
import com.dorm.system.vo.VisitorVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/visitor")
@RequiredArgsConstructor
public class AdminVisitorController {

    private final DormVisitorService dormVisitorService;

    @SaCheckPermission("visitor:list")
    @GetMapping("/page")
    public R<Page<VisitorVO>> page(VisitorPageDTO dto) {
        return R.ok(dormVisitorService.pageVisitors(dto));
    }

    @SaCheckPermission("visitor:list")
    @GetMapping("/{id}")
    public R<DormVisitor> getById(@PathVariable Long id) {
        return R.ok(dormVisitorService.getById(id));
    }

    @SaCheckPermission("visitor:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody DormVisitor visitor) {
        dormVisitorService.addVisitor(visitor);
        return R.ok();
    }

    @SaCheckPermission("visitor:leave")
    @PutMapping("/leave/{id}")
    public R<Void> visitorLeave(@PathVariable Long id) {
        dormVisitorService.visitorLeave(id);
        return R.ok();
    }

    @SaCheckPermission("visitor:delete")
    @DeleteMapping("/batch")
    public R<Void> deleteBatch(@RequestBody List<Long> ids) {
        dormVisitorService.batchDeleteVisitors(ids);
        return R.ok();
    }
}