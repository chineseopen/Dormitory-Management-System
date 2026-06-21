// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Repair request API for the student portal.
 * Lets a student submit a repair request and view their own repair history.
 *
 * @module api/student-repair
 */

import request from '@/utils/request'

// Submit a new repair request
export function submitRepair(data) {
  return request({
    url: '/api/repair',
    method: 'post',
    data
  })
}

// Query the current student's repair requests with pagination
export function getMyRepairList(params) {
  return request({
    url: '/api/repair/page',
    method: 'get',
    params
  })
}