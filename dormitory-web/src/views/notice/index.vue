<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Notice/announcement management page for the admin portal.
  Provides a searchable, paginated table of notices with type and status tags,
  plus a dialog to create or edit notices and actions to publish or delete them.
-->
<template>
  <div class="page-container">
    <!-- Page header with add button -->
    <div class="page-header">
      <h2 class="page-title">通知公告</h2>
      <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增通知</el-button>
    </div>

    <!-- Search bar -->
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="标题"><el-input v-model="searchForm.title" placeholder="请输入" clearable /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="请选择" clearable>
            <el-option label="通知" :value="1" /><el-option label="公告" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="已发布" :value="1" /><el-option label="草稿" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="handleSearch">查询</el-button><el-button @click="handleReset">重置</el-button></el-form-item>
      </el-form>
    </div>

    <!-- Data table -->
    <div class="page-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="title" label="标题" min-width="250" align="center" />
        <!-- Type column with colored tag -->
        <el-table-column prop="type" label="类型" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'primary' : 'warning'" size="small">{{ row.type === 1 ? '通知' : '公告' }}</el-tag>
          </template>
        </el-table-column>
        <!-- Status column with colored tag -->
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '已发布' : '草稿' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" align="center" />
        <!-- Row action buttons -->
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 0" link type="success" size="small" @click="handlePublish(row)">发布</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- Pagination -->
      <Pagination :total="total" v-model:page="pagination.page" v-model:page-size="pagination.pageSize" @change="fetchData" />
    </div>

    <!-- Add/Edit dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="类型" prop="type"><el-radio-group v-model="form.type"><el-radio :value="1">通知</el-radio><el-radio :value="2">公告</el-radio></el-radio-group></el-form-item>
        <el-form-item label="内容" prop="content"><el-input v-model="form.content" type="textarea" :rows="6" /></el-form-item>
        <el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio :value="1">已发布</el-radio><el-radio :value="0">草稿</el-radio></el-radio-group></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNoticeList, addNotice, updateNotice, deleteNotice, publishNotice } from '@/api/notice'
import Pagination from '@/components/Pagination.vue'

// Loading state, table data and total count
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// Dialog visibility, title and form reference
const dialogVisible = ref(false)
const dialogTitle = ref('新增通知')
const formRef = ref(null)

// Search form and pagination state
const searchForm = reactive({ title: '', type: '', status: '' })
const pagination = reactive({ page: 1, pageSize: 10 })

// Add/edit form model
const form = reactive({ id: null, title: '', type: 1, content: '', status: 0 })
const formRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

// Fetch the list on mount
onMounted(() => fetchData())

// Fetch the paginated notice list from the backend
async function fetchData() {
  loading.value = true
  try { const res = await getNoticeList({ ...searchForm, pageNum: pagination.page, pageSize: pagination.pageSize }); tableData.value = res.data.records; total.value = res.data.total }
  finally { loading.value = false }
}

// Run a search (reset to the first page)
function handleSearch() { pagination.page = 1; fetchData() }

// Reset the search form and re-query
function handleReset() { Object.assign(searchForm, { title: '', type: '', status: '' }); handleSearch() }

// Open the dialog in "add" mode
function handleAdd() { dialogTitle.value = '新增通知'; Object.assign(form, { id: null, title: '', type: 1, content: '', status: 0 }); dialogVisible.value = true }

// Open the dialog in "edit" mode with the selected row
function handleEdit(row) { dialogTitle.value = '编辑通知'; Object.assign(form, row); dialogVisible.value = true }

// Submit the add/edit form
async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  if (form.id) { await updateNotice(form); ElMessage.success('修改成功') }
  else { await addNotice(form); ElMessage.success('新增成功') }
  dialogVisible.value = false; fetchData()
}

// Publish a draft notice
async function handlePublish(row) { await publishNotice(row.id); ElMessage.success('发布成功'); fetchData() }

// Delete a notice after confirmation
function handleDelete(row) {
  ElMessageBox.confirm('确定要删除该通知吗？', '提示', { type: 'warning' })
    .then(async () => { await deleteNotice([row.id]); ElMessage.success('删除成功'); fetchData() })
}
</script>