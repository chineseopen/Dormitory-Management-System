<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Student portal home page.
  Displays a personalized greeting, accommodation summary cards, quick-action
  shortcuts and previews of the student's recent repair requests and notices.
-->
<template>
  <div class="student-home">
    <!-- Welcome banner with time-based greeting -->
    <div class="welcome-section">
      <h2>{{ greeting }}，{{ studentInfo.name || '同学' }}</h2>
      <p class="welcome-desc">欢迎使用宿舍管理系统学生端</p>
    </div>

    <!-- Accommodation summary cards: building, room and student id -->
    <el-row :gutter="16" class="info-cards">
      <el-col :span="8">
        <el-card shadow="never" class="info-card">
          <div class="info-item">
            <el-icon :size="32" color="#2563eb"><OfficeBuilding /></el-icon>
            <div class="info-text">
              <span class="info-label">所在楼栋</span>
              <span class="info-value">{{ studentInfo.buildingName || '未分配' }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="info-card">
          <div class="info-item">
            <el-icon :size="32" color="#06b6d4"><House /></el-icon>
            <div class="info-text">
              <span class="info-label">房间号</span>
              <span class="info-value">{{ studentInfo.roomNumber || '未分配' }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="info-card">
          <div class="info-item">
            <el-icon :size="32" color="#10b981"><User /></el-icon>
            <div class="info-text">
              <span class="info-label">学号</span>
              <span class="info-value">{{ studentInfo.studentNo || '-' }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Quick actions and recent activity row -->
    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <!-- Quick navigation shortcuts to common student tasks -->
        <el-card shadow="never">
          <template #header><span class="card-title">快捷操作</span></template>
          <div class="quick-actions">
            <div class="action-item" @click="$router.push('/student-portal/repair')">
              <div class="action-icon" style="background: rgba(239,68,68,0.1); color: #ef4444">
                <el-icon :size="24"><SetUp /></el-icon>
              </div>
              <span>提交报修</span>
            </div>
            <div class="action-item" @click="$router.push('/student-portal/fee')">
              <div class="action-icon" style="background: rgba(20,184,166,0.1); color: #14b8a6">
                <el-icon :size="24"><Coin /></el-icon>
              </div>
              <span>查看水电费</span>
            </div>
            <div class="action-item" @click="$router.push('/student-portal/notice')">
              <div class="action-icon" style="background: rgba(245,158,11,0.1); color: #f59e0b">
                <el-icon :size="24"><Bell /></el-icon>
              </div>
              <span>通知公告</span>
            </div>
            <div class="action-item" @click="$router.push('/student-portal/profile')">
              <div class="action-icon" style="background: rgba(14,165,233,0.1); color: #0ea5e9">
                <el-icon :size="24"><User /></el-icon>
              </div>
              <span>个人信息</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <!-- Recent repair requests preview (latest 5 records) -->
        <el-card shadow="never">
          <template #header>
            <div class="card-header-flex">
              <span class="card-title">最近报修</span>
              <el-button link type="primary" size="small" @click="$router.push('/student-portal/repair')">查看更多</el-button>
            </div>
          </template>
          <div class="repair-list">
            <div v-if="recentRepairs.length === 0" class="empty-text">暂无报修记录</div>
            <div v-for="item in recentRepairs" :key="item.id" class="repair-item">
              <div class="repair-info">
                <span class="repair-title">{{ item.title }}</span>
                <el-tag :type="item.status === 1 ? 'warning' : item.status === 2 ? 'primary' : 'success'" size="small">
                  {{ item.statusDesc }}
                </el-tag>
              </div>
              <span class="repair-time">{{ item.createTime }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Latest notices preview (clickable to the notice list) -->
    <el-card shadow="never" style="margin-top: 16px">
      <template #header>
        <div class="card-header-flex">
          <span class="card-title">最新通知</span>
          <el-button link type="primary" size="small" @click="$router.push('/student-portal/notice')">查看更多</el-button>
        </div>
      </template>
      <div class="notice-list">
        <div v-if="recentNotices.length === 0" class="empty-text">暂无通知公告</div>
        <div v-for="item in recentNotices" :key="item.id" class="notice-item" @click="$router.push('/student-portal/notice')">
          <el-tag :type="item.type === 1 ? 'primary' : item.type === 3 ? 'danger' : 'warning'" size="small">
            {{ item.type === 1 ? '通知' : item.type === 3 ? '紧急' : '公告' }}
          </el-tag>
          <span class="notice-title">{{ item.title }}</span>
          <span class="notice-time">{{ item.createTime }}</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { getStudentInfo } from '@/api/student-info'
import { getMyRepairList } from '@/api/student-repair'
import { getStudentNoticeList } from '@/api/student-notice'

// Reactive state for the student profile, recent repairs and recent notices
const studentInfo = ref({})
const recentRepairs = ref([])
const recentNotices = ref([])

// Time-based greeting (morning / afternoon / evening)
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 12) return '上午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

// Load student info, recent repairs and recent notices in parallel on mount
onMounted(async () => {
  // Fetch the current student's personal information
  try {
    const res = await getStudentInfo()
    if (res.data) {
      studentInfo.value = res.data
    }
  } catch (e) {
    // Load failed - keep default empty state
  }

  // Fetch the latest 5 repair requests for the current student
  try {
    const res = await getMyRepairList({ pageNum: 1, pageSize: 5 })
    if (res.data && res.data.records) {
      recentRepairs.value = res.data.records
    }
  } catch (e) {
    // Load failed - keep default empty state
  }

  // Fetch the latest 5 published notices
  try {
    const res = await getStudentNoticeList({ pageNum: 1, pageSize: 5 })
    if (res.data && res.data.records) {
      recentNotices.value = res.data.records
    }
  } catch (e) {
    // Load failed - keep default empty state
  }
})
</script>

<style lang="scss" scoped>
/* Welcome banner: title and description */
.welcome-section {
  margin-bottom: $spacing-6;

  h2 {
    font-size: 24px;
    font-weight: 600;
    color: $color-text-primary;
    margin: 0 0 $spacing-1 0;
  }

  .welcome-desc {
    font-size: 14px;
    color: $color-text-secondary;
    margin: 0;
  }
}

.info-card {
  .info-item {
    display: flex;
    align-items: center;
    gap: $spacing-4;
  }

  .info-text {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  .info-label {
    font-size: 12px;
    color: $color-text-secondary;
  }

  .info-value {
    font-size: 18px;
    font-weight: 600;
    color: $color-text-primary;
  }
}

.card-title {
  font-weight: 600;
  font-size: 15px;
}

.card-header-flex {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quick-actions {
  display: flex;
  gap: $spacing-6;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-2;
  cursor: pointer;
  padding: $spacing-4;
  border-radius: $border-radius-base;
  transition: background-color $duration-fast $ease-out;

  &:hover {
    background-color: $color-bg-page;
  }

  span {
    font-size: 13px;
    color: $color-text-regular;
  }
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: $border-radius-md;
  display: flex;
  align-items: center;
  justify-content: center;
}

.repair-list {
  .empty-text {
    text-align: center;
    color: $color-text-secondary;
    padding: $spacing-6 0;
    font-size: 13px;
  }
}

.repair-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $spacing-3 0;
  border-bottom: 1px solid $color-border-light;

  &:last-child {
    border-bottom: none;
  }
}

.repair-info {
  display: flex;
  align-items: center;
  gap: $spacing-2;
}

.repair-title {
  font-size: 13px;
  color: $color-text-regular;
}

.repair-time {
  font-size: 12px;
  color: $color-text-secondary;
}

.notice-list {
  .empty-text {
    text-align: center;
    color: $color-text-secondary;
    padding: $spacing-6 0;
    font-size: 13px;
  }
}

.notice-item {
  display: flex;
  align-items: center;
  gap: $spacing-3;
  padding: $spacing-3 0;
  border-bottom: 1px solid $color-border-light;
  cursor: pointer;
  transition: background-color $duration-fast $ease-out;

  &:hover {
    background-color: $color-bg-page;
  }

  &:last-child {
    border-bottom: none;
  }
}

.notice-title {
  flex: 1;
  font-size: 13px;
  color: $color-text-regular;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notice-time {
  font-size: 12px;
  color: $color-text-secondary;
}
</style>