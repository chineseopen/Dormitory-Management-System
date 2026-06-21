/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.result.R;
import com.dorm.system.dto.UserPageDTO;
import com.dorm.system.entity.SysUser;
import com.dorm.system.service.SysUserService;
import com.dorm.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端 - 用户管理接口
 * <p>路径前缀：/admin/user</p>
 */
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final SysUserService sysUserService;

    /**
     * 分页查询用户列表
     */
    @SaCheckPermission("system:user:query")
    @GetMapping("/page")
    public R<Page<UserVO>> page(UserPageDTO dto) {
        return R.ok(sysUserService.pageUsers(dto));
    }

    /**
     * 查询用户详情
     */
    @SaCheckPermission("system:user:query")
    @GetMapping("/{id}")
    public R<SysUser> getById(@PathVariable Long id) {
        return R.ok(sysUserService.getById(id));
    }

    /**
     * 新增用户
     */
    @SaCheckPermission("system:user:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysUser user) {
        sysUserService.addUser(user);
        return R.ok();
    }

    /**
     * 编辑用户
     */
    @SaCheckPermission("system:user:edit")
    @PutMapping
    public R<Void> update(@Validated @RequestBody SysUser user) {
        sysUserService.updateUser(user);
        return R.ok();
    }

    /**
     * 批量删除用户
     */
    @SaCheckPermission("system:user:delete")
    @DeleteMapping("/batch")
    public R<Void> deleteBatch(@RequestBody List<Long> ids) {
        sysUserService.batchDeleteUsers(ids);
        return R.ok();
    }

    /**
     * 重置用户密码
     */
    @SaCheckPermission("system:user:edit")
    @PutMapping("/resetPassword/{id}")
    public R<Void> resetPassword(@PathVariable Long id) {
        sysUserService.resetPassword(id);
        return R.ok();
    }

    /**
     * 分配角色
     */
    @SaCheckPermission("system:user:edit")
    @PutMapping("/assignRoles")
    public R<Void> assignRoles(@RequestParam Long userId, @RequestBody List<Long> roleIds) {
        sysUserService.assignRoles(userId, roleIds);
        return R.ok();
    }
}