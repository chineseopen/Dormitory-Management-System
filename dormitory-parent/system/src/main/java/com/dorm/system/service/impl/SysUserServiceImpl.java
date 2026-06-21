/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dorm.common.exception.BusinessException;
import com.dorm.common.result.ResultCode;
import com.dorm.system.dto.ChangePasswordDTO;
import com.dorm.system.dto.LoginDTO;
import com.dorm.system.dto.UserPageDTO;
import com.dorm.system.entity.*;
import com.dorm.system.mapper.*;
import com.dorm.system.service.SysUserService;
import com.dorm.system.vo.LoginVO;
import com.dorm.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统用户 Service 实现
 *
 * <p>核心业务：
 * <ol>
 *   <li>管理端登录 —— 校验账号密码 + Sa-Token 登录</li>
 *   <li>获取当前用户信息 —— 查询角色和权限列表</li>
 *   <li>用户分页查询 —— 支持按用户名/手机号模糊搜索</li>
 *   <li>用户新增 —— 密码BCrypt加密 + 唯一性校验</li>
 *   <li>用户编辑 —— 只更新允许修改的字段</li>
 *   <li>批量删除 —— 软删除 + 管理员保护</li>
 * </ol>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final SysPermissionMapper sysPermissionMapper;

    /**
     * 管理端登录
     *
     * <p>登录流程：
     * <ol>
     *   <li>根据用户名查询用户</li>
     *   <li>校验密码（BCrypt 验证）</li>
     *   <li>校验账号状态</li>
     *   <li>Sa-Token 执行登录</li>
     *   <li>查询角色和权限列表，组装返回</li>
     * </ol>
     * </p>
     */
    @Override
    public LoginVO login(LoginDTO dto) {
        // 1. 根据用户名查询用户
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, dto.getUsername())
        );
        if (user == null) {
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // 2. 校验密码
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // 3. 校验账号状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        // 4. Sa-Token 执行登录
        StpUtil.login(user.getId());

        // 5. 组装返回信息
        return buildLoginVO(user);
    }

    /**
     * 获取当前登录用户信息
     */
    @Override
    public LoginVO getUserInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return buildLoginVO(user);
    }

    /**
     * 分页查询用户列表
     *
     * <p>查询逻辑：
     * <ol>
     *   <li>构建动态查询条件：用户名模糊匹配、手机号模糊匹配、状态精确匹配</li>
     *   <li>按创建时间倒序排列</li>
     *   <li>Entity 转 VO 输出，隐藏密码等敏感字段</li>
     * </ol>
     * </p>
     */
    @Override
    public Page<UserVO> pageUsers(UserPageDTO dto) {
        Page<SysUser> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        // 构建动态查询条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(dto.getUsername()), SysUser::getUsername, dto.getUsername())
               .like(StrUtil.isNotBlank(dto.getPhone()), SysUser::getPhone, dto.getPhone())
               .eq(dto.getStatus() != null, SysUser::getStatus, dto.getStatus())
               .orderByDesc(SysUser::getCreateTime);

        Page<SysUser> resultPage = sysUserMapper.selectPage(page, wrapper);

        // Entity → VO 转换
        Page<UserVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<UserVO> voList = resultPage.getRecords().stream()
                .map(this::toUserVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 新增用户
     *
     * <p>前置校验：用户名唯一性校验，密码 BCrypt 加密</p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(SysUser user) {
        // 校验用户名唯一性
        Long count = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, user.getUsername())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        // 密码 BCrypt 加密
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        sysUserMapper.insert(user);

        // 保存用户角色关联
        saveUserRoles(user.getId(), user.getRoleIds());
    }

    /**
     * 编辑用户
     *
     * <p>只更新允许修改的字段，不更新密码</p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(SysUser user) {
        SysUser existUser = sysUserMapper.selectById(user.getId());
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 只更新允许修改的字段
        existUser.setNickname(user.getNickname());
        existUser.setPhone(user.getPhone());
        existUser.setEmail(user.getEmail());
        existUser.setGender(user.getGender());
        existUser.setStatus(user.getStatus());
        existUser.setAvatar(user.getAvatar());
        sysUserMapper.updateById(existUser);

        // 更新用户角色关联
        if (user.getRoleIds() != null) {
            saveUserRoles(user.getId(), user.getRoleIds());
        }
    }

    /**
     * 批量删除用户
     *
     * <p>安全限制：不允许删除超级管理员</p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteUsers(List<Long> ids) {
        // 检查是否包含超级管理员
        Long adminCount = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .in(SysUser::getId, ids)
                        .eq(SysUser::getUsername, "admin")
        );
        if (adminCount > 0) {
            throw new BusinessException("不允许删除超级管理员");
        }

        sysUserMapper.deleteBatchIds(ids);
    }

    /**
     * 重置用户密码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 重置为默认密码 123456
        user.setPassword(BCrypt.hashpw("123456"));
        sysUserMapper.updateById(user);
    }

    /**
     * 修改密码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(ChangePasswordDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 校验旧密码
        if (!BCrypt.checkpw(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }
        user.setPassword(BCrypt.hashpw(dto.getNewPassword()));
        sysUserMapper.updateById(user);
    }

    /**
     * 更新当前用户基本信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(SysUser user) {
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser existUser = sysUserMapper.selectById(userId);
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }
        existUser.setNickname(user.getNickname());
        existUser.setPhone(user.getPhone());
        existUser.setEmail(user.getEmail());
        sysUserMapper.updateById(existUser);
    }

    /**
     * 分配角色
     *
     * <p>先删除原有角色关联，再批量插入新角色</p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        saveUserRoles(userId, roleIds);
    }

    /**
     * 保存用户角色关联（先删后插）
     */
    private void saveUserRoles(Long userId, List<Long> roleIds) {
        // 先删除原有角色关联
        sysUserRoleMapper.delete(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId)
        );

        // 再批量插入新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }
    }

    /**
     * 组装登录返回信息
     */
    private LoginVO buildLoginVO(SysUser user) {
        LoginVO vo = new LoginVO();
        vo.setToken(StpUtil.getTokenValue());
        vo.setUser(toUserVO(user));

        // 查询用户角色
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, user.getId())
        );
        if (userRoles.isEmpty()) {
            vo.setRoles(Collections.emptyList());
            vo.setPermissions(Collections.emptyList());
            return vo;
        }

        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        List<SysRole> roles = sysRoleMapper.selectBatchIds(roleIds);
        vo.setRoles(roles.stream().map(SysRole::getCode).collect(Collectors.toList()));

        // 查询角色权限
        List<SysRolePermission> rolePermissions = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>()
                        .in(SysRolePermission::getRoleId, roleIds)
        );
        if (rolePermissions.isEmpty()) {
            vo.setPermissions(Collections.emptyList());
            return vo;
        }

        List<Long> permissionIds = rolePermissions.stream()
                .map(SysRolePermission::getPermissionId)
                .distinct()
                .collect(Collectors.toList());
        List<SysPermission> permissions = sysPermissionMapper.selectBatchIds(permissionIds);
        vo.setPermissions(permissions.stream().map(SysPermission::getCode).collect(Collectors.toList()));

        return vo;
    }

    /**
     * Entity 转 VO —— 脱敏输出
     */
    private UserVO toUserVO(SysUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setGender(user.getGender());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());

        // 查询用户角色ID列表
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, user.getId())
        );
        vo.setRoleIds(userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList()));
        return vo;
    }
}