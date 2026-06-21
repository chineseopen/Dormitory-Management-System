/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dorm.system.dto.RoomChangePageDTO;
import com.dorm.system.entity.DormRoomChange;
import com.dorm.system.vo.RoomChangeVO;

/**
 * 调宿记录 Service 接口
 */
public interface DormRoomChangeService extends IService<DormRoomChange> {

    /**
     * 分页查询调宿记录
     *
     * @param dto 分页查询参数
     * @return 分页结果
     */
    Page<RoomChangeVO> pageRoomChanges(RoomChangePageDTO dto);

    /**
     * 审核调宿申请
     *
     * @param id     调宿记录ID
     * @param status 审核状态：2-通过 3-拒绝
     * @param remark 审核备注
     */
    void auditRoomChange(Long id, Integer status, String remark);
}