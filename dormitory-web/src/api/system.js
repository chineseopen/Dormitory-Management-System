// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * System administration API requests for the admin portal.
 * Groups user management, role management and permission management endpoints.
 *
 * @module api/system
 */

import request from '@/utils/request'

// ==================== User management ====================
// Query system users with pagination and filters
export function getUserList(params) {
  return request({ url: '/admin/user/page', method: 'get', params })
}

// Create a new system user
export function addUser(data) {
  return request({ url: '/admin/user', method: 'post', data })
}

// Update an existing system user
export function updateUser(data) {
  return request({ url: '/admin/user', method: 'put', data })
}

// Batch delete system users by id list
export function deleteUser(ids) {
  return request({ url: '/admin/user/batch', method: 'delete', data: ids })
}

// Reset a user's password to the default
export function resetPassword(id) {
  return request({ url: `/admin/user/resetPassword/${id}`, method: 'put' })
}

// Assign roles to a user
export function assignRoles(userId, roleIds) {
  return request({ url: '/admin/user/assignRoles', method: 'put', params: { userId }, data: roleIds })
}

// ==================== Role management ====================
// Query roles with pagination and filters
export function getRoleList(params) {
  return request({ url: '/admin/role/page', method: 'get', params })
}

// List all roles without pagination (used by dropdown selectors)
export function listAllRoles() {
  return request({ url: '/admin/role/listAll', method: 'get' })
}

// Create a new role
export function addRole(data) {
  return request({ url: '/admin/role', method: 'post', data })
}

// Update an existing role
export function updateRole(data) {
  return request({ url: '/admin/role', method: 'put', data })
}

// Batch delete roles by id list
export function deleteRole(ids) {
  return request({ url: '/admin/role/batch', method: 'delete', data: ids })
}

// Assign permissions to a role
export function assignPermissions(roleId, permissionIds) {
  return request({ url: '/admin/role/assignPermissions', method: 'put', params: { roleId }, data: permissionIds })
}

// ==================== Permission management ====================
// Query permissions with pagination and filters
export function getPermissionList(params) {
  return request({ url: '/admin/permission/page', method: 'get', params })
}

// Get the permission tree structure (menu/button hierarchy)
export function getPermissionTree() {
  return request({ url: '/admin/permission/tree', method: 'get' })
}

// Create a new permission node
export function addPermission(data) {
  return request({ url: '/admin/permission', method: 'post', data })
}

// Update an existing permission node
export function updatePermission(data) {
  return request({ url: '/admin/permission', method: 'put', data })
}

// Batch delete permissions by id list
export function deletePermission(ids) {
  return request({ url: '/admin/permission/batch', method: 'delete', data: ids })
}