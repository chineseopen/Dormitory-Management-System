/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.result.R;
import com.dorm.system.dto.RoomChangePageDTO;
import com.dorm.system.service.DormRoomChangeService;
import com.dorm.system.vo.RoomChangeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端 - 调宿记录接口
 * <p>路径前缀：/admin/roomchange</p>
 */
@RestController
@RequestMapping("/admin/roomchange")
@RequiredArgsConstructor
public class AdminRoomChangeController {

    private final DormRoomChangeService dormRoomChangeService;

    /**
     * 分页查询调宿记录
     */
    @SaCheckPermission("student:change")
    @GetMapping("/page")
    public R<Page<RoomChangeVO>> page(RoomChangePageDTO dto) {
        return R.ok(dormRoomChangeService.pageRoomChanges(dto));
    }

    /**
     * 审核调宿申请
     *
     * @param id     调宿记录ID
     * @param status 审核状态：2-通过 3-拒绝
     * @param remark 审核备注
     */
    @SaCheckPermission("student:change:audit")
    @PostMapping("/audit/{id}")
    public R<Void> audit(@PathVariable Long id,
                        @RequestParam Integer status,
                        @RequestParam(required = false) String remark) {
        dormRoomChangeService.auditRoomChange(id, status, remark);
        return R.ok();
    }
}