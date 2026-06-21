/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报修信息视图对象
 */
@Data
public class RepairVO {

    /** 报修ID */
    private Long id;

    /** 报修学生ID */
    private Long studentId;

    /** 报修学生姓名 */
    private String studentName;

    /** 楼栋ID */
    private Long buildingId;

    /** 楼栋名称 */
    private String buildingName;

    /** 房间ID */
    private Long roomId;

    /** 房间号 */
    private String roomNumber;

    /** 报修标题 */
    private String title;

    /** 报修内容 */
    private String content;

    /** 图片地址 */
    private String images;

    /** 状态：1-待处理 2-处理中 3-已完成 4-已关闭 */
    private Integer status;

    /** 状态描述 */
    private String statusDesc;

    /** 维修人员ID */
    private Long repairUserId;

    /** 维修时间 */
    private LocalDateTime repairTime;

    /** 维修结果 */
    private String result;

    /** 创建时间 */
    private LocalDateTime createTime;
}
