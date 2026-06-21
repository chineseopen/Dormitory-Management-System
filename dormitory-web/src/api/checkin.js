// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Check-in record API requests for the admin portal.
 * Provides a paginated query of student check-in history.
 *
 * @module api/checkin
 */

import request from '@/utils/request'

// Query check-in records with pagination and filters
export function getCheckInList(params) {
  return request({ url: '/admin/checkin/page', method: 'get', params })
}