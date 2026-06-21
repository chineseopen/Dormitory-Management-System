/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 楼栋信息视图对象
 */
@Data
public class BuildingVO {

    /** 楼栋ID */
    private Long id;

    /** 楼栋名称 */
    private String name;

    /** 楼栋类型：1-男生 2-女生 */
    private Integer type;

    /** 楼栋类型描述 */
    private String typeDesc;

    /** 楼层数 */
    private Integer floors;

    /** 每层房间数 */
    private Integer roomsPerFloor;

    /** 楼管员ID */
    private Long managerId;

    /** 楼管员姓名 */
    private String managerName;

    /** 描述 */
    private String description;

    /** 创建时间 */
    private LocalDateTime createTime;
}
