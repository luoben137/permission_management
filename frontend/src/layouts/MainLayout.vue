<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="sidebar">
      <div class="logo">
        <h3>权限管理</h3>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        class="sidebar-menu"
      >
        <el-menu-item
          v-if="hasPermission('USER_MANAGE')"
          index="/users"
        >
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item
          v-if="hasPermission('ROLE_MANAGE')"
          index="/roles"
        >
          <el-icon><UserFilled /></el-icon>
          <span>角色管理</span>
        </el-menu-item>
        <el-menu-item
          v-if="hasPermission('PERMISSION_MANAGE')"
          index="/permissions"
        >
          <el-icon><Lock /></el-icon>
          <span>权限点管理</span>
        </el-menu-item>
        <el-menu-item
          v-if="hasPermission('AUDIT_VIEW')"
          index="/audit-logs"
        >
          <el-icon><Document /></el-icon>
          <span>审计日志</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <span>权限管理系统</span>
        </div>
        <div class="header-right">
          <span class="username">{{ userInfo?.username }}</span>
          <el-button type="danger" size="small" @click="handleLogout">登出</el-button>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { User, UserFilled, Lock, Document } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const activeMenu = computed(() => route.path)
const userInfo = computed(() => authStore.user)

const hasPermission = (permission) => {
  return authStore.hasPermission(permission)
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要登出吗？', '提示', {
      type: 'warning'
    })
    await authStore.logout()
  } catch (error) {
    // User cancelled
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  color: #fff;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3a4a;
}

.logo h3 {
  margin: 0;
  color: #fff;
  font-size: 18px;
}

.sidebar-menu {
  border: none;
  background-color: #304156;
}

.sidebar-menu :deep(.el-menu-item) {
  color: #bfcbd9;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background-color: #263445;
  color: #fff;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: #1890ff;
  color: #fff;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-left {
  font-size: 18px;
  font-weight: bold;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  color: #666;
}

.main-content {
  background-color: #f0f2f5;
  padding: 0;
}
</style>

