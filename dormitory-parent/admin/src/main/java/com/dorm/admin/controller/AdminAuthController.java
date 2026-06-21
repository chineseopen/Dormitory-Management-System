/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.admin.controller;

import com.dorm.common.result.R;
import com.dorm.system.dto.ChangePasswordDTO;
import com.dorm.system.dto.LoginDTO;
import com.dorm.system.entity.SysUser;
import com.dorm.system.service.SysUserService;
import com.dorm.system.vo.LoginVO;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端 - 认证接口
 * <p>路径前缀：/admin/auth</p>
 */
@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final SysUserService sysUserService;

    /**
     * 管理端登录
     */
    @PostMapping("/login")
    public R<LoginVO> login(@Validated @RequestBody LoginDTO dto) {
        return R.ok(sysUserService.login(dto));
    }

    /**
     * 管理端登出
     */
    @PostMapping("/logout")
    public R<Void> logout() {
        StpUtil.logout();
        return R.ok();
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/userInfo")
    public R<LoginVO> getUserInfo() {
        return R.ok(sysUserService.getUserInfo());
    }

    /**
     * 修改密码
     */
    @PutMapping("/changePassword")
    public R<Void> changePassword(@Validated @RequestBody ChangePasswordDTO dto) {
        sysUserService.changePassword(dto);
        return R.ok();
    }

    /**
     * 更新当前用户基本信息
     */
    @PutMapping("/profile")
    public R<Void> updateProfile(@RequestBody SysUser user) {
        sysUserService.updateProfile(user);
        return R.ok();
    }
}
