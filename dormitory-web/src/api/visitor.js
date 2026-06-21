// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Visitor management API requests for the admin portal.
 * Provides visitor registration, departure, query and batch deletion.
 *
 * @module api/visitor
 */

import request from '@/utils/request'

// Query visitor records with pagination and filters
export function getVisitorList(params) {
  return request({ url: '/admin/visitor/page', method: 'get', params })
}

// Register a new visitor entry
export function registerVisitor(data) {
  return request({ url: '/admin/visitor', method: 'post', data })
}

// Mark a visitor as having left
export function visitorLeave(id) {
  return request({ url: `/admin/visitor/leave/${id}`, method: 'put' })
}

// Batch delete visitor records by id list
export function deleteVisitor(ids) {
  return request({ url: '/admin/visitor/batch', method: 'delete', data: ids })
}