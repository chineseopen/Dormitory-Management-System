// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Application bootstrap entry for the Dormitory Management System web frontend.
 * Creates the Vue application instance, registers global plugins, icons,
 * the permission directive and global styles, then mounts it to the DOM.
 *
 * @module main
 */

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import permissionDirective from './directive/permission'

// Global styles: design tokens reset and route transition animations
import './styles/global.scss'
import './styles/transition.scss'

// Create the root Vue application instance
const app = createApp(App)

// Register every Element Plus icon as a global component
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// Register the custom permission directive (v-permission)
app.directive('permission', permissionDirective)

// Install Pinia for state management
app.use(createPinia())
// Install the Vue Router for navigation
app.use(router)
// Install Element Plus with the Chinese locale
app.use(ElementPlus, { locale: zhCn })

// Mount the application to the #app root element
app.mount('#app')