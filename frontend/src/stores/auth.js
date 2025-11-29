import { defineStore } from 'pinia'
import { login as loginApi, logout as logoutApi, checkAuth as checkAuthApi } from '@/api/auth'
import { ElMessage } from 'element-plus'
import router from '@/router'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    isAuthenticated: false
  }),

  getters: {
    hasPermission: (state) => (permission) => {
      if (!state.user || !state.user.permissions) {
        return false
      }
      return state.user.permissions.includes(permission)
    },

    hasRole: (state) => (role) => {
      if (!state.user || !state.user.roles) {
        return false
      }
      return state.user.roles.includes(role)
    }
  },

  actions: {
    async login(credentials) {
      try {
        const response = await loginApi(credentials)
        if (response.data) {
          this.user = response.data
          this.isAuthenticated = true
          ElMessage.success('登录成功')
          router.push('/')
          return true
        }
        return false
      } catch (error) {
        ElMessage.error(error.message || '登录失败')
        return false
      }
    },

    async logout() {
      try {
        await logoutApi()
        this.user = null
        this.isAuthenticated = false
        ElMessage.success('登出成功')
        router.push('/login')
      } catch (error) {
        ElMessage.error(error.message || '登出失败')
      }
    },

    async checkAuth() {
      try {
        const response = await checkAuthApi()
        if (response.data) {
          this.user = response.data
          this.isAuthenticated = true
          return true
        }
        this.user = null
        this.isAuthenticated = false
        return false
      } catch (error) {
        this.user = null
        this.isAuthenticated = false
        throw error
      }
    }
  }
})

