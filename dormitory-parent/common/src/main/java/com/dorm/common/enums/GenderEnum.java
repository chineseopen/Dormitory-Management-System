/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 性别枚举
 */
@Getter
@AllArgsConstructor
public enum GenderEnum {

    MALE(1, "男"),
    FEMALE(2, "女");

    /** 编码 */
    private final int code;

    /** 描述 */
    private final String desc;

    /**
     * 根据编码获取枚举
     */
    public static GenderEnum getByCode(int code) {
        for (GenderEnum e : values()) {
            if (e.getCode() == code) {
                return e;
            }
        }
        return null;
    }
}
