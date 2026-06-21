/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 水电费视图对象
 */
@Data
public class FeeVO {

    /** 费用ID */
    private Long id;

    /** 楼栋ID */
    private Long buildingId;

    /** 楼栋名称 */
    private String buildingName;

    /** 房间ID */
    private Long roomId;

    /** 房间号 */
    private String roomNumber;

    /** 费用类型：1-水费 2-电费 */
    private Integer feeType;

    /** 费用类型描述 */
    private String feeTypeDesc;

    /** 金额 */
    private BigDecimal amount;

    /** 账期月份 */
    private String month;

    /** 上期读数 */
    private BigDecimal previousReading;

    /** 本期读数 */
    private BigDecimal currentReading;

    /** 状态：1-未缴费 2-已缴费 */
    private Integer status;

    /** 状态描述 */
    private String statusDesc;

    /** 缴费时间 */
    private LocalDateTime payTime;

    /** 创建时间 */
    private LocalDateTime createTime;
}
