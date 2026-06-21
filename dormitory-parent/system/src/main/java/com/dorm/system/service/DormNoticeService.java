/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dorm.system.dto.NoticePageDTO;
import com.dorm.system.entity.DormNotice;
import com.dorm.system.vo.NoticeVO;

import java.util.List;

/**
 * 通知公告 Service 接口
 */
public interface DormNoticeService extends IService<DormNotice> {

    /**
     * 分页查询通知公告列表
     *
     * @param dto 分页查询参数
     * @return 分页结果
     */
    Page<NoticeVO> pageNotices(NoticePageDTO dto);

    /**
     * 新增通知公告
     *
     * @param notice 通知公告信息
     */
    void addNotice(DormNotice notice);

    /**
     * 编辑通知公告
     *
     * @param notice 通知公告信息
     */
    void updateNotice(DormNotice notice);

    /**
     * 发布通知公告
     *
     * @param id 通知公告ID
     */
    void publishNotice(Long id);

    /**
     * 批量删除通知公告
     *
     * @param ids 通知公告ID列表
     */
    void batchDeleteNotices(List<Long> ids);

    /**
     * 查询通知公告详情
     *
     * @param id 通知公告ID
     * @return 通知公告详情
     */
    NoticeVO getNoticeDetail(Long id);
}
