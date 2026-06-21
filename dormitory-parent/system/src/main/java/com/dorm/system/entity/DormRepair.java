/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报修实体
 * <p>对应数据库表 dorm_repair</p>
 */
@Data
@TableName("dorm_repair")
public class DormRepair {

    /** 主键ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 报修学生ID */
    private Long studentId;

    /** 楼栋ID */
    private Long buildingId;

    /** 房间ID */
    private Long roomId;

    /** 报修标题 */
    private String title;

    /** 报修内容 */
    private String content;

    /** 图片地址，多个用逗号分隔 */
    private String images;

    /** 状态：1-待处理 2-处理中 3-已完成 4-已关闭 */
    private Integer status;

    /** 维修人员ID */
    private Long repairUserId;

    /** 维修时间 */
    private LocalDateTime repairTime;

    /** 维修结果 */
    private String result;

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
