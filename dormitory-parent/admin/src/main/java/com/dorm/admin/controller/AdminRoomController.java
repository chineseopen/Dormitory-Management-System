/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.result.R;
import com.dorm.system.dto.RoomPageDTO;
import com.dorm.system.entity.DormBed;
import com.dorm.system.entity.DormRoom;
import com.dorm.system.service.DormRoomService;
import com.dorm.system.vo.RoomVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端 - 房间管理接口
 * <p>路径前缀：/admin/room</p>
 */
@RestController
@RequestMapping("/admin/room")
@RequiredArgsConstructor
public class AdminRoomController {

    private final DormRoomService dormRoomService;

    /**
     * 分页查询房间列表
     */
    @SaCheckPermission("room:list")
    @GetMapping("/page")
    public R<Page<RoomVO>> page(RoomPageDTO dto) {
        return R.ok(dormRoomService.pageRooms(dto));
    }

    /**
     * 根据楼栋ID查询房间列表
     */
    @GetMapping("/listByBuilding/{buildingId}")
    public R<List<RoomVO>> listByBuildingId(@PathVariable Long buildingId) {
        return R.ok(dormRoomService.listByBuildingId(buildingId));
    }

    /**
     * 查询房间下的空闲床位列表
     */
    @GetMapping("/{roomId}/beds")
    public R<List<DormBed>> listBedsByRoomId(@PathVariable Long roomId) {
        return R.ok(dormRoomService.listBedsByRoomId(roomId));
    }

    /**
     * 查询房间详情
     */
    @SaCheckPermission("room:list")
    @GetMapping("/{id}")
    public R<DormRoom> getById(@PathVariable Long id) {
        return R.ok(dormRoomService.getById(id));
    }

    /**
     * 新增房间
     */
    @SaCheckPermission("room:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody DormRoom room) {
        dormRoomService.addRoom(room);
        return R.ok();
    }

    /**
     * 编辑房间
     */
    @SaCheckPermission("room:edit")
    @PutMapping
    public R<Void> update(@Validated @RequestBody DormRoom room) {
        dormRoomService.updateRoom(room);
        return R.ok();
    }

    /**
     * 批量删除房间
     */
    @SaCheckPermission("room:delete")
    @DeleteMapping("/batch")
    public R<Void> deleteBatch(@RequestBody List<Long> ids) {
        dormRoomService.batchDeleteRooms(ids);
        return R.ok();
    }
}
