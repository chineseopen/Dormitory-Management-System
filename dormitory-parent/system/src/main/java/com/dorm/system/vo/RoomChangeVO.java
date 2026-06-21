/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 调宿记录视图对象
 */
@Data
public class RoomChangeVO {

    /** 主键ID */
    private Long id;

    /** 学生ID */
    private Long studentId;

    /** 学生姓名 */
    private String studentName;

    /** 学号 */
    private String studentNo;

    /** 原楼栋ID */
    private Long oldBuildingId;

    /** 原楼栋名称 */
    private String oldBuildingName;

    /** 原房间ID */
    private Long oldRoomId;

    /** 原房间号 */
    private String oldRoomNumber;

    /** 原床位ID */
    private Long oldBedId;

    /** 原床位号 */
    private String oldBedNo;

    /** 新楼栋ID */
    private Long newBuildingId;

    /** 新楼栋名称 */
    private String newBuildingName;

    /** 新房间ID */
    private Long newRoomId;

    /** 新房间号 */
    private String newRoomNumber;

    /** 新床位ID */
    private Long newBedId;

    /** 新床位号 */
    private String newBedNo;

    /** 换房原因 */
    private String reason;

    /** 状态：1-待审核 2-已通过 3-已拒绝 */
    private Integer status;

    /** 状态描述 */
    private String statusDesc;

    /** 审核人ID */
    private Long auditUserId;

    /** 审核人姓名 */
    private String auditUserName;

    /** 审核时间 */
    private LocalDateTime auditTime;

    /** 审核备注 */
    private String auditRemark;

    /** 创建时间 */
    private LocalDateTime createTime;
}
