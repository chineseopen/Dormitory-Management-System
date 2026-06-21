/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 报修状态枚举
 */
@Getter
@AllArgsConstructor
public enum RepairStatusEnum {

    PENDING(1, "待处理"),
    PROCESSING(2, "处理中"),
    COMPLETED(3, "已完成"),
    CLOSED(4, "已关闭");

    /** 编码 */
    private final int code;

    /** 描述 */
    private final String desc;

    /**
     * 根据编码获取枚举
     */
    public static RepairStatusEnum getByCode(int code) {
        for (RepairStatusEnum e : values()) {
            if (e.getCode() == code) {
                return e;
            }
        }
        return null;
    }
}
