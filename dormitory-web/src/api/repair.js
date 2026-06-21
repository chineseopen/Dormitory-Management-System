// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Repair request API for the admin portal.
 * Provides query, detail, submit, handle and delete operations for repair tickets.
 *
 * @module api/repair
 */

import request from '@/utils/request'

// Query repair requests with pagination and filters
export function getRepairList(params) {
  return request({ url: '/admin/repair/page', method: 'get', params })
}

// Get a single repair request's detail by id
export function getRepairDetail(id) {
  return request({ url: `/admin/repair/${id}`, method: 'get' })
}

// Submit a new repair request
export function submitRepair(data) {
  return request({ url: '/admin/repair', method: 'post', data })
}

// Handle/process a repair request (update status and result)
export function handleRepair(data) {
  return request({ url: '/admin/repair/handle', method: 'put', data })
}

// Batch delete repair requests by id list
export function deleteRepair(ids) {
  return request({ url: '/admin/repair/batch', method: 'delete', data: ids })
}