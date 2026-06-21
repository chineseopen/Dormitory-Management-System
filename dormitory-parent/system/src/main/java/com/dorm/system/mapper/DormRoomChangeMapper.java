/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorm.system.entity.DormRoomChange;
import org.apache.ibatis.annotations.Mapper;

/**
 * 换房记录 Mapper
 */
@Mapper
public interface DormRoomChangeMapper extends BaseMapper<DormRoomChange> {
}
