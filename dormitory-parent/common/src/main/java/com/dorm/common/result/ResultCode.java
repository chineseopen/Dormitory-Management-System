/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 状态码枚举
 * <p>统一管理所有业务状态码</p>
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /** 成功 */
    SUCCESS(200, "操作成功"),

    /** 失败 */
    FAIL(500, "操作失败"),

    /** 参数错误 */
    PARAM_ERROR(400, "参数错误"),

    /** 未认证 */
    UNAUTHORIZED(401, "未认证，请先登录"),

    /** 无权限 */
    FORBIDDEN(403, "无权限访问"),

    /** 资源不存在 */
    NOT_FOUND(404, "资源不存在"),

    /** 用户名或密码错误 */
    LOGIN_ERROR(1001, "用户名或密码错误"),

    /** 账号已禁用 */
    ACCOUNT_DISABLED(1002, "账号已禁用"),

    /** 用户名已存在 */
    USERNAME_EXISTS(1003, "用户名已存在"),

    /** 学号已存在 */
    STUDENT_NO_EXISTS(1004, "学号已存在"),

    /** 房间已满 */
    ROOM_FULL(2001, "房间已满，无法入住"),

    /** 床位已被占用 */
    BED_OCCUPIED(2002, "床位已被占用"),

    /** 楼栋类型不匹配 */
    BUILDING_TYPE_MISMATCH(2003, "楼栋类型与学生性别不匹配");

    /** 状态码 */
    private final int code;

    /** 提示信息 */
    private final String msg;
}
