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
import com.dorm.system.dto.RolePageDTO;
import com.dorm.system.entity.SysRole;
import com.dorm.system.entity.SysRolePermission;
import com.dorm.system.entity.SysUserRole;
import com.dorm.system.mapper.SysRoleMapper;
import com.dorm.system.mapper.SysRolePermissionMapper;
import com.dorm.system.mapper.SysUserRoleMapper;
import com.dorm.system.service.SysRoleService;
import com.dorm.system.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色 Service 实现
 *
 * <p>核心业务：
 * <ol>
 *   <li>角色分页查询 —— 支持按名称、编码模糊搜索，状态筛选</li>
 *   <li>新增角色 —— 编码唯一性校验</li>
 *   <li>编辑角色 —— 校验存在性</li>
 *   <li>删除角色 —— 校验是否存在用户关联</li>
 *   <li>分配权限 —— 先删后插</li>
 * </ol>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;

    /**
     * 分页查询角色列表
     */
    @Override
    public Page<RoleVO> pageRoles(RolePageDTO dto) {
        Page<SysRole> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(dto.getName()), SysRole::getName, dto.getName())
               .like(StrUtil.isNotBlank(dto.getCode()), SysRole::getCode, dto.getCode())
               .eq(dto.getStatus() != null, SysRole::getStatus, dto.getStatus())
               .orderByDesc(SysRole::getCreateTime);

        Page<SysRole> resultPage = sysRoleMapper.selectPage(page, wrapper);

        Page<RoleVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<RoleVO> voList = resultPage.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 查询所有角色列表（下拉选择用）
     */
    @Override
    public List<RoleVO> listAll() {
        List<SysRole> list = sysRoleMapper.selectList(
                new LambdaQueryWrapper<SysRole>().orderByAsc(SysRole::getCreateTime)
        );
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 新增角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRole(SysRole role) {
        // 编码唯一性校验
        Long count = sysRoleMapper.selectCount(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getCode, role.getCode())
        );
        if (count > 0) {
            throw new BusinessException("角色编码已存在");
        }
        sysRoleMapper.insert(role);
    }

    /**
     * 编辑角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(SysRole role) {
        SysRole exist = sysRoleMapper.selectById(role.getId());
        if (exist == null) {
            throw new BusinessException("角色不存在");
        }
        sysRoleMapper.updateById(role);
    }

    /**
     * 批量删除角色
     *
     * <p>安全限制：存在用户关联的角色不允许删除</p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteRoles(List<Long> ids) {
        // 检查是否有用户关联
        Long userCount = sysUserRoleMapper.selectCount(
                new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getRoleId, ids)
        );
        if (userCount > 0) {
            throw new BusinessException("存在用户关联的角色不允许删除");
        }

        // 删除角色权限关联
        sysRolePermissionMapper.delete(
                new LambdaQueryWrapper<SysRolePermission>().in(SysRolePermission::getRoleId, ids)
        );

        sysRoleMapper.deleteBatchIds(ids);
    }

    /**
     * 分配权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // 先删除原有权限
        sysRolePermissionMapper.delete(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId)
        );

        // 再批量插入新权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<SysRolePermission> list = permissionIds.stream().map(pid -> {
                SysRolePermission rp = new SysRolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(pid);
                return rp;
            }).collect(Collectors.toList());
            for (SysRolePermission rp : list) {
                sysRolePermissionMapper.insert(rp);
            }
        }
    }

    /**
     * Entity 转 VO
     */
    private RoleVO toVO(SysRole role) {
        RoleVO vo = new RoleVO();
        vo.setId(role.getId());
        vo.setName(role.getName());
        vo.setCode(role.getCode());
        vo.setDescription(role.getDescription());
        vo.setStatus(role.getStatus());
        vo.setStatusDesc(role.getStatus() == 1 ? "启用" : "禁用");
        vo.setCreateTime(role.getCreateTime());

        // 统计关联用户数
        Long userCount = sysUserRoleMapper.selectCount(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, role.getId())
        );
        vo.setUserCount(userCount);

        // 查询关联权限ID
        List<SysRolePermission> rpList = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, role.getId())
        );
        vo.setPermissionIds(rpList.stream().map(SysRolePermission::getPermissionId).collect(Collectors.toList()));
        return vo;
    }
}
