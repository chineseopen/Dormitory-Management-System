<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Student portal utility fee page.
  Lets the student browse their own utility fee records filtered by month,
  showing meter readings, amounts and payment status.
-->
<template>
  <div class="page-container">
    <!-- Page header -->
    <div class="page-header">
      <h2 class="page-title">水电费查询</h2>
    </div>

    <!-- Fee records card with month filter and data table -->
    <el-card shadow="never">
      <!-- Month filter dropdown -->
      <div class="filter-bar">
        <el-select v-model="month" placeholder="选择月份" clearable style="width: 160px" @change="loadData">
          <el-option v-for="m in monthOptions" :key="m" :label="m" :value="m" />
        </el-select>
      </div>

      <!-- Fee records table -->
      <el-table :data="feeList" v-loading="loading" stripe>
        <el-table-column prop="month" label="账期" width="100" align="center" />
        <el-table-column prop="feeTypeDesc" label="类型" width="80" align="center" />
        <el-table-column prop="previousReading" label="上期读数" width="110" align="center" />
        <el-table-column prop="currentReading" label="本期读数" width="110" align="center" />
        <el-table-column prop="amount" label="金额(元)" width="100" align="center">
          <template #default="{ row }">
            <span style="font-weight: 600; color: #ef4444">{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="statusDesc" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'warning' : 'success'" size="small">
              {{ row.statusDesc }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payTime" label="缴费时间" min-width="150" align="center">
          <template #default="{ row }">{{ row.payTime || '-' }}</template>
        </el-table-column>
      </el-table>

      <div v-if="feeList.length === 0 && !loading" class="empty-text">暂无水电费记录</div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyFeeList } from '@/api/student-fee'

// Reactive state: loading flag, fee records and selected month filter
const loading = ref(false)
const feeList = ref([])
const month = ref('')

// Generate the last 12 months as selectable options (YYYY-MM format)
const monthOptions = computed(() => {
  const now = new Date()
  const options = []
  for (let i = 0; i < 12; i++) {
    const d = new Date(now.getFullYear(), now.getMonth() - i, 1)
    const y = d.getFullYear()
    const m = String(d.getMonth() + 1).padStart(2, '0')
    options.push(`${y}-${m}`)
  }
  return options
})

// Fetch fee records, optionally filtered by the selected month
async function loadData() {
  loading.value = true
  try {
    const params = {}
    if (month.value) params.month = month.value
    const res = await getMyFeeList(params)
    if (res.data) {
      feeList.value = res.data
    }
  } catch (e) {
    ElMessage.error('加载水电费失败')
  } finally {
    loading.value = false
  }
}

// Initial data load on component mount
onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
/* Filter bar above the table */
.filter-bar {
  margin-bottom: $spacing-4;
}

/* Empty state placeholder */
.empty-text {
  text-align: center;
  color: $color-text-secondary;
  padding: $spacing-8 0;
  font-size: 13px;
}
</style>