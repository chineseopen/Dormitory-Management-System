<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<template>
  <div class="page-container">
    <div class="action-bar">
      <el-input v-model="searchKey" placeholder="搜索权限名称" clearable style="width: 240px" />
      <el-button type="primary" @click="openDialog()">新增权限</el-button>
    </div>

    <div class="table-wrapper">
      <el-table :data="filteredTreeData" row-key="id" :tree-props="{ children: 'children' }" stripe default-expand-all v-loading="loading">
        <el-table-column prop="name" label="权限名称" min-width="200" align="center" />
        <el-table-column prop="code" label="权限标识" width="180" align="center" />
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? '' : 'success'" size="small">{{ row.type === 1 ? '菜单' : '按钮' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <el-button v-if="row.type === 1" text type="primary" size="small" @click="openDialog(null, row)">新增子权限</el-button>
            <el-button text type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-button text type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级权限">
          <el-tree-select v-model="form.parentId" :data="parentTreeOptions" :props="{ label: 'name', value: 'id', children: 'children' }" placeholder="顶级权限" clearable check-strictly style="width: 100%" />
        </el-form-item>
        <el-form-item label="权限名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="权限标识" prop="code">
          <el-input v-model="form.code" placeholder="如 building:add" />
        </el-form-item>
        <el-form-item label="权限类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :value="1">菜单</el-radio>
            <el-radio :value="2">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" style="width: 100%" />
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
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPermissionTree, addPermission, updatePermission, deletePermission } from '@/api/system'

const loading = ref(false)
const searchKey = ref('')
const treeData = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await getPermissionTree()
    treeData.value = res.data
  } finally { loading.value = false }
}

const filterTree = (data, key) => {
  if (!key) return data
  return data.reduce((acc, node) => {
    const match = node.name.includes(key) || node.code.includes(key)
    const children = node.children ? filterTree(node.children, key) : []
    if (match || children.length) acc.push({ ...node, children })
    return acc
  }, [])
}

const filteredTreeData = computed(() => filterTree(treeData.value, searchKey.value))

const parentTreeOptions = computed(() => {
  const filter = (data) => data.reduce((acc, node) => {
    if (node.type === 1) {
      const item = { id: node.id, name: node.name, children: node.children ? filter(node.children) : [] }
      if (!item.children.length) delete item.children
      acc.push(item)
    }
    return acc
  }, [])
  return filter(treeData.value)
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const form = reactive({ id: undefined, parentId: 0, name: '', code: '', type: 1, sort: 0, status: 1 })
const rules = {
  name: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入权限标识', trigger: 'blur' }],
  type: [{ required: true, message: '请选择权限类型', trigger: 'change' }]
}
const dialogTitle = computed(() => isEdit.value ? '编辑权限' : '新增权限')

const openDialog = (row, parent) => {
  isEdit.value = !!row
  if (row) { Object.assign(form, row) }
  else { Object.assign(form, { id: undefined, parentId: parent?.id || 0, name: '', code: '', type: 1, sort: 0, status: 1 }) }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (isEdit.value) { await updatePermission(form); ElMessage.success('编辑成功') }
  else { await addPermission(form); ElMessage.success('新增成功') }
  dialogVisible.value = false; loadData()
}

const handleDelete = (row) => {
  if (row.children && row.children.length) { ElMessage.warning('请先删除子权限'); return }
  ElMessageBox.confirm(`确定删除权限"${row.name}"吗？`, '提示', { type: 'warning' }).then(async () => {
    await deletePermission([row.id]); ElMessage.success('删除成功'); loadData()
  }).catch(() => {})
}

onMounted(() => loadData())
</script>

<style scoped>
.page-container { padding: 20px; }
.action-bar { display: flex; justify-content: space-between; align-items: center; background: #fff; padding: 20px; border-radius: 12px; margin-bottom: 16px; }
.table-wrapper { background: #fff; padding: 20px; border-radius: 12px; }
</style>
