/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.result.R;
import com.dorm.system.dto.CheckInDTO;
import com.dorm.system.dto.RoomChangeDTO;
import com.dorm.system.dto.StudentPageDTO;
import com.dorm.system.entity.DormStudent;
import com.dorm.system.service.DormStudentService;
import com.dorm.system.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端 - 住宿管理接口
 * <p>路径前缀：/admin/student</p>
 */
@RestController
@RequestMapping("/admin/student")
@RequiredArgsConstructor
public class AdminStudentController {

    private final DormStudentService dormStudentService;

    /**
     * 分页查询学生列表
     */
    @SaCheckPermission("student:list")
    @GetMapping("/page")
    public R<Page<StudentVO>> page(StudentPageDTO dto) {
        return R.ok(dormStudentService.pageStudents(dto));
    }

    /**
     * 查询学生详情
     */
    @SaCheckPermission("student:list")
    @GetMapping("/{id}")
    public R<StudentVO> getById(@PathVariable Long id) {
        return R.ok(dormStudentService.getStudentInfo(id));
    }

    /**
     * 新增学生
     */
    @SaCheckPermission("student:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody DormStudent student) {
        dormStudentService.addStudent(student);
        return R.ok();
    }

    /**
     * 编辑学生
     */
    @SaCheckPermission("student:edit")
    @PutMapping
    public R<Void> update(@Validated @RequestBody DormStudent student) {
        dormStudentService.updateStudent(student);
        return R.ok();
    }

    /**
     * 批量删除学生
     */
    @SaCheckPermission("student:delete")
    @DeleteMapping("/batch")
    public R<Void> deleteBatch(@RequestBody List<Long> ids) {
        dormStudentService.batchDeleteStudents(ids);
        return R.ok();
    }

    /**
     * 办理入住
     */
    @SaCheckPermission("student:checkin:add")
    @PostMapping("/checkIn")
    public R<Void> checkIn(@Validated @RequestBody CheckInDTO dto) {
        dormStudentService.checkIn(dto);
        return R.ok();
    }

    /**
     * 办理退宿
     */
    @SaCheckPermission("student:checkin:out")
    @PostMapping("/checkOut/{studentId}")
    public R<Void> checkOut(@PathVariable Long studentId) {
        dormStudentService.checkOut(studentId);
        return R.ok();
    }

    /**
     * 调换宿舍
     */
    @SaCheckPermission("student:edit")
    @PutMapping("/changeRoom")
    public R<Void> changeRoom(@Validated @RequestBody RoomChangeDTO dto) {
        dormStudentService.changeRoom(dto);
        return R.ok();
    }
}
