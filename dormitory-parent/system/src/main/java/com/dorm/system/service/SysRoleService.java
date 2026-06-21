/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dorm.system.dto.RolePageDTO;
import com.dorm.system.entity.SysRole;
import com.dorm.system.vo.RoleVO;

import java.util.List;

/**
 * 角色 Service 接口
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 分页查询角色列表
     *
     * @param dto 分页查询参数
     * @return 分页结果
     */
    Page<RoleVO> pageRoles(RolePageDTO dto);

    /**
     * 查询所有角色列表（下拉选择用）
     *
     * @return 角色列表
     */
    List<RoleVO> listAll();

    /**
     * 新增角色
     *
     * @param role 角色信息
     */
    void addRole(SysRole role);

    /**
     * 编辑角色
     *
     * @param role 角色信息
     */
    void updateRole(SysRole role);

    /**
     * 批量删除角色
     *
     * @param ids 角色ID列表
     */
    void batchDeleteRoles(List<Long> ids);

    /**
     * 分配权限
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);
}
