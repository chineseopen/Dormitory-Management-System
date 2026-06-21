/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生实体
 * <p>对应数据库表 dorm_student</p>
 */
@Data
@TableName("dorm_student")
public class DormStudent {

    /** 主键ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 关联系统用户ID */
    private Long userId;

    /** 学号 */
    private String studentNo;

    /** 姓名 */
    private String name;

    /** 性别：1-男 2-女 */
    private Integer gender;

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

    /** 所在房间ID */
    private Long roomId;

    /** 所在床位ID */
    private Long bedId;

    /** 入住日期 */
    private LocalDate checkInDate;

    /** 状态：0-未入住 1-在住 2-退宿 3-休学 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除：0-未删除 1-已删除 */
    @TableLogic
    private Integer deleted;
}
