/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.admin.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.dorm.common.result.R;
import com.dorm.system.service.DormNoticeReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端 - 公告已读接口
 * <p>路径前缀：/admin/notice/read</p>
 */
@RestController
@RequestMapping("/admin/notice/read")
@RequiredArgsConstructor
public class AdminNoticeReadController {

    private final DormNoticeReadService dormNoticeReadService;

    /**
     * 标记公告已读
     */
    @PostMapping("/{noticeId}")
    public R<Void> markAsRead(@PathVariable Long noticeId) {
        dormNoticeReadService.markAsRead(noticeId);
        return R.ok();
    }

    /**
     * 批量标记公告已读
     */
    @PostMapping("/batch")
    public R<Void> batchMarkAsRead(@RequestBody List<Long> noticeIds) {
        dormNoticeReadService.batchMarkAsRead(noticeIds);
        return R.ok();
    }

    /**
     * 获取当前用户已读的公告ID列表
     */
    @GetMapping("/readIds")
    public R<List<Long>> getReadNoticeIds() {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.ok(dormNoticeReadService.getReadNoticeIds(userId));
    }
}
