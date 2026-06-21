/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dorm.system.dto.RepairHandleDTO;
import com.dorm.system.dto.RepairPageDTO;
import com.dorm.system.entity.DormRepair;
import com.dorm.system.vo.RepairVO;

import java.util.List;

/**
 * 报修 Service 接口
 */
public interface DormRepairService extends IService<DormRepair> {

    /**
     * 分页查询报修列表
     *
     * @param dto 分页查询参数
     * @return 分页结果
     */
    Page<RepairVO> pageRepairs(RepairPageDTO dto);

    /**
     * 学生提交报修
     *
     * @param repair 报修信息
     */
    void submitRepair(DormRepair repair);

    /**
     * 处理报修
     *
     * @param dto 处理参数
     */
    void handleRepair(RepairHandleDTO dto);

    /**
     * 查询学生的报修记录
     *
     * @param studentId 学生ID
     * @param pageNum   页码
     * @param pageSize  每页条数
     * @return 分页结果
     */
    Page<RepairVO> pageStudentRepairs(Long studentId, Long pageNum, Long pageSize);

    /**
     * 批量删除报修记录
     *
     * @param ids 报修ID列表
     */
    void batchDeleteRepairs(List<Long> ids);

    /**
     * 查询待处理报修数量
     *
     * @return 待处理报修数量
     */
    Long getPendingCount();
}
