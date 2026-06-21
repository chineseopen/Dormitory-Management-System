<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<template>
  <div class="page-container">
    <div class="search-bar">
      <el-form :model="query" inline>
        <el-form-item label="用户名">
          <el-input v-model="query.username" placeholder="用户名" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="query.phone" placeholder="手机号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="openDialog()">新增用户</el-button>
    </div>

    <div class="table-wrapper">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="username" label="用户名" width="120" align="center" />
        <el-table-column prop="nickname" label="昵称" width="100" align="center" />
        <el-table-column prop="gender" label="性别" width="70" align="center">
          <template #default="{ row }">{{ row.gender === 1 ? '男' : row.gender === 2 ? '女' : '未知' }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" align="center" />
        <el-table-column prop="email" label="邮箱" min-width="160" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" align="center" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-button text type="warning" size="small" @click="handleResetPwd(row)">重置密码</el-button>
            <el-button text type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="query.page" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" @change="loadData" />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roleIds" multiple placeholder="请选择角色" style="width: 100%">
            <el-option v-for="r in roleOptions" :key="r.id" :label="r.name" :value="r.id" />
          </el-select>
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
import { getUserList, addUser, updateUser, deleteUser, resetPassword, listAllRoles } from '@/api/system'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const roleOptions = ref([])
const query = reactive({ username: '', phone: '', status: undefined, page: 1, pageSize: 10 })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList({ username: query.username, phone: query.phone, status: query.status, pageNum: query.page, pageSize: query.pageSize })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

const handleSearch = () => { query.page = 1; loadData() }
const resetQuery = () => { query.username = ''; query.phone = ''; query.status = undefined; query.page = 1; loadData() }

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const form = reactive({ id: undefined, username: '', nickname: '', password: '', gender: 1, phone: '', email: '', status: 1, roleIds: [] })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const openDialog = (row) => {
  isEdit.value = !!row
  if (row) { Object.assign(form, { ...row, password: '', roleIds: row.roleIds || [] }) }
  else { Object.assign(form, { id: undefined, username: '', nickname: '', password: '', gender: 1, phone: '', email: '', status: 1, roleIds: [] }) }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (isEdit.value) { await updateUser(form); ElMessage.success('编辑成功') }
  else { await addUser(form); ElMessage.success('新增成功') }
  dialogVisible.value = false; loadData()
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除用户"${row.nickname}"吗？`, '提示', { type: 'warning' }).then(async () => {
    await deleteUser([row.id]); ElMessage.success('删除成功'); loadData()
  }).catch(() => {})
}

const handleResetPwd = (row) => {
  ElMessageBox.confirm(`确定重置用户"${row.nickname}"的密码为默认密码吗？`, '提示', { type: 'warning' }).then(async () => {
    await resetPassword(row.id); ElMessage.success('密码已重置')
  }).catch(() => {})
}

const fetchRoles = async () => {
  const res = await listAllRoles()
  roleOptions.value = res.data
}

onMounted(() => { fetchRoles(); loadData() })
</script>

<style scoped>
.page-container { padding: 20px; }
.search-bar { display: flex; justify-content: space-between; align-items: flex-start; background: #fff; padding: 20px; border-radius: 12px; margin-bottom: 16px; }
.table-wrapper { background: #fff; padding: 20px; border-radius: 12px; }
.pagination-wrapper { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
