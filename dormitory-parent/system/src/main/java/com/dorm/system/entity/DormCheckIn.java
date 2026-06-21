/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 入住记录实体
 * <p>对应数据库表 dorm_check_in</p>
 */
@Data
@TableName("dorm_check_in")
public class DormCheckIn {

    /** 主键ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 学生ID */
    private Long studentId;

    /** 楼栋ID */
    private Long buildingId;

    /** 房间ID */
    private Long roomId;

    /** 床位ID */
    private Long bedId;

    /** 入住日期 */
    private LocalDate checkInDate;

    /** 退宿日期 */
    private LocalDate checkOutDate;

    /** 状态：1-在住 2-已退宿 */
    private Integer status;

    /** 备注 */
    private String remark;

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
