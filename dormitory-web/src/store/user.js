// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Pinia store for the authenticated user state.
 * Holds the auth token and user profile (roles/permissions) and exposes
 * actions for login, fetching user info and logout.
 *
 * @module store/user
 */

import { defineStore } from 'pinia'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { login as loginApi, getUserInfo as getUserInfoApi, logout as logoutApi } from '@/api/auth'

export const useUserStore = defineStore('user', {
  // Reactive user state: token and profile information
  state: () => ({
    token: getToken() || '',
    userInfo: {
      id: '',
      username: '',
      realName: '',
      avatar: '',
      phone: '',
      email: '',
      roles: [],
      permissions: []
    }
  }),

  // Derived getters for login status, roles and permissions
  getters: {
    isLogin: (state) => !!state.token,
    roles: (state) => state.userInfo.roles,
    permissions: (state) => state.userInfo.permissions
  },

  actions: {
    // Perform login: persist token and store the returned user info
    async login(loginForm) {
      const res = await loginApi(loginForm)
      const { token, user, roles, permissions } = res.data
      this.token = token
      setToken(token)
      // Store user info directly from the login response
      this.userInfo = {
        id: user.id,
        username: user.username,
        realName: user.nickname,
        avatar: user.avatar || '',
        phone: user.phone || '',
        email: user.email || '',
        roles: roles || [],
        permissions: permissions || []
      }
      return res
    },

    // Fetch the current user info from the backend and store it
    async getUserInfo() {
      const res = await getUserInfoApi()
      const { user, roles, permissions } = res.data
      this.userInfo = {
        id: user.id,
        username: user.username,
        realName: user.nickname,
        avatar: user.avatar || '',
        phone: user.phone || '',
        email: user.email || '',
        roles: roles || [],
        permissions: permissions || []
      }
      return this.userInfo
    },

    // Log out: call the logout API, clear token and reset user info
    logout() {
      logoutApi().catch(() => {})
      this.token = ''
      this.userInfo = {
        id: '',
        username: '',
        realName: '',
        avatar: '',
        phone: '',
        email: '',
        roles: [],
        permissions: []
      }
      removeToken()
    }
  }
})