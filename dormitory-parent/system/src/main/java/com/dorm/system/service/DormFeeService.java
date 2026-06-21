/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dorm.system.dto.FeePageDTO;
import com.dorm.system.entity.DormFee;
import com.dorm.system.vo.FeeVO;

import java.util.List;

/**
 * 水电费 Service 接口
 */
public interface DormFeeService extends IService<DormFee> {

    /**
     * 分页查询水电费列表
     *
     * @param dto 分页查询参数
     * @return 分页结果
     */
    Page<FeeVO> pageFees(FeePageDTO dto);

    /**
     * 新增水电费记录
     *
     * @param fee 水电费信息
     */
    void addFee(DormFee fee);

    /**
     * 缴费
     *
     * @param id 费用记录ID
     */
    void payFee(Long id);

    /**
     * 批量缴费
     *
     * @param ids 费用记录ID列表
     */
    void batchPayFees(List<Long> ids);

    /**
     * 批量删除水电费记录
     *
     * @param ids 费用ID列表
     */
    void batchDeleteFees(List<Long> ids);

    /**
     * 查询未缴费数量
     *
     * @return 未缴费数量
     */
    Long getUnpaidCount();
}
