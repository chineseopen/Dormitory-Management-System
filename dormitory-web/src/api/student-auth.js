// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Authentication API requests for the student portal.
 * Covers student login, registration and logout.
 *
 * @module api/student-auth
 */

import request from '@/utils/request'

// Student login with student id and password
export function studentLogin(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

// Student self-registration
export function studentRegister(data) {
  return request({
    url: '/api/auth/register',
    method: 'post',
    data
  })
}

// Log out the current student
export function studentLogout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}