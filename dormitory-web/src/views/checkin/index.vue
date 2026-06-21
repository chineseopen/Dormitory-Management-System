<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Check-in records page for the admin portal.
  Displays a searchable, paginated, read-only table of student check-in
  history (building, room, bed, check-in/check-out dates and status).
-->
<template>
  <div class="page-container">
    <!-- Page header -->
    <div class="page-header">
      <h2 class="page-title">入住记录</h2>
    </div>

    <!-- Search bar -->
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="姓名">
          <el-input v-model="searchForm.studentName" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="学号">
          <el-input v-model="searchForm.studentNo" placeholder="请输入学号" clearable />
        </el-form-item>
        <el-form-item label="楼栋">
          <el-select v-model="searchForm.buildingId" placeholder="请选择" clearable>
            <el-option v-for="b in buildingList" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="在住" :value="1" />
            <el-option label="已退宿" :value="2" />
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
        <el-table-column prop="studentName" label="姓名" width="80" align="center" />
        <el-table-column prop="studentNo" label="学号" width="120" align="center" />
        <el-table-column prop="buildingName" label="楼栋" width="100" align="center" />
        <el-table-column prop="roomNumber" label="房间号" width="80" align="center" />
        <el-table-column prop="bedNo" label="床位号" width="80" align="center" />
        <el-table-column prop="checkInDate" label="入住日期" width="120" align="center" />
        <el-table-column prop="checkOutDate" label="退宿日期" width="120" align="center" />
        <!-- Status column with colored tag -->
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.statusDesc }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip align="center" />
        <el-table-column prop="createTime" label="创建时间" width="160" align="center" />
      </el-table>

      <!-- Pagination -->
      <Pagination
        :total="total"
        v-model:page="pagination.page"
        v-model:page-size="pagination.pageSize"
        @change="fetchData"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCheckInList } from '@/api/checkin'
import { listAllBuildings } from '@/api/building'
import Pagination from '@/components/Pagination.vue'

// Loading state, table data and total count
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// Building options for the dropdown
const buildingList = ref([])

// Search form and pagination state
const searchForm = reactive({ studentName: '', studentNo: '', buildingId: '', status: '' })
const pagination = reactive({ page: 1, pageSize: 10 })

// Fetch buildings and records on mount
onMounted(() => {
  fetchBuildings()
  fetchData()
})

// Fetch all buildings for the dropdown options
async function fetchBuildings() {
  const res = await listAllBuildings()
  buildingList.value = res.data
}

// Fetch the paginated check-in records from the backend
async function fetchData() {
  loading.value = true
  try {
    const res = await getCheckInList({ ...searchForm, pageNum: pagination.page, pageSize: pagination.pageSize })
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
  Object.assign(searchForm, { studentName: '', studentNo: '', buildingId: '', status: '' })
  handleSearch()
}
</script>