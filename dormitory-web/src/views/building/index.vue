<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Building management page for the admin portal.
  Provides a searchable, paginated table of dormitory buildings, plus a dialog
  form to create or edit building records (name, type, floors, rooms per floor,
  manager). Supports single-record deletion with confirmation.
-->
<template>
  <div class="page-container">
    <!-- Page header with add button -->
    <div class="page-header">
      <h2 class="page-title">楼栋管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增楼栋
      </el-button>
    </div>

    <!-- Search bar -->
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="楼栋名称">
          <el-input v-model="searchForm.name" placeholder="请输入楼栋名称" clearable />
        </el-form-item>
        <el-form-item label="楼栋类型">
          <el-select v-model="searchForm.type" placeholder="请选择" clearable>
            <el-option label="男生宿舍" :value="1" />
            <el-option label="女生宿舍" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- Data table -->
    <div class="page-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="name" label="楼栋名称" min-width="100" align="center" />
        <el-table-column prop="typeDesc" label="楼栋类型" min-width="100" align="center" />
        <el-table-column prop="floors" label="楼层数" width="80" align="center" />
        <el-table-column prop="roomsPerFloor" label="每层房间数" width="90" align="center" />
        <el-table-column prop="managerName" label="管理员" width="100" align="center" />
        <!-- Status column with colored tag -->
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="120" align="center" />
        <!-- Row action buttons -->
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <Pagination
        :total="total"
        v-model:page="pagination.page"
        v-model:page-size="pagination.pageSize"
        @change="fetchData"
      />
    </div>

    <!-- Add/Edit dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="90px">
        <el-form-item label="楼栋名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入楼栋名称" />
        </el-form-item>
        <el-form-item label="楼栋类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择">
            <el-option label="男生宿舍" :value="1" />
            <el-option label="女生宿舍" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼层数" prop="floors">
          <el-input-number v-model="form.floors" :min="1" :max="30" />
        </el-form-item>
        <el-form-item label="每层房间数" prop="roomsPerFloor">
          <el-input-number v-model="form.roomsPerFloor" :min="1" :max="20" />
        </el-form-item>
        <el-form-item label="楼管员ID" prop="managerId">
          <el-input v-model="form.managerId" placeholder="请输入楼管员用户ID" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBuildingList, addBuilding, updateBuilding, deleteBuilding } from '@/api/building'
import Pagination from '@/components/Pagination.vue'

// Loading state, table data and total count
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// Dialog visibility, title and form reference
const dialogVisible = ref(false)
const dialogTitle = ref('新增楼栋')
const formRef = ref(null)

// Search form and pagination state
const searchForm = reactive({ name: '', type: '' })
const pagination = reactive({ page: 1, pageSize: 10 })

// Add/edit form model
const form = reactive({
  id: null,
  name: '',
  type: null,
  floors: 6,
  roomsPerFloor: 4,
  managerId: '',
  description: ''
})

// Validation rules for the add/edit form
const formRules = {
  name: [{ required: true, message: '请输入楼栋名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择楼栋类型', trigger: 'change' }],
  floors: [{ required: true, message: '请输入楼层数', trigger: 'blur' }]
}

// Fetch the list on mount
onMounted(() => fetchData())

// Fetch the paginated building list from the backend
async function fetchData() {
  loading.value = true
  try {
    const res = await getBuildingList({ ...searchForm, pageNum: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

// Run a search (reset to the first page)
function handleSearch() {
  pagination.page = 1
  fetchData()
}

// Reset the search form and re-query
function handleReset() {
  searchForm.name = ''
  searchForm.type = ''
  handleSearch()
}

// Open the dialog in "add" mode
function handleAdd() {
  dialogTitle.value = '新增楼栋'
  Object.assign(form, { id: null, name: '', type: null, floors: 6, roomsPerFloor: 4, managerId: '', description: '' })
  dialogVisible.value = true
}

// Open the dialog in "edit" mode with the selected row
function handleEdit(row) {
  dialogTitle.value = '编辑楼栋'
  Object.assign(form, row)
  dialogVisible.value = true
}

// Submit the add/edit form
async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  if (form.id) {
    await updateBuilding(form)
    ElMessage.success('修改成功')
  } else {
    await addBuilding(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  fetchData()
}

// Delete a building after confirmation
function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除 ${row.name} 吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    await deleteBuilding([row.id])
    ElMessage.success('删除成功')
    fetchData()
  })
}
</script>