/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.result.R;
import com.dorm.system.dto.BuildingPageDTO;
import com.dorm.system.entity.DormBuilding;
import com.dorm.system.service.DormBuildingService;
import com.dorm.system.vo.BuildingVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端 - 楼栋管理接口
 * <p>路径前缀：/admin/building</p>
 */
@RestController
@RequestMapping("/admin/building")
@RequiredArgsConstructor
public class AdminBuildingController {

    private final DormBuildingService dormBuildingService;

    /**
     * 分页查询楼栋列表
     */
    @SaCheckPermission("building:list")
    @GetMapping("/page")
    public R<Page<BuildingVO>> page(BuildingPageDTO dto) {
        return R.ok(dormBuildingService.pageBuildings(dto));
    }

    /**
     * 查询所有楼栋列表（下拉选择用）
     */
    @GetMapping("/listAll")
    public R<List<BuildingVO>> listAll() {
        return R.ok(dormBuildingService.listAll());
    }

    /**
     * 查询楼栋详情
     */
    @SaCheckPermission("building:list")
    @GetMapping("/{id}")
    public R<DormBuilding> getById(@PathVariable Long id) {
        return R.ok(dormBuildingService.getById(id));
    }

    /**
     * 新增楼栋
     */
    @SaCheckPermission("building:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody DormBuilding building) {
        dormBuildingService.addBuilding(building);
        return R.ok();
    }

    /**
     * 编辑楼栋
     */
    @SaCheckPermission("building:edit")
    @PutMapping
    public R<Void> update(@Validated @RequestBody DormBuilding building) {
        dormBuildingService.updateBuilding(building);
        return R.ok();
    }

    /**
     * 批量删除楼栋
     */
    @SaCheckPermission("building:delete")
    @DeleteMapping("/batch")
    public R<Void> deleteBatch(@RequestBody List<Long> ids) {
        dormBuildingService.batchDeleteBuildings(ids);
        return R.ok();
    }
}
