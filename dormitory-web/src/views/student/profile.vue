<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Student portal profile page.
  Shows the student's avatar, basic information (read-only) and an editable
  contact form (phone/email) that can be saved back to the server.
-->
<template>
  <div class="page-container">
    <!-- Page header -->
    <div class="page-header">
      <h2 class="page-title">个人信息</h2>
    </div>

    <el-row :gutter="16">
      <!-- Left column: avatar and name summary -->
      <el-col :span="8">
        <el-card shadow="never">
          <div class="profile-center">
            <el-avatar :size="80" class="profile-avatar">
              {{ studentInfo.name?.charAt(0) || 'S' }}
            </el-avatar>
            <h3 class="profile-name">{{ studentInfo.name || '-' }}</h3>
            <p class="profile-no">{{ studentInfo.studentNo || '-' }}</p>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <!-- Basic information descriptions (read-only) -->
        <el-card shadow="never">
          <template #header><span class="card-title">基本信息</span></template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="学号">{{ studentInfo.studentNo || '-' }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{ studentInfo.name || '-' }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ studentInfo.gender === 1 ? '男' : '女' }}</el-descriptions-item>
            <el-descriptions-item label="学院">{{ studentInfo.college || '-' }}</el-descriptions-item>
            <el-descriptions-item label="专业">{{ studentInfo.major || '-' }}</el-descriptions-item>
            <el-descriptions-item label="班级">{{ studentInfo.className || '-' }}</el-descriptions-item>
            <el-descriptions-item label="楼栋">{{ studentInfo.buildingName || '未分配' }}</el-descriptions-item>
            <el-descriptions-item label="房间号">{{ studentInfo.roomNumber || '未分配' }}</el-descriptions-item>
            <el-descriptions-item label="入住日期">{{ studentInfo.checkInDate || '-' }}</el-descriptions-item>
            <el-descriptions-item label="状态">{{ studentInfo.status === 1 ? '在住' : '退宿' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- Editable contact information form -->
        <el-card shadow="never" style="margin-top: 16px">
          <template #header><span class="card-title">联系方式</span></template>
          <el-form :model="contactForm" label-width="80px">
            <el-form-item label="手机号">
              <el-input v-model="contactForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="contactForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSave">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getStudentInfo, updateStudentContact } from '@/api/student-info'

// Reactive state: student info, saving flag and editable contact form
const studentInfo = ref({})
const saving = ref(false)
const contactForm = reactive({ phone: '', email: '' })

// Load the student's personal information and prefill the contact form
async function loadInfo() {
  try {
    const res = await getStudentInfo()
    if (res.data) {
      studentInfo.value = res.data
      contactForm.phone = res.data.phone || ''
      contactForm.email = res.data.email || ''
    }
  } catch (e) {
    ElMessage.error('加载个人信息失败')
  }
}

// Save the edited contact information back to the server
async function handleSave() {
  saving.value = true
  try {
    await updateStudentContact({
      phone: contactForm.phone,
      email: contactForm.email
    })
    ElMessage.success('联系方式修改成功')
    loadInfo()
  } catch (e) {
    ElMessage.error('修改失败')
  } finally {
    saving.value = false
  }
}

// Initial data load on component mount
onMounted(() => {
  loadInfo()
})
</script>

<style lang="scss" scoped>
/* Profile center: avatar and name column */
.profile-center {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: $spacing-6 0;
}

.profile-avatar {
  background-color: #0ea5e9;
  color: #fff;
  font-size: 28px;
  margin-bottom: $spacing-4;
}

.profile-name {
  font-size: 18px;
  font-weight: 600;
  color: $color-text-primary;
  margin: 0 0 $spacing-1 0;
}

.profile-no {
  font-size: 13px;
  color: $color-text-secondary;
  margin: 0;
}

/* Card title typography */
.card-title {
  font-weight: 600;
  font-size: 15px;
}
</style>