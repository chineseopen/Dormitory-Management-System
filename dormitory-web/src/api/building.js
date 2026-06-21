// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Building management API requests for the admin portal.
 * Provides CRUD operations and a list-all helper for dormitory buildings.
 *
 * @module api/building
 */

import request from '@/utils/request'

// Query buildings with pagination and filters
export function getBuildingList(params) {
  return request({ url: '/admin/building/page', method: 'get', params })
}

// Get a single building's detail by id
export function getBuildingDetail(id) {
  return request({ url: `/admin/building/${id}`, method: 'get' })
}

// Create a new building
export function addBuilding(data) {
  return request({ url: '/admin/building', method: 'post', data })
}

// Update an existing building
export function updateBuilding(data) {
  return request({ url: '/admin/building', method: 'put', data })
}

// Batch delete buildings by id list
export function deleteBuilding(ids) {
  return request({ url: '/admin/building/batch', method: 'delete', data: ids })
}

// List all buildings without pagination (used by dropdown selectors)
export function listAllBuildings() {
  return request({ url: '/admin/building/listAll', method: 'get' })
}