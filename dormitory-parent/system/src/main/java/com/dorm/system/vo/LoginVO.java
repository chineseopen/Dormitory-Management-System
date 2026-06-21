/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.vo;

import lombok.Data;

import java.util.List;

/**
 * 登录返回视图对象
 */
@Data
public class LoginVO {

    /** Token 令牌 */
    private String token;

    /** 用户信息 */
    private UserVO user;

    /** 角色编码列表 */
    private List<String> roles;

    /** 权限编码列表 */
    private List<String> permissions;
}
