/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知公告视图对象
 */
@Data
public class NoticeVO {

    /** 主键ID */
    private Long id;

    /** 公告标题 */
    private String title;

    /** 公告内容 */
    private String content;

    /** 类型：1-通知 2-公告 3-紧急 */
    private Integer type;

    /** 类型描述 */
    private String typeDesc;

    /** 发布人ID */
    private Long publisherId;

    /** 发布人姓名 */
    private String publisherName;

    /** 状态：0-草稿 1-已发布 2-已下架 */
    private Integer status;

    /** 状态描述 */
    private String statusDesc;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
