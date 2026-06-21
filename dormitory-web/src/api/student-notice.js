// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Notice API requests for the student portal.
 * Lets a student browse published notices, view detail and mark notices as read.
 *
 * @module api/student-notice
 */

import request from '@/utils/request'

// Query published notices with pagination
export function getStudentNoticeList(params) {
  return request({ url: '/api/notice/page', method: 'get', params })
}

// Get a single notice's detail by id
export function getStudentNoticeDetail(id) {
  return request({ url: `/api/notice/${id}`, method: 'get' })
}

// Mark a single notice as read
export function markNoticeRead(noticeId) {
  return request({ url: `/api/notice/read/${noticeId}`, method: 'post' })
}

// Batch mark multiple notices as read
export function batchMarkNoticeRead(noticeIds) {
  return request({ url: '/api/notice/read/batch', method: 'post', data: noticeIds })
}

// Get the list of notice ids already read by the current student
export function getReadNoticeIds() {
  return request({ url: '/api/notice/read/readIds', method: 'get' })
}