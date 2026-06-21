/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.dto;

import lombok.Data;

/**
 * 用户分页查询参数
 */
@Data
public class UserPageDTO {

    /** 当前页码 */
    private Long pageNum = 1L;

    /** 每页条数 */
    private Long pageSize = 10L;

    /** 用户名（模糊搜索） */
    private String username;

    /** 手机号（模糊搜索） */
    private String phone;

    /** 状态：0-禁用 1-启用 */
    private Integer status;
}
