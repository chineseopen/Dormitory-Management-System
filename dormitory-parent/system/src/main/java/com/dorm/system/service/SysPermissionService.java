/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dorm.system.dto.PermissionPageDTO;
import com.dorm.system.entity.SysPermission;
import com.dorm.system.vo.PermissionVO;

import java.util.List;

/**
 * 权限 Service 接口
 */
public interface SysPermissionService extends IService<SysPermission> {

    /**
     * 分页查询权限列表
     *
     * @param dto 分页查询参数
     * @return 分页结果
     */
    Page<PermissionVO> pagePermissions(PermissionPageDTO dto);

    /**
     * 查询权限树
     *
     * @return 权限树形结构
     */
    List<PermissionVO> permissionTree();

    /**
     * 新增权限
     *
     * @param permission 权限信息
     */
    void addPermission(SysPermission permission);

    /**
     * 编辑权限
     *
     * @param permission 权限信息
     */
    void updatePermission(SysPermission permission);

    /**
     * 批量删除权限
     *
     * @param ids 权限ID列表
     */
    void batchDeletePermissions(List<Long> ids);
}
