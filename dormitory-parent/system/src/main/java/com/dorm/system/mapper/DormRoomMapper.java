/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorm.system.entity.DormRoom;
import com.dorm.system.vo.RoomVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 房间 Mapper
 */
@Mapper
public interface DormRoomMapper extends BaseMapper<DormRoom> {

    @Select("SELECT r.id, r.building_id, b.name AS building_name, r.floor_id, f.floor_number, " +
            "r.room_number, r.capacity, r.current_count, r.status, r.create_time " +
            "FROM dorm_room r " +
            "LEFT JOIN dorm_building b ON r.building_id = b.id " +
            "LEFT JOIN dorm_floor f ON r.floor_id = f.id " +
            "WHERE r.deleted = 0 " +
            "AND (#{buildingId} IS NULL OR r.building_id = #{buildingId}) " +
            "AND (#{floorId} IS NULL OR r.floor_id = #{floorId}) " +
            "AND (#{roomNumber} IS NULL OR r.room_number LIKE CONCAT('%', #{roomNumber}, '%')) " +
            "AND (#{status} IS NULL OR r.status = #{status}) " +
            "ORDER BY r.create_time DESC")
    List<RoomVO> selectRoomVOList(@Param("buildingId") Long buildingId,
                                  @Param("floorId") Long floorId,
                                  @Param("roomNumber") String roomNumber,
                                  @Param("status") Integer status);
}