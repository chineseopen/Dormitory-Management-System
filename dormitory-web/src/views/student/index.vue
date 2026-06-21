<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Accommodation (student) management page for the admin portal.
  Provides a searchable, paginated table of students with contextual actions
  (check-in, change room, check-out, edit, delete) and three dialogs:
  check-in registration, room change, and student create/edit.
-->
<template>
  <div class="page-container">
    <!-- Page header with add button -->
    <div class="page-header">
      <h2 class="page-title">住宿管理</h2>
      <el-button type="primary" @click="openStudentDialog()">
        <el-icon><Plus /></el-icon>新增学生
      </el-button>
    </div>

    <!-- Search bar -->
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
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
            <el-option label="未入住" :value="0" />
            <el-option label="在住" :value="1" />
            <el-option label="退宿" :value="2" />
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
        <el-table-column prop="name" label="姓名" width="80" align="center" />
        <el-table-column prop="studentNo" label="学号" width="120" align="center" />
        <el-table-column prop="genderDesc" label="性别" width="60" align="center" />
        <el-table-column prop="college" label="学院" min-width="120" show-overflow-tooltip align="center" />
        <el-table-column prop="buildingName" label="楼栋" width="100" align="center" />
        <el-table-column prop="roomNumber" label="房间号" width="80" align="center" />
        <el-table-column prop="phone" label="联系电话" width="120" align="center" />
        <!-- Status column with colored tag -->
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ row.statusDesc }}</el-tag>
          </template>
        </el-table-column>
        <!-- Row action buttons (contextual based on status) -->
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              link type="primary" size="small"
              @click="openCheckInDialog(row)"
            >入住</el-button>
            <el-button
              v-if="row.status === 1"
              link type="warning" size="small"
              @click="openChangeRoomDialog(row)"
            >调宿</el-button>
            <el-button
              v-if="row.status === 1"
              link type="danger" size="small"
              @click="handleCheckOut(row)"
            >退宿</el-button>
            <el-button
              link type="primary" size="small"
              @click="openStudentDialog(row)"
            >编辑</el-button>
            <el-button
              v-if="row.status !== 1"
              link type="danger" size="small"
              @click="handleDeleteStudent(row)"
            >删除</el-button>
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

    <!-- Check-in registration dialog -->
    <el-dialog v-model="checkInDialogVisible" title="入住登记" width="520px">
      <el-form ref="checkInFormRef" :model="checkInForm" :rules="checkInRules" label-width="80px">
        <el-form-item label="学生">
          {{ currentRow?.name }} ({{ currentRow?.studentNo }})
        </el-form-item>
        <el-form-item label="楼栋" prop="buildingId">
          <el-select v-model="checkInForm.buildingId" placeholder="请选择楼栋" @change="onBuildingChange">
            <el-option v-for="b in buildingList" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间" prop="roomId">
          <el-select v-model="checkInForm.roomId" placeholder="请选择房间" @change="onRoomChange">
            <el-option v-for="r in roomList" :key="r.id" :label="r.roomNumber" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="床位" prop="bedId">
          <el-select v-model="checkInForm.bedId" placeholder="请选择床位">
            <el-option v-for="b in bedList" :key="b.id" :label="b.bedNumber" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="checkInForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkInDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCheckIn">确定</el-button>
      </template>
    </el-dialog>

    <!-- Room change dialog -->
    <el-dialog v-model="changeRoomDialogVisible" title="调换宿舍" width="520px">
      <el-form ref="changeRoomFormRef" :model="changeRoomForm" :rules="changeRoomRules" label-width="80px">
        <el-form-item label="学生">
          {{ currentRow?.name }} ({{ currentRow?.studentNo }})
        </el-form-item>
        <el-form-item label="原宿舍">
          {{ currentRow?.buildingName }} {{ currentRow?.roomNumber }}室
        </el-form-item>
        <el-form-item label="新楼栋" prop="newBuildingId">
          <el-select v-model="changeRoomForm.newBuildingId" placeholder="请选择楼栋" @change="onChangeBuildingChange">
            <el-option v-for="b in buildingList" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="新房间" prop="newRoomId">
          <el-select v-model="changeRoomForm.newRoomId" placeholder="请选择房间" @change="onChangeRoomChange">
            <el-option v-for="r in newRoomList" :key="r.id" :label="r.roomNumber" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="新床位" prop="newBedId">
          <el-select v-model="changeRoomForm.newBedId" placeholder="请选择床位">
            <el-option v-for="b in newBedList" :key="b.id" :label="b.bedNumber" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="调宿原因" prop="reason">
          <el-input v-model="changeRoomForm.reason" type="textarea" :rows="2" placeholder="请输入调宿原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="changeRoomDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitChangeRoom">确定</el-button>
      </template>
    </el-dialog>

    <!-- Add/Edit student dialog -->
    <el-dialog v-model="studentDialogVisible" :title="studentIsEdit ? '编辑学生' : '新增学生'" width="560px" destroy-on-close>
      <el-form ref="studentFormRef" :model="studentForm" :rules="studentRules" label-width="90px">
        <el-form-item label="学号" prop="studentNo">
          <el-input v-model="studentForm.studentNo" :disabled="studentIsEdit" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="studentForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="studentForm.gender">
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="学院">
          <el-input v-model="studentForm.college" placeholder="请输入学院" />
        </el-form-item>
        <el-form-item label="专业">
          <el-input v-model="studentForm.major" placeholder="请输入专业" />
        </el-form-item>
        <el-form-item label="班级">
          <el-input v-model="studentForm.className" placeholder="请输入班级" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="studentForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="studentForm.email" placeholder="请输入邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="studentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStudent">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStudentList, checkIn, checkOut, changeRoom, addStudent, updateStudent, deleteStudent } from '@/api/student'
import { listAllBuildings } from '@/api/building'
import { getRoomList, getBedsByRoomId } from '@/api/room'
import Pagination from '@/components/Pagination.vue'

// Loading state, table data and total count
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// Dropdown option lists and dependent room/bed lists
const buildingList = ref([])
const roomList = ref([])
const bedList = ref([])
const newRoomList = ref([])
const newBedList = ref([])

// Currently selected row (shared by dialogs)
const currentRow = ref(null)

// Dialog visibility flags and form references
const checkInDialogVisible = ref(false)
const changeRoomDialogVisible = ref(false)
const checkInFormRef = ref(null)
const changeRoomFormRef = ref(null)

// Search form and pagination state
const searchForm = reactive({ name: '', studentNo: '', buildingId: '', status: '' })
const pagination = reactive({ page: 1, pageSize: 10 })

// Check-in form model and validation rules
const checkInForm = reactive({
  studentId: null,
  buildingId: '',
  roomId: '',
  bedId: '',
  remark: ''
})
const checkInRules = {
  buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
  roomId: [{ required: true, message: '请选择房间', trigger: 'change' }],
  bedId: [{ required: true, message: '请选择床位', trigger: 'change' }]
}

// Room change form model and validation rules
const changeRoomForm = reactive({
  studentId: null,
  newBuildingId: '',
  newRoomId: '',
  newBedId: '',
  reason: ''
})
const changeRoomRules = {
  newBuildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
  newRoomId: [{ required: true, message: '请选择房间', trigger: 'change' }],
  newBedId: [{ required: true, message: '请选择床位', trigger: 'change' }],
  reason: [{ required: true, message: '请输入调宿原因', trigger: 'blur' }]
}

// Fetch buildings and students on mount
onMounted(() => {
  fetchBuildings()
  fetchData()
})

// Fetch all buildings for the dropdown options
async function fetchBuildings() {
  const res = await listAllBuildings()
  buildingList.value = res.data
}

// Fetch the paginated student list from the backend
async function fetchData() {
  loading.value = true
  try {
    const res = await getStudentList({ ...searchForm, pageNum: pagination.page, pageSize: pagination.pageSize })
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
  Object.assign(searchForm, { name: '', studentNo: '', buildingId: '', status: '' })
  handleSearch()
}

// Map a numeric status to an Element Plus tag type
function getStatusType(status) {
  switch (status) {
    case 0: return 'info'
    case 1: return 'success'
    case 2: return 'warning'
    case 3: return 'danger'
    default: return 'info'
  }
}

// ===== Check-in registration =====

// Open the check-in dialog for a not-yet-checked-in student
async function openCheckInDialog(row) {
  currentRow.value = row
  Object.assign(checkInForm, { studentId: row.id, buildingId: '', roomId: '', bedId: '', remark: '' })
  roomList.value = []
  bedList.value = []
  checkInDialogVisible.value = true
}

// When the building changes, reload the room list and reset dependent fields
async function onBuildingChange() {
  checkInForm.roomId = ''
  checkInForm.bedId = ''
  bedList.value = []
  if (checkInForm.buildingId) {
    const res = await getRoomList({ buildingId: checkInForm.buildingId, pageSize: 100 })
    roomList.value = res.data.records || []
  } else {
    roomList.value = []
  }
}

// When the room changes, reload the bed list and reset the selected bed
async function onRoomChange() {
  checkInForm.bedId = ''
  if (checkInForm.roomId) {
    const res = await getBedsByRoomId(checkInForm.roomId)
    bedList.value = res.data || []
  } else {
    bedList.value = []
  }
}

// Submit the check-in form
async function submitCheckIn() {
  const valid = await checkInFormRef.value?.validate().catch(() => false)
  if (!valid) return
  await checkIn(checkInForm)
  ElMessage.success('入住登记成功')
  checkInDialogVisible.value = false
  fetchData()
}

// ===== Check-out =====

// Check out a student after confirmation
function handleCheckOut(row) {
  ElMessageBox.confirm(`确定要为 ${row.name} 办理退宿吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await checkOut(row.id)
      ElMessage.success('退宿成功')
      fetchData()
    })
    .catch(() => {})
}

// ===== Room change =====

// Open the room change dialog for a checked-in student
async function openChangeRoomDialog(row) {
  currentRow.value = row
  Object.assign(changeRoomForm, { studentId: row.id, newBuildingId: '', newRoomId: '', newBedId: '', reason: '' })
  newRoomList.value = []
  newBedList.value = []
  changeRoomDialogVisible.value = true
}

// When the new building changes, reload the new room list
async function onChangeBuildingChange() {
  changeRoomForm.newRoomId = ''
  changeRoomForm.newBedId = ''
  newBedList.value = []
  if (changeRoomForm.newBuildingId) {
    const res = await getRoomList({ buildingId: changeRoomForm.newBuildingId, pageSize: 100 })
    newRoomList.value = res.data.records || []
  } else {
    newRoomList.value = []
  }
}

// When the new room changes, reload the new bed list
async function onChangeRoomChange() {
  changeRoomForm.newBedId = ''
  if (changeRoomForm.newRoomId) {
    const res = await getBedsByRoomId(changeRoomForm.newRoomId)
    newBedList.value = res.data || []
  } else {
    newBedList.value = []
  }
}

// Submit the room change form
async function submitChangeRoom() {
  const valid = await changeRoomFormRef.value?.validate().catch(() => false)
  if (!valid) return
  await changeRoom(changeRoomForm)
  ElMessage.success('调宿成功')
  changeRoomDialogVisible.value = false
  fetchData()
}

// ===== Add/Edit student =====

const studentDialogVisible = ref(false)
const studentIsEdit = ref(false)
const studentFormRef = ref(null)

// Student add/edit form model
const studentForm = reactive({
  id: undefined,
  studentNo: '',
  name: '',
  gender: 1,
  college: '',
  major: '',
  className: '',
  phone: '',
  email: ''
})
const studentRules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }]
}

// Open the student dialog in add or edit mode
function openStudentDialog(row) {
  studentIsEdit.value = !!row
  if (row) {
    Object.assign(studentForm, {
      id: row.id,
      studentNo: row.studentNo,
      name: row.name,
      gender: row.gender,
      college: row.college || '',
      major: row.major || '',
      className: row.className || '',
      phone: row.phone || '',
      email: row.email || ''
    })
  } else {
    Object.assign(studentForm, {
      id: undefined,
      studentNo: '',
      name: '',
      gender: 1,
      college: '',
      major: '',
      className: '',
      phone: '',
      email: ''
    })
  }
  studentDialogVisible.value = true
}

// Submit the student add/edit form
async function submitStudent() {
  const valid = await studentFormRef.value?.validate().catch(() => false)
  if (!valid) return
  if (studentIsEdit.value) {
    await updateStudent(studentForm)
    ElMessage.success('编辑成功')
  } else {
    await addStudent(studentForm)
    ElMessage.success('新增成功')
  }
  studentDialogVisible.value = false
  fetchData()
}

// Delete a student after confirmation
function handleDeleteStudent(row) {
  ElMessageBox.confirm(`确定删除学生"${row.name}"吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteStudent([row.id])
      ElMessage.success('删除成功')
      fetchData()
    })
    .catch(() => {})
}
</script>