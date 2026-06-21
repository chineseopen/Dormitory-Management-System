/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.common.exception;

import com.dorm.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常基类
 * <p>所有业务异常需继承此类，由全局异常处理器统一拦截</p>
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 错误码 */
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        this(ResultCode.FAIL.getCode(), message);
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
    }
}
