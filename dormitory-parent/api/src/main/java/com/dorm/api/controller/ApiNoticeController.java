/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.result.R;
import com.dorm.system.dto.NoticePageDTO;
import com.dorm.system.service.DormNoticeReadService;
import com.dorm.system.service.DormNoticeService;
import com.dorm.system.vo.NoticeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生端 - 通知公告接口
 * <p>路径前缀：/api/notice</p>
 */
@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class ApiNoticeController {

    private final DormNoticeService dormNoticeService;
    private final DormNoticeReadService dormNoticeReadService;

    /**
     * 查询已发布的通知列表
     */
    @GetMapping("/page")
    public R<Page<NoticeVO>> page(@RequestParam(defaultValue = "1") Long pageNum,
                                  @RequestParam(defaultValue = "10") Long pageSize) {
        NoticePageDTO dto = new NoticePageDTO();
        dto.setPageNum(pageNum);
        dto.setPageSize(pageSize);
        dto.setStatus(1); // 只查询已发布
        return R.ok(dormNoticeService.pageNotices(dto));
    }

    /**
     * 查询通知详情
     */
    @GetMapping("/{id}")
    public R<NoticeVO> detail(@PathVariable Long id) {
        return R.ok(dormNoticeService.getNoticeDetail(id));
    }

    /**
     * 标记通知已读
     */
    @PostMapping("/read/{noticeId}")
    public R<Void> markAsRead(@PathVariable Long noticeId) {
        dormNoticeReadService.markAsRead(noticeId);
        return R.ok();
    }

    /**
     * 批量标记通知已读
     */
    @PostMapping("/read/batch")
    public R<Void> batchMarkAsRead(@RequestBody List<Long> noticeIds) {
        dormNoticeReadService.batchMarkAsRead(noticeIds);
        return R.ok();
    }

    /**
     * 获取当前用户已读的通知ID列表
     */
    @GetMapping("/read/readIds")
    public R<List<Long>> getReadNoticeIds() {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.ok(dormNoticeReadService.getReadNoticeIds(userId));
    }
}
