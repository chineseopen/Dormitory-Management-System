<!--
  Copyright (C) 2026 程序员-X
  SPDX-License-Identifier: MIT
-->

<!--
  Root component of the Dormitory Management System web application.
  Acts as the top-level container that renders the active route view.
-->
<template>
  <!-- Router outlet: renders the matched route component -->
  <router-view />
</template>

<script setup>
// Root component - route view container
</script>

<style lang="scss">
/* Root element fills the entire viewport */
#app {
  width: 100%;
  height: 100%;
}
</style>