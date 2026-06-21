// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Utility fee API requests for the student portal.
 * Lets a student query their own utility fee records.
 *
 * @module api/student-fee
 */

import request from '@/utils/request'

// Query the current student's utility fee records
export function getMyFeeList(params) {
  return request({
    url: '/api/fee/list',
    method: 'get',
    params
  })
}