/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.dto;

import lombok.Data;

/**
 * 报修分页查询参数
 */
@Data
public class RepairPageDTO {

    /** 当前页码 */
    private Long pageNum = 1L;

    /** 每页条数 */
    private Long pageSize = 10L;

    /** 楼栋ID */
    private Long buildingId;

    /** 报修状态：1-待处理 2-处理中 3-已完成 4-已关闭 */
    private Integer status;

    /** 报修学生ID */
    private Long studentId;
}
