<template>
  <div class="courses-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课程管理</span>
          <el-button type="primary" @click="showAddDialog">
            <el-icon><Plus /></el-icon> 添加课程
          </el-button>
        </div>
      </template>

      <!-- 课程表格 -->
      <el-table :data="courses" stripe border style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="courseName" label="课程名称" width="150" />
        <el-table-column prop="currentCount" label="当前人数" width="100" />
        <el-table-column prop="maxCount" label="最大人数" width="100" />
        <el-table-column prop="description" label="课程描述" min-width="200" />
        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <el-button size="small" @click="showStudentList(row)">学员</el-button>
            <el-button size="small" @click="showEditDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑课程对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑课程' : '添加课程'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" />
        </el-form-item>
        <el-form-item label="最大人数" prop="maxCount">
          <el-input-number v-model="form.maxCount" :min="1" :max="100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 学员列表对话框 -->
    <el-dialog v-model="studentDialogVisible" :title="`${currentCourse?.courseName || ''} - 学员列表`" width="600px">
      <el-table :data="courseStudents" stripe border style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="genderName" label="性别" width="60" />
        <el-table-column prop="className" label="班级" width="100" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="handleDropCourse(row)">退课</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 选课操作 -->
      <el-divider>添加学员</el-divider>
      <el-form :inline="true">
        <el-form-item label="选择幼儿">
          <el-select v-model="selectChildId" placeholder="选择幼儿" filterable style="width: 200px">
            <el-option v-for="child in allChildren" :key="child.id" :label="`${child.name} (${child.className || ''})`" :value="child.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSelectCourse" :disabled="!selectChildId">选课</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { courseApi, childApi } from '../api'

const courses = ref([])
const allChildren = ref([])
const dialogVisible = ref(false)
const studentDialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const submitting = ref(false)
const formRef = ref()
const currentCourse = ref(null)
const courseStudents = ref([])
const selectChildId = ref(null)

const form = reactive({
  courseName: '',
  maxCount: 30,
  description: ''
})

const rules = {
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  maxCount: [{ required: true, message: '请输入最大人数', trigger: 'change' }]
}

const loadCourses = async () => {
  const res = await courseApi.getAll()
  if (res.success) courses.value = res.data
}

const loadChildren = async () => {
  const res = await childApi.getAll()
  if (res.success) allChildren.value = (res.data || []).filter(c => c.status === 1)
}

const showAddDialog = () => {
  isEdit.value = false
  editId.value = null
  Object.assign(form, { courseName: '', maxCount: 30, description: '' })
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  isEdit.value = true
  editId.value = row.id
  Object.assign(form, {
    courseName: row.courseName,
    maxCount: row.maxCount,
    description: row.description || ''
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    const res = isEdit.value ? await courseApi.update(editId.value, form) : await courseApi.create(form)
    if (res.success) {
      ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
      dialogVisible.value = false
      loadCourses()
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确认删除课程「${row.courseName}」？`, '确认删除', { type: 'warning' })
  const res = await courseApi.delete(row.id)
  if (res.success) {
    ElMessage.success('删除成功')
    loadCourses()
  } else {
    ElMessage.error(res.message)
  }
}

const showStudentList = async (row) => {
  currentCourse.value = row
  selectChildId.value = null
  studentDialogVisible.value = true
  await loadCourseStudents(row.id)
  await loadChildren()
}

const loadCourseStudents = async (courseId) => {
  // Reuse getAll children and filter by course, or use getChildCourses if available
  const res = await courseApi.getAll()
  if (res.success) {
    const course = (res.data || []).find(c => c.id === courseId)
    courseStudents.value = course?.students || []
  }
}

const handleSelectCourse = async () => {
  if (!selectChildId.value || !currentCourse.value) return
  const res = await courseApi.selectCourse(selectChildId.value, currentCourse.value.id)
  if (res.success) {
    ElMessage.success('选课成功')
    selectChildId.value = null
    loadCourseStudents(currentCourse.value.id)
    loadCourses()
  } else {
    ElMessage.error(res.message)
  }
}

const handleDropCourse = async (row) => {
  await ElMessageBox.confirm(`确认为「${row.name}」退选此课程？`, '确认退课', { type: 'warning' })
  const res = await courseApi.dropCourse(row.id, currentCourse.value.id)
  if (res.success) {
    ElMessage.success('退课成功')
    loadCourseStudents(currentCourse.value.id)
    loadCourses()
  } else {
    ElMessage.error(res.message)
  }
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
