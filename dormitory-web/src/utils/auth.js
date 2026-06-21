// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Token storage utilities backed by localStorage.
 * Provides get/set/remove helpers for the access token and refresh token.
 *
 * @module utils/auth
 */

// localStorage keys for the access token and refresh token
const TOKEN_KEY = 'dormitory_token'
const REFRESH_TOKEN_KEY = 'dormitory_refresh_token'

// Retrieve the access token from localStorage
export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

// Persist the access token to localStorage
export function setToken(token) {
  return localStorage.setItem(TOKEN_KEY, token)
}

// Remove both the access token and the refresh token from localStorage
export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(REFRESH_TOKEN_KEY)
}

// Retrieve the refresh token from localStorage
export function getRefreshToken() {
  return localStorage.getItem(REFRESH_TOKEN_KEY)
}

// Persist the refresh token to localStorage
export function setRefreshToken(token) {
  return localStorage.setItem(REFRESH_TOKEN_KEY, token)
}