/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 换房申请请求参数
 */
@Data
public class RoomChangeDTO {

    /** 学生ID */
    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    /** 新楼栋ID */
    @NotNull(message = "新楼栋ID不能为空")
    private Long newBuildingId;

    /** 新房间ID */
    @NotNull(message = "新房间ID不能为空")
    private Long newRoomId;

    /** 新床位ID */
    @NotNull(message = "新床位ID不能为空")
    private Long newBedId;

    /** 换房原因 */
    private String reason;
}
