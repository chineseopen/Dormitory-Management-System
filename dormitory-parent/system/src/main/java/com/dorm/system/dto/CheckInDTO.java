/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 入住办理请求参数
 */
@Data
public class CheckInDTO {

    /** 学生ID */
    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    /** 楼栋ID */
    @NotNull(message = "楼栋ID不能为空")
    private Long buildingId;

    /** 房间ID */
    @NotNull(message = "房间ID不能为空")
    private Long roomId;

    /** 床位ID */
    @NotNull(message = "床位ID不能为空")
    private Long bedId;

    /** 备注 */
    private String remark;
}
