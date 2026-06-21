<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Room management page for the admin portal.
  Provides a searchable, paginated table of dormitory rooms with an occupancy
  progress bar, plus a dialog form to create or edit rooms (building, floor,
  room number, capacity). Supports single-record deletion with confirmation.
-->
<template>
  <div class="page-container">
    <!-- Page header with add button -->
    <div class="page-header">
      <h2 class="page-title">房间管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增房间
      </el-button>
    </div>

    <!-- Search bar -->
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="楼栋">
          <el-select v-model="searchForm.buildingId" placeholder="请选择楼栋" clearable>
            <el-option v-for="b in buildingList" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号">
          <el-input v-model="searchForm.roomNumber" placeholder="请输入房间号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="停用" :value="0" />
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
        <el-table-column prop="buildingName" label="楼栋" min-width="100" align="center" />
        <el-table-column prop="roomNumber" label="房间号" width="100" align="center" />
        <el-table-column prop="floorNumber" label="楼层" width="70" align="center" />
        <el-table-column prop="capacity" label="容纳人数" width="90" align="center" />
        <el-table-column prop="currentCount" label="已住人数" width="90" align="center" />
        <!-- Occupancy rate progress bar -->
        <el-table-column label="入住率" width="100" align="center">
          <template #default="{ row }">
            <el-progress
              :percentage="Math.round((row.currentCount / row.capacity) * 100)"
              :stroke-width="6"
              :color="row.currentCount >= row.capacity ? '#ef4444' : '#2563eb'"
            />
          </template>
        </el-table-column>
        <!-- Status column with colored tag -->
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
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
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="楼栋" prop="buildingId">
          <el-select v-model="form.buildingId" placeholder="请选择">
            <el-option v-for="b in buildingList" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号" prop="roomNumber">
          <el-input v-model="form.roomNumber" placeholder="请输入房间号" />
        </el-form-item>
        <el-form-item label="楼层" prop="floorId">
          <el-input-number v-model="form.floorId" :min="1" :max="30" />
        </el-form-item>
        <el-form-item label="容纳人数" prop="capacity">
          <el-input-number v-model="form.capacity" :min="1" :max="12" />
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
import { getRoomList, addRoom, updateRoom, deleteRoom } from '@/api/room'
import { listAllBuildings } from '@/api/building'
import Pagination from '@/components/Pagination.vue'

// Loading state, table data and total count
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// Dialog visibility, title and form reference
const dialogVisible = ref(false)
const dialogTitle = ref('新增房间')
const formRef = ref(null)

// Building options for the dropdowns
const buildingList = ref([])

// Search form and pagination state
const searchForm = reactive({ buildingId: '', roomNumber: '', status: '' })
const pagination = reactive({ page: 1, pageSize: 10 })

// Add/edit form model
const form = reactive({
  id: null, buildingId: '', floorId: '', roomNumber: '', capacity: 4, status: 1
})

// Validation rules for the add/edit form
const formRules = {
  buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
  floorId: [{ required: true, message: '请输入楼层', trigger: 'blur' }],
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入容纳人数', trigger: 'blur' }]
}

// Fetch buildings and rooms on mount
onMounted(() => { fetchBuildings(); fetchData() })

// Fetch all buildings for the dropdown options
async function fetchBuildings() {
  const res = await listAllBuildings()
  buildingList.value = res.data
}

// Fetch the paginated room list from the backend
async function fetchData() {
  loading.value = true
  try {
    const res = await getRoomList({ ...searchForm, pageNum: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

// Run a search (reset to the first page)
function handleSearch() { pagination.page = 1; fetchData() }

// Reset the search form and re-query
function handleReset() { Object.assign(searchForm, { buildingId: '', roomNumber: '', status: '' }); handleSearch() }

// Open the dialog in "add" mode
function handleAdd() {
  dialogTitle.value = '新增房间'
  Object.assign(form, { id: null, buildingId: '', floorId: '', roomNumber: '', capacity: 4, status: 1 })
  dialogVisible.value = true
}

// Open the dialog in "edit" mode with the selected row
function handleEdit(row) {
  dialogTitle.value = '编辑房间'
  Object.assign(form, row)
  dialogVisible.value = true
}

// Submit the add/edit form
async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  if (form.id) { await updateRoom(form); ElMessage.success('修改成功') }
  else { await addRoom(form); ElMessage.success('新增成功') }
  dialogVisible.value = false
  fetchData()
}

// Delete a room after confirmation
function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除 ${row.buildingName} ${row.roomNumber} 吗？`, '提示', { type: 'warning' })
    .then(async () => { await deleteRoom([row.id]); ElMessage.success('删除成功'); fetchData() })
}
</script>