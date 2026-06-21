/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 床位实体
 * <p>对应数据库表 dorm_bed</p>
 */
@Data
@TableName("dorm_bed")
public class DormBed {

    /** 主键ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 房间ID */
    private Long roomId;

    /** 床位号 */
    private Integer bedNumber;

    /** 床位状态：1-空闲 2-已占用 3-维修中 */
    private Integer status;

    /** 入住学生ID */
    private Long studentId;

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
