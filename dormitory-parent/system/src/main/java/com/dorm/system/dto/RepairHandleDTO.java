/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 报修审核请求参数
 */
@Data
public class RepairHandleDTO {

    /** 报修ID */
    @NotNull(message = "报修ID不能为空")
    private Long repairId;

    /** 状态：2-处理中 3-已完成 4-已关闭 */
    @NotNull(message = "处理状态不能为空")
    private Integer status;

    /** 维修结果 */
    private String result;
}
