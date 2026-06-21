/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dorm.common.result.R;
import com.dorm.system.entity.DormStudent;
import com.dorm.system.mapper.DormStudentMapper;
import com.dorm.system.service.DormStudentService;
import com.dorm.system.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 学生端 - 个人信息接口
 * <p>路径前缀：/api/student</p>
 */
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class ApiStudentController {

    private final DormStudentService dormStudentService;
    private final DormStudentMapper dormStudentMapper;

    /**
     * 获取当前登录学生信息
     */
    @GetMapping("/info")
    public R<StudentVO> getInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        // 根据系统用户ID查询学生信息
        DormStudent student = dormStudentMapper.selectOne(
                new LambdaQueryWrapper<DormStudent>()
                        .eq(DormStudent::getUserId, userId)
        );
        if (student == null) {
            return R.fail("学生信息不存在");
        }
        return R.ok(dormStudentService.getStudentInfo(student.getId()));
    }

    /**
     * 更新学生联系方式
     */
    @PutMapping("/updateContact")
    public R<Void> updateContact(@RequestParam String phone, @RequestParam(required = false) String email) {
        Long userId = StpUtil.getLoginIdAsLong();
        DormStudent student = dormStudentMapper.selectOne(
                new LambdaQueryWrapper<DormStudent>()
                        .eq(DormStudent::getUserId, userId)
        );
        if (student == null) {
            return R.fail("学生信息不存在");
        }
        student.setPhone(phone);
        student.setEmail(email);
        dormStudentService.updateById(student);
        return R.ok();
    }
}
