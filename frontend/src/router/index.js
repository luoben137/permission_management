import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/users',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/Users.vue'),
        meta: { requiresAuth: true, permission: 'USER_MANAGE' }
      },
      {
        path: 'roles',
        name: 'Roles',
        component: () => import('@/views/Roles.vue'),
        meta: { requiresAuth: true, permission: 'ROLE_MANAGE' }
      },
      {
        path: 'permissions',
        name: 'Permissions',
        component: () => import('@/views/Permissions.vue'),
        meta: { requiresAuth: true, permission: 'PERMISSION_MANAGE' }
      },
      {
        path: 'audit-logs',
        name: 'AuditLogs',
        component: () => import('@/views/AuditLogs.vue'),
        meta: { requiresAuth: true, permission: 'AUDIT_VIEW' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth === false) {
    next()
    return
  }

  if (!authStore.isAuthenticated) {
    try {
      await authStore.checkAuth()
    } catch (error) {
      if (to.path !== '/login') {
        next('/login')
        return
      }
    }
  }

  if (to.meta.permission && !authStore.hasPermission(to.meta.permission)) {
    ElMessage.error('没有权限访问该页面')
    next('/')
    return
  }

  next()
})

export default router

