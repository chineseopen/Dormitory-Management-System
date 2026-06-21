/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 房间状态枚举
 */
@Getter
@AllArgsConstructor
public enum RoomStatusEnum {

    EMPTY(1, "空闲"),
    FULL(2, "已满"),
    REPAIRING(3, "维修中"),
    DISABLED(4, "停用");

    /** 编码 */
    private final int code;

    /** 描述 */
    private final String desc;

    /**
     * 根据编码获取枚举
     */
    public static RoomStatusEnum getByCode(int code) {
        for (RoomStatusEnum e : values()) {
            if (e.getCode() == code) {
                return e;
            }
        }
        return null;
    }
}
