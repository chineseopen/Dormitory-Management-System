/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.admin.controller;

import com.dorm.common.result.R;
import com.dorm.system.service.DormBuildingService;
import com.dorm.system.service.DormRepairService;
import com.dorm.system.service.DormStudentService;
import com.dorm.system.service.DormVisitorService;
import com.dorm.system.service.DormFeeService;
import com.dorm.system.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 - 工作台统计接口
 * <p>路径前缀：/admin/dashboard</p>
 */
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DormBuildingService dormBuildingService;
    private final DormStudentService dormStudentService;
    private final DormRepairService dormRepairService;
    private final DormVisitorService dormVisitorService;
    private final DormFeeService dormFeeService;

    /**
     * 获取工作台统计数据
     */
    @GetMapping("/statistics")
    public R<DashboardVO> statistics() {
        DashboardVO vo = new DashboardVO();
        vo.setBuildingCount(dormBuildingService.count());
        vo.setRoomCount(dormBuildingService.getRoomCount());
        vo.setStudentCount(dormStudentService.count());
        vo.setPendingRepairCount(dormRepairService.getPendingCount());
        vo.setVisitingCount(dormVisitorService.getVisitingCount());
        vo.setUnpaidFeeCount(dormFeeService.getUnpaidCount());
        return R.ok(vo);
    }
}
