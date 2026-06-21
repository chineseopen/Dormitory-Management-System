/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色视图对象
 */
@Data
public class RoleVO {

    /** 主键ID */
    private Long id;

    /** 角色名称 */
    private String name;

    /** 角色编码 */
    private String code;

    /** 描述 */
    private String description;

    /** 状态：0-禁用 1-启用 */
    private Integer status;

    /** 状态描述 */
    private String statusDesc;

    /** 关联用户数 */
    private Long userCount;

    /** 关联权限ID列表 */
    private List<Long> permissionIds;

    /** 创建时间 */
    private LocalDateTime createTime;
}
