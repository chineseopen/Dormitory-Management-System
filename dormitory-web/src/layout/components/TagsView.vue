<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Tags view component for the admin portal.
  Displays the list of visited route tabs, allows closing individual tabs
  (except pinned ones) and provides "close other" / "close all" actions.
-->
<template>
  <div class="tags-view">
    <!-- Horizontal scrollable list of tag tabs -->
    <el-scrollbar>
      <div class="tags-container">
        <router-link
          v-for="tag in appStore.visitedViews"
          :key="tag.path"
          :to="tag.path"
          class="tag-item"
          :class="{ active: isActive(tag) }"
        >
          <span class="tag-title">{{ tag.title }}</span>
          <!-- Close icon (hidden for pinned/affix tabs) -->
          <el-icon
            v-if="!tag.affix"
            class="tag-close"
            @click.prevent.stop="closeTag(tag)"
          >
            <Close />
          </el-icon>
        </router-link>
      </div>
    </el-scrollbar>

    <!-- Right-side actions dropdown -->
    <div class="tags-actions">
      <el-dropdown trigger="click" @command="handleCommand">
        <div class="tags-action-btn">
          <el-icon><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="closeOther">关闭其他</el-dropdown-item>
            <el-dropdown-item command="closeAll">关闭所有</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/store/app'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()

// A tag is active when its path matches the current route path
function isActive(tag) {
  return tag.path === route.path
}

// Close a single tag tab
function closeTag(tag) {
  appStore.removeVisitedView(tag)
  appStore.removeCachedView(tag)

  // If the closed tab was the current page, navigate to the latest remaining tab
  if (isActive(tag)) {
    const views = appStore.visitedViews
    const latest = views[views.length - 1]
    if (latest) {
      router.push(latest.path)
    } else {
      router.push('/')
    }
  }
}

// Handle dropdown commands: close other tabs or close all tabs
function handleCommand(command) {
  if (command === 'closeOther') {
    appStore.closeOtherVisitedViews(route)
  } else if (command === 'closeAll') {
    appStore.closeAllVisitedViews()
    router.push('/')
  }
}
</script>

<style lang="scss" scoped>
/* Tags bar: horizontal bar below the navbar */
.tags-view {
  height: $tagsview-height;
  display: flex;
  align-items: center;
  background-color: $color-bg-card;
  border-bottom: 1px solid $color-border-light;
  padding: 0 $spacing-2;
}

.tags-container {
  display: flex;
  align-items: center;
  gap: $spacing-1;
  white-space: nowrap;
}

/* Individual tag tab */
.tag-item {
  display: inline-flex;
  align-items: center;
  gap: $spacing-1;
  height: 26px;
  padding: 0 $spacing-2;
  font-size: $font-size-xs;
  color: $color-text-secondary;
  background-color: $color-bg-page;
  border-radius: $border-radius-sm;
  text-decoration: none;
  cursor: pointer;
  transition: all $duration-fast $ease-out;
  border: 1px solid transparent;

  &:hover {
    color: $color-primary-light;
    background-color: rgba($color-primary-light, 0.06);
  }

  /* Active tab styling */
  &.active {
    color: $color-primary-light;
    background-color: rgba($color-primary-light, 0.1);
    border-color: rgba($color-primary-light, 0.2);
    font-weight: $font-weight-medium;
  }
}

/* Close icon */
.tag-close {
  font-size: 12px;
  border-radius: 50%;
  transition: background-color $duration-fast $ease-out;

  &:hover {
    background-color: rgba(0, 0, 0, 0.12);
  }
}

.tags-actions {
  margin-left: auto;
  flex-shrink: 0;
}

/* Actions dropdown trigger button */
.tags-action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  cursor: pointer;
  border-radius: $border-radius-sm;
  color: $color-text-secondary;
  transition: background-color $duration-fast $ease-out;

  &:hover {
    background-color: $color-bg-page;
  }
}
</style>