<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Student portal repair request page.
  Lists the student's own repair requests in a paginated table and provides
  a dialog to submit a new repair request (title + content).
-->
<template>
  <div class="page-container">
    <!-- Page header with submit button -->
    <div class="page-header">
      <h2 class="page-title">报修管理</h2>
      <el-button type="primary" @click="showAddDialog = true">提交报修</el-button>
    </div>

    <!-- Repair records table -->
    <el-card shadow="never">
      <el-table :data="repairList" v-loading="loading" stripe>
        <el-table-column prop="title" label="报修标题" min-width="150" align="center" />
        <el-table-column prop="buildingName" label="楼栋" width="120" align="center" />
        <el-table-column prop="roomNumber" label="房间" width="100" align="center" />
        <el-table-column prop="statusDesc" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'warning' : row.status === 2 ? 'primary' : 'success'" size="small">
              {{ row.statusDesc }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="result" label="处理结果" min-width="150" align="center">
          <template #default="{ row }">{{ row.result || '-' }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="170" align="center" />
      </el-table>
      <!-- Pagination controls -->
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 16px; justify-content: flex-end"
      />
    </el-card>

    <!-- Submit repair request dialog -->
    <el-dialog v-model="showAddDialog" title="提交报修" width="500px" @close="resetForm">
      <el-form :model="addForm" :rules="addRules" ref="addFormRef" label-width="80px">
        <el-form-item label="报修标题" prop="title">
          <el-input v-model="addForm.title" placeholder="请输入报修标题" />
        </el-form-item>
        <el-form-item label="报修内容" prop="content">
          <el-input v-model="addForm.content" type="textarea" :rows="4" placeholder="请描述报修内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyRepairList, submitRepair } from '@/api/student-repair'

// Reactive state: loading flags, repair list, pagination and dialog visibility
const loading = ref(false)
const submitLoading = ref(false)
const repairList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showAddDialog = ref(false)
const addFormRef = ref(null)

// Add form model and validation rules
const addForm = ref({ title: '', content: '' })

const addRules = {
  title: [{ required: true, message: '请输入报修标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入报修内容', trigger: 'blur' }]
}

// Fetch paginated repair records for the current student
async function loadData() {
  loading.value = true
  try {
    const res = await getMyRepairList({ pageNum: pageNum.value, pageSize: pageSize.value })
    if (res.data) {
      repairList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (e) {
    ElMessage.error('加载报修记录失败')
  } finally {
    loading.value = false
  }
}

// Reset the add form fields and validation state
function resetForm() {
  addForm.value = { title: '', content: '' }
  addFormRef.value?.resetFields()
}

// Validate and submit a new repair request
async function handleSubmit() {
  const valid = await addFormRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    await submitRepair(addForm.value)
    ElMessage.success('报修提交成功')
    showAddDialog.value = false
    resetForm()
    loadData()
  } catch (e) {
    ElMessage.error('报修提交失败')
  } finally {
    submitLoading.value = false
  }
}

// Initial data load on component mount
onMounted(() => {
  loadData()
})
</script>