/*
 * Copyright (C) 2026 程序员-X
 * SPDX-License-Identifier: MIT
 */

package com.dorm.system.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 工作台统计 VO
 */
@Data
public class DashboardVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 楼栋总数 */
    private Long buildingCount;

    /** 房间总数 */
    private Long roomCount;

    /** 入住人数 */
    private Long studentCount;

    /** 待处理报修数 */
    private Long pendingRepairCount;

    /** 在访人数 */
    private Long visitingCount;

    /** 未缴费数 */
    private Long unpaidFeeCount;
}
