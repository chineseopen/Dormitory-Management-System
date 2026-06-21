/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dorm.common.enums.BuildingTypeEnum;
import com.dorm.common.exception.BusinessException;
import com.dorm.system.dto.BuildingPageDTO;
import com.dorm.system.entity.DormBuilding;
import com.dorm.system.entity.DormRoom;
import com.dorm.system.entity.SysUser;
import com.dorm.system.mapper.DormBuildingMapper;
import com.dorm.system.mapper.DormRoomMapper;
import com.dorm.system.mapper.SysUserMapper;
import com.dorm.system.service.DormBuildingService;
import com.dorm.system.vo.BuildingVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 楼栋 Service 实现
 *
 * <p>核心业务：
 * <ol>
 *   <li>楼栋分页查询 —— 支持按名称模糊搜索、类型筛选</li>
 *   <li>新增楼栋 —— 名称唯一性校验</li>
 *   <li>编辑楼栋 —— 只更新允许修改的字段</li>
 *   <li>删除楼栋 —— 校验是否存在房间</li>
 * </ol>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class DormBuildingServiceImpl extends ServiceImpl<DormBuildingMapper, DormBuilding> implements DormBuildingService {

    private final DormBuildingMapper dormBuildingMapper;
    private final DormRoomMapper dormRoomMapper;
    private final SysUserMapper sysUserMapper;

    /**
     * 分页查询楼栋列表
     */
    @Override
    public Page<BuildingVO> pageBuildings(BuildingPageDTO dto) {
        Page<DormBuilding> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<DormBuilding> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(dto.getName()), DormBuilding::getName, dto.getName())
               .eq(dto.getType() != null, DormBuilding::getType, dto.getType())
               .orderByDesc(DormBuilding::getCreateTime);

        Page<DormBuilding> resultPage = dormBuildingMapper.selectPage(page, wrapper);

        Page<BuildingVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<BuildingVO> voList = resultPage.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 查询所有楼栋列表（下拉选择用）
     */
    @Override
    public List<BuildingVO> listAll() {
        List<DormBuilding> list = dormBuildingMapper.selectList(
                new LambdaQueryWrapper<DormBuilding>()
                        .orderByAsc(DormBuilding::getName)
        );
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 新增楼栋
     *
     * <p>前置校验：楼栋名称唯一性校验</p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBuilding(DormBuilding building) {
        // 校验楼栋名称唯一性
        Long count = dormBuildingMapper.selectCount(
                new LambdaQueryWrapper<DormBuilding>().eq(DormBuilding::getName, building.getName())
        );
        if (count > 0) {
            throw new BusinessException("楼栋名称已存在");
        }
        dormBuildingMapper.insert(building);
    }

    /**
     * 编辑楼栋
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBuilding(DormBuilding building) {
        DormBuilding exist = dormBuildingMapper.selectById(building.getId());
        if (exist == null) {
            throw new BusinessException("楼栋不存在");
        }
        dormBuildingMapper.updateById(building);
    }

    /**
     * 批量删除楼栋
     *
     * <p>安全限制：存在房间的楼栋不允许删除</p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteBuildings(List<Long> ids) {
        // 检查楼栋下是否存在房间
        Long roomCount = dormRoomMapper.selectCount(
                new LambdaQueryWrapper<DormRoom>()
                        .in(DormRoom::getBuildingId, ids)
        );
        if (roomCount > 0) {
            throw new BusinessException("存在房间的楼栋不允许删除");
        }

        dormBuildingMapper.deleteBatchIds(ids);
    }

    /**
     * 查询房间总数
     */
    @Override
    public Long getRoomCount() {
        return dormRoomMapper.selectCount(new LambdaQueryWrapper<>());
    }

    /**
     * Entity 转 VO
     */
    private BuildingVO toVO(DormBuilding building) {
        BuildingVO vo = new BuildingVO();
        vo.setId(building.getId());
        vo.setName(building.getName());
        vo.setType(building.getType());
        vo.setTypeDesc(BuildingTypeEnum.getByCode(building.getType()) != null
                ? BuildingTypeEnum.getByCode(building.getType()).getDesc() : "");
        vo.setFloors(building.getFloors());
        vo.setRoomsPerFloor(building.getRoomsPerFloor());
        vo.setManagerId(building.getManagerId());
        vo.setDescription(building.getDescription());
        vo.setCreateTime(building.getCreateTime());

        // 查询楼管员姓名
        if (building.getManagerId() != null) {
            SysUser manager = sysUserMapper.selectById(building.getManagerId());
            if (manager != null) {
                vo.setManagerName(manager.getNickname());
            }
        }
        return vo;
    }
}