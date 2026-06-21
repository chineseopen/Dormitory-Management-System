/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 访客视图对象
 */
@Data
public class VisitorVO {

    /** 主键ID */
    private Long id;

    /** 楼栋ID */
    private Long buildingId;

    /** 楼栋名称 */
    private String buildingName;

    /** 来访人姓名 */
    private String visitorName;

    /** 来访人手机号 */
    private String visitorPhone;

    /** 来访人身份证号 */
    private String visitorIdCard;

    /** 被访学生ID */
    private Long targetStudentId;

    /** 被访学生姓名 */
    private String targetStudentName;

    /** 来访事由 */
    private String reason;

    /** 来访时间 */
    private LocalDateTime visitTime;

    /** 离开时间 */
    private LocalDateTime leaveTime;

    /** 状态：1-在访 2-已离开 */
    private Integer status;

    /** 状态描述 */
    private String statusDesc;

    /** 创建时间 */
    private LocalDateTime createTime;
}
