/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.dto;

import lombok.Data;

/**
 * 权限分页查询参数
 */
@Data
public class PermissionPageDTO {

    /** 页码 */
    private Long pageNum = 1L;

    /** 每页条数 */
    private Long pageSize = 10L;

    /** 权限名称（模糊搜索） */
    private String name;

    /** 类型：1-菜单 2-按钮 */
    private Integer type;

    /** 状态：0-禁用 1-启用 */
    private Integer status;
}
