/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorm.system.entity.DormVisitor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 来访登记 Mapper
 */
@Mapper
public interface DormVisitorMapper extends BaseMapper<DormVisitor> {
}
