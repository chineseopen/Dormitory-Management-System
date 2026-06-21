/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.dto;

import lombok.Data;

/**
 * 通知公告分页查询参数
 */
@Data
public class NoticePageDTO {

    /** 页码 */
    private Long pageNum = 1L;

    /** 每页条数 */
    private Long pageSize = 10L;

    /** 标题（模糊搜索） */
    private String title;

    /** 类型：1-通知 2-公告 3-紧急 */
    private Integer type;

    /** 状态：0-草稿 1-已发布 2-已下架 */
    private Integer status;
}
