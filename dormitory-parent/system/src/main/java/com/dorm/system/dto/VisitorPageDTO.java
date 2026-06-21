/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.dto;

import lombok.Data;

/**
 * 访客分页查询参数
 */
@Data
public class VisitorPageDTO {

    /** 页码 */
    private Long pageNum = 1L;

    /** 每页条数 */
    private Long pageSize = 10L;

    /** 楼栋ID */
    private Long buildingId;

    /** 状态：1-在访 2-已离开 */
    private Integer status;

    /** 来访人姓名（模糊搜索） */
    private String visitorName;
}
