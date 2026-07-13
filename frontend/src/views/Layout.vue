<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo" :class="{ collapsed: isCollapse }">
        <span class="logo-icon">🏫</span>
        <span v-show="!isCollapse" class="logo-text">幼儿园管理系统</span>
      </div>
      <el-menu :default-active="route.path" router :collapse="isCollapse" background-color="#304156" text-color="#bfcbd9" active-text-color="#409eff">
        <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <template #title>{{ item.title }}</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentMenu?.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ user?.realName || '用户' }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 页面内容 -->
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)

const user = computed(() => {
  const u = localStorage.getItem('user')
  return u ? JSON.parse(u) : null
})

const menuItems = [
  { path: '/dashboard', title: '仪表盘', icon: 'DataBoard' },
  { path: '/children', title: '幼儿管理', icon: 'User' },
  { path: '/courses', title: '课程管理', icon: 'Reading' },
  { path: '/menus', title: '食谱管理', icon: 'Food' },
  { path: '/attendance', title: '考勤管理', icon: 'Calendar' },
  { path: '/transfers', title: '调班管理', icon: 'Switch' }
]

const currentMenu = computed(() => menuItems.find(m => m.path === route.path))

const handleCommand = (cmd) => {
  if (cmd === 'logout') {
    localStorage.removeItem('user')
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #263445;
  color: white;
}

.logo-icon {
  font-size: 28px;
}

.logo-text {
  margin-left: 10px;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
}

.logo.collapsed .logo-icon {
  font-size: 24px;
}

.header {
  background: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #606266;
}

.main {
  background-color: #f5f7fa;
  padding: 20px;
}
</style>
