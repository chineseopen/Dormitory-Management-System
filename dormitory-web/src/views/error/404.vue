<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  404 Not Found error page.
  Displayed when the user navigates to a route that does not exist.
  Provides a button to return to the home page.
-->
<template>
  <div class="error-page">
    <div class="error-content">
      <!-- Large error code -->
      <h1 class="error-code">404</h1>
      <!-- Error description text -->
      <p class="error-desc">抱歉，您访问的页面不存在</p>
      <!-- Return to home button -->
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
/* Full-screen centered error page */
.error-page {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: $color-bg-page;
}

.error-content {
  text-align: center;
}

/* Oversized error code */
.error-code {
  font-size: 120px;
  font-weight: $font-weight-bold;
  color: $color-primary-light;
  line-height: 1;
  margin-bottom: $spacing-4;
}

.error-desc {
  font-size: $font-size-lg;
  color: $color-text-secondary;
  margin-bottom: $spacing-8;
}
</style>