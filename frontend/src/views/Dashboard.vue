<template>
  <div class="dashboard">
    <h2>数据概览</h2>
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card blue" @click="$router.push('/children')">
          <div class="stat-icon">👶</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalChildren }}</div>
            <div class="stat-label">在园幼儿</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card green" @click="$router.push('/children')">
          <div class="stat-icon">📚</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalClasses }}</div>
            <div class="stat-label">班级数量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card orange" @click="$router.push('/courses')">
          <div class="stat-icon">🎨</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalCourses }}</div>
            <div class="stat-label">兴趣课程</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card red">
          <div class="stat-icon">📊</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.attendanceRate }}%</div>
            <div class="stat-label">今日出勤率</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>班级人数统计</span>
              <el-tag type="info" size="small">共 {{ classStats.length }} 个班</el-tag>
            </div>
          </template>
          <div v-for="cls in classStats" :key="cls.className" class="class-row">
            <span class="class-name">{{ cls.className }}</span>
            <el-progress
              :percentage="Math.round((cls.currentCount / cls.maxCount) * 100)"
              :color="cls.currentCount / cls.maxCount > 0.8 ? '#f56c6c' : '#409eff'"
              :format="() => `${cls.currentCount}/${cls.maxCount}人`"
            />
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>课程选课统计</span>
              <el-tag type="info" size="small">共 {{ courseStats.length }} 门课</el-tag>
            </div>
          </template>
          <div v-for="course in courseStats" :key="course.courseName" class="class-row">
            <span class="class-name">{{ course.courseName }}</span>
            <el-progress
              :percentage="Math.round(course.rate)"
              :color="course.rate > 80 ? '#f56c6c' : course.rate > 50 ? '#e6a23c' : '#67c23a'"
              :format="() => `${course.currentCount}人 (${course.rate}%)`"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>本周食谱</span>
              <el-button type="primary" link @click="$router.push('/menus')">查看全部 →</el-button>
            </div>
          </template>
          <el-table :data="weeklyMenu" stripe size="small" max-height="250">
            <el-table-column prop="dayOfWeek" label="星期" width="70">
              <template #default="{ row }">{{ dayMap[row.dayOfWeek] }}</template>
            </el-table-column>
            <el-table-column prop="mealType" label="餐次" width="70">
              <template #default="{ row }">{{ mealMap[row.mealType] }}</template>
            </el-table-column>
            <el-table-column prop="dishName" label="菜品" min-width="150" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { statisticsApi, menuApi } from '../api'

const stats = ref({ totalChildren: 0, totalClasses: 0, totalCourses: 0, attendanceRate: 0 })
const classStats = ref([])
const courseStats = ref([])
const weeklyMenu = ref([])

const dayMap = { 1: '周一', 2: '周二', 3: '周三', 4: '周四', 5: '周五' }
const mealMap = { 1: '早餐', 2: '午餐', 3: '晚餐' }

onMounted(async () => {
  try {
    const [classRes, courseRes, menuRes] = await Promise.all([
      statisticsApi.getClassStats(),
      statisticsApi.getCourseStats(),
      menuApi.getCurrentWeek()
    ])
    if (classRes.success) {
      classStats.value = classRes.data
      stats.value.totalClasses = classRes.data.length
      stats.value.totalChildren = classRes.data.reduce((sum, c) => sum + c.currentCount, 0)
    }
    if (courseRes.success) {
      courseStats.value = courseRes.data
      stats.value.totalCourses = courseRes.data.length
    }
    if (menuRes.success) {
      weeklyMenu.value = (menuRes.data || []).slice(0, 15)
    }
  } catch (e) {
    console.error('加载统计数据失败', e)
  }
})
</script>

<style scoped>
.dashboard h2 {
  margin-bottom: 20px;
  color: #303133;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-card.blue { border-left: 4px solid #409eff; }
.stat-card.green { border-left: 4px solid #67c23a; }
.stat-card.orange { border-left: 4px solid #e6a23c; }
.stat-card.red { border-left: 4px solid #f56c6c; }

.stat-icon {
  font-size: 48px;
  margin-right: 20px;
}

.stat-value {
  font-size: 32px;
  font-weight: 600;
  color: #303133;
}

.stat-label {
  color: #909399;
  margin-top: 4px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.class-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.class-name {
  width: 80px;
  color: #606266;
  flex-shrink: 0;
}
</style>
