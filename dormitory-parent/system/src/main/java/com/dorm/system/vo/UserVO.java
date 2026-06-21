/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户信息视图对象
 */
@Data
public class UserVO {

    /** 用户ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** 头像地址 */
    private String avatar;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 性别：1-男 2-女 */
    private Integer gender;

    /** 状态：0-禁用 1-启用 */
    private Integer status;

    /** 关联角色ID列表 */
    private List<Long> roleIds;

    /** 创建时间 */
    private LocalDateTime createTime;
}
