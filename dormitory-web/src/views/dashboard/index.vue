<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Dashboard workbench page for the admin portal.
  Displays an overview of key statistics (buildings, rooms, students, repairs,
  visitors, fees), a grid of quick-action shortcuts and the latest published notices.
-->
<template>
  <div class="page-container dashboard">
    <!-- Page header with greeting -->
    <div class="page-header">
      <h2 class="page-title">工作台</h2>
      <span class="greeting">{{ greeting }}，{{ userStore.userInfo.realName || '管理员' }}</span>
    </div>

    <!-- Statistics cards row -->
    <el-row :gutter="16" class="stat-cards">
      <el-col :span="4" v-for="stat in stats" :key="stat.key">
        <div class="stat-card" :style="{ borderTopColor: stat.color }">
          <div class="stat-info">
            <span class="stat-title">{{ stat.title }}</span>
            <span class="stat-value">{{ stat.value }}</span>
          </div>
          <div class="stat-icon" :style="{ backgroundColor: stat.color + '15', color: stat.color }">
            <el-icon :size="24"><component :is="stat.icon" /></el-icon>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- Quick actions and latest notices -->
    <el-row :gutter="16" style="margin-top: 16px">
      <!-- Quick action shortcuts -->
      <el-col :span="16">
        <el-card shadow="never">
          <template #header>
            <span class="card-header-title">快捷操作</span>
          </template>
          <el-row :gutter="16">
            <el-col :span="6" v-for="action in quickActions" :key="action.title">
              <div class="quick-action" @click="$router.push(action.path)">
                <div class="action-icon" :style="{ backgroundColor: action.color + '15', color: action.color }">
                  <el-icon :size="20"><component :is="action.icon" /></el-icon>
                </div>
                <span class="action-title">{{ action.title }}</span>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>

      <!-- Latest notices panel -->
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center">
              <span class="card-header-title">最新通知</span>
              <el-button link type="primary" @click="$router.push('/notice')">查看更多</el-button>
            </div>
          </template>
          <div class="notice-list">
            <!-- Empty state -->
            <div v-if="notices.length === 0" class="notice-empty">暂无通知</div>
            <!-- Notice items -->
            <div v-for="notice in notices" :key="notice.id" class="notice-item" @click="$router.push('/notice')">
              <el-tag :type="notice.type === 1 ? 'primary' : notice.type === 3 ? 'danger' : 'warning'" size="small">
                {{ notice.type === 1 ? '通知' : notice.type === 3 ? '紧急' : '公告' }}
              </el-tag>
              <span class="notice-title">{{ notice.title }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getDashboardStatistics } from '@/api/dashboard'
import { getNoticeList } from '@/api/notice'

const userStore = useUserStore()

// Time-based greeting (morning / afternoon / evening)
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 12) return '上午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

// Aggregated statistics fetched from the backend
const dashboardData = ref({
  buildingCount: 0,
  roomCount: 0,
  studentCount: 0,
  pendingRepairCount: 0,
  visitingCount: 0,
  unpaidFeeCount: 0
})

// Map raw statistics into display objects with icons and colors
const stats = computed(() => [
  { key: 'building', title: '楼栋总数', value: dashboardData.value.buildingCount, icon: 'OfficeBuilding', color: '#2563eb' },
  { key: 'room', title: '房间总数', value: dashboardData.value.roomCount, icon: 'House', color: '#06b6d4' },
  { key: 'student', title: '入住人数', value: dashboardData.value.studentCount, icon: 'User', color: '#10b981' },
  { key: 'repair', title: '待处理报修', value: dashboardData.value.pendingRepairCount, icon: 'SetUp', color: '#ef4444' },
  { key: 'visitor', title: '在访人数', value: dashboardData.value.visitingCount, icon: 'Avatar', color: '#0ea5e9' },
  { key: 'fee', title: '未缴费数', value: dashboardData.value.unpaidFeeCount, icon: 'Coin', color: '#14b8a6' }
])

// Quick action shortcuts linking to management pages
const quickActions = [
  { title: '楼栋管理', icon: 'OfficeBuilding', path: '/building', color: '#2563eb' },
  { title: '房间管理', icon: 'House', path: '/room', color: '#06b6d4' },
  { title: '住宿管理', icon: 'User', path: '/student', color: '#10b981' },
  { title: '报修管理', icon: 'SetUp', path: '/repair', color: '#ef4444' },
  { title: '访客管理', icon: 'Avatar', path: '/visitor', color: '#0ea5e9' },
  { title: '水电费', icon: 'Coin', path: '/fee', color: '#14b8a6' },
  { title: '通知公告', icon: 'Bell', path: '/notice', color: '#0ea5e9' },
  { title: '系统管理', icon: 'Setting', path: '/system/user', color: '#64748b' }
]

// Latest published notices
const notices = ref([])

// Load statistics and latest notices on mount
onMounted(async () => {
  // Fetch dashboard statistics (keep defaults on failure)
  try {
    const res = await getDashboardStatistics()
    if (res.data) {
      dashboardData.value = res.data
    }
  } catch (e) {
    // Statistics failed to load, keep default values
  }

  // Fetch the latest 5 published notices (keep empty list on failure)
  try {
    const res = await getNoticeList({ pageNum: 1, pageSize: 5, status: 1 })
    if (res.data && res.data.records) {
      notices.value = res.data.records
    }
  } catch (e) {
    // Notices failed to load, keep empty list
  }
})
</script>

<style lang="scss" scoped>
/* Greeting text in the header */
.greeting {
  font-size: $font-size-sm;
  color: $color-text-secondary;
}

/* Statistics cards */
.stat-cards {
  .stat-card {
    background: $color-bg-card;
    border-radius: $border-radius-md;
    padding: $spacing-5;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-top: 3px solid transparent;
    transition: box-shadow $duration-base $ease-out;

    &:hover {
      box-shadow: $shadow-md;
    }
  }

  .stat-info {
    display: flex;
    flex-direction: column;
    gap: $spacing-2;
  }

  .stat-title {
    font-size: $font-size-sm;
    color: $color-text-secondary;
  }

  .stat-value {
    font-size: $font-size-3xl;
    font-weight: $font-weight-bold;
    color: $color-text-primary;
  }

  .stat-icon {
    width: 48px;
    height: 48px;
    border-radius: $border-radius-md;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

/* Card header title */
.card-header-title {
  font-weight: $font-weight-semibold;
  font-size: $font-size-md;
}

/* Quick action shortcut tiles */
.quick-action {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-2;
  padding: $spacing-4;
  border-radius: $border-radius-base;
  cursor: pointer;
  transition: background-color $duration-fast $ease-out;

  &:hover {
    background-color: $color-bg-page;
  }

  .action-icon {
    width: 40px;
    height: 40px;
    border-radius: $border-radius-base;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .action-title {
    font-size: $font-size-xs;
    color: $color-text-regular;
  }
}

/* Latest notices list */
.notice-list {
  .notice-empty {
    text-align: center;
    color: $color-text-secondary;
    padding: $spacing-6 0;
    font-size: $font-size-sm;
  }

  .notice-item {
    display: flex;
    align-items: center;
    gap: $spacing-2;
    padding: $spacing-2 0;
    border-bottom: 1px solid $color-border-light;
    cursor: pointer;
    transition: color $duration-fast $ease-out;

    &:hover {
      color: $color-primary;
    }

    &:last-child {
      border-bottom: none;
    }
  }

  .notice-title {
    font-size: $font-size-sm;
    color: $color-text-regular;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>