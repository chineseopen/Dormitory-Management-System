/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.dto;

import lombok.Data;

/**
 * 房间分页查询参数
 */
@Data
public class RoomPageDTO {

    /** 当前页码 */
    private Long pageNum = 1L;

    /** 每页条数 */
    private Long pageSize = 10L;

    /** 楼栋ID */
    private Long buildingId;

    /** 楼层ID */
    private Long floorId;

    /** 房间号（模糊搜索） */
    private String roomNumber;

    /** 房间状态：1-空闲 2-已满 3-维修中 4-停用 */
    private Integer status;
}
