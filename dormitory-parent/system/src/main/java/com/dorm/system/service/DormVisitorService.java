/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dorm.system.dto.VisitorPageDTO;
import com.dorm.system.entity.DormVisitor;
import com.dorm.system.vo.VisitorVO;

import java.util.List;

/**
 * 访客 Service 接口
 */
public interface DormVisitorService extends IService<DormVisitor> {

    /**
     * 分页查询访客列表
     *
     * @param dto 分页查询参数
     * @return 分页结果
     */
    Page<VisitorVO> pageVisitors(VisitorPageDTO dto);

    /**
     * 新增访客记录
     *
     * @param visitor 访客信息
     */
    void addVisitor(DormVisitor visitor);

    /**
     * 访客离开
     *
     * @param id 访客记录ID
     */
    void visitorLeave(Long id);

    /**
     * 批量删除访客记录
     *
     * @param ids 访客ID列表
     */
    void batchDeleteVisitors(List<Long> ids);

    /**
     * 查询在访人数
     *
     * @return 在访人数
     */
    Long getVisitingCount();
}
