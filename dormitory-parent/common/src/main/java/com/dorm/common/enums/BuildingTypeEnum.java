/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 楼栋类型枚举
 */
@Getter
@AllArgsConstructor
public enum BuildingTypeEnum {

    MALE(1, "男生宿舍"),
    FEMALE(2, "女生宿舍");

    /** 编码 */
    private final int code;

    /** 描述 */
    private final String desc;

    /**
     * 根据编码获取枚举
     */
    public static BuildingTypeEnum getByCode(int code) {
        for (BuildingTypeEnum e : values()) {
            if (e.getCode() == code) {
                return e;
            }
        }
        return null;
    }
}
