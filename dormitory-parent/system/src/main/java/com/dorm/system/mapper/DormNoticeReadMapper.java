/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorm.system.entity.DormNoticeRead;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公告已读记录 Mapper 接口
 */
@Mapper
public interface DormNoticeReadMapper extends BaseMapper<DormNoticeRead> {
}