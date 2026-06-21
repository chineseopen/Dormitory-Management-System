/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dorm.system.dto.CheckInPageDTO;
import com.dorm.system.entity.DormCheckIn;
import com.dorm.system.vo.CheckInVO;

/**
 * 入住记录 Service 接口
 */
public interface DormCheckInService extends IService<DormCheckIn> {

    /**
     * 分页查询入住记录
     *
     * @param dto 分页查询参数
     * @return 分页结果
     */
    Page<CheckInVO> pageCheckIns(CheckInPageDTO dto);
}
