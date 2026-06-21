// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Notice/announcement API requests for the admin portal.
 * Provides CRUD operations and a publish action for notices.
 *
 * @module api/notice
 */

import request from '@/utils/request'

// Query notices with pagination and filters
export function getNoticeList(params) {
  return request({ url: '/admin/notice/page', method: 'get', params })
}

// Get a single notice's detail by id
export function getNoticeDetail(id) {
  return request({ url: `/admin/notice/${id}`, method: 'get' })
}

// Create a new notice
export function addNotice(data) {
  return request({ url: '/admin/notice', method: 'post', data })
}

// Update an existing notice
export function updateNotice(data) {
  return request({ url: '/admin/notice', method: 'put', data })
}

// Batch delete notices by id list
export function deleteNotice(ids) {
  return request({ url: '/admin/notice/batch', method: 'delete', data: ids })
}

// Publish a notice (make it visible to students)
export function publishNotice(id) {
  return request({ url: `/admin/notice/publish/${id}`, method: 'put' })
}