<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<template>
  <div class="page-container">
    <div class="search-bar">
      <el-form :model="query" inline>
        <el-form-item label="角色名称">
          <el-input v-model="query.name" placeholder="角色名称" clearable />
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
      <el-button type="primary" @click="openDialog()">新增角色</el-button>
    </div>

    <div class="table-wrapper">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="name" label="角色名称" width="140" align="center" />
        <el-table-column prop="code" label="角色编码" width="150" align="center" />
        <el-table-column prop="description" label="描述" min-width="200" align="center" />
        <el-table-column prop="userCount" label="用户数" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" align="center" />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-button text type="primary" size="small" @click="openPermDialog(row)">权限</el-button>
            <el-button text type="danger" size="small" @click="handleDelete(row)" :disabled="row.code === 'admin'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="query.page" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" @change="loadData" />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="form.code" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="permDialogVisible" :title="`权限配置 - ${permForm.roleName}`" width="560px" destroy-on-close>
      <el-tree ref="treeRef" :data="permTree" show-checkbox node-key="id" :default-checked-keys="permForm.checkedKeys" :props="{ label: 'name', children: 'children' }" />
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePermSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, addRole, updateRole, deleteRole, assignPermissions, getPermissionTree } from '@/api/system'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ name: '', status: undefined, page: 1, pageSize: 10 })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRoleList({ name: query.name, status: query.status, pageNum: query.page, pageSize: query.pageSize })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

const handleSearch = () => { query.page = 1; loadData() }
const resetQuery = () => { query.name = ''; query.status = undefined; query.page = 1; loadData() }

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const form = reactive({ id: undefined, name: '', code: '', description: '', status: 1 })
const rules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const openDialog = (row) => {
  isEdit.value = !!row
  if (row) { Object.assign(form, row) }
  else { Object.assign(form, { id: undefined, name: '', code: '', description: '', status: 1 }) }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (isEdit.value) { await updateRole(form); ElMessage.success('编辑成功') }
  else { await addRole(form); ElMessage.success('新增成功') }
  dialogVisible.value = false; loadData()
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除角色"${row.name}"吗？`, '提示', { type: 'warning' }).then(async () => {
    await deleteRole([row.id]); ElMessage.success('删除成功'); loadData()
  }).catch(() => {})
}

// 权限配置
const permDialogVisible = ref(false)
const permForm = reactive({ roleId: null, roleName: '', checkedKeys: [] })
const treeRef = ref(null)
const permTree = ref([])

const openPermDialog = async (row) => {
  Object.assign(permForm, { roleId: row.id, roleName: row.name, checkedKeys: row.permissionIds || [] })
  if (!permTree.value.length) {
    const res = await getPermissionTree()
    permTree.value = res.data
  }
  permDialogVisible.value = true
}

const handlePermSubmit = async () => {
  const checkedKeys = treeRef.value.getCheckedKeys()
  await assignPermissions(permForm.roleId, checkedKeys)
  permDialogVisible.value = false
  ElMessage.success('权限配置成功')
  loadData()
}

onMounted(() => loadData())
</script>

<style scoped>
.page-container { padding: 20px; }
.search-bar { display: flex; justify-content: space-between; align-items: flex-start; background: #fff; padding: 20px; border-radius: 12px; margin-bottom: 16px; }
.table-wrapper { background: #fff; padding: 20px; border-radius: 12px; }
.pagination-wrapper { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
