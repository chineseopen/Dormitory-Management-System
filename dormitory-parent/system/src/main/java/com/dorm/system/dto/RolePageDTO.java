/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.dto;

import lombok.Data;

/**
 * 角色分页查询参数
 */
@Data
public class RolePageDTO {

    /** 页码 */
    private Long pageNum = 1L;

    /** 每页条数 */
    private Long pageSize = 10L;

    /** 角色名称（模糊搜索） */
    private String name;

    /** 角色编码（模糊搜索） */
    private String code;

    /** 状态：0-禁用 1-启用 */
    private Integer status;
}
