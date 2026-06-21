/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dorm.common.enums.RoomStatusEnum;
import com.dorm.common.exception.BusinessException;
import com.dorm.system.dto.RoomPageDTO;
import com.dorm.system.entity.DormBed;
import com.dorm.system.entity.DormBuilding;
import com.dorm.system.entity.DormFloor;
import com.dorm.system.entity.DormRoom;
import com.dorm.system.mapper.DormBedMapper;
import com.dorm.system.mapper.DormBuildingMapper;
import com.dorm.system.mapper.DormFloorMapper;
import com.dorm.system.mapper.DormRoomMapper;
import com.dorm.system.service.DormRoomService;
import com.dorm.system.vo.RoomVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 房间 Service 实现
 *
 * <p>核心业务：
 * <ol>
 *   <li>房间分页查询 —— 支持按楼栋/楼层/房间号/状态筛选</li>
 *   <li>新增房间 —— 自动创建床位</li>
 *   <li>编辑房间</li>
 *   <li>删除房间 —— 校验是否存在入住学生</li>
 * </ol>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class DormRoomServiceImpl extends ServiceImpl<DormRoomMapper, DormRoom> implements DormRoomService {

    private final DormRoomMapper dormRoomMapper;
    private final DormBuildingMapper dormBuildingMapper;
    private final DormFloorMapper dormFloorMapper;
    private final DormBedMapper dormBedMapper;

    /**
     * 分页查询房间列表
     *
     * <p>使用 MyBatis-Plus 分页查询，再批量查询关联的楼栋/楼层信息，避免全表加载和 N+1 查询</p>
     */
    @Override
    public Page<RoomVO> pageRooms(RoomPageDTO dto) {
        Page<DormRoom> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<DormRoom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dto.getBuildingId() != null, DormRoom::getBuildingId, dto.getBuildingId())
               .eq(dto.getFloorId() != null, DormRoom::getFloorId, dto.getFloorId())
               .like(StrUtil.isNotBlank(dto.getRoomNumber()), DormRoom::getRoomNumber, dto.getRoomNumber())
               .eq(dto.getStatus() != null, DormRoom::getStatus, dto.getStatus())
               .orderByDesc(DormRoom::getCreateTime);

        Page<DormRoom> resultPage = dormRoomMapper.selectPage(page, wrapper);

        // 批量查询关联的楼栋和楼层信息，避免 N+1 查询
        List<Long> buildingIds = resultPage.getRecords().stream()
                .map(DormRoom::getBuildingId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        List<Long> floorIds = resultPage.getRecords().stream()
                .map(DormRoom::getFloorId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, DormBuilding> buildingMap = buildingIds.isEmpty()
                ? Collections.emptyMap()
                : dormBuildingMapper.selectBatchIds(buildingIds).stream()
                .collect(Collectors.toMap(DormBuilding::getId, Function.identity()));
        Map<Long, DormFloor> floorMap = floorIds.isEmpty()
                ? Collections.emptyMap()
                : dormFloorMapper.selectBatchIds(floorIds).stream()
                .collect(Collectors.toMap(DormFloor::getId, Function.identity()));

        List<RoomVO> voList = resultPage.getRecords().stream()
                .map(room -> toVO(room, buildingMap, floorMap))
                .collect(Collectors.toList());

        Page<RoomVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 根据楼栋ID查询房间列表
     */
    @Override
    public List<RoomVO> listByBuildingId(Long buildingId) {
        List<DormRoom> list = dormRoomMapper.selectList(
                new LambdaQueryWrapper<DormRoom>()
                        .eq(DormRoom::getBuildingId, buildingId)
                        .orderByAsc(DormRoom::getRoomNumber)
        );
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 新增房间
     *
     * <p>新增房间后自动创建对应数量的床位</p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRoom(DormRoom room) {
        Long floorId = ensureFloorExists(room.getBuildingId(), room.getFloorId());
        room.setFloorId(floorId);

        room.setCurrentCount(0);
        dormRoomMapper.insert(room);

        for (int i = 1; i <= room.getCapacity(); i++) {
            DormBed bed = new DormBed();
            bed.setRoomId(room.getId());
            bed.setBedNumber(i);
            bed.setStatus(1);
            dormBedMapper.insert(bed);
        }
    }

    private Long ensureFloorExists(Long buildingId, Long floorId) {
        DormFloor floor = dormFloorMapper.selectOne(
                new LambdaQueryWrapper<DormFloor>()
                        .eq(DormFloor::getBuildingId, buildingId)
                        .eq(DormFloor::getFloorNumber, floorId.intValue())
        );
        if (floor == null) {
            floor = new DormFloor();
            floor.setBuildingId(buildingId);
            floor.setFloorNumber(floorId.intValue());
            dormFloorMapper.insert(floor);
        }
        return floor.getId();
    }

    /**
     * 编辑房间
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoom(DormRoom room) {
        DormRoom exist = dormRoomMapper.selectById(room.getId());
        if (exist == null) {
            throw new BusinessException("房间不存在");
        }
        dormRoomMapper.updateById(room);
    }

    /**
     * 批量删除房间
     *
     * <p>安全限制：有入住学生的房间不允许删除</p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteRooms(List<Long> ids) {
        // 检查是否有入住学生
        Long count = dormRoomMapper.selectCount(
                new LambdaQueryWrapper<DormRoom>()
                        .in(DormRoom::getId, ids)
                        .gt(DormRoom::getCurrentCount, 0)
        );
        if (count > 0) {
            throw new BusinessException("有入住学生的房间不允许删除");
        }

        // 删除房间下的床位
        dormBedMapper.delete(
                new LambdaQueryWrapper<DormBed>()
                        .in(DormBed::getRoomId, ids)
        );

        dormRoomMapper.deleteBatchIds(ids);
    }

    /**
     * 查询房间下的空闲床位列表
     */
    @Override
    public List<DormBed> listBedsByRoomId(Long roomId) {
        return dormBedMapper.selectList(
                new LambdaQueryWrapper<DormBed>()
                        .eq(DormBed::getRoomId, roomId)
                        .eq(DormBed::getStatus, 1)
                        .orderByAsc(DormBed::getBedNumber)
        );
    }

    /**
     * Entity 转 VO（使用预加载的关联数据，避免 N+1 查询）
     */
    private RoomVO toVO(DormRoom room, Map<Long, DormBuilding> buildingMap, Map<Long, DormFloor> floorMap) {
        RoomVO vo = new RoomVO();
        vo.setId(room.getId());
        vo.setBuildingId(room.getBuildingId());
        vo.setFloorId(room.getFloorId());
        vo.setRoomNumber(room.getRoomNumber());
        vo.setCapacity(room.getCapacity());
        vo.setCurrentCount(room.getCurrentCount());
        vo.setStatus(room.getStatus());
        vo.setStatusDesc(RoomStatusEnum.getByCode(room.getStatus()) != null
                ? RoomStatusEnum.getByCode(room.getStatus()).getDesc() : "");
        vo.setCreateTime(room.getCreateTime());

        if (room.getBuildingId() != null) {
            DormBuilding building = buildingMap.get(room.getBuildingId());
            if (building != null) {
                vo.setBuildingName(building.getName());
            }
        }
        if (room.getFloorId() != null) {
            DormFloor floor = floorMap.get(room.getFloorId());
            if (floor != null) {
                vo.setFloorNumber(floor.getFloorNumber());
            }
        }
        return vo;
    }

    /**
     * Entity 转 VO
     */
    private RoomVO toVO(DormRoom room) {
        RoomVO vo = new RoomVO();
        vo.setId(room.getId());
        vo.setBuildingId(room.getBuildingId());
        vo.setFloorId(room.getFloorId());
        vo.setRoomNumber(room.getRoomNumber());
        vo.setCapacity(room.getCapacity());
        vo.setCurrentCount(room.getCurrentCount());
        vo.setStatus(room.getStatus());
        vo.setStatusDesc(RoomStatusEnum.getByCode(room.getStatus()) != null
                ? RoomStatusEnum.getByCode(room.getStatus()).getDesc() : "");
        vo.setCreateTime(room.getCreateTime());

        // 查询楼栋名称
        if (room.getBuildingId() != null) {
            DormBuilding building = dormBuildingMapper.selectById(room.getBuildingId());
            if (building != null) {
                vo.setBuildingName(building.getName());
            }
        }

        // 查询楼层号
        if (room.getFloorId() != null) {
            DormFloor floor = dormFloorMapper.selectById(room.getFloorId());
            if (floor != null) {
                vo.setFloorNumber(floor.getFloorNumber());
            }
        }
        return vo;
    }
}