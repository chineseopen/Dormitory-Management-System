<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Student portal layout shell.
  Renders the student header (title + user dropdown), the left navigation menu
  and the main content area where the matched route component is displayed.
-->
<template>
  <div class="student-layout">
    <!-- Top header: app title and user actions -->
    <div class="student-header">
      <div class="header-left">
        <!-- App title, clicking it returns to the student home -->
        <h1 class="app-title" @click="$router.push('/student-portal')">宿舍管理系统</h1>
        <span class="app-subtitle">学生端</span>
      </div>
      <div class="header-right">
        <!-- Current student name -->
        <span class="user-name">{{ userStore.userInfo.realName || '同学' }}</span>
        <!-- User avatar with dropdown menu -->
        <el-dropdown @command="handleCommand">
          <el-avatar :size="32" class="user-avatar">
            {{ userStore.userInfo.realName?.charAt(0) || 'S' }}
          </el-avatar>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- Body: sidebar navigation + main content -->
    <div class="student-container">
      <!-- Left navigation menu -->
      <div class="student-sidebar">
        <el-menu
          :default-active="currentRoute"
          router
          class="sidebar-menu"
        >
          <el-menu-item index="/student-portal">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/student-portal/repair">
            <el-icon><SetUp /></el-icon>
            <span>报修管理</span>
          </el-menu-item>
          <el-menu-item index="/student-portal/fee">
            <el-icon><Coin /></el-icon>
            <span>水电费</span>
          </el-menu-item>
          <el-menu-item index="/student-portal/notice">
            <el-icon><Bell /></el-icon>
            <span>通知公告</span>
          </el-menu-item>
          <el-menu-item index="/student-portal/profile">
            <el-icon><User /></el-icon>
            <span>个人信息</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- Main content rendered by the router -->
      <div class="student-main">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// Highlight the menu item matching the current route path
const currentRoute = computed(() => route.path)

// Handle dropdown menu commands (profile navigation or logout)
function handleCommand(command) {
  if (command === 'profile') {
    router.push('/student-portal/profile')
  } else if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style lang="scss" scoped>
/* Root layout: full-screen vertical flex */
.student-layout {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: $color-bg-page;
}

/* Dark header bar */
.student-header {
  height: 56px;
  background: #0f172a;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 $spacing-6;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: $spacing-3;
}

.app-title {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  cursor: pointer;
  margin: 0;
}

.app-subtitle {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  background: rgba(255, 255, 255, 0.1);
  padding: 2px 8px;
  border-radius: 4px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: $spacing-3;
}

.user-name {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

.user-avatar {
  background-color: #0ea5e9;
  color: #fff;
  cursor: pointer;
}

/* Body: sidebar + main content side by side */
.student-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* Left sidebar with fixed width */
.student-sidebar {
  width: 200px;
  background: $color-bg-card;
  border-right: 1px solid $color-border-light;
  flex-shrink: 0;
}

.sidebar-menu {
  border-right: none;
  height: 100%;
}

/* Scrollable main content area */
.student-main {
  flex: 1;
  overflow-y: auto;
  padding: $spacing-6;
}
</style>