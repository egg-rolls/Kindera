<template>
  <div class="dashboard">
    <h2>数据概览</h2>
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card blue">
          <div class="stat-icon">👶</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalChildren }}</div>
            <div class="stat-label">在园幼儿</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card green">
          <div class="stat-icon">📚</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalClasses }}</div>
            <div class="stat-label">班级数量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card orange">
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
          <template #header>班级人数统计</template>
          <div v-for="cls in classStats" :key="cls.className" class="class-row">
            <span class="class-name">{{ cls.className }}</span>
            <el-progress :percentage="(cls.currentCount / cls.maxCount) * 100" :format="() => `${cls.currentCount}/${cls.maxCount}人`" />
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>课程选课统计</template>
          <div v-for="course in courseStats" :key="course.courseName" class="class-row">
            <span class="class-name">{{ course.courseName }}</span>
            <el-progress :percentage="course.rate" :format="() => `${course.currentCount}人`" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { statisticsApi, classApi, courseApi } from '../api'

const stats = ref({ totalChildren: 0, totalClasses: 0, totalCourses: 0, attendanceRate: 0 })
const classStats = ref([])
const courseStats = ref([])

onMounted(async () => {
  try {
    const [classRes, courseRes] = await Promise.all([
      statisticsApi.getClassStats(),
      statisticsApi.getCourseStats()
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

.class-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.class-name {
  width: 80px;
  color: #606266;
}
</style>
