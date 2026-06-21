/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.dto;

import lombok.Data;

/**
 * 水电费分页查询参数
 */
@Data
public class FeePageDTO {

    /** 页码 */
    private Long pageNum = 1L;

    /** 每页条数 */
    private Long pageSize = 10L;

    /** 楼栋ID */
    private Long buildingId;

    /** 房间ID */
    private Long roomId;

    /** 费用类型：1-水费 2-电费 */
    private Integer feeType;

    /** 状态：1-未缴费 2-已缴费 */
    private Integer status;

    /** 账期月份，格式yyyy-MM */
    private String month;
}
