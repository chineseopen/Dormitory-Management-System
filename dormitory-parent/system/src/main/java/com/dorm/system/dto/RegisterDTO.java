/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 学生注册请求参数
 */
@Data
public class RegisterDTO {

    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 密码 */
    @NotBlank(message = "密码不能为空")
    private String password;

    /** 学号 */
    @NotBlank(message = "学号不能为空")
    private String studentNo;

    /** 姓名 */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /** 性别：1-男 2-女 */
    private Integer gender;

    /** 手机号 */
    private String phone;
}
