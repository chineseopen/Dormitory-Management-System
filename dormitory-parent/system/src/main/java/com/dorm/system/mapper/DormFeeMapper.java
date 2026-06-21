/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorm.system.entity.DormFee;
import org.apache.ibatis.annotations.Mapper;

/**
 * 水电费 Mapper
 */
@Mapper
public interface DormFeeMapper extends BaseMapper<DormFee> {
}
