<template>
  <div class="children-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>幼儿管理</span>
          <el-button type="primary" @click="showAddDialog">
            <el-icon><Plus /></el-icon> 添加幼儿
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="姓名">
          <el-input v-model="searchName" placeholder="输入姓名搜索" clearable @clear="loadChildren" @keyup.enter="loadChildren" />
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="searchClassId" placeholder="选择班级" clearable @change="loadChildren">
            <el-option v-for="cls in classes" :key="cls.id" :label="cls.className" :value="cls.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadChildren">搜索</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="children" stripe border style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="genderName" label="性别" width="60" />
        <el-table-column prop="birthDate" label="出生日期" width="120" />
        <el-table-column prop="className" label="班级" width="100" />
        <el-table-column prop="parentName" label="家长" width="100" />
        <el-table-column prop="parentPhone" label="联系电话" width="130" />
        <el-table-column prop="statusName" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="showEditDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)" :disabled="row.status === 0">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑幼儿' : '添加幼儿'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio label="M">男</el-radio>
            <el-radio label="F">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期" prop="birthDate">
          <el-date-picker v-model="form.birthDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="班级" prop="classId">
          <el-select v-model="form.classId" style="width: 100%">
            <el-option v-for="cls in classes" :key="cls.id" :label="`${cls.className}（${cls.currentCount}/${cls.maxCount}人）`" :value="cls.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="家长姓名" prop="parentName">
          <el-input v-model="form.parentName" />
        </el-form-item>
        <el-form-item label="联系电话" prop="parentPhone">
          <el-input v-model="form.parentPhone" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { childApi, classApi } from '../api'

const children = ref([])
const classes = ref([])
const searchName = ref('')
const searchClassId = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const submitting = ref(false)
const formRef = ref()

const form = reactive({
  name: '',
  gender: 'M',
  birthDate: '',
  classId: null,
  parentName: '',
  parentPhone: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  birthDate: [{ required: true, message: '请选择出生日期', trigger: 'change' }],
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }],
  parentName: [{ required: true, message: '请输入家长姓名', trigger: 'blur' }],
  parentPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

const loadChildren = async () => {
  const params = {}
  if (searchName.value) params.name = searchName.value
  if (searchClassId.value) params.classId = searchClassId.value
  const res = await childApi.getAll(params)
  if (res.success) children.value = res.data
}

const loadClasses = async () => {
  const res = await classApi.getAll()
  if (res.success) classes.value = res.data
}

const showAddDialog = () => {
  isEdit.value = false
  editId.value = null
  Object.assign(form, { name: '', gender: 'M', birthDate: '', classId: null, parentName: '', parentPhone: '' })
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  isEdit.value = true
  editId.value = row.id
  Object.assign(form, {
    name: row.name,
    gender: row.gender,
    birthDate: row.birthDate,
    classId: row.classId,
    parentName: row.parentName,
    parentPhone: row.parentPhone
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    const res = isEdit.value ? await childApi.update(editId.value, form) : await childApi.create(form)
    if (res.success) {
      ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
      dialogVisible.value = false
      loadChildren()
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确认删除幼儿「${row.name}」？删除后将标记为离园状态。`, '确认删除', { type: 'warning' })
  const res = await childApi.delete(row.id)
  if (res.success) {
    ElMessage.success('删除成功')
    loadChildren()
  } else {
    ElMessage.error(res.message)
  }
}

onMounted(() => {
  loadChildren()
  loadClasses()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}
</style>
