/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dorm.common.exception.BusinessException;
import com.dorm.system.dto.NoticePageDTO;
import com.dorm.system.entity.DormNotice;
import com.dorm.system.entity.SysUser;
import com.dorm.system.mapper.DormNoticeMapper;
import com.dorm.system.mapper.SysUserMapper;
import com.dorm.system.service.DormNoticeService;
import com.dorm.system.vo.NoticeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知公告 Service 实现
 *
 * <p>核心业务：
 * <ol>
 *   <li>通知公告分页查询 —— 支持按标题模糊搜索、类型和状态筛选</li>
 *   <li>新增通知公告 —— 默认状态为草稿</li>
 *   <li>编辑通知公告 —— 校验存在性</li>
 *   <li>发布通知公告 —— 更新状态为已发布</li>
 *   <li>批量删除通知公告</li>
 * </ol>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class DormNoticeServiceImpl extends ServiceImpl<DormNoticeMapper, DormNotice> implements DormNoticeService {

    private final DormNoticeMapper dormNoticeMapper;
    private final SysUserMapper sysUserMapper;

    /**
     * 分页查询通知公告列表
     */
    @Override
    public Page<NoticeVO> pageNotices(NoticePageDTO dto) {
        Page<DormNotice> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<DormNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(dto.getTitle()), DormNotice::getTitle, dto.getTitle())
               .eq(dto.getType() != null, DormNotice::getType, dto.getType())
               .eq(dto.getStatus() != null, DormNotice::getStatus, dto.getStatus())
               .orderByDesc(DormNotice::getCreateTime);

        Page<DormNotice> resultPage = dormNoticeMapper.selectPage(page, wrapper);

        Page<NoticeVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<NoticeVO> voList = resultPage.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 新增通知公告
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addNotice(DormNotice notice) {
        if (notice.getStatus() == null) {
            notice.setStatus(0); // 默认草稿
        }
        dormNoticeMapper.insert(notice);
    }

    /**
     * 编辑通知公告
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotice(DormNotice notice) {
        DormNotice exist = dormNoticeMapper.selectById(notice.getId());
        if (exist == null) {
            throw new BusinessException("公告不存在");
        }
        dormNoticeMapper.updateById(notice);
    }

    /**
     * 发布通知公告
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishNotice(Long id) {
        DormNotice notice = dormNoticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        notice.setStatus(1); // 已发布
        dormNoticeMapper.updateById(notice);
    }

    /**
     * 批量删除通知公告
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteNotices(List<Long> ids) {
        dormNoticeMapper.deleteBatchIds(ids);
    }

    /**
     * 查询通知公告详情
     */
    @Override
    public NoticeVO getNoticeDetail(Long id) {
        DormNotice notice = dormNoticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        return toVO(notice);
    }

    /**
     * Entity 转 VO
     */
    private NoticeVO toVO(DormNotice notice) {
        NoticeVO vo = new NoticeVO();
        vo.setId(notice.getId());
        vo.setTitle(notice.getTitle());
        vo.setContent(notice.getContent());
        vo.setType(notice.getType());
        vo.setPublisherId(notice.getPublisherId());
        vo.setStatus(notice.getStatus());
        vo.setCreateTime(notice.getCreateTime());
        vo.setUpdateTime(notice.getUpdateTime());

        // 类型描述
        String typeDesc = switch (notice.getType()) {
            case 1 -> "通知";
            case 2 -> "公告";
            case 3 -> "紧急";
            default -> "";
        };
        vo.setTypeDesc(typeDesc);

        // 状态描述
        String statusDesc = switch (notice.getStatus()) {
            case 0 -> "草稿";
            case 1 -> "已发布";
            case 2 -> "已下架";
            default -> "";
        };
        vo.setStatusDesc(statusDesc);

        // 查询发布人姓名
        if (notice.getPublisherId() != null) {
            SysUser user = sysUserMapper.selectById(notice.getPublisherId());
            if (user != null) {
                vo.setPublisherName(user.getNickname());
            }
        }
        return vo;
    }
}
