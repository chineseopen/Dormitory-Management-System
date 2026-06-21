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
import com.dorm.system.dto.PermissionPageDTO;
import com.dorm.system.entity.SysPermission;
import com.dorm.system.entity.SysRolePermission;
import com.dorm.system.mapper.SysPermissionMapper;
import com.dorm.system.mapper.SysRolePermissionMapper;
import com.dorm.system.service.SysPermissionService;
import com.dorm.system.vo.PermissionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限 Service 实现
 *
 * <p>核心业务：
 * <ol>
 *   <li>权限分页查询 —— 支持按名称模糊搜索、类型和状态筛选</li>
 *   <li>权限树查询 —— 构建树形结构</li>
 *   <li>新增权限 —— 编码唯一性校验</li>
 *   <li>编辑权限 —— 校验存在性</li>
 *   <li>删除权限 —— 校验角色关联和子权限</li>
 * </ol>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    private final SysPermissionMapper sysPermissionMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;

    /**
     * 分页查询权限列表
     */
    @Override
    public Page<PermissionVO> pagePermissions(PermissionPageDTO dto) {
        Page<SysPermission> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(dto.getName()), SysPermission::getName, dto.getName())
               .eq(dto.getType() != null, SysPermission::getType, dto.getType())
               .eq(dto.getStatus() != null, SysPermission::getStatus, dto.getStatus())
               .orderByAsc(SysPermission::getSort);

        Page<SysPermission> resultPage = sysPermissionMapper.selectPage(page, wrapper);

        Page<PermissionVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<PermissionVO> voList = resultPage.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 查询权限树
     */
    @Override
    public List<PermissionVO> permissionTree() {
        List<SysPermission> all = sysPermissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>().orderByAsc(SysPermission::getSort)
        );
        List<PermissionVO> voList = all.stream().map(this::toVO).collect(Collectors.toList());
        return buildTree(voList, 0L);
    }

    /**
     * 新增权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPermission(SysPermission permission) {
        // 编码唯一性校验
        Long count = sysPermissionMapper.selectCount(
                new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getCode, permission.getCode())
        );
        if (count > 0) {
            throw new BusinessException("权限编码已存在");
        }
        sysPermissionMapper.insert(permission);
    }

    /**
     * 编辑权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePermission(SysPermission permission) {
        SysPermission exist = sysPermissionMapper.selectById(permission.getId());
        if (exist == null) {
            throw new BusinessException("权限不存在");
        }
        sysPermissionMapper.updateById(permission);
    }

    /**
     * 批量删除权限
     *
     * <p>安全限制：存在角色关联或子权限的权限不允许删除</p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeletePermissions(List<Long> ids) {
        // 检查是否有角色关联
        Long rpCount = sysRolePermissionMapper.selectCount(
                new LambdaQueryWrapper<SysRolePermission>().in(SysRolePermission::getPermissionId, ids)
        );
        if (rpCount > 0) {
            throw new BusinessException("存在角色关联的权限不允许删除");
        }

        // 检查是否有子权限
        Long childCount = sysPermissionMapper.selectCount(
                new LambdaQueryWrapper<SysPermission>().in(SysPermission::getParentId, ids)
        );
        if (childCount > 0) {
            throw new BusinessException("存在子权限不允许删除");
        }

        sysPermissionMapper.deleteBatchIds(ids);
    }

    /**
     * Entity 转 VO
     */
    private PermissionVO toVO(SysPermission permission) {
        PermissionVO vo = new PermissionVO();
        vo.setId(permission.getId());
        vo.setParentId(permission.getParentId());
        vo.setName(permission.getName());
        vo.setCode(permission.getCode());
        vo.setType(permission.getType());
        vo.setTypeDesc(permission.getType() == 1 ? "菜单" : "按钮");
        vo.setSort(permission.getSort());
        vo.setStatus(permission.getStatus());
        vo.setStatusDesc(permission.getStatus() == 1 ? "启用" : "禁用");
        vo.setCreateTime(permission.getCreateTime());
        return vo;
    }

    /**
     * 构建权限树
     */
    private List<PermissionVO> buildTree(List<PermissionVO> all, Long parentId) {
        List<PermissionVO> tree = new ArrayList<>();
        for (PermissionVO vo : all) {
            if (parentId.equals(vo.getParentId())) {
                vo.setChildren(buildTree(all, vo.getId()));
                tree.add(vo);
            }
        }
        return tree;
    }
}
