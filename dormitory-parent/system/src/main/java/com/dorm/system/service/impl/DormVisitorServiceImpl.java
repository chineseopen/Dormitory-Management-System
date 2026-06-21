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
import com.dorm.system.dto.VisitorPageDTO;
import com.dorm.system.entity.DormBuilding;
import com.dorm.system.entity.DormStudent;
import com.dorm.system.entity.DormVisitor;
import com.dorm.system.mapper.DormBuildingMapper;
import com.dorm.system.mapper.DormStudentMapper;
import com.dorm.system.mapper.DormVisitorMapper;
import com.dorm.system.service.DormVisitorService;
import com.dorm.system.vo.VisitorVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 访客 Service 实现
 *
 * <p>核心业务：
 * <ol>
 *   <li>访客分页查询 —— 支持按楼栋、状态筛选，姓名模糊搜索</li>
 *   <li>新增访客记录 —— 默认状态为在访</li>
 *   <li>访客离开 —— 更新状态和离开时间</li>
 *   <li>批量删除访客记录</li>
 * </ol>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class DormVisitorServiceImpl extends ServiceImpl<DormVisitorMapper, DormVisitor> implements DormVisitorService {

    private final DormVisitorMapper dormVisitorMapper;
    private final DormBuildingMapper dormBuildingMapper;
    private final DormStudentMapper dormStudentMapper;

    /**
     * 分页查询访客列表
     */
    @Override
    public Page<VisitorVO> pageVisitors(VisitorPageDTO dto) {
        Page<DormVisitor> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<DormVisitor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dto.getBuildingId() != null, DormVisitor::getBuildingId, dto.getBuildingId())
               .eq(dto.getStatus() != null, DormVisitor::getStatus, dto.getStatus())
               .like(StrUtil.isNotBlank(dto.getVisitorName()), DormVisitor::getVisitorName, dto.getVisitorName())
               .orderByDesc(DormVisitor::getCreateTime);

        Page<DormVisitor> resultPage = dormVisitorMapper.selectPage(page, wrapper);

        Page<VisitorVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<VisitorVO> voList = resultPage.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 新增访客记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addVisitor(DormVisitor visitor) {
        visitor.setStatus(1); // 在访
        visitor.setVisitTime(LocalDateTime.now());
        dormVisitorMapper.insert(visitor);
    }

    /**
     * 访客离开
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void visitorLeave(Long id) {
        DormVisitor visitor = dormVisitorMapper.selectById(id);
        if (visitor == null) {
            throw new BusinessException("访客记录不存在");
        }
        visitor.setStatus(2); // 已离开
        visitor.setLeaveTime(LocalDateTime.now());
        dormVisitorMapper.updateById(visitor);
    }

    /**
     * 批量删除访客记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteVisitors(List<Long> ids) {
        dormVisitorMapper.deleteBatchIds(ids);
    }

    /**
     * 查询在访人数
     */
    @Override
    public Long getVisitingCount() {
        return dormVisitorMapper.selectCount(
                new LambdaQueryWrapper<DormVisitor>()
                        .eq(DormVisitor::getStatus, 1)
        );
    }

    /**
     * Entity 转 VO
     */
    private VisitorVO toVO(DormVisitor visitor) {
        VisitorVO vo = new VisitorVO();
        vo.setId(visitor.getId());
        vo.setBuildingId(visitor.getBuildingId());
        vo.setVisitorName(visitor.getVisitorName());
        vo.setVisitorPhone(visitor.getVisitorPhone());
        vo.setVisitorIdCard(visitor.getVisitorIdCard());
        vo.setTargetStudentId(visitor.getTargetStudentId());
        vo.setReason(visitor.getReason());
        vo.setVisitTime(visitor.getVisitTime());
        vo.setLeaveTime(visitor.getLeaveTime());
        vo.setStatus(visitor.getStatus());
        vo.setStatusDesc(visitor.getStatus() == 1 ? "在访" : "已离开");
        vo.setCreateTime(visitor.getCreateTime());

        // 查询楼栋名称
        if (visitor.getBuildingId() != null) {
            DormBuilding building = dormBuildingMapper.selectById(visitor.getBuildingId());
            if (building != null) {
                vo.setBuildingName(building.getName());
            }
        }

        // 查询被访学生姓名
        if (visitor.getTargetStudentId() != null) {
            DormStudent student = dormStudentMapper.selectById(visitor.getTargetStudentId());
            if (student != null) {
                vo.setTargetStudentName(student.getName());
            }
        }
        return vo;
    }
}
