/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告已读记录实体
 * <p>对应数据库表 dorm_notice_read</p>
 */
@Data
@TableName("dorm_notice_read")
public class DormNoticeRead {

    /** 主键ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 公告ID */
    private Long noticeId;

    /** 用户ID */
    private Long userId;

    /** 已读时间 */
    private LocalDateTime readTime;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 逻辑删除：0-未删除 1-已删除 */
    @TableLogic
    private Integer deleted;
}
