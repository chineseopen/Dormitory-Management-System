<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Room change (transfer) records page for the admin portal.
  Displays a searchable, paginated table of room change applications with
  old/new room details and an audit status tag, plus an audit dialog to
  approve or reject pending applications.
-->
<template>
  <div class="page-container">
    <!-- Page header -->
    <div class="page-header">
      <h2 class="page-title">调宿记录</h2>
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
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="待审核" :value="1" />
            <el-option label="已通过" :value="2" />
            <el-option label="已拒绝" :value="3" />
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
        <!-- Original room composite column -->
        <el-table-column label="原宿舍" min-width="180" align="center">
          <template #default="{ row }">
            {{ row.oldBuildingName }} {{ row.oldRoomNumber }}室 {{ row.oldBedNo }}号
          </template>
        </el-table-column>
        <!-- New room composite column -->
        <el-table-column label="新宿舍" min-width="180" align="center">
          <template #default="{ row }">
            {{ row.newBuildingName }} {{ row.newRoomNumber }}室 {{ row.newBedNo }}号
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="调宿原因" min-width="150" show-overflow-tooltip align="center" />
        <!-- Status column with colored tag -->
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ row.statusDesc }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditUserName" label="审核人" width="80" align="center" />
        <el-table-column prop="auditTime" label="审核时间" width="160" align="center" />
        <el-table-column prop="createTime" label="申请时间" width="160" align="center" />
        <!-- Row action buttons (only for pending applications) -->
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1"
              link type="success" size="small"
              @click="handleAudit(row, 2)"
            >通过</el-button>
            <el-button
              v-if="row.status === 1"
              link type="danger" size="small"
              @click="handleAudit(row, 3)"
            >拒绝</el-button>
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

    <!-- Audit dialog -->
    <el-dialog v-model="auditDialogVisible" :title="auditTitle" width="450px">
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="学生">
          {{ currentRow?.studentName }} ({{ currentRow?.studentNo }})
        </el-form-item>
        <el-form-item label="原宿舍">
          {{ currentRow?.oldBuildingName }} {{ currentRow?.oldRoomNumber }}室 {{ currentRow?.oldBedNo }}号
        </el-form-item>
        <el-form-item label="新宿舍">
          {{ currentRow?.newBuildingName }} {{ currentRow?.newRoomNumber }}室 {{ currentRow?.newBedNo }}号
        </el-form-item>
        <el-form-item label="调宿原因">
          {{ currentRow?.reason }}
        </el-form-item>
        <el-form-item label="审核备注">
          <el-input v-model="auditForm.remark" type="textarea" :rows="3" placeholder="请输入审核备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAudit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getRoomChangeList, auditRoomChange } from '@/api/roomchange'
import Pagination from '@/components/Pagination.vue'

// Loading state, table data and total count
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// Audit dialog visibility, title and current row
const auditDialogVisible = ref(false)
const auditTitle = ref('')
const currentRow = ref(null)

// Search form and pagination state
const searchForm = reactive({ studentName: '', studentNo: '', status: '' })
const pagination = reactive({ page: 1, pageSize: 10 })

// Audit form model (status: 2 = approve, 3 = reject)
const auditForm = reactive({ status: null, remark: '' })

// Fetch the list on mount
onMounted(() => {
  fetchData()
})

// Fetch the paginated room change list from the backend
async function fetchData() {
  loading.value = true
  try {
    const res = await getRoomChangeList({ ...searchForm, pageNum: pagination.page, pageSize: pagination.pageSize })
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
  Object.assign(searchForm, { studentName: '', studentNo: '', status: '' })
  handleSearch()
}

// Map a numeric audit status to an Element Plus tag type
function getStatusType(status) {
  switch (status) {
    case 1: return 'warning'
    case 2: return 'success'
    case 3: return 'danger'
    default: return 'info'
  }
}

// Open the audit dialog for a pending application (approve/reject)
function handleAudit(row, status) {
  currentRow.value = row
  auditForm.status = status
  auditForm.remark = ''
  auditTitle.value = status === 2 ? '通过调宿申请' : '拒绝调宿申请'
  auditDialogVisible.value = true
}

// Submit the audit decision
async function submitAudit() {
  await auditRoomChange(currentRow.value.id, auditForm.status, auditForm.remark)
  ElMessage.success('审核成功')
  auditDialogVisible.value = false
  fetchData()
}
</script>