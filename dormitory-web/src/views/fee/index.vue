<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Utility fee (water/electricity) management page for the admin portal.
  Displays summary statistics, a searchable paginated table of fee records
  with meter readings and payment status, a "pay" action for unpaid records,
  and a dialog to record new fee entries.
-->
<template>
  <div class="page-container">
    <!-- Page header with add button -->
    <div class="page-header">
      <h2 class="page-title">水电费管理</h2>
      <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>录入水电费</el-button>
    </div>

    <!-- Summary statistics cards -->
    <el-row :gutter="16" style="margin-bottom: 16px">
      <el-col :span="6"><div class="stat-card"><span class="stat-label">总费用</span><span class="stat-value">¥{{ stats.totalFee }}</span></div></el-col>
      <el-col :span="6"><div class="stat-card"><span class="stat-label">已缴费</span><span class="stat-value" style="color: #10b981">¥{{ stats.paidFee }}</span></div></el-col>
      <el-col :span="6"><div class="stat-card"><span class="stat-label">未缴费</span><span class="stat-value" style="color: #ef4444">¥{{ stats.unpaidFee }}</span></div></el-col>
      <el-col :span="6"><div class="stat-card"><span class="stat-label">未缴户数</span><span class="stat-value" style="color: #14b8a6">{{ stats.unpaidCount }}</span></div></el-col>
    </el-row>

    <!-- Search bar -->
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="楼栋">
          <el-select v-model="searchForm.buildingId" placeholder="请选择" clearable>
            <el-option v-for="b in buildingList" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="月份"><el-input v-model="searchForm.month" placeholder="如 2024-10" clearable /></el-form-item>
        <el-form-item label="缴费状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="未缴费" :value="1" /><el-option label="已缴费" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="费用类型">
          <el-select v-model="searchForm.feeType" placeholder="请选择" clearable>
            <el-option label="水费" :value="1" /><el-option label="电费" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="handleSearch">查询</el-button><el-button @click="handleReset">重置</el-button></el-form-item>
      </el-form>
    </div>

    <!-- Data table -->
    <div class="page-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="buildingName" label="楼栋" width="80" align="center" />
        <el-table-column prop="roomNumber" label="房间" width="80" align="center" />
        <!-- Fee type column -->
        <el-table-column prop="feeType" label="费用类型" width="90" align="center">
          <template #default="{ row }">{{ row.feeType === 1 ? '水费' : '电费' }}</template>
        </el-table-column>
        <el-table-column prop="month" label="月份" width="100" align="center" />
        <el-table-column prop="previousReading" label="上期读数" width="100" align="center" />
        <el-table-column prop="currentReading" label="本期读数" width="100" align="center" />
        <el-table-column prop="amount" label="金额(元)" width="100" align="center" />
        <!-- Payment status column -->
        <el-table-column prop="status" label="缴费状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 2 ? 'success' : 'danger'" size="small">{{ row.status === 2 ? '已缴费' : '未缴费' }}</el-tag>
          </template>
        </el-table-column>
        <!-- Row action buttons -->
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" link type="primary" size="small" @click="handlePay(row)">缴费</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- Pagination -->
      <Pagination :total="total" v-model:page="pagination.page" v-model:page-size="pagination.pageSize" @change="fetchData" />
    </div>

    <!-- Add fee record dialog -->
    <el-dialog v-model="dialogVisible" title="录入水电费" width="500px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="90px">
        <el-form-item label="楼栋" prop="buildingId">
          <el-select v-model="form.buildingId">
            <el-option v-for="b in buildingList" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号" prop="roomId"><el-input v-model="form.roomId" placeholder="请输入房间ID" /></el-form-item>
        <el-form-item label="费用类型" prop="feeType">
          <el-select v-model="form.feeType">
            <el-option label="水费" :value="1" /><el-option label="电费" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="月份" prop="month"><el-input v-model="form.month" placeholder="如 2024-10" /></el-form-item>
        <el-form-item label="上期读数" prop="previousReading"><el-input-number v-model="form.previousReading" :precision="1" :min="0" /></el-form-item>
        <el-form-item label="本期读数" prop="currentReading"><el-input-number v-model="form.currentReading" :precision="1" :min="0" /></el-form-item>
        <el-form-item label="金额" prop="amount"><el-input-number v-model="form.amount" :precision="2" :min="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getFeeList, addFee, payFee, deleteFee } from '@/api/fee'
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
const searchForm = reactive({ buildingId: '', month: '', status: '', feeType: '' })
const pagination = reactive({ page: 1, pageSize: 10 })

// Summary statistics computed from the current page of records
const stats = reactive({ totalFee: 0, paidFee: 0, unpaidFee: 0, unpaidCount: 0 })

// Add fee record form model
const form = reactive({ buildingId: '', roomId: '', feeType: 1, month: '', previousReading: 0, currentReading: 0, amount: 0 })
const formRules = {
  buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
  roomId: [{ required: true, message: '请输入房间ID', trigger: 'blur' }],
  feeType: [{ required: true, message: '请选择费用类型', trigger: 'change' }],
  month: [{ required: true, message: '请输入月份', trigger: 'blur' }]
}

// Fetch buildings and fee records on mount
onMounted(() => { fetchBuildings(); fetchData() })

// Fetch all buildings for the dropdown options
async function fetchBuildings() {
  const res = await listAllBuildings()
  buildingList.value = res.data
}

// Compute summary statistics from the loaded records
function calcStats(data) {
  let totalFee = 0, paidFee = 0, unpaidFee = 0, unpaidCount = 0
  data.forEach(item => {
    const amt = Number(item.amount) || 0
    totalFee += amt
    if (item.status === 2) { paidFee += amt }
    else { unpaidFee += amt; unpaidCount++ }
  })
  Object.assign(stats, { totalFee: totalFee.toFixed(2), paidFee: paidFee.toFixed(2), unpaidFee: unpaidFee.toFixed(2), unpaidCount })
}

// Fetch the paginated fee records from the backend
async function fetchData() {
  loading.value = true
  try {
    const res = await getFeeList({ ...searchForm, pageNum: pagination.page, pageSize: pagination.pageSize })
    tableData.value = res.data.records
    total.value = res.data.total
    calcStats(res.data.records)
  } finally { loading.value = false }
}

// Run a search (reset to the first page)
function handleSearch() { pagination.page = 1; fetchData() }

// Reset the search form and re-query
function handleReset() { Object.assign(searchForm, { buildingId: '', month: '', status: '', feeType: '' }); handleSearch() }

// Open the add fee record dialog
function handleAdd() { dialogVisible.value = true }

// Submit the add fee record form
async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  await addFee(form); ElMessage.success('录入成功'); dialogVisible.value = false; fetchData()
}

// Mark a fee record as paid
async function handlePay(row) {
  await payFee(row.id); ElMessage.success('缴费成功'); fetchData()
}
</script>

<style lang="scss" scoped>
/* Summary statistics card */
.stat-card {
  background: $color-bg-card;
  border-radius: $border-radius-md;
  padding: $spacing-4 $spacing-5;
  display: flex;
  flex-direction: column;
  gap: $spacing-1;
}
.stat-label { font-size: $font-size-sm; color: $color-text-secondary; }
.stat-value { font-size: $font-size-xl; font-weight: $font-weight-bold; color: $color-primary-light; }
</style>