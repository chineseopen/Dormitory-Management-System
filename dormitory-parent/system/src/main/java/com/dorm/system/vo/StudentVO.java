/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生信息视图对象
 */
@Data
public class StudentVO {

    /** 学生ID */
    private Long id;

    /** 关联系统用户ID */
    private Long userId;

    /** 学号 */
    private String studentNo;

    /** 姓名 */
    private String name;

    /** 性别：1-男 2-女 */
    private Integer gender;

    /** 性别描述 */
    private String genderDesc;

    /** 学院 */
    private String college;

    /** 专业 */
    private String major;

    /** 班级 */
    private String className;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 所在楼栋ID */
    private Long buildingId;

    /** 所在楼栋名称 */
    private String buildingName;

    /** 所在房间ID */
    private Long roomId;

    /** 所在房间号 */
    private String roomNumber;

    /** 所在床位ID */
    private Long bedId;

    /** 入住日期 */
    private LocalDate checkInDate;

    /** 状态：0-未入住 1-在住 2-退宿 3-休学 */
    private Integer status;

    /** 状态描述 */
    private String statusDesc;

    /** 创建时间 */
    private LocalDateTime createTime;
}
