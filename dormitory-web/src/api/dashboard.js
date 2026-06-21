// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Dashboard API requests for the admin portal.
 * Fetches aggregated statistics displayed on the dashboard workbench.
 *
 * @module api/dashboard
 */

import request from '@/utils/request'

// Get dashboard statistics (counts of buildings, rooms, students, etc.)
export function getDashboardStatistics() {
  return request({
    url: '/admin/dashboard/statistics',
    method: 'get'
  })
}