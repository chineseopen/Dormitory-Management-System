/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目启动类
 * <p>自动扫描所有子模块的 Spring 组件</p>
 */
@SpringBootApplication(scanBasePackages = "com.dorm")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
