/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorm.system.entity.DormBuilding;
import org.apache.ibatis.annotations.Mapper;

/**
 * 楼栋 Mapper
 */
@Mapper
public interface DormBuildingMapper extends BaseMapper<DormBuilding> {
}
