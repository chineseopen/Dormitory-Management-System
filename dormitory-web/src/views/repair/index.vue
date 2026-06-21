<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Repair request management page for the admin portal.
  Displays a searchable, paginated table of repair requests with urgency and
  status tags, plus contextual actions to start processing, mark as completed,
  or delete a request.
-->
<template>
  <div class="page-container">
    <!-- Page header -->
    <div class="page-header">
      <h2 class="page-title">报修管理</h2>
    </div>

    <!-- Search bar -->
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="楼栋"><el-input v-model="searchForm.buildingName" placeholder="请输入" clearable /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="待处理" :value="1" /><el-option label="处理中" :value="2" /><el-option label="已完成" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="handleSearch">查询</el-button><el-button @click="handleReset">重置</el-button></el-form-item>
      </el-form>
    </div>

    <!-- Data table -->
    <div class="page-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="title" label="报修标题" min-width="150" align="center" />
        <el-table-column prop="type" label="类型" width="80" align="center" />
        <el-table-column prop="buildingName" label="楼栋" width="80" align="center" />
        <el-table-column prop="roomNumber" label="房间" width="80" align="center" />
        <el-table-column prop="reporter" label="报修人" width="80" align="center" />
        <!-- Urgency column with colored tag -->
        <el-table-column prop="urgency" label="紧急程度" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.urgency === 3 ? 'danger' : row.urgency === 2 ? 'warning' : 'info'" size="small">
              {{ row.urgency === 3 ? '紧急' : row.urgency === 2 ? '一般' : '低' }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- Status column with colored tag -->
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'danger' : row.status === 2 ? 'warning' : 'success'" size="small">
              {{ row.status === 1 ? '待处理' : row.status === 2 ? '处理中' : '已完成' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="报修时间" width="160" align="center" />
        <!-- Row action buttons (contextual based on status) -->
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" link type="primary" size="small" @click="handleProcess(row)">处理</el-button>
            <el-button v-if="row.status === 2" link type="success" size="small" @click="handleComplete(row)">完成</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- Pagination -->
      <Pagination :total="total" v-model:page="pagination.page" v-model:page-size="pagination.pageSize" @change="fetchData" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRepairList, handleRepair, deleteRepair } from '@/api/repair'
import Pagination from '@/components/Pagination.vue'

// Loading state, table data and total count
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// Search form and pagination state
const searchForm = reactive({ buildingName: '', status: '' })
const pagination = reactive({ page: 1, pageSize: 10 })

// Fetch the list on mount
onMounted(() => fetchData())

// Fetch the paginated repair list from the backend
async function fetchData() {
  loading.value = true
  try { const res = await getRepairList({ ...searchForm, pageNum: pagination.page, pageSize: pagination.pageSize }); tableData.value = res.data.records; total.value = res.data.total }
  finally { loading.value = false }
}

// Run a search (reset to the first page)
function handleSearch() { pagination.page = 1; fetchData() }

// Reset the search form and re-query
function handleReset() { Object.assign(searchForm, { buildingName: '', status: '' }); handleSearch() }

// Start processing a pending repair request
async function handleProcess(row) {
  await handleRepair({ repairId: row.id, status: 2 }); ElMessage.success('已开始处理'); fetchData()
}

// Mark an in-progress repair request as completed
async function handleComplete(row) {
  await handleRepair({ repairId: row.id, status: 3 }); ElMessage.success('已标记为完成'); fetchData()
}

// Delete a repair request after confirmation
function handleDelete(row) {
  ElMessageBox.confirm('确定要删除该报修记录吗？', '提示', { type: 'warning' })
    .then(async () => { await deleteRepair([row.id]); ElMessage.success('删除成功'); fetchData() })
}
</script>