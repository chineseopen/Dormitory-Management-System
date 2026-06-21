/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限视图对象
 */
@Data
public class PermissionVO {

    /** 主键ID */
    private Long id;

    /** 父权限ID */
    private Long parentId;

    /** 权限名称 */
    private String name;

    /** 权限编码 */
    private String code;

    /** 类型：1-菜单 2-按钮 */
    private Integer type;

    /** 类型描述 */
    private String typeDesc;

    /** 排序 */
    private Integer sort;

    /** 状态：0-禁用 1-启用 */
    private Integer status;

    /** 状态描述 */
    private String statusDesc;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 子权限列表 */
    private List<PermissionVO> children;
}
