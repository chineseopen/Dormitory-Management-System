/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 楼栋实体
 * <p>对应数据库表 dorm_building</p>
 */
@Data
@TableName("dorm_building")
public class DormBuilding {

    /** 主键ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 楼栋名称 */
    private String name;

    /** 楼栋类型：1-男生 2-女生 */
    private Integer type;

    /** 楼层数 */
    private Integer floors;

    /** 每层房间数 */
    private Integer roomsPerFloor;

    /** 楼管员ID */
    private Long managerId;

    /** 描述 */
    private String description;

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
