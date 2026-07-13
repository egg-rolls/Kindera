import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '仪表盘', icon: 'DataBoard' }
      },
      {
        path: 'children',
        name: 'Children',
        component: () => import('../views/Children.vue'),
        meta: { title: '幼儿管理', icon: 'User' }
      },
      {
        path: 'courses',
        name: 'Courses',
        component: () => import('../views/Courses.vue'),
        meta: { title: '课程管理', icon: 'Reading' }
      },
      {
        path: 'menus',
        name: 'Menus',
        component: () => import('../views/Menus.vue'),
        meta: { title: '食谱管理', icon: 'Food' }
      },
      {
        path: 'attendance',
        name: 'Attendance',
        component: () => import('../views/Attendance.vue'),
        meta: { title: '考勤管理', icon: 'Calendar' }
      },
      {
        path: 'transfers',
        name: 'Transfers',
        component: () => import('../views/Transfers.vue'),
        meta: { title: '调班管理', icon: 'Switch' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const user = localStorage.getItem('user')
  if (to.path !== '/login' && !user) {
    next('/login')
  } else {
    next()
  }
})

export default router
