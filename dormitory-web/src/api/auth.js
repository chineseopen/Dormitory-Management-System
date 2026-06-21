// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Authentication API requests for the admin portal.
 * Covers login, fetching user info, logout, password change and profile update.
 *
 * @module api/auth
 */

import request from '@/utils/request'

// Admin login with username and password
export function login(data) {
  return request({
    url: '/admin/auth/login',
    method: 'post',
    data
  })
}

// Get the current logged-in admin user info
export function getUserInfo() {
  return request({
    url: '/admin/auth/userInfo',
    method: 'get'
  })
}

// Log out the current admin user
export function logout() {
  return request({
    url: '/admin/auth/logout',
    method: 'post'
  })
}

// Change the current admin user's password
export function changePassword(data) {
  return request({
    url: '/admin/auth/changePassword',
    method: 'put',
    data
  })
}

// Update the current admin user's basic profile
export function updateProfile(data) {
  return request({
    url: '/admin/auth/profile',
    method: 'put',
    data
  })
}