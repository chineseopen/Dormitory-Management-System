// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Room change (transfer) API requests for the admin portal.
 * Provides a paginated query of room change requests and an audit action.
 *
 * @module api/roomchange
 */

import request from '@/utils/request'

// Query room change requests with pagination and filters
export function getRoomChangeList(params) {
  return request({ url: '/admin/roomchange/page', method: 'get', params })
}

// Audit (approve/reject) a room change request with a remark
export function auditRoomChange(id, status, remark) {
  return request({ url: `/admin/roomchange/audit/${id}`, method: 'post', params: { status, remark } })
}