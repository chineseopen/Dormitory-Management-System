// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Room management API requests for the admin portal.
 * Provides room CRUD, bed lookup by room and batch deletion.
 *
 * @module api/room
 */

import request from '@/utils/request'

// Query rooms with pagination and filters
export function getRoomList(params) {
  return request({ url: '/admin/room/page', method: 'get', params })
}

// Get a single room's detail by id
export function getRoomDetail(id) {
  return request({ url: `/admin/room/${id}`, method: 'get' })
}

// Get all beds belonging to a specific room
export function getBedsByRoomId(roomId) {
  return request({ url: `/admin/room/${roomId}/beds`, method: 'get' })
}

// Create a new room
export function addRoom(data) {
  return request({ url: '/admin/room', method: 'post', data })
}

// Update an existing room
export function updateRoom(data) {
  return request({ url: '/admin/room', method: 'put', data })
}

// Batch delete rooms by id list
export function deleteRoom(ids) {
  return request({ url: '/admin/room/batch', method: 'delete', data: ids })
}