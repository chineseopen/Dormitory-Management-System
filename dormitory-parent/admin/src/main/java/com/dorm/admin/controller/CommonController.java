/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.admin.controller;

import com.dorm.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 通用接口 - 文件上传
 * <p>路径前缀：/common</p>
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${file.upload-path:./uploads}")
    private String uploadPath;

    @Value("${file.base-url:/uploads}")
    private String baseUrl;

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.fail("上传文件不能为空");
        }

        // 获取原始文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 按日期分目录存储
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
        String relativePath = datePath + "/" + fileName;

        // 创建目录
        Path dirPath = Paths.get(uploadPath, datePath);
        try {
            Files.createDirectories(dirPath);
        } catch (IOException e) {
            log.error("创建上传目录失败: {}", dirPath, e);
            return R.fail("文件上传失败");
        }

        // 保存文件
        Path filePath = dirPath.resolve(fileName);
        try {
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            log.error("文件保存失败: {}", filePath, e);
            return R.fail("文件上传失败");
        }

        // 返回访问URL
        String url = baseUrl + "/" + relativePath;
        return R.ok(url);
    }
}
