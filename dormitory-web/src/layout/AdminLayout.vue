<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Admin portal layout shell.
  Composes the sidebar, navbar, tags-view and the main content area.
  Adjusts the main container margin when the sidebar is collapsed.
-->
<template>
  <div class="admin-layout" :class="{ 'sidebar-collapsed': appStore.sidebarCollapsed }">
    <!-- Left navigation sidebar -->
    <Sidebar />

    <!-- Main content area: navbar, tags and page outlet -->
    <div class="main-container">
      <!-- Top navigation bar -->
      <Navbar />

      <!-- Tags view (open tabs) -->
      <TagsView />

      <!-- Page content rendered by the router -->
      <div class="app-main">
        <router-view :key="$route.path" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAppStore } from '@/store/app'
import Sidebar from './components/Sidebar.vue'
import Navbar from './components/Navbar.vue'
import TagsView from './components/TagsView.vue'

// Access the global app store for sidebar collapse state
const appStore = useAppStore()
</script>

<style lang="scss" scoped>
/* Root layout: full-screen flex container */
.admin-layout {
  display: flex;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

/* Main content shifts based on the sidebar width */
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  overflow: hidden;
  margin-left: $sidebar-width;
  transition: margin-left $duration-base $ease-out;

  /* Narrower margin when the sidebar is collapsed */
  .sidebar-collapsed & {
    margin-left: $sidebar-collapsed-width;
  }
}

/* Scrollable page content area */
.app-main {
  flex: 1;
  overflow-y: auto;
  background-color: $color-bg-page;
  padding: $spacing-6;
}
</style>