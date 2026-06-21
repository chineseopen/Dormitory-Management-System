/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 来访登记实体
 * <p>对应数据库表 dorm_visitor</p>
 */
@Data
@TableName("dorm_visitor")
public class DormVisitor {

    /** 主键ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 楼栋ID */
    private Long buildingId;

    /** 来访人姓名 */
    private String visitorName;

    /** 来访人手机号 */
    private String visitorPhone;

    /** 来访人身份证号 */
    private String visitorIdCard;

    /** 被访学生ID */
    private Long targetStudentId;

    /** 来访事由 */
    private String reason;

    /** 来访时间 */
    private LocalDateTime visitTime;

    /** 离开时间 */
    private LocalDateTime leaveTime;

    /** 状态：1-在访 2-已离开 */
    private Integer status;

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
