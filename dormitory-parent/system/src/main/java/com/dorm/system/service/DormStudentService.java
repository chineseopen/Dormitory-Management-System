/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dorm.system.dto.CheckInDTO;
import com.dorm.system.dto.RegisterDTO;
import com.dorm.system.dto.RoomChangeDTO;
import com.dorm.system.dto.StudentPageDTO;
import com.dorm.system.entity.DormStudent;
import com.dorm.system.vo.LoginVO;
import com.dorm.system.vo.StudentVO;

import java.util.List;

/**
 * 学生 Service 接口
 */
public interface DormStudentService extends IService<DormStudent> {

    /**
     * 学生注册
     *
     * @param dto 注册参数
     * @return 登录信息
     */
    LoginVO register(RegisterDTO dto);

    /**
     * 学生登录
     *
     * @param studentId 学生ID
     * @return 登录信息
     */
    LoginVO studentLogin(Long studentId);

    /**
     * 分页查询学生列表
     *
     * @param dto 分页查询参数
     * @return 分页结果
     */
    Page<StudentVO> pageStudents(StudentPageDTO dto);

    /**
     * 办理入住
     *
     * @param dto 入住参数
     */
    void checkIn(CheckInDTO dto);

    /**
     * 办理退宿
     *
     * @param studentId 学生ID
     */
    void checkOut(Long studentId);

    /**
     * 获取学生个人信息
     *
     * @param studentId 学生ID
     * @return 学生信息
     */
    StudentVO getStudentInfo(Long studentId);

    /**
     * 新增学生
     *
     * @param student 学生信息
     */
    void addStudent(DormStudent student);

    /**
     * 编辑学生
     *
     * @param student 学生信息
     */
    void updateStudent(DormStudent student);

    /**
     * 调换宿舍
     *
     * @param dto 换房参数
     */
    void changeRoom(RoomChangeDTO dto);

    /**
     * 批量删除学生
     *
     * @param ids 学生ID列表
     */
    void batchDeleteStudents(List<Long> ids);
}
