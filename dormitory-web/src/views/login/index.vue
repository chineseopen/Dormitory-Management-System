<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Login page for both admin and student users.
  Renders a decorative banner on the left and a login form on the right.
  Supports "remember me" to persist credentials and redirects users based on
  their role (admin -> admin portal, student -> student portal) after login.
-->
<template>
  <div class="login-page">
    <div class="login-container">
      <!-- Left decorative banner -->
      <div class="login-banner">
        <div class="banner-content">
          <el-icon :size="48" class="banner-icon"><School /></el-icon>
          <h1>宿舍管理系统</h1>
          <p>高效管理 · 智慧服务 · 安全保障</p>
        </div>
        <!-- Decorative circles -->
        <div class="banner-decoration">
          <div class="deco-circle deco-1"></div>
          <div class="deco-circle deco-2"></div>
          <div class="deco-circle deco-3"></div>
        </div>
      </div>

      <!-- Right login form -->
      <div class="login-form-wrapper">
        <div class="login-form-inner">
          <h2 class="form-title">欢迎登录</h2>
          <p class="form-subtitle">请输入您的账号信息</p>

          <!-- Login form with validation -->
          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            size="large"
            @keyup.enter="handleLogin"
          >
            <!-- Username field -->
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                :prefix-icon="User"
              />
            </el-form-item>

            <!-- Password field -->
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>

            <!-- Remember me option -->
            <el-form-item>
              <div class="form-options">
                <el-checkbox v-model="rememberMe">记住我</el-checkbox>
              </div>
            </el-form-item>

            <!-- Submit button -->
            <el-form-item>
              <el-button
                type="primary"
                :loading="loading"
                class="login-btn"
                @click="handleLogin"
              >
                登 录
              </el-button>
            </el-form-item>
          </el-form>

          <div class="form-footer">
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// Form reference, loading state and remember-me flag
const loginFormRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)

// Reactive login form model
const loginForm = reactive({
  username: '',
  password: ''
})

// Validation rules for the login form
const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// localStorage key for persisting remembered credentials
const REMEMBER_KEY = 'dormitory_remember'

onMounted(() => {
  // Restore remembered credentials if available
  const saved = localStorage.getItem(REMEMBER_KEY)
  if (saved) {
    try {
      const { username, password } = JSON.parse(saved)
      loginForm.username = username
      loginForm.password = password
      rememberMe.value = true
    } catch (e) {
      localStorage.removeItem(REMEMBER_KEY)
    }
  } else {
    // Pre-fill the demo account by default
    loginForm.username = 'admin'
    loginForm.password = 'admin123'
  }
})

// Handle the login submission
async function handleLogin() {
  // Validate the form before submitting
  const valid = await loginFormRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.login(loginForm)

    // Persist or clear remembered credentials based on the checkbox
    if (rememberMe.value) {
      localStorage.setItem(REMEMBER_KEY, JSON.stringify({
        username: loginForm.username,
        password: loginForm.password
      }))
    } else {
      localStorage.removeItem(REMEMBER_KEY)
    }

    ElMessage.success('登录成功')

    // Redirect based on role: explicit redirect param, student portal, or admin home
    const roles = userStore.roles || []
    const isStudent = roles.includes('student')
    const redirect = route.query.redirect
    if (redirect) {
      router.push(redirect)
    } else if (isStudent) {
      router.push('/student-portal')
    } else {
      router.push('/')
    }
  } catch (e) {
    // Errors are already handled by the request interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
/* Full-screen centered login page */
.login-page {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: $color-bg-page;
}

/* Login card container */
.login-container {
  display: flex;
  width: 900px;
  min-height: 520px;
  border-radius: $border-radius-xl;
  overflow: hidden;
  box-shadow: $shadow-lg;
}

/* Left decorative banner with gradient background */
.login-banner {
  position: relative;
  width: 400px;
  background: linear-gradient(135deg, #0f172a 0%, #1e40af 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-shrink: 0;
}

/* Banner text content */
.banner-content {
  position: relative;
  z-index: 2;
  text-align: center;
  color: #ffffff;

  .banner-icon {
    margin-bottom: $spacing-6;
    color: $color-accent;
  }

  h1 {
    font-size: $font-size-2xl;
    font-weight: $font-weight-bold;
    margin-bottom: $spacing-3;
  }

  p {
    font-size: $font-size-sm;
    color: rgba(255, 255, 255, 0.7);
    letter-spacing: 2px;
  }
}

/* Decorative circles on the banner */
.banner-decoration {
  position: absolute;
  inset: 0;

  .deco-circle {
    position: absolute;
    border-radius: 50%;
    border: 1px solid rgba(255, 255, 255, 0.08);
  }

  .deco-1 {
    width: 300px;
    height: 300px;
    top: -80px;
    right: -60px;
  }

  .deco-2 {
    width: 200px;
    height: 200px;
    bottom: -40px;
    left: -40px;
  }

  .deco-3 {
    width: 120px;
    height: 120px;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background: rgba($color-accent, 0.08);
    border: none;
  }
}

/* Right form area */
.login-form-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $color-bg-card;
  padding: $spacing-10;
}

.login-form-inner {
  width: 100%;
  max-width: 340px;
}

.form-title {
  font-size: $font-size-2xl;
  font-weight: $font-weight-bold;
  color: $color-text-primary;
  margin-bottom: $spacing-2;
}

.form-subtitle {
  font-size: $font-size-sm;
  color: $color-text-secondary;
  margin-bottom: $spacing-8;
}

/* Form options row (remember me) */
.form-options {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* Full-width login button */
.login-btn {
  width: 100%;
  height: 44px;
  font-size: $font-size-md;
  font-weight: $font-weight-medium;
  border-radius: $border-radius-base;
}

.form-footer {
  text-align: center;
  margin-top: $spacing-6;
  font-size: $font-size-xs;
  color: $color-text-placeholder;
}
</style>