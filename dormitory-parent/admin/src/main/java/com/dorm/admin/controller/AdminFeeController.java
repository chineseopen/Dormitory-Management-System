/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.result.R;
import com.dorm.system.dto.FeePageDTO;
import com.dorm.system.entity.DormFee;
import com.dorm.system.service.DormFeeService;
import com.dorm.system.vo.FeeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/fee")
@RequiredArgsConstructor
public class AdminFeeController {

    private final DormFeeService dormFeeService;

    @SaCheckPermission("fee:list")
    @GetMapping("/page")
    public R<Page<FeeVO>> page(FeePageDTO dto) {
        return R.ok(dormFeeService.pageFees(dto));
    }

    @SaCheckPermission("fee:list")
    @GetMapping("/{id}")
    public R<DormFee> getById(@PathVariable Long id) {
        return R.ok(dormFeeService.getById(id));
    }

    @SaCheckPermission("fee:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody DormFee fee) {
        dormFeeService.addFee(fee);
        return R.ok();
    }

    @SaCheckPermission("fee:pay")
    @PutMapping("/pay/{id}")
    public R<Void> payFee(@PathVariable Long id) {
        dormFeeService.payFee(id);
        return R.ok();
    }

    @SaCheckPermission("fee:pay")
    @PutMapping("/batchPay")
    public R<Void> batchPayFees(@RequestBody List<Long> ids) {
        dormFeeService.batchPayFees(ids);
        return R.ok();
    }

    @SaCheckPermission("fee:delete")
    @DeleteMapping("/batch")
    public R<Void> deleteBatch(@RequestBody List<Long> ids) {
        dormFeeService.batchDeleteFees(ids);
        return R.ok();
    }
}
