<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Student portal notice page.
  Lists published notices as cards with read/unread indicators,
  supports pagination and opens a detail dialog when a notice is clicked.
-->
<template>
  <div class="student-notice">
    <!-- Page header -->
    <div class="page-header">
      <h2 class="page-title">通知公告</h2>
    </div>

    <!-- Notice list: each item is a clickable card -->
    <div class="notice-list" v-loading="loading">
      <div v-if="noticeList.length === 0" class="empty-state">
        <el-empty description="暂无通知公告" />
      </div>
      <div
        v-for="item in noticeList"
        :key="item.id"
        class="notice-card"
        :class="{ 'is-unread': !readIds.includes(item.id) }"
        @click="handleRead(item)"
      >
        <!-- Card header: type tag, unread badge and publish time -->
        <div class="notice-header">
          <div class="notice-tags">
            <el-tag :type="getTypeTag(item.type)" size="small">{{ getTypeName(item.type) }}</el-tag>
            <el-tag v-if="!readIds.includes(item.id)" type="danger" size="small" effect="dark">未读</el-tag>
          </div>
          <span class="notice-time">{{ item.createTime }}</span>
        </div>
        <!-- Notice title and preview content -->
        <h3 class="notice-title">{{ item.title }}</h3>
        <p class="notice-content">{{ item.content }}</p>
        <!-- Card footer: publisher name -->
        <div class="notice-footer">
          <span class="notice-publisher">发布人：{{ item.publisherName || '系统' }}</span>
        </div>
      </div>
    </div>

    <!-- Pagination controls -->
    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @change="fetchData"
      />
    </div>

    <!-- Notice detail dialog -->
    <el-dialog v-model="detailVisible" :title="currentNotice.title" width="600px">
      <div class="notice-detail">
        <div class="detail-meta">
          <el-tag :type="getTypeTag(currentNotice.type)" size="small">{{ getTypeName(currentNotice.type) }}</el-tag>
          <span class="detail-time">{{ currentNotice.createTime }}</span>
        </div>
        <div class="detail-content">{{ currentNotice.content }}</div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getStudentNoticeList, markNoticeRead, getReadNoticeIds } from '@/api/student-notice'

// Reactive state: loading flag, notice list, total count and read notice ids
const loading = ref(false)
const noticeList = ref([])
const total = ref(0)
const readIds = ref([])
const detailVisible = ref(false)
const currentNotice = ref({})
const pagination = reactive({ page: 1, pageSize: 10 })

// On mount: first load the read notice ids, then fetch the notice list
onMounted(async () => {
  await fetchReadIds()
  fetchData()
})

// Fetch paginated notices from the API
async function fetchData() {
  loading.value = true
  try {
    const res = await getStudentNoticeList({ pageNum: pagination.page, pageSize: pagination.pageSize })
    if (res.data) {
      noticeList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } finally {
    loading.value = false
  }
}

// Fetch the list of notice ids already read by the current student
async function fetchReadIds() {
  try {
    const res = await getReadNoticeIds()
    if (res.data) {
      readIds.value = res.data
    }
  } catch (e) {
    // Ignore errors - read state is non-critical
  }
}

// Open the detail dialog and mark the notice as read if unread
async function handleRead(item) {
  currentNotice.value = item
  detailVisible.value = true

  // Mark as read only if the notice is currently unread
  if (!readIds.value.includes(item.id)) {
    try {
      await markNoticeRead(item.id)
      readIds.value.push(item.id)
    } catch (e) {
      // Ignore errors - read state is non-critical
    }
  }
}

// Map a notice type code to its display name
function getTypeName(type) {
  const map = { 1: '通知', 2: '公告', 3: '紧急' }
  return map[type] || '通知'
}

// Map a notice type code to its tag color
function getTypeTag(type) {
  const map = { 1: 'primary', 2: 'warning', 3: 'danger' }
  return map[type] || 'primary'
}
</script>

<style lang="scss" scoped>
/* Page header: title typography */
.page-header {
  margin-bottom: $spacing-6;

  .page-title {
    font-size: $font-size-xl;
    font-weight: $font-weight-semibold;
    color: $color-text-primary;
    margin: 0;
  }
}

/* Notice list container */
.notice-list {
  min-height: 200px;
}

/* Empty state placeholder */
.empty-state {
  padding: $spacing-12 0;
}

/* Notice card: hover elevation and unread highlight */
.notice-card {
  background: $color-bg-card;
  border: 1px solid $color-border-light;
  border-radius: $border-radius-md;
  padding: $spacing-5;
  margin-bottom: $spacing-4;
  cursor: pointer;
  transition: border-color $duration-fast $ease-out, box-shadow $duration-fast $ease-out;

  &:hover {
    border-color: $color-primary-lighter;
    box-shadow: $shadow-sm;
  }

  &.is-unread {
    border-left: 3px solid $color-primary;
  }
}

/* Card header row: tags and publish time */
.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $spacing-2;
}

.notice-tags {
  display: flex;
  gap: $spacing-2;
}

.notice-time {
  font-size: $font-size-xs;
  color: $color-text-secondary;
}

/* Notice title typography */
.notice-title {
  font-size: $font-size-md;
  font-weight: $font-weight-semibold;
  color: $color-text-primary;
  margin: 0 0 $spacing-2 0;
}

/* Notice content preview (clamped to 2 lines) */
.notice-content {
  font-size: $font-size-sm;
  color: $color-text-regular;
  line-height: $line-height-relaxed;
  margin: 0 0 $spacing-3 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* Card footer: publisher name */
.notice-footer {
  .notice-publisher {
    font-size: $font-size-xs;
    color: $color-text-secondary;
  }
}

/* Pagination wrapper aligned to the right */
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: $spacing-4;
}

/* Notice detail dialog content */
.notice-detail {
  .detail-meta {
    display: flex;
    align-items: center;
    gap: $spacing-3;
    margin-bottom: $spacing-5;
  }

  .detail-time {
    font-size: $font-size-sm;
    color: $color-text-secondary;
  }

  .detail-content {
    font-size: $font-size-base;
    color: $color-text-regular;
    line-height: $line-height-relaxed;
    white-space: pre-wrap;
  }
}
</style>