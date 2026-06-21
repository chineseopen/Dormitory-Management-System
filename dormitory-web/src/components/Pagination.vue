<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Reusable pagination component wrapping Element Plus el-pagination.
 * Supports v-model for the current page and page size, and emits a change event
 * with the new page/pageSize whenever the user navigates or changes the page size.
-->
<template>
  <div class="pagination-wrapper">
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="currentPageSize"
      :page-sizes="pageSizes"
      :total="total"
      :background="true"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'

// Component props: total count, current page, page size and selectable sizes
const props = defineProps({
  // Total number of records
  total: {
    type: Number,
    default: 0
  },
  // Current page number (v-model:page)
  page: {
    type: Number,
    default: 1
  },
  // Items per page (v-model:pageSize)
  pageSize: {
    type: Number,
    default: 10
  },
  // Available page size options
  pageSizes: {
    type: Array,
    default: () => [10, 20, 50, 100]
  }
})

// Emitted events for two-way binding and change notifications
const emit = defineEmits(['update:page', 'update:pageSize', 'change'])

// Two-way binding for the current page
const currentPage = computed({
  get: () => props.page,
  set: (val) => emit('update:page', val)
})

// Two-way binding for the page size
const currentPageSize = computed({
  get: () => props.pageSize,
  set: (val) => emit('update:pageSize', val)
})

// When the page size changes, reset to the first page
function handleSizeChange(val) {
  emit('change', { page: 1, pageSize: val })
}

// When the current page changes
function handleCurrentChange(val) {
  emit('change', { page: val, pageSize: props.pageSize })
}
</script>

<style lang="scss" scoped>
/* Right-aligned pagination container */
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding-top: $spacing-4;
}
</style>