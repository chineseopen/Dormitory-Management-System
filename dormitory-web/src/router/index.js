// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Vue Router configuration for the Dormitory Management System web frontend.
 * Defines all application routes (admin portal, student portal and 404 page),
 * the global navigation guards for authentication and role-based access control,
 * and the post-navigation hook that maintains the tags-view tabs.
 *
 * @module router
 */

import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { useUserStore } from '@/store/user'
import { useAppStore } from '@/store/app'

// Disable the NProgress loading spinner, keep only the progress bar
NProgress.configure({ showSpinner: false })

// Route table: login page, admin portal, student portal and 404 fallback
const routes = [
  {
    // Standalone login page (not nested in any layout)
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', hidden: true }
  },
  {
    // Admin portal: uses the AdminLayout shell and redirects to the dashboard
    path: '/',
    component: () => import('@/layout/AdminLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '工作台', icon: 'Odometer', affix: true }
      },
      {
        path: 'building',
        name: 'Building',
        component: () => import('@/views/building/index.vue'),
        meta: { title: '楼栋管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'room',
        name: 'Room',
        component: () => import('@/views/room/index.vue'),
        meta: { title: '房间管理', icon: 'House' }
      },
      {
        path: 'student',
        name: 'Student',
        component: () => import('@/views/student/index.vue'),
        meta: { title: '住宿管理', icon: 'User' }
      },
      {
        path: 'checkin',
        name: 'CheckIn',
        component: () => import('@/views/checkin/index.vue'),
        meta: { title: '入住记录', icon: 'Document' }
      },
      {
        path: 'roomchange',
        name: 'RoomChange',
        component: () => import('@/views/roomchange/index.vue'),
        meta: { title: '调宿记录', icon: 'Switch' }
      },
      {
        path: 'repair',
        name: 'Repair',
        component: () => import('@/views/repair/index.vue'),
        meta: { title: '报修管理', icon: 'SetUp' }
      },
      {
        path: 'visitor',
        name: 'Visitor',
        component: () => import('@/views/visitor/index.vue'),
        meta: { title: '访客管理', icon: 'Avatar' }
      },
      {
        path: 'fee',
        name: 'Fee',
        component: () => import('@/views/fee/index.vue'),
        meta: { title: '水电费管理', icon: 'Coin' }
      },
      {
        path: 'notice',
        name: 'Notice',
        component: () => import('@/views/notice/index.vue'),
        meta: { title: '通知公告', icon: 'Bell' }
      },
      {
        // System management group with nested children
        path: 'system',
        name: 'System',
        redirect: '/system/user',
        meta: { title: '系统管理', icon: 'Setting' },
        children: [
          {
            path: 'user',
            name: 'SystemUser',
            component: () => import('@/views/system/user/index.vue'),
            meta: { title: '用户管理' }
          },
          {
            path: 'role',
            name: 'SystemRole',
            component: () => import('@/views/system/role/index.vue'),
            meta: { title: '角色管理' }
          },
          {
            path: 'permission',
            name: 'SystemPermission',
            component: () => import('@/views/system/permission/index.vue'),
            meta: { title: '权限管理' }
          }
        ]
      },
      {
        // Personal center page (hidden from the sidebar menu)
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', icon: 'UserFilled', hidden: true }
      }
    ]
  },
  {
    // Student portal: uses the StudentLayout shell and redirects to the home page
    path: '/student-portal',
    component: () => import('@/layout/StudentLayout.vue'),
    redirect: '/student-portal/home',
    meta: { title: '学生端', hidden: true },
    children: [
      {
        path: 'home',
        name: 'StudentHome',
        component: () => import('@/views/student/home.vue'),
        meta: { title: '学生首页', hidden: true }
      },
      {
        path: 'repair',
        name: 'StudentRepair',
        component: () => import('@/views/student/repair.vue'),
        meta: { title: '报修管理', hidden: true }
      },
      {
        path: 'fee',
        name: 'StudentFee',
        component: () => import('@/views/student/fee.vue'),
        meta: { title: '水电费', hidden: true }
      },
      {
        path: 'notice',
        name: 'StudentNotice',
        component: () => import('@/views/student/notice.vue'),
        meta: { title: '通知公告', hidden: true }
      },
      {
        path: 'profile',
        name: 'StudentProfile',
        component: () => import('@/views/student/profile.vue'),
        meta: { title: '个人信息', hidden: true }
      }
    ]
  },
  {
    // Catch-all route for unmatched paths, renders the 404 page
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404', hidden: true }
  }
]

// Create the router instance with HTML5 history mode
const router = createRouter({
  history: createWebHistory(),
  routes,
  // Always scroll to top when navigating to a new route
  scrollBehavior: () => ({ top: 0 })
})

// Whitelist of routes that do not require authentication
const whiteList = ['/login']

// Global before-each guard: handles auth, role-based access and user info loading
router.beforeEach(async (to, from, next) => {
  NProgress.start()
  // Update the document title based on the target route meta
  document.title = to.meta.title ? `${to.meta.title} - 宿舍管理系统` : '宿舍管理系统'

  const token = getToken()

  if (token) {
    if (to.path === '/login') {
      // Already logged in: redirect away from the login page to the home page
      next({ path: '/' })
    } else {
      const userStore = useUserStore()
      // Check whether user info has already been loaded
      if (userStore.roles.length > 0) {
        // Role-based access control
        const roles = userStore.roles || []
        const isStudent = roles.includes('student')

        // Student portal routes are only accessible to students
        if (to.path.startsWith('/student-portal') && !isStudent) {
          next({ path: '/' })
          return
        }
        // Admin portal routes are only accessible to admins (except the shared profile page)
        if (!to.path.startsWith('/student-portal') && isStudent && to.path !== '/profile') {
          next({ path: '/student-portal' })
          return
        }
        next()
      } else {
        try {
          // First navigation after login: fetch user info then retry the route
          await userStore.getUserInfo()
          next({ ...to, replace: true })
        } catch {
          // Failed to fetch user info: clear credentials and redirect to login
          userStore.logout()
          next(`/login?redirect=${to.path}`)
        }
      }
    }
  } else {
    // No token: allow whitelisted routes, otherwise redirect to login with a redirect param
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
    }
  }
})

// Global after-each hook: stop the progress bar and record the visited tag
router.afterEach((to) => {
  NProgress.done()

  // Add the current route to the tags-view tabs (skip hidden routes)
  const appStore = useAppStore()
  if (to.name && !to.meta?.hidden) {
    appStore.addVisitedView(to)
    appStore.addCachedView(to)
  }
})

export default router