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
import com.dorm.system.dto.FeePageDTO;
import com.dorm.system.entity.DormBuilding;
import com.dorm.system.entity.DormFee;
import com.dorm.system.entity.DormRoom;
import com.dorm.system.mapper.DormBuildingMapper;
import com.dorm.system.mapper.DormFeeMapper;
import com.dorm.system.mapper.DormRoomMapper;
import com.dorm.system.service.DormFeeService;
import com.dorm.system.vo.FeeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 水电费 Service 实现
 *
 * <p>核心业务：
 * <ol>
 *   <li>水电费分页查询 —— 支持按楼栋、房间、类型、状态筛选，账期月份精确匹配</li>
 *   <li>新增水电费记录 —— 默认状态为未缴费</li>
 *   <li>单条缴费 —— 更新状态和缴费时间</li>
 *   <li>批量缴费 —— 逐条更新未缴费记录</li>
 *   <li>批量删除水电费记录</li>
 * </ol>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class DormFeeServiceImpl extends ServiceImpl<DormFeeMapper, DormFee> implements DormFeeService {

    private final DormFeeMapper dormFeeMapper;
    private final DormBuildingMapper dormBuildingMapper;
    private final DormRoomMapper dormRoomMapper;

    /**
     * 分页查询水电费列表
     */
    @Override
    public Page<FeeVO> pageFees(FeePageDTO dto) {
        Page<DormFee> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<DormFee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dto.getBuildingId() != null, DormFee::getBuildingId, dto.getBuildingId())
               .eq(dto.getRoomId() != null, DormFee::getRoomId, dto.getRoomId())
               .eq(dto.getFeeType() != null, DormFee::getFeeType, dto.getFeeType())
               .eq(dto.getStatus() != null, DormFee::getStatus, dto.getStatus())
               .eq(StrUtil.isNotBlank(dto.getMonth()), DormFee::getMonth, dto.getMonth())
               .orderByDesc(DormFee::getCreateTime);

        Page<DormFee> resultPage = dormFeeMapper.selectPage(page, wrapper);

        Page<FeeVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<FeeVO> voList = resultPage.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 新增水电费记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFee(DormFee fee) {
        fee.setStatus(1); // 未缴费
        dormFeeMapper.insert(fee);
    }

    /**
     * 缴费
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payFee(Long id) {
        DormFee fee = dormFeeMapper.selectById(id);
        if (fee == null) {
            throw new BusinessException("费用记录不存在");
        }
        fee.setStatus(2); // 已缴费
        fee.setPayTime(LocalDateTime.now());
        dormFeeMapper.updateById(fee);
    }

    /**
     * 批量缴费
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchPayFees(List<Long> ids) {
        for (Long id : ids) {
            DormFee fee = dormFeeMapper.selectById(id);
            if (fee != null && fee.getStatus() == 1) {
                fee.setStatus(2);
                fee.setPayTime(LocalDateTime.now());
                dormFeeMapper.updateById(fee);
            }
        }
    }

    /**
     * 批量删除水电费记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteFees(List<Long> ids) {
        dormFeeMapper.deleteBatchIds(ids);
    }

    /**
     * 查询未缴费数量
     */
    @Override
    public Long getUnpaidCount() {
        return dormFeeMapper.selectCount(
                new LambdaQueryWrapper<DormFee>()
                        .eq(DormFee::getStatus, 1)
        );
    }

    /**
     * Entity 转 VO
     */
    private FeeVO toVO(DormFee fee) {
        FeeVO vo = new FeeVO();
        vo.setId(fee.getId());
        vo.setBuildingId(fee.getBuildingId());
        vo.setRoomId(fee.getRoomId());
        vo.setFeeType(fee.getFeeType());
        vo.setFeeTypeDesc(fee.getFeeType() == 1 ? "水费" : "电费");
        vo.setAmount(fee.getAmount());
        vo.setMonth(fee.getMonth());
        vo.setPreviousReading(fee.getPreviousReading());
        vo.setCurrentReading(fee.getCurrentReading());
        vo.setStatus(fee.getStatus());
        vo.setStatusDesc(fee.getStatus() == 1 ? "未缴费" : "已缴费");
        vo.setPayTime(fee.getPayTime());
        vo.setCreateTime(fee.getCreateTime());

        // 查询楼栋名称
        if (fee.getBuildingId() != null) {
            DormBuilding building = dormBuildingMapper.selectById(fee.getBuildingId());
            if (building != null) {
                vo.setBuildingName(building.getName());
            }
        }

        // 查询房间号
        if (fee.getRoomId() != null) {
            DormRoom room = dormRoomMapper.selectById(fee.getRoomId());
            if (room != null) {
                vo.setRoomNumber(room.getRoomNumber());
            }
        }
        return vo;
    }
}
