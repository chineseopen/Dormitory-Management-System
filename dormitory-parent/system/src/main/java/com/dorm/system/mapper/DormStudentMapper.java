/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorm.system.entity.DormStudent;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生 Mapper
 */
@Mapper
public interface DormStudentMapper extends BaseMapper<DormStudent> {
}
