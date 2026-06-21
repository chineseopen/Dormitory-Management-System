/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dorm.system.dto.BuildingPageDTO;
import com.dorm.system.entity.DormBuilding;
import com.dorm.system.vo.BuildingVO;

import java.util.List;

/**
 * 楼栋 Service 接口
 */
public interface DormBuildingService extends IService<DormBuilding> {

    /**
     * 分页查询楼栋列表
     *
     * @param dto 分页查询参数
     * @return 分页结果
     */
    Page<BuildingVO> pageBuildings(BuildingPageDTO dto);

    /**
     * 查询所有楼栋列表（下拉选择用）
     *
     * @return 楼栋列表
     */
    List<BuildingVO> listAll();

    /**
     * 新增楼栋
     *
     * @param building 楼栋信息
     */
    void addBuilding(DormBuilding building);

    /**
     * 编辑楼栋
     *
     * @param building 楼栋信息
     */
    void updateBuilding(DormBuilding building);

    /**
     * 批量删除楼栋
     *
     * @param ids 楼栋ID列表
     */
    void batchDeleteBuildings(List<Long> ids);

    /**
     * 查询房间总数
     *
     * @return 房间总数
     */
    Long getRoomCount();
}
