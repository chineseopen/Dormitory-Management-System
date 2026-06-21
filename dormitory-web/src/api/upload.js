// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * File upload API request.
 * Uploads a file using multipart/form-data and returns the uploaded file info.
 *
 * @module api/upload
 */

import request from '@/utils/request'

// Upload a single file (multipart form data)
export function uploadFile(data) {
  return request({
    url: '/common/upload',
    method: 'post',
    data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}