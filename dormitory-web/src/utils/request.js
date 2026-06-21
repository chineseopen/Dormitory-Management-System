// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Axios HTTP client wrapper with request/response interceptors.
 * Automatically attaches the auth token, normalizes successful responses,
 * surfaces error messages and handles token expiration by redirecting to login.
 *
 * @module utils/request
 */

import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getToken, removeToken } from './auth'
import router from '@/router'

// Create a pre-configured axios instance
const service = axios.create({
  baseURL: '',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor: attach the access token to the Authorization header
service.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor: unwrap business data and handle business/HTTP errors
service.interceptors.response.use(
  (response) => {
    const res = response.data
    // Convention: code === 200 means the business request succeeded
    if (res.code !== 200) {
      ElMessage({
        message: res.msg || '请求失败',
        type: 'error',
        duration: 3000
      })

      // Token expired or invalid: prompt the user to log in again
      if (res.code === 401) {
        ElMessageBox.confirm('登录已过期，请重新登录', '提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          removeToken()
          router.push('/login')
        })
      }

      return Promise.reject(new Error(res.msg || '请求失败'))
    }

    return res
  },
  (error) => {
    // Map common HTTP status codes to friendly messages
    const status = error.response?.status
    const messages = {
      400: '请求参数错误',
      401: '未授权，请重新登录',
      403: '拒绝访问',
      404: '请求资源不存在',
      500: '服务器内部错误'
    }

    ElMessage({
      message: messages[status] || `请求失败 (${status || '网络错误'})`,
      type: 'error',
      duration: 3000
    })

    // On 401, clear credentials and redirect to the login page
    if (status === 401) {
      removeToken()
      router.push('/login')
    }

    return Promise.reject(error)
  }
)

export default service