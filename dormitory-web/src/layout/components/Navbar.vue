<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Top navigation bar for the admin portal.
  Contains the sidebar collapse toggle, breadcrumb navigation on the left,
  and the fullscreen toggle plus the user dropdown on the right.
-->
<template>
  <div class="navbar">
    <!-- Left section: collapse toggle and breadcrumb -->
    <div class="navbar-left">
      <!-- Sidebar collapse/expand toggle button -->
      <div class="hamburger" @click="appStore.toggleSidebar">
        <el-icon :size="20">
          <Fold v-if="!appStore.sidebarCollapsed" />
          <Expand v-else />
        </el-icon>
      </div>

      <!-- Breadcrumb built from the matched route records -->
      <el-breadcrumb separator="/">
        <el-breadcrumb-item
          v-for="item in breadcrumbs"
          :key="item.path"
          :to="item.path"
        >
          {{ item.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- Right section: fullscreen toggle and user dropdown -->
    <div class="navbar-right">
      <!-- Fullscreen toggle button -->
      <div class="navbar-action" @click="toggleFullscreen">
        <el-icon :size="18">
          <FullScreen />
        </el-icon>
      </div>

      <!-- User avatar and name dropdown -->
      <el-dropdown trigger="click" @command="handleCommand">
        <div class="navbar-user">
          <el-avatar :size="30" class="user-avatar">
            {{ userStore.userInfo.realName?.charAt(0) || 'A' }}
          </el-avatar>
          <span class="user-name">{{ userStore.userInfo.realName || '管理员' }}</span>
          <el-icon><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><UserFilled /></el-icon>个人中心
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/store/app'
import { useUserStore } from '@/store/user'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

// Build breadcrumb items from the matched route records that have a title
const breadcrumbs = computed(() => {
  const matched = route.matched.filter((item) => item.meta?.title)
  return matched.map((item) => ({
    path: item.path,
    title: item.meta.title
  }))
})

// Toggle the browser fullscreen mode
function toggleFullscreen() {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

// Handle dropdown menu commands (profile navigation or logout)
function handleCommand(command) {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      router.push('/login')
    })
  }
}
</script>

<style lang="scss" scoped>
/* Navbar: horizontal bar with bottom shadow */
.navbar {
  height: $navbar-height;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 $spacing-4;
  background-color: $color-bg-card;
  border-bottom: 1px solid $color-border-light;
  box-shadow: $shadow-sm;
  z-index: 10;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: $spacing-3;
}

/* Collapse toggle button */
.hamburger {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  cursor: pointer;
  border-radius: $border-radius-base;
  color: $color-text-regular;
  transition: background-color $duration-fast $ease-out;

  &:hover {
    background-color: $color-bg-page;
  }
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: $spacing-2;
}

/* Generic action icon button */
.navbar-action {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  cursor: pointer;
  border-radius: $border-radius-base;
  color: $color-text-secondary;
  transition: background-color $duration-fast $ease-out, color $duration-fast $ease-out;

  &:hover {
    background-color: $color-bg-page;
    color: $color-text-primary;
  }
}

/* User info block with avatar and name */
.navbar-user {
  display: flex;
  align-items: center;
  gap: $spacing-2;
  padding: $spacing-1 $spacing-3;
  cursor: pointer;
  border-radius: $border-radius-base;
  transition: background-color $duration-fast $ease-out;

  &:hover {
    background-color: $color-bg-page;
  }
}

.user-avatar {
  background-color: $color-primary-light;
  font-size: $font-size-sm;
}

.user-name {
  font-size: $font-size-sm;
  color: $color-text-primary;
  font-weight: $font-weight-medium;
}
</style>