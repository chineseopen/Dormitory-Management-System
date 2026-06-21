/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.dto;

import lombok.Data;

/**
 * 学生分页查询参数
 */
@Data
public class StudentPageDTO {

    /** 当前页码 */
    private Long pageNum = 1L;

    /** 每页条数 */
    private Long pageSize = 10L;

    /** 学号（模糊搜索） */
    private String studentNo;

    /** 姓名（模糊搜索） */
    private String name;

    /** 楼栋ID */
    private Long buildingId;

    /** 状态：0-未入住 1-在住 2-退宿 3-休学 */
    private Integer status;
}
