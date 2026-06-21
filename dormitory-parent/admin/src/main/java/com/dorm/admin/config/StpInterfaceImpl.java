/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.admin.config;

import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dorm.system.entity.SysPermission;
import com.dorm.system.entity.SysRole;
import com.dorm.system.entity.SysRolePermission;
import com.dorm.system.entity.SysUserRole;
import com.dorm.system.mapper.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sa-Token 权限接口实现
 * <p>告诉 Sa-Token 如何从数据库查询当前登录用户的角色和权限列表</p>
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final SysPermissionMapper sysPermissionMapper;

    public StpInterfaceImpl(SysUserRoleMapper sysUserRoleMapper,
                            SysRoleMapper sysRoleMapper,
                            SysRolePermissionMapper sysRolePermissionMapper,
                            SysPermissionMapper sysPermissionMapper) {
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysRolePermissionMapper = sysRolePermissionMapper;
        this.sysPermissionMapper = sysPermissionMapper;
    }

    /**
     * 返回指定账号 id 拥有的权限码列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 1. 查询用户角色关联
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, Long.parseLong(loginId.toString()))
        );
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 获取角色ID列表
        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        // 3. 查询角色-权限关联
        List<SysRolePermission> rolePermissions = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>()
                        .in(SysRolePermission::getRoleId, roleIds)
        );
        if (rolePermissions.isEmpty()) {
            return new ArrayList<>();
        }

        // 4. 获取权限ID列表并去重
        List<Long> permissionIds = rolePermissions.stream()
                .map(SysRolePermission::getPermissionId)
                .distinct()
                .collect(Collectors.toList());

        // 5. 查询权限详情，返回权限编码列表
        List<SysPermission> permissions = sysPermissionMapper.selectBatchIds(permissionIds);
        return permissions.stream()
                .map(SysPermission::getCode)
                .collect(Collectors.toList());
    }

    /**
     * 返回指定账号 id 拥有的角色标识列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 1. 查询用户角色关联
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, Long.parseLong(loginId.toString()))
        );
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 获取角色ID列表
        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        // 3. 查询角色详情，返回角色编码列表
        List<SysRole> roles = sysRoleMapper.selectBatchIds(roleIds);
        return roles.stream()
                .map(SysRole::getCode)
                .collect(Collectors.toList());
    }
}
