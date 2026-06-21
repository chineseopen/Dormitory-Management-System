// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Vite build configuration for the Dormitory Management System web frontend.
 * Defines plugins, path aliases, SCSS preprocessing options and the dev server proxy.
 *
 * @module vite.config
 */

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import path from 'path'

export default defineConfig({
  // Register build plugins: Vue SFC support and Element Plus on-demand auto import
  plugins: [
    vue(),
    // Auto import APIs (Vue, vue-router, pinia) and Element Plus resolvers
    AutoImport({
      resolvers: [ElementPlusResolver()],
      imports: ['vue', 'vue-router', 'pinia'],
      dts: false
    }),
    // Auto register Element Plus components on demand
    Components({
      resolvers: [ElementPlusResolver()],
      dts: false
    })
  ],

  // Path aliases so source files can be imported via the '@' shortcut
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },

  // SCSS preprocessing: inject global design-token variables into every style block
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@use "@/styles/variables.scss" as *;`
      }
    }
  },

  // Local development server: port, auto-open browser and API proxy to the backend
  server: {
    port: 3000,
    open: true,
    proxy: {
      // Proxy admin-side requests to the backend service
      '/admin': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      // Proxy student-side (api) requests to the backend service
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})