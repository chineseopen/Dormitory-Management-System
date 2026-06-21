/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 房间信息视图对象
 */
@Data
public class RoomVO {

    /** 房间ID */
    private Long id;

    /** 楼栋ID */
    private Long buildingId;

    /** 楼栋名称 */
    private String buildingName;

    /** 楼层ID */
    private Long floorId;

    /** 楼层号 */
    private Integer floorNumber;

    /** 房间号 */
    private String roomNumber;

    /** 容纳人数 */
    private Integer capacity;

    /** 当前入住人数 */
    private Integer currentCount;

    /** 房间状态：1-空闲 2-已满 3-维修中 4-停用 */
    private Integer status;

    /** 房间状态描述 */
    private String statusDesc;

    /** 创建时间 */
    private LocalDateTime createTime;
}
