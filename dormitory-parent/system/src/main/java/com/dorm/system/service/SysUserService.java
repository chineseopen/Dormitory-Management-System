/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dorm.system.dto.ChangePasswordDTO;
import com.dorm.system.dto.LoginDTO;
import com.dorm.system.dto.UserPageDTO;
import com.dorm.system.entity.SysUser;
import com.dorm.system.vo.LoginVO;
import com.dorm.system.vo.UserVO;

import java.util.List;

/**
 * 系统用户 Service 接口
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 管理端登录
     *
     * @param dto 登录参数
     * @return 登录信息（含 Token、用户信息、角色、权限）
     */
    LoginVO login(LoginDTO dto);

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    LoginVO getUserInfo();

    /**
     * 分页查询用户列表
     *
     * @param dto 分页查询参数
     * @return 分页结果
     */
    Page<UserVO> pageUsers(UserPageDTO dto);

    /**
     * 新增用户
     *
     * @param user 用户信息
     */
    void addUser(SysUser user);

    /**
     * 编辑用户
     *
     * @param user 用户信息
     */
    void updateUser(SysUser user);

    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     */
    void batchDeleteUsers(List<Long> ids);

    /**
     * 重置用户密码
     *
     * @param userId 用户ID
     */
    void resetPassword(Long userId);

    /**
     * 修改密码
     *
     * @param dto 修改密码参数
     */
    void changePassword(ChangePasswordDTO dto);

    /**
     * 更新当前用户基本信息
     *
     * @param user 用户信息（nickname/phone/email）
     */
    void updateProfile(SysUser user);

    /**
     * 分配角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    void assignRoles(Long userId, List<Long> roleIds);
}
