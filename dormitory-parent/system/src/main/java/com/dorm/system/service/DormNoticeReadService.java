/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dorm.system.entity.DormNoticeRead;

import java.util.List;

/**
 * 公告已读记录 Service 接口
 */
public interface DormNoticeReadService extends IService<DormNoticeRead> {

    /**
     * 标记公告已读
     *
     * @param noticeId 公告ID
     */
    void markAsRead(Long noticeId);

    /**
     * 批量标记公告已读
     *
     * @param noticeIds 公告ID列表
     */
    void batchMarkAsRead(List<Long> noticeIds);

    /**
     * 查询用户已读的公告ID列表
     *
     * @param userId 用户ID
     * @return 已读公告ID列表
     */
    List<Long> getReadNoticeIds(Long userId);
}
