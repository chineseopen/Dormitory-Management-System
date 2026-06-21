/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.result.R;
import com.dorm.system.dto.PermissionPageDTO;
import com.dorm.system.entity.SysPermission;
import com.dorm.system.service.SysPermissionService;
import com.dorm.system.vo.PermissionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/permission")
@RequiredArgsConstructor
public class AdminPermissionController {

    private final SysPermissionService sysPermissionService;

    @SaCheckPermission("system:permission")
    @GetMapping("/page")
    public R<Page<PermissionVO>> page(PermissionPageDTO dto) {
        return R.ok(sysPermissionService.pagePermissions(dto));
    }

    @SaCheckPermission("system:permission")
    @GetMapping("/tree")
    public R<List<PermissionVO>> tree() {
        return R.ok(sysPermissionService.permissionTree());
    }

    @SaCheckPermission("system:permission")
    @GetMapping("/{id}")
    public R<SysPermission> getById(@PathVariable Long id) {
        return R.ok(sysPermissionService.getById(id));
    }

    @SaCheckPermission("system:permission:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysPermission permission) {
        sysPermissionService.addPermission(permission);
        return R.ok();
    }

    @SaCheckPermission("system:permission:edit")
    @PutMapping
    public R<Void> update(@Validated @RequestBody SysPermission permission) {
        sysPermissionService.updatePermission(permission);
        return R.ok();
    }

    @SaCheckPermission("system:permission:delete")
    @DeleteMapping("/batch")
    public R<Void> deleteBatch(@RequestBody List<Long> ids) {
        sysPermissionService.batchDeletePermissions(ids);
        return R.ok();
    }
}
