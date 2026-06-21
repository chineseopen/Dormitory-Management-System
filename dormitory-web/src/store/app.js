// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Pinia store for global application UI state.
 * Manages the sidebar collapse state, the tags-view (visited/cached) tabs
 * and the current device type (desktop/mobile).
 *
 * @module store/app
 */

import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  // Reactive application state
  state: () => ({
    // Whether the sidebar is collapsed
    sidebarCollapsed: false,
    // List of visited tag views shown in the tags bar
    visitedViews: [],
    // List of route names kept alive in cache
    cachedViews: [],
    // Current device type
    device: 'desktop'
  }),

  actions: {
    // Toggle the sidebar between expanded and collapsed
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed
    },

    // Set the current device type (desktop or mobile)
    setDevice(device) {
      this.device = device
    },

    // Add a visited view tag (skip duplicates)
    addVisitedView(view) {
      if (this.visitedViews.some((v) => v.path === view.path)) return
      this.visitedViews.push({
        name: view.name,
        path: view.path,
        title: view.meta?.title || '未命名',
        affix: view.meta?.affix || false
      })
    },

    // Remove a visited view tag by path
    removeVisitedView(view) {
      const index = this.visitedViews.findIndex((v) => v.path === view.path)
      if (index > -1) {
        this.visitedViews.splice(index, 1)
      }
    },

    // Close all visited views except the given one and pinned (affix) views
    closeOtherVisitedViews(view) {
      this.visitedViews = this.visitedViews.filter(
        (v) => v.affix || v.path === view.path
      )
    },

    // Close all visited views except pinned (affix) ones
    closeAllVisitedViews() {
      this.visitedViews = this.visitedViews.filter((v) => v.affix)
    },

    // Add a route name to the keep-alive cache (only if marked as keepAlive)
    addCachedView(view) {
      if (!view.name || this.cachedViews.includes(view.name)) return
      if (view.meta?.keepAlive) {
        this.cachedViews.push(view.name)
      }
    },

    // Remove a route name from the keep-alive cache
    removeCachedView(view) {
      const index = this.cachedViews.indexOf(view.name)
      if (index > -1) {
        this.cachedViews.splice(index, 1)
      }
    }
  }
})