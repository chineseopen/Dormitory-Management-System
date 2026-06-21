<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Visitor management page for the admin portal.
  Provides a searchable, paginated table of visitor records with visit/leave
  times and status tags, plus a registration dialog and actions to record a
  visitor's departure or delete a record.
-->
<template>
  <div class="page-container">
    <!-- Page header with register button -->
    <div class="page-header">
      <h2 class="page-title">访客管理</h2>
      <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>访客登记</el-button>
    </div>

    <!-- Search bar -->
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="访客姓名"><el-input v-model="searchForm.visitorName" placeholder="请输入" clearable /></el-form-item>
        <el-form-item label="楼栋">
          <el-select v-model="searchForm.buildingId" placeholder="请选择" clearable>
            <el-option v-for="b in buildingList" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="handleSearch">查询</el-button><el-button @click="handleReset">重置</el-button></el-form-item>
      </el-form>
    </div>

    <!-- Data table -->
    <div class="page-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="visitorName" label="访客姓名" width="100" align="center" />
        <el-table-column prop="visitorPhone" label="联系电话" width="120" align="center" />
        <el-table-column prop="visitorIdCard" label="身份证号" width="170" align="center" />
        <el-table-column prop="targetStudentName" label="被访学生" width="100" align="center" />
        <el-table-column prop="buildingName" label="楼栋" width="80" align="center" />
        <el-table-column prop="reason" label="来访原因" min-width="120" align="center" />
        <el-table-column prop="visitTime" label="来访时间" width="160" align="center" />
        <el-table-column prop="leaveTime" label="离开时间" width="160" align="center" />
        <!-- Status column with colored tag -->
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'warning' : 'success'" size="small">{{ row.status === 1 ? '在访' : '已离开' }}</el-tag>
          </template>
        </el-table-column>
        <!-- Row action buttons -->
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" link type="primary" size="small" @click="handleLeave(row)">离开登记</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- Pagination -->
      <Pagination :total="total" v-model:page="pagination.page" v-model:page-size="pagination.pageSize" @change="fetchData" />
    </div>

    <!-- Visitor registration dialog -->
    <el-dialog v-model="dialogVisible" title="访客登记" width="500px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="访客姓名" prop="visitorName"><el-input v-model="form.visitorName" /></el-form-item>
        <el-form-item label="联系电话" prop="visitorPhone"><el-input v-model="form.visitorPhone" /></el-form-item>
        <el-form-item label="身份证号" prop="visitorIdCard"><el-input v-model="form.visitorIdCard" /></el-form-item>
        <el-form-item label="被访学生" prop="targetStudentId"><el-input v-model="form.targetStudentId" placeholder="请输入学生ID" /></el-form-item>
        <el-form-item label="楼栋" prop="buildingId">
          <el-select v-model="form.buildingId">
            <el-option v-for="b in buildingList" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="来访原因" prop="reason"><el-input v-model="form.reason" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getVisitorList, registerVisitor, visitorLeave, deleteVisitor } from '@/api/visitor'
import { listAllBuildings } from '@/api/building'
import Pagination from '@/components/Pagination.vue'

// Loading state, table data and total count
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// Dialog visibility and form reference
const dialogVisible = ref(false)
const formRef = ref(null)

// Building options for the dropdowns
const buildingList = ref([])

// Search form and pagination state
const searchForm = reactive({ visitorName: '', buildingId: '' })
const pagination = reactive({ page: 1, pageSize: 10 })

// Registration form model and validation rules
const form = reactive({ visitorName: '', visitorPhone: '', visitorIdCard: '', targetStudentId: '', buildingId: '', reason: '' })
const formRules = {
  visitorName: [{ required: true, message: '请输入访客姓名', trigger: 'blur' }],
  visitorPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入来访原因', trigger: 'blur' }]
}

// Fetch buildings and visitors on mount
onMounted(() => { fetchBuildings(); fetchData() })

// Fetch all buildings for the dropdown options
async function fetchBuildings() {
  const res = await listAllBuildings()
  buildingList.value = res.data
}

// Fetch the paginated visitor list from the backend
async function fetchData() {
  loading.value = true
  try { const res = await getVisitorList({ ...searchForm, pageNum: pagination.page, pageSize: pagination.pageSize }); tableData.value = res.data.records; total.value = res.data.total }
  finally { loading.value = false }
}

// Run a search (reset to the first page)
function handleSearch() { pagination.page = 1; fetchData() }

// Reset the search form and re-query
function handleReset() { Object.assign(searchForm, { visitorName: '', buildingId: '' }); handleSearch() }

// Open the registration dialog
function handleAdd() { dialogVisible.value = true }

// Submit the registration form
async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  await registerVisitor(form); ElMessage.success('登记成功'); dialogVisible.value = false; fetchData()
}

// Record a visitor's departure
async function handleLeave(row) {
  await visitorLeave(row.id); ElMessage.success('离开登记成功'); fetchData()
}

// Delete a visitor record after confirmation
function handleDelete(row) {
  ElMessageBox.confirm('确定要删除该访客记录吗？', '提示', { type: 'warning' })
    .then(async () => { await deleteVisitor([row.id]); ElMessage.success('删除成功'); fetchData() })
}
</script>