/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dorm.system.entity.DormNoticeRead;
import com.dorm.system.mapper.DormNoticeReadMapper;
import com.dorm.system.service.DormNoticeReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 公告已读记录 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class DormNoticeReadServiceImpl extends ServiceImpl<DormNoticeReadMapper, DormNoticeRead>
        implements DormNoticeReadService {

    @Override
    public void markAsRead(Long noticeId) {
        Long userId = StpUtil.getLoginIdAsLong();
        // 检查是否已读
        Long count = lambdaQuery()
                .eq(DormNoticeRead::getNoticeId, noticeId)
                .eq(DormNoticeRead::getUserId, userId)
                .count();
        if (count == 0) {
            DormNoticeRead read = new DormNoticeRead();
            read.setNoticeId(noticeId);
            read.setUserId(userId);
            read.setReadTime(LocalDateTime.now());
            save(read);
        }
    }

    @Override
    public void batchMarkAsRead(List<Long> noticeIds) {
        Long userId = StpUtil.getLoginIdAsLong();
        // 查询已读的公告ID
        List<Long> readIds = lambdaQuery()
                .eq(DormNoticeRead::getUserId, userId)
                .in(DormNoticeRead::getNoticeId, noticeIds)
                .list()
                .stream()
                .map(DormNoticeRead::getNoticeId)
                .collect(Collectors.toList());
        // 过滤出未读的公告
        List<DormNoticeRead> toSave = noticeIds.stream()
                .filter(id -> !readIds.contains(id))
                .map(id -> {
                    DormNoticeRead read = new DormNoticeRead();
                    read.setNoticeId(id);
                    read.setUserId(userId);
                    read.setReadTime(LocalDateTime.now());
                    return read;
                })
                .collect(Collectors.toList());
        if (!toSave.isEmpty()) {
            saveBatch(toSave);
        }
    }

    @Override
    public List<Long> getReadNoticeIds(Long userId) {
        return lambdaQuery()
                .eq(DormNoticeRead::getUserId, userId)
                .list()
                .stream()
                .map(DormNoticeRead::getNoticeId)
                .collect(Collectors.toList());
    }
}
