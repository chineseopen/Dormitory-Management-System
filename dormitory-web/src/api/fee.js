// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Utility fee API requests for the admin portal.
 * Provides fee CRUD, single/batch payment and batch deletion.
 *
 * @module api/fee
 */

import request from '@/utils/request'

// Query utility fee records with pagination and filters
export function getFeeList(params) {
  return request({ url: '/admin/fee/page', method: 'get', params })
}

// Create a new utility fee record
export function addFee(data) {
  return request({ url: '/admin/fee', method: 'post', data })
}

// Mark a single fee record as paid
export function payFee(id) {
  return request({ url: `/admin/fee/pay/${id}`, method: 'put' })
}

// Batch mark multiple fee records as paid
export function batchPayFee(ids) {
  return request({ url: '/admin/fee/batchPay', method: 'put', data: ids })
}

// Batch delete fee records by id list
export function deleteFee(ids) {
  return request({ url: '/admin/fee/batch', method: 'delete', data: ids })
}