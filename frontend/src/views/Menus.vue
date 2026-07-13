<template>
  <div class="menus-page">
    <!-- 本周食谱 -->
    <el-card style="margin-bottom: 20px">
      <template #header>
        <div class="card-header">
          <span>本周食谱</span>
          <el-button type="primary" @click="showArrangeDialog">
            <el-icon><Calendar /></el-icon> 安排食谱
          </el-button>
        </div>
      </template>

      <el-table :data="weeklyMenu" stripe border style="width: 100%" v-loading="menuLoading">
        <el-table-column prop="dayOfWeek" label="星期" width="80">
          <template #default="{ row }">{{ dayMap[row.dayOfWeek] || row.dayOfWeek }}</template>
        </el-table-column>
        <el-table-column prop="mealType" label="餐次" width="80">
          <template #default="{ row }">{{ mealMap[row.mealType] || row.mealType }}</template>
        </el-table-column>
        <el-table-column prop="dishName" label="菜品" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200" />
      </el-table>
    </el-card>

    <!-- 菜品库 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>菜品库</span>
          <el-button type="primary" @click="showAddDishDialog">
            <el-icon><Plus /></el-icon> 添加菜品
          </el-button>
        </div>
      </template>

      <el-table :data="dishes" stripe border style="width: 100%" v-loading="dishLoading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="dishName" label="菜品名称" width="150" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="description" label="描述" min-width="200" />
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
          <el-input v-model="dishForm.dishName" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="dishForm.category" style="width: 100%">
            <el-option label="主食" value="主食" />
            <el-option label="热菜" value="热菜" />
            <el-option label="凉菜" value="凉菜" />
            <el-option label="汤" value="汤" />
            <el-option label="水果" value="水果" />
            <el-option label="点心" value="点心" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="dishForm.description" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dishDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleDishSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 安排食谱对话框 -->
    <el-dialog v-model="arrangeDialogVisible" title="安排食谱" width="500px">
      <el-form ref="arrangeFormRef" :model="arrangeForm" :rules="arrangeRules" label-width="80px">
        <el-form-item label="星期" prop="dayOfWeek">
          <el-select v-model="arrangeForm.dayOfWeek" style="width: 100%">
            <el-option v-for="(label, key) in dayMap" :key="key" :label="label" :value="key" />
          </el-select>
        </el-form-item>
        <el-form-item label="餐次" prop="mealType">
          <el-select v-model="arrangeForm.mealType" style="width: 100%">
            <el-option v-for="(label, key) in mealMap" :key="key" :label="label" :value="key" />
          </el-select>
        </el-form-item>
        <el-form-item label="菜品" prop="dishId">
          <el-select v-model="arrangeForm.dishId" filterable style="width: 100%">
            <el-option v-for="dish in dishes" :key="dish.id" :label="`${dish.dishName} (${dish.category || ''})`" :value="dish.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="arrangeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleArrange" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { menuApi } from '../api'

const weeklyMenu = ref([])
const dishes = ref([])
const menuLoading = ref(false)
const dishLoading = ref(false)
const submitting = ref(false)

const dishDialogVisible = ref(false)
const arrangeDialogVisible = ref(false)
const isEditDish = ref(false)
const editDishId = ref(null)
const dishFormRef = ref()
const arrangeFormRef = ref()

const dayMap = { '1': '周一', '2': '周二', '3': '周三', '4': '周四', '5': '周五' }
const mealMap = { 'breakfast': '早餐', 'lunch': '午餐', 'dinner': '晚餐', 'snack': '点心' }

const dishForm = reactive({
  dishName: '',
  category: '热菜',
  description: ''
})

const dishRules = {
  dishName: [{ required: true, message: '请输入菜品名称', trigger: 'blur' }]
}

const arrangeForm = reactive({
  dayOfWeek: '',
  mealType: '',
  dishId: null
})

const arrangeRules = {
  dayOfWeek: [{ required: true, message: '请选择星期', trigger: 'change' }],
  mealType: [{ required: true, message: '请选择餐次', trigger: 'change' }],
  dishId: [{ required: true, message: '请选择菜品', trigger: 'change' }]
}

const loadWeeklyMenu = async () => {
  menuLoading.value = true
  try {
    const res = await menuApi.getCurrentWeek()
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

const showAddDishDialog = () => {
  isEditDish.value = false
  editDishId.value = null
  Object.assign(dishForm, { dishName: '', category: '热菜', description: '' })
  dishDialogVisible.value = true
}

const showEditDishDialog = (row) => {
  isEditDish.value = true
  editDishId.value = row.id
  Object.assign(dishForm, {
    dishName: row.dishName,
    category: row.category || '热菜',
    description: row.description || ''
  })
  dishDialogVisible.value = true
}

const handleDishSubmit = async () => {
  await dishFormRef.value.validate()
  submitting.value = true
  try {
    const res = isEditDish.value
      ? await menuApi.updateDish?.(editDishId.value, dishForm) || { success: false, message: '暂不支持编辑' }
      : await menuApi.createDish(dishForm)
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
  const res = await menuApi.deleteDish?.(row.id) || { success: false, message: '暂不支持删除' }
  if (res.success) {
    ElMessage.success('删除成功')
    loadDishes()
  } else {
    ElMessage.error(res.message)
  }
}

const showArrangeDialog = () => {
  Object.assign(arrangeForm, { dayOfWeek: '', mealType: '', dishId: null })
  arrangeDialogVisible.value = true
}

const handleArrange = async () => {
  await arrangeFormRef.value.validate()
  submitting.value = true
  try {
    const res = await menuApi.arrangeMenu(arrangeForm)
    if (res.success) {
      ElMessage.success('安排成功')
      arrangeDialogVisible.value = false
      loadWeeklyMenu()
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    submitting.value = false
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
</style>
