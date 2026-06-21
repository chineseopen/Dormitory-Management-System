/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 换房记录实体
 * <p>对应数据库表 dorm_room_change</p>
 */
@Data
@TableName("dorm_room_change")
public class DormRoomChange {

    /** 主键ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 学生ID */
    private Long studentId;

    /** 原楼栋ID */
    private Long oldBuildingId;

    /** 原房间ID */
    private Long oldRoomId;

    /** 原床位ID */
    private Long oldBedId;

    /** 新楼栋ID */
    private Long newBuildingId;

    /** 新房间ID */
    private Long newRoomId;

    /** 新床位ID */
    private Long newBedId;

    /** 换房原因 */
    private String reason;

    /** 状态：1-待审核 2-已通过 3-已拒绝 */
    private Integer status;

    /** 审核人ID */
    private Long auditUserId;

    /** 审核时间 */
    private LocalDateTime auditTime;

    /** 审核备注 */
    private String auditRemark;

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
