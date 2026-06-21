// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Student personal info API requests for the student portal.
 * Lets a student fetch and update their own contact information.
 *
 * @module api/student-info
 */

import request from '@/utils/request'

// Get the current student's personal information
export function getStudentInfo() {
  return request({
    url: '/api/student/info',
    method: 'get'
  })
}

// Update the current student's contact information (phone/email)
export function updateStudentContact(data) {
  return request({
    url: '/api/student/updateContact',
    method: 'put',
    params: data
  })
}