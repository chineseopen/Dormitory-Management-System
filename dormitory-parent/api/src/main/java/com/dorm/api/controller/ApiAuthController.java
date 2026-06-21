/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.api.controller;

import com.dorm.common.result.R;
import com.dorm.system.dto.LoginDTO;
import com.dorm.system.dto.RegisterDTO;
import com.dorm.system.service.DormStudentService;
import com.dorm.system.service.SysUserService;
import com.dorm.system.vo.LoginVO;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 学生端 - 认证接口
 * <p>路径前缀：/api/auth</p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ApiAuthController {

    private final SysUserService sysUserService;
    private final DormStudentService dormStudentService;

    /**
     * 学生登录
     */
    @PostMapping("/login")
    public R<LoginVO> login(@Validated @RequestBody LoginDTO dto) {
        return R.ok(sysUserService.login(dto));
    }

    /**
     * 学生注册
     */
    @PostMapping("/register")
    public R<LoginVO> register(@Validated @RequestBody RegisterDTO dto) {
        return R.ok(dormStudentService.register(dto));
    }

    /**
     * 学生登出
     */
    @PostMapping("/logout")
    public R<Void> logout() {
        StpUtil.logout();
        return R.ok();
    }
}
