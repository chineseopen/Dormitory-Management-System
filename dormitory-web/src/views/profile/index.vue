<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  User profile page for the admin portal.
  Shows the current user's avatar, name and role on the left, and provides
  forms to edit basic information (real name, phone, email) and change the
  password on the right. After a password change the user is logged out.
-->
<template>
  <div class="page-container">
    <!-- Page header -->
    <div class="page-header">
      <h2 class="page-title">个人中心</h2>
    </div>

    <el-row :gutter="16">
      <!-- User summary card -->
      <el-col :span="8">
        <el-card shadow="never">
          <div class="user-profile">
            <el-avatar :size="80" class="profile-avatar">
              {{ userStore.userInfo.realName?.charAt(0) || 'A' }}
            </el-avatar>
            <h3 class="profile-name">{{ userStore.userInfo.realName || '管理员' }}</h3>
            <p class="profile-role">{{ userStore.userInfo.roles?.includes('admin') ? '超级管理员' : '普通用户' }}</p>
          </div>
        </el-card>
      </el-col>

      <!-- Forms column -->
      <el-col :span="16">
        <!-- Basic information form -->
        <el-card shadow="never">
          <template #header><span class="card-header-title">基本信息</span></template>
          <el-form :model="infoForm" label-width="80px">
            <el-form-item label="用户名"><el-input v-model="infoForm.username" disabled /></el-form-item>
            <el-form-item label="姓名"><el-input v-model="infoForm.realName" /></el-form-item>
            <el-form-item label="手机号"><el-input v-model="infoForm.phone" /></el-form-item>
            <el-form-item label="邮箱"><el-input v-model="infoForm.email" /></el-form-item>
            <el-form-item><el-button type="primary" :loading="infoLoading" @click="handleSaveInfo">保存修改</el-button></el-form-item>
          </el-form>
        </el-card>

        <!-- Change password form -->
        <el-card shadow="never" style="margin-top: 16px">
          <template #header><span class="card-header-title">修改密码</span></template>
          <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="80px">
            <el-form-item label="原密码" prop="oldPassword"><el-input v-model="pwdForm.oldPassword" type="password" show-password /></el-form-item>
            <el-form-item label="新密码" prop="newPassword"><el-input v-model="pwdForm.newPassword" type="password" show-password /></el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword"><el-input v-model="pwdForm.confirmPassword" type="password" show-password /></el-form-item>
            <el-form-item><el-button type="primary" :loading="pwdLoading" @click="handleChangePwd">修改密码</el-button></el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { changePassword, updateProfile } from '@/api/auth'

const userStore = useUserStore()
const pwdFormRef = ref(null)
const infoLoading = ref(false)
const pwdLoading = ref(false)

// Basic information form (pre-filled from the store)
const infoForm = reactive({
  username: userStore.userInfo.username,
  realName: userStore.userInfo.realName,
  phone: userStore.userInfo.phone || '',
  email: userStore.userInfo.email || ''
})

// Change password form model
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

// Validation rules for the password form
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '密码不少于6位', trigger: 'blur' }],
  confirmPassword: [{
    validator: (rule, value, callback) => {
      if (value !== pwdForm.newPassword) callback(new Error('两次密码不一致'))
      else callback()
    }, trigger: 'blur'
  }]
}

// Save the basic information form
async function handleSaveInfo() {
  infoLoading.value = true
  try {
    await updateProfile({
      nickname: infoForm.realName,
      phone: infoForm.phone,
      email: infoForm.email
    })
    ElMessage.success('信息修改成功')
    // Refresh the user info in the store
    await userStore.getUserInfo()
  } catch (e) {
    ElMessage.error('信息修改失败')
  } finally {
    infoLoading.value = false
  }
}

// Submit the change password form
async function handleChangePwd() {
  const valid = await pwdFormRef.value?.validate().catch(() => false)
  if (!valid) return
  pwdLoading.value = true
  try {
    await changePassword(pwdForm)
    ElMessage.success('密码修改成功，请重新登录')
    // Reset the form fields
    Object.assign(pwdForm, { oldPassword: '', newPassword: '', confirmPassword: '' })
    // Log out after a short delay
    setTimeout(() => {
      userStore.logout()
    }, 1500)
  } catch (e) {
    ElMessage.error('密码修改失败')
  } finally {
    pwdLoading.value = false
  }
}
</script>

<style lang="scss" scoped>
/* User profile summary block */
.user-profile {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: $spacing-6 0;
}

.profile-avatar {
  background-color: $color-primary-light;
  font-size: $font-size-2xl;
  margin-bottom: $spacing-4;
}

.profile-name {
  font-size: $font-size-lg;
  font-weight: $font-weight-semibold;
  color: $color-text-primary;
  margin-bottom: $spacing-1;
}

.profile-role {
  font-size: $font-size-sm;
  color: $color-text-secondary;
}

.card-header-title {
  font-weight: $font-weight-semibold;
  font-size: $font-size-md;
}
</style>