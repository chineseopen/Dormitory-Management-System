// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Student management API requests for the admin portal.
 * Provides student CRUD, check-in, check-out and room change operations.
 *
 * @module api/student
 */

import request from '@/utils/request'

// Query students with pagination and filters
export function getStudentList(params) {
  return request({ url: '/admin/student/page', method: 'get', params })
}

// Get a single student's detail by id
export function getStudentDetail(id) {
  return request({ url: `/admin/student/${id}`, method: 'get' })
}

// Create a new student
export function addStudent(data) {
  return request({ url: '/admin/student', method: 'post', data })
}

// Update an existing student
export function updateStudent(data) {
  return request({ url: '/admin/student', method: 'put', data })
}

// Batch delete students by id list
export function deleteStudent(ids) {
  return request({ url: '/admin/student/batch', method: 'delete', data: ids })
}

// Check a student into a room (assign a bed)
export function checkIn(data) {
  return request({ url: '/admin/student/checkIn', method: 'post', data })
}

// Check a student out of their current room
export function checkOut(id) {
  return request({ url: `/admin/student/checkOut/${id}`, method: 'post' })
}

// Transfer a student to a different room/bed
export function changeRoom(data) {
  return request({ url: '/admin/student/changeRoom', method: 'put', data })
}