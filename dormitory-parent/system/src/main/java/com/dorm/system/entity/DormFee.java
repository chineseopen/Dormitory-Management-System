/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 水电费实体
 * <p>对应数据库表 dorm_fee</p>
 */
@Data
@TableName("dorm_fee")
public class DormFee {

    /** 主键ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 楼栋ID */
    private Long buildingId;

    /** 房间ID */
    private Long roomId;

    /** 费用类型：1-水费 2-电费 */
    private Integer feeType;

    /** 金额 */
    private BigDecimal amount;

    /** 账期月份，格式yyyy-MM */
    private String month;

    /** 上期读数 */
    private BigDecimal previousReading;

    /** 本期读数 */
    private BigDecimal currentReading;

    /** 状态：1-未缴费 2-已缴费 */
    private Integer status;

    /** 缴费时间 */
    private LocalDateTime payTime;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除：0-未删除 1-已删除 */
    @TableLogic
    private Integer deleted;
}
