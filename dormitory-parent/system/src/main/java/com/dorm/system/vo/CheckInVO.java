/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 入住记录视图对象
 */
@Data
public class CheckInVO {

    /** 主键ID */
    private Long id;

    /** 学生ID */
    private Long studentId;

    /** 学生姓名 */
    private String studentName;

    /** 学号 */
    private String studentNo;

    /** 楼栋ID */
    private Long buildingId;

    /** 楼栋名称 */
    private String buildingName;

    /** 房间ID */
    private Long roomId;

    /** 房间号 */
    private String roomNumber;

    /** 床位ID */
    private Long bedId;

    /** 床位号 */
    private String bedNo;

    /** 入住日期 */
    private LocalDate checkInDate;

    /** 退宿日期 */
    private LocalDate checkOutDate;

    /** 状态：1-在住 2-已退宿 */
    private Integer status;

    /** 状态描述 */
    private String statusDesc;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;
}
