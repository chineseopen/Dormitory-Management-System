<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Left navigation sidebar for the admin portal.
  Renders the logo and the navigation menu built from a static route definition,
  supporting both single menu items and grouped sub-menus, with collapse support.
-->
<template>
  <div class="sidebar" :class="{ 'is-collapsed': appStore.sidebarCollapsed }">
    <!-- Logo area: icon and application title -->
    <div class="sidebar-logo">
      <div class="logo-icon">
        <el-icon :size="24"><School /></el-icon>
      </div>
      <transition name="fade">
        <span v-show="!appStore.sidebarCollapsed" class="logo-text">宿舍管理系统</span>
      </transition>
    </div>

    <!-- Scrollable navigation menu -->
    <el-scrollbar>
      <el-menu
        :default-active="activeMenu"
        :collapse="appStore.sidebarCollapsed"
        :collapse-transition="false"
        background-color="#0f172a"
        text-color="#94a3b8"
        active-text-color="#ffffff"
        router
      >
        <template v-for="route in menuRoutes" :key="route.path">
          <!-- Group with multiple children renders as a sub-menu -->
          <el-sub-menu
            v-if="route.children && route.children.length > 1"
            :index="route.path"
          >
            <template #title>
              <el-icon v-if="route.meta?.icon">
                <component :is="route.meta.icon" />
              </el-icon>
              <span>{{ route.meta?.title }}</span>
            </template>
            <el-menu-item
              v-for="child in route.children"
              :key="child.path"
              :index="`/${route.path}/${child.path}`"
            >
              <span>{{ child.meta?.title }}</span>
            </el-menu-item>
          </el-sub-menu>

          <!-- Single menu item (hidden routes are skipped) -->
          <el-menu-item
            v-else-if="!route.meta?.hidden"
            :index="`/${route.path}`"
          >
            <el-icon v-if="route.meta?.icon">
              <component :is="route.meta.icon" />
            </el-icon>
            <template #title>
              <span>{{ route.meta?.title }}</span>
            </template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/store/app'

const route = useRoute()
const appStore = useAppStore()

// The currently active menu item matches the current route path
const activeMenu = computed(() => route.path)

// Static menu route definition (excludes login page and 404)
const menuRoutes = computed(() => {
  const mainRoute = {
    path: '/',
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', meta: { title: '工作台', icon: 'Odometer' } },
      { path: 'building', meta: { title: '楼栋管理', icon: 'OfficeBuilding' } },
      { path: 'room', meta: { title: '房间管理', icon: 'House' } },
      { path: 'student', meta: { title: '住宿管理', icon: 'User' } },
      { path: 'checkin', meta: { title: '入住记录', icon: 'Document' } },
      { path: 'roomchange', meta: { title: '调宿记录', icon: 'Switch' } },
      { path: 'repair', meta: { title: '报修管理', icon: 'SetUp' } },
      { path: 'visitor', meta: { title: '访客管理', icon: 'Avatar' } },
      { path: 'fee', meta: { title: '水电费管理', icon: 'Coin' } },
      { path: 'notice', meta: { title: '通知公告', icon: 'Bell' } },
      {
        path: 'system',
        meta: { title: '系统管理', icon: 'Setting' },
        children: [
          { path: 'user', meta: { title: '用户管理' } },
          { path: 'role', meta: { title: '角色管理' } },
          { path: 'permission', meta: { title: '权限管理' } }
        ]
      }
    ]
  }
  return mainRoute.children
})
</script>

<style lang="scss" scoped>
/* Fixed sidebar with dark background */
.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  width: $sidebar-width;
  background-color: $color-bg-sidebar;
  z-index: 1001;
  transition: width $duration-base $ease-out;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  /* Narrower width when collapsed */
  &.is-collapsed {
    width: $sidebar-collapsed-width;
  }
}

/* Logo block at the top */
.sidebar-logo {
  height: $navbar-height;
  display: flex;
  align-items: center;
  padding: 0 $spacing-4;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  flex-shrink: 0;
  overflow: hidden;

  /* Center the icon when collapsed */
  .is-collapsed & {
    justify-content: center;
    padding: 0;
  }
}

.logo-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  color: $color-accent;
  flex-shrink: 0;
}

.logo-text {
  margin-left: $spacing-3;
  font-size: $font-size-md;
  font-weight: $font-weight-semibold;
  color: #ffffff;
  white-space: nowrap;
  overflow: hidden;
}

/* Element Plus menu style overrides */
:deep(.el-menu) {
  border-right: none;
}

/* Menu items: rounded, spaced and hoverable */
:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  height: 44px;
  line-height: 44px;
  font-size: $font-size-sm;
  border-radius: $border-radius-sm;
  margin: 2px $spacing-2;
  width: calc(100% - #{$spacing-4});

  &:hover {
    background-color: $color-bg-sidebar-hover !important;
  }
}

/* Active menu item: highlighted background with accent bar */
:deep(.el-menu-item.is-active) {
  background-color: $color-bg-sidebar-active !important;
  color: #ffffff !important;

  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 3px;
    height: 20px;
    background: $color-accent;
    border-radius: 0 2px 2px 0;
  }
}

/* Indent nested menu items */
:deep(.el-sub-menu .el-menu-item) {
  padding-left: $spacing-10 !important;
  min-width: auto;
}

/* Scrollable menu area fills remaining height */
:deep(.el-scrollbar) {
  flex: 1;
}
</style>