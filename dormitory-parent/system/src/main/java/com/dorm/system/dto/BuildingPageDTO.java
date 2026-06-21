/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.dto;

import lombok.Data;

/**
 * 楼栋分页查询参数
 */
@Data
public class BuildingPageDTO {

    /** 当前页码 */
    private Long pageNum = 1L;

    /** 每页条数 */
    private Long pageSize = 10L;

    /** 楼栋名称（模糊搜索） */
    private String name;

    /** 楼栋类型：1-男生 2-女生 */
    private Integer type;
}
