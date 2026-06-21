/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.result.R;
import com.dorm.system.dto.RolePageDTO;
import com.dorm.system.entity.SysRole;
import com.dorm.system.service.SysRoleService;
import com.dorm.system.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/role")
@RequiredArgsConstructor
public class AdminRoleController {

    private final SysRoleService sysRoleService;

    @SaCheckPermission("system:role")
    @GetMapping("/page")
    public R<Page<RoleVO>> page(RolePageDTO dto) {
        return R.ok(sysRoleService.pageRoles(dto));
    }

    @SaCheckPermission("system:role")
    @GetMapping("/listAll")
    public R<List<RoleVO>> listAll() {
        return R.ok(sysRoleService.listAll());
    }

    @SaCheckPermission("system:role")
    @GetMapping("/{id}")
    public R<SysRole> getById(@PathVariable Long id) {
        return R.ok(sysRoleService.getById(id));
    }

    @SaCheckPermission("system:role:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysRole role) {
        sysRoleService.addRole(role);
        return R.ok();
    }

    @SaCheckPermission("system:role:edit")
    @PutMapping
    public R<Void> update(@Validated @RequestBody SysRole role) {
        sysRoleService.updateRole(role);
        return R.ok();
    }

    @SaCheckPermission("system:role:delete")
    @DeleteMapping("/batch")
    public R<Void> deleteBatch(@RequestBody List<Long> ids) {
        sysRoleService.batchDeleteRoles(ids);
        return R.ok();
    }

    @SaCheckPermission("system:role:edit")
    @PutMapping("/assignPermissions")
    public R<Void> assignPermissions(@RequestParam Long roleId, @RequestBody List<Long> permissionIds) {
        sysRoleService.assignPermissions(roleId, permissionIds);
        return R.ok();
    }
}
