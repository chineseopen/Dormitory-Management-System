<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Reusable file upload component wrapping Element Plus el-upload.
 * Supports v-model for the file list, size validation, custom action URL,
 * and emits success/remove events. Automatically attaches the auth token header.
-->
<template>
  <div class="file-upload">
    <el-upload
      v-model:file-list="fileList"
      :action="action"
      :headers="headers"
      :accept="accept"
      :limit="limit"
      :multiple="multiple"
      :disabled="disabled"
      :auto-upload="autoUpload"
      :before-upload="handleBeforeUpload"
      :on-success="handleSuccess"
      :on-error="handleError"
      :on-exceed="handleExceed"
      :on-remove="handleRemove"
      :list-type="listType"
    >
      <!-- Default upload trigger button (overridable via slot) -->
      <slot name="trigger">
        <el-button type="primary" :disabled="disabled">
          <el-icon><Upload /></el-icon>
          点击上传
        </el-button>
      </slot>
      <!-- Optional tip text below the trigger -->
      <template #tip>
        <div v-if="tip" class="upload-tip">{{ tip }}</div>
      </template>
    </el-upload>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getToken } from '@/utils/auth'

// Component props: configure upload behavior and limits
const props = defineProps({
  // Bound file list (v-model)
  modelValue: {
    type: Array,
    default: () => []
  },
  // Upload endpoint URL
  action: {
    type: String,
    default: '/api/upload'
  },
  // Accepted file types
  accept: {
    type: String,
    default: ''
  },
  // Maximum number of files
  limit: {
    type: Number,
    default: 5
  },
  // Maximum file size in MB
  maxSize: {
    type: Number,
    default: 10 // MB
  },
  // Allow multiple files
  multiple: {
    type: Boolean,
    default: false
  },
  // Disable the uploader
  disabled: {
    type: Boolean,
    default: false
  },
  // Upload automatically on file select
  autoUpload: {
    type: Boolean,
    default: true
  },
  // Element Plus list type (text/picture/picture-card)
  listType: {
    type: String,
    default: 'text'
  },
  // Tip text shown below the trigger
  tip: {
    type: String,
    default: ''
  }
})

// Emitted events for v-model sync and upload lifecycle
const emit = defineEmits(['update:modelValue', 'success', 'remove'])

// Two-way binding for the file list
const fileList = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// Attach the auth token to upload requests
const headers = computed(() => ({
  Authorization: getToken()
}))

// Validate file size before uploading
function handleBeforeUpload(file) {
  if (props.maxSize) {
    const isLt = file.size / 1024 / 1024 < props.maxSize
    if (!isLt) {
      ElMessage.error(`文件大小不能超过 ${props.maxSize}MB`)
      return false
    }
  }
  return true
}

// Handle a successful upload response
function handleSuccess(response, file) {
  if (response.code === 200) {
    ElMessage.success('上传成功')
    emit('success', response.data, file)
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}

// Handle an upload error
function handleError() {
  ElMessage.error('上传失败，请重试')
}

// Handle exceeding the file limit
function handleExceed() {
  ElMessage.warning(`最多只能上传 ${props.limit} 个文件`)
}

// Handle file removal
function handleRemove(file) {
  emit('remove', file)
}
</script>

<style lang="scss" scoped>
/* Upload tip text styling */
.upload-tip {
  font-size: $font-size-xs;
  color: $color-text-placeholder;
  margin-top: $spacing-1;
}
</style>