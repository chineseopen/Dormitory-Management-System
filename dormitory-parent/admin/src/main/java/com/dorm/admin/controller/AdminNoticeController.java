/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.result.R;
import com.dorm.system.dto.NoticePageDTO;
import com.dorm.system.entity.DormNotice;
import com.dorm.system.service.DormNoticeService;
import com.dorm.system.vo.NoticeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/notice")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final DormNoticeService dormNoticeService;

    @SaCheckPermission("notice:list")
    @GetMapping("/page")
    public R<Page<NoticeVO>> page(NoticePageDTO dto) {
        return R.ok(dormNoticeService.pageNotices(dto));
    }

    @SaCheckPermission("notice:list")
    @GetMapping("/{id}")
    public R<DormNotice> getById(@PathVariable Long id) {
        return R.ok(dormNoticeService.getById(id));
    }

    @SaCheckPermission("notice:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody DormNotice notice) {
        notice.setPublisherId(StpUtil.getLoginIdAsLong());
        dormNoticeService.addNotice(notice);
        return R.ok();
    }

    @SaCheckPermission("notice:edit")
    @PutMapping
    public R<Void> update(@Validated @RequestBody DormNotice notice) {
        dormNoticeService.updateNotice(notice);
        return R.ok();
    }

    @SaCheckPermission("notice:publish")
    @PutMapping("/publish/{id}")
    public R<Void> publish(@PathVariable Long id) {
        dormNoticeService.publishNotice(id);
        return R.ok();
    }

    @SaCheckPermission("notice:delete")
    @DeleteMapping("/batch")
    public R<Void> deleteBatch(@RequestBody List<Long> ids) {
        dormNoticeService.batchDeleteNotices(ids);
        return R.ok();
    }
}
