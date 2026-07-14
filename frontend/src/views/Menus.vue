<template>
  <div class="menus-page">
    <!-- 周历食谱 -->
    <el-card style="margin-bottom: 20px">
      <template #header>
        <div class="card-header">
          <div class="week-nav">
            <el-button @click="changeWeek(-1)" :icon="ArrowLeft">上一周</el-button>
            <span class="week-title">{{ weekLabel }}</span>
            <el-button @click="changeWeek(1)">下一周<el-icon><ArrowRight /></el-icon></el-button>
            <el-button @click="goThisWeek" style="margin-left: 12px">本周</el-button>
          </div>
          <div>
            <el-button type="success" @click="handleCopyLastWeek" :loading="copying">
              <el-icon><CopyDocument /></el-icon> 复制上周
            </el-button>
          </div>
        </div>
      </template>

      <div class="week-calendar" v-loading="menuLoading">
        <table class="calendar-table">
          <thead>
            <tr>
              <th class="meal-header"></th>
              <th v-for="day in 5" :key="day" :class="{ 'today-col': isToday(day) }">
                <div class="day-label">{{ dayMap[day] }}</div>
                <div class="date-label">{{ getDayDate(day) }}</div>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="meal in [1, 2, 3]" :key="meal">
              <td class="meal-label">
                <span :class="'meal-tag meal-' + meal">{{ mealMap[meal] }}</span>
              </td>
              <td v-for="day in 5" :key="day"
                  :class="{ 'today-col': isToday(day), 'meal-cell': true }"
                  @click="showQuickAdd(day, meal)">
                <div class="cell-dishes">
                  <div v-for="item in getCellMenus(day, meal)" :key="item.id" class="dish-chip">
                    <span class="dish-chip-name">{{ item.dishName }}</span>
                    <el-icon class="dish-chip-del" @click.stop="handleDeleteMenu(item)"><Close /></el-icon>
                  </div>
                  <div v-if="getCellMenus(day, meal).length === 0" class="empty-hint">+ 添加</div>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </el-card>

    <!-- 菜品库 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>菜品库（{{ dishes.length }}道）</span>
          <el-button type="primary" @click="showAddDishDialog">
            <el-icon><Plus /></el-icon> 添加菜品
          </el-button>
        </div>
      </template>

      <!-- 菜品分类筛选 -->
      <div class="dish-filter">
        <el-radio-group v-model="dishFilter" size="small">
          <el-radio-button :value="0">全部</el-radio-button>
          <el-radio-button v-for="(label, key) in dishTypeMap" :key="key" :value="Number(key)">{{ label }}</el-radio-button>
        </el-radio-group>
      </div>

      <el-table :data="filteredDishes" stripe border style="width: 100%" v-loading="dishLoading" size="small">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="dishName" label="菜品名称" width="150" />
        <el-table-column prop="dishType" label="分类" width="80">
          <template #default="{ row }">
            <el-tag :type="dishTagType(row.dishType)" size="small">{{ dishTypeMap[row.dishType] || '未知' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" @click="showEditDishDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDeleteDish(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑菜品对话框 -->
    <el-dialog v-model="dishDialogVisible" :title="isEditDish ? '编辑菜品' : '添加菜品'" width="500px">
      <el-form ref="dishFormRef" :model="dishForm" :rules="dishRules" label-width="80px">
        <el-form-item label="菜品名称" prop="dishName">
          <el-input v-model="dishForm.dishName" placeholder="如：红烧排骨" />
        </el-form-item>
        <el-form-item label="分类" prop="dishType">
          <el-select v-model="dishForm.dishType" style="width: 100%">
            <el-option v-for="opt in dishTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dishDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleDishSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 快速添加食谱对话框 -->
    <el-dialog v-model="quickAddVisible" title="添加菜品到食谱" width="450px">
      <p class="quick-add-title">{{ quickAddLabel }}</p>
      <el-select v-model="quickAddDishId" filterable placeholder="选择菜品" style="width: 100%">
        <el-option-group v-for="type in [1,2,3,4,5]" :key="type" :label="dishTypeMap[type]">
          <el-option v-for="dish in dishesByType(type)" :key="dish.id"
            :label="dish.dishName" :value="dish.id" />
        </el-option-group>
      </el-select>
      <template #footer>
        <el-button @click="quickAddVisible = false">取消</el-button>
        <el-button type="primary" @click="handleQuickAdd" :loading="submitting">添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, ArrowRight, CopyDocument, Close, Plus } from '@element-plus/icons-vue'
import { menuApi } from '../api'

const weeklyMenu = ref([])
const dishes = ref([])
const menuLoading = ref(false)
const dishLoading = ref(false)
const submitting = ref(false)
const copying = ref(false)
const dishFilter = ref(0)

const dishDialogVisible = ref(false)
const quickAddVisible = ref(false)
const isEditDish = ref(false)
const editDishId = ref(null)
const dishFormRef = ref()

// 当前查看的周（周一日期）
const currentMonday = ref(getMonday(new Date()))

const dayMap = { 1: '周一', 2: '周二', 3: '周三', 4: '周四', 5: '周五' }
const mealMap = { 1: '早餐', 2: '午餐', 3: '晚餐' }
const dishTypeMap = { 1: '主食', 2: '荤菜', 3: '素菜', 4: '汤', 5: '水果' }
const dishTypeOptions = [
  { label: '主食', value: 1 },
  { label: '荤菜', value: 2 },
  { label: '素菜', value: 3 },
  { label: '汤', value: 4 },
  { label: '水果', value: 5 }
]
const dishTagType = (t) => ['', 'info', 'danger', 'success', 'warning', ''][t] || ''

const dishForm = reactive({ dishName: '', dishType: 2 })
const dishRules = {
  dishName: [{ required: true, message: '请输入菜品名称', trigger: 'blur' }],
  dishType: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

// 快速添加
const quickAddDay = ref(1)
const quickAddMeal = ref(1)
const quickAddDishId = ref(null)

const quickAddLabel = computed(() => {
  return `${dayMap[quickAddDay.value]} ${mealMap[quickAddMeal.value]}`
})

// 计算属性
const weekLabel = computed(() => {
  const start = currentMonday.value
  const end = new Date(start)
  end.setDate(end.getDate() + 4)
  return `${formatDate(start)} ~ ${formatDate(end)}`
})

const filteredDishes = computed(() => {
  if (dishFilter.value === 0) return dishes.value
  return dishes.value.filter(d => d.dishType === dishFilter.value)
})

// 工具函数
function getMonday(d) {
  const date = new Date(d)
  const day = date.getDay()
  const diff = date.getDate() - day + (day === 0 ? -6 : 1)
  date.setDate(diff)
  date.setHours(0, 0, 0, 0)
  return date
}

function formatDate(d) {
  return `${d.getMonth() + 1}/${d.getDate()}`
}

function formatDateISO(d) {
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${dd}`
}

function getDayDate(day) {
  const d = new Date(currentMonday.value)
  d.setDate(d.getDate() + day - 1)
  return formatDate(d)
}

function isToday(day) {
  const d = new Date(currentMonday.value)
  d.setDate(d.getDate() + day - 1)
  const today = new Date()
  return d.toDateString() === today.toDateString()
}

function getCellMenus(day, meal) {
  return weeklyMenu.value.filter(m => m.dayOfWeek === day && m.mealType === meal)
}

function dishesByType(type) {
  return dishes.value.filter(d => d.dishType === type)
}

// 周导航
function changeWeek(offset) {
  const d = new Date(currentMonday.value)
  d.setDate(d.getDate() + offset * 7)
  currentMonday.value = d
  loadWeeklyMenu()
}

function goThisWeek() {
  currentMonday.value = getMonday(new Date())
  loadWeeklyMenu()
}

// 加载数据
const loadWeeklyMenu = async () => {
  menuLoading.value = true
  try {
    const weekStart = formatDateISO(currentMonday.value)
    const res = await menuApi.getWeekMenu(weekStart)
    if (res.success) weeklyMenu.value = res.data || []
  } finally {
    menuLoading.value = false
  }
}

const loadDishes = async () => {
  dishLoading.value = true
  try {
    const res = await menuApi.getDishes()
    if (res.success) dishes.value = res.data || []
  } finally {
    dishLoading.value = false
  }
}

// 菜品 CRUD
const showAddDishDialog = () => {
  isEditDish.value = false
  editDishId.value = null
  Object.assign(dishForm, { dishName: '', dishType: 2 })
  dishDialogVisible.value = true
}

const showEditDishDialog = (row) => {
  isEditDish.value = true
  editDishId.value = row.id
  Object.assign(dishForm, { dishName: row.dishName, dishType: row.dishType || 2 })
  dishDialogVisible.value = true
}

const handleDishSubmit = async () => {
  await dishFormRef.value.validate()
  submitting.value = true
  try {
    const payload = { dishName: dishForm.dishName, dishType: dishForm.dishType }
    const res = isEditDish.value
      ? await menuApi.updateDish(editDishId.value, payload)
      : await menuApi.createDish(payload)
    if (res.success) {
      ElMessage.success(isEditDish.value ? '修改成功' : '添加成功')
      dishDialogVisible.value = false
      loadDishes()
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    submitting.value = false
  }
}

const handleDeleteDish = async (row) => {
  await ElMessageBox.confirm(`确认删除菜品「${row.dishName}」？`, '确认删除', { type: 'warning' })
  const res = await menuApi.deleteDish(row.id)
  if (res.success) {
    ElMessage.success('删除成功')
    loadDishes()
  } else {
    ElMessage.error(res.message)
  }
}

// 食谱 CRUD
const showQuickAdd = (day, meal) => {
  quickAddDay.value = day
  quickAddMeal.value = meal
  quickAddDishId.value = null
  quickAddVisible.value = true
}

const handleQuickAdd = async () => {
  if (!quickAddDishId.value) {
    ElMessage.warning('请选择菜品')
    return
  }
  submitting.value = true
  try {
    const res = await menuApi.arrangeMenu({
      weekStart: formatDateISO(currentMonday.value),
      dayOfWeek: quickAddDay.value,
      mealType: quickAddMeal.value,
      dishId: quickAddDishId.value
    })
    if (res.success) {
      ElMessage.success('添加成功')
      quickAddVisible.value = false
      loadWeeklyMenu()
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    submitting.value = false
  }
}

const handleDeleteMenu = async (item) => {
  await ElMessageBox.confirm(`确认删除「${item.dishName}」？`, '确认删除', { type: 'warning' })
  const res = await menuApi.deleteMenu(item.id)
  if (res.success) {
    ElMessage.success('已删除')
    loadWeeklyMenu()
  } else {
    ElMessage.error(res.message)
  }
}

const handleCopyLastWeek = async () => {
  await ElMessageBox.confirm('将上周食谱复制到当前查看的这一周，是否继续？', '复制食谱', { type: 'info' })
  copying.value = true
  try {
    const res = await menuApi.copyLastWeek()
    if (res.success) {
      ElMessage.success(res.message)
      loadWeeklyMenu()
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    copying.value = false
  }
}

onMounted(() => {
  loadWeeklyMenu()
  loadDishes()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.week-nav {
  display: flex;
  align-items: center;
  gap: 12px;
}

.week-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  min-width: 160px;
  text-align: center;
}

/* 周历表格 */
.calendar-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
}

.calendar-table th,
.calendar-table td {
  border: 1px solid #ebeef5;
  padding: 8px;
  text-align: center;
  vertical-align: top;
}

.calendar-table th {
  background: #f5f7fa;
  font-weight: 600;
  color: #303133;
  padding: 12px 8px;
}

.day-label {
  font-size: 14px;
}

.date-label {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.today-col {
  background: #ecf5ff !important;
}

.meal-header {
  width: 80px;
}

.meal-label {
  background: #f5f7fa;
  font-weight: 600;
  vertical-align: middle !important;
  width: 80px;
}

.meal-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 13px;
}

.meal-1 { background: #fdf6ec; color: #e6a23c; }
.meal-2 { background: #ecf5ff; color: #409eff; }
.meal-3 { background: #f0f9eb; color: #67c23a; }

.meal-cell {
  cursor: pointer;
  min-height: 80px;
  transition: background 0.2s;
}

.meal-cell:hover {
  background: #f5f7fa;
}

.cell-dishes {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  justify-content: center;
}

.dish-chip {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  background: #f0f2f5;
  border-radius: 4px;
  padding: 2px 8px;
  font-size: 12px;
  color: #303133;
}

.dish-chip-name {
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dish-chip-del {
  cursor: pointer;
  color: #909399;
  font-size: 12px;
}

.dish-chip-del:hover {
  color: #f56c6c;
}

.empty-hint {
  color: #c0c4cc;
  font-size: 12px;
  padding: 20px 0;
}

.dish-filter {
  margin-bottom: 16px;
}

.quick-add-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
}
</style>
