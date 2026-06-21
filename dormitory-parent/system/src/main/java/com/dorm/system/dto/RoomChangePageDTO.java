/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.dto;

import lombok.Data;

/**
 * 调宿记录分页查询参数
 */
@Data
public class RoomChangePageDTO {

    /** 当前页 */
    private Long pageNum = 1L;

    /** 每页条数 */
    private Long pageSize = 10L;

    /** 学生姓名（模糊查询） */
    private String studentName;

    /** 学号（模糊查询） */
    private String studentNo;

    /** 状态：1-待审核 2-已通过 3-已拒绝 */
    private Integer status;
}
