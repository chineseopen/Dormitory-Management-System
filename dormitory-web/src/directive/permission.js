// Copyright (C) 2026 程序员-X
// SPDX-License-Identifier: MIT

/**
 * Custom Vue directive for element-level permission control.
 * Usage: v-permission="['admin']" (role array) or v-permission="'building:add'" (permission string).
 * Removes the bound element from the DOM when the current user lacks the required role/permission.
 *
 * @module directive/permission
 */

import { useUserStore } from '@/store/user'

// Check whether the current user satisfies the binding value; remove the element if not
function checkPermission(el, binding) {
  const { value } = binding
  if (!value) return

  const userStore = useUserStore()
  const permissions = userStore.permissions
  const roles = userStore.roles

  // Super admin owns all permissions (wildcard '*')
  if (permissions.includes('*')) return

  let hasPermission = false

  if (Array.isArray(value)) {
    // If every entry is a plain role name (no colon), treat the array as roles
    if (value.every((v) => typeof v === 'string' && !v.includes(':'))) {
      hasPermission = value.some((role) => roles.includes(role))
    } else {
      // Otherwise treat the array as permission strings
      hasPermission = value.some((perm) => permissions.includes(perm))
    }
  } else if (typeof value === 'string') {
    // Single value: a colon indicates a permission, otherwise a role
    if (value.includes(':')) {
      hasPermission = permissions.includes(value)
    } else {
      hasPermission = roles.includes(value)
    }
  }

  // Remove the element from the DOM when access is denied
  if (!hasPermission) {
    el.parentNode?.removeChild(el)
  }
}

export default {
  // Evaluate on mount
  mounted(el, binding) {
    checkPermission(el, binding)
  },
  // Re-evaluate on update (e.g. after user info refresh)
  updated(el, binding) {
    checkPermission(el, binding)
  }
}