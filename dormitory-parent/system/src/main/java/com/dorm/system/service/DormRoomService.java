/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dorm.system.dto.RoomPageDTO;
import com.dorm.system.entity.DormBed;
import com.dorm.system.entity.DormRoom;
import com.dorm.system.vo.RoomVO;

import java.util.List;

/**
 * 房间 Service 接口
 */
public interface DormRoomService extends IService<DormRoom> {

    /**
     * 分页查询房间列表
     *
     * @param dto 分页查询参数
     * @return 分页结果
     */
    Page<RoomVO> pageRooms(RoomPageDTO dto);

    /**
     * 根据楼栋ID查询房间列表
     *
     * @param buildingId 楼栋ID
     * @return 房间列表
     */
    List<RoomVO> listByBuildingId(Long buildingId);

    /**
     * 新增房间
     *
     * @param room 房间信息
     */
    void addRoom(DormRoom room);

    /**
     * 编辑房间
     *
     * @param room 房间信息
     */
    void updateRoom(DormRoom room);

    /**
     * 批量删除房间
     *
     * @param ids 房间ID列表
     */
    void batchDeleteRooms(List<Long> ids);

    /**
     * 查询房间下的空闲床位列表
     *
     * @param roomId 房间ID
     * @return 空闲床位列表
     */
    List<DormBed> listBedsByRoomId(Long roomId);
}
