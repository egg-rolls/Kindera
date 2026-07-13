<template>
  <div class="attendance-page">
    <!-- 考勤记录 -->
    <el-card style="margin-bottom: 20px">
      <template #header>
        <div class="card-header">
          <span>考勤记录</span>
          <div>
            <el-button type="success" @click="showBatchDialog">
              <el-icon><Finished /></el-icon> 批量考勤
            </el-button>
            <el-button type="primary" @click="showSingleDialog">
              <el-icon><Edit /></el-icon> 单个考勤
            </el-button>
          </div>
        </div>
      </template>

      <!-- 筛选条件 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="班级">
          <el-select v-model="searchClassId" placeholder="选择班级" clearable @change="loadRecords">
            <el-option v-for="cls in classes" :key="cls.id" :label="cls.className" :value="cls.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker v-model="searchDate" type="date" value-format="YYYY-MM-DD" clearable @change="loadRecords" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadRecords">查询</el-button>
        </el-form-item>
      </el-form>

      <!-- 考勤统计 -->
      <el-row :gutter="20" style="margin-bottom: 20px" v-if="stats.total > 0">
        <el-col :span="6"><el-statistic title="总记录" :value="stats.total" /></el-col>
        <el-col :span="6"><el-statistic title="出勤" :value="stats.present" /></el-col>
        <el-col :span="6"><el-statistic title="缺勤" :value="stats.absent" /></el-col>
        <el-col :span="6"><el-statistic title="出勤率" :value="stats.rate" suffix="%" /></el-col>
      </el-row>

      <!-- 记录表格 -->
      <el-table :data="records" stripe border style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="childName" label="幼儿姓名" width="120" />
        <el-table-column prop="className" label="班级" width="100" />
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="statusName" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'warning' : 'danger'">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" />
      </el-table>
    </el-card>

    <!-- 单个考勤对话框 -->
    <el-dialog v-model="singleDialogVisible" title="记录考勤" width="500px">
      <el-form ref="singleFormRef" :model="singleForm" :rules="singleRules" label-width="80px">
        <el-form-item label="幼儿" prop="childId">
          <el-select v-model="singleForm.childId" filterable placeholder="选择幼儿" style="width: 100%">
            <el-option v-for="child in children" :key="child.id" :label="`${child.name} (${child.className || ''})`" :value="child.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期" prop="date">
          <el-date-picker v-model="singleForm.date" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="singleForm.status">
            <el-radio :value="1">出勤</el-radio>
            <el-radio :value="2">迟到</el-radio>
            <el-radio :value="0">缺勤</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="singleForm.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="singleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSingleRecord" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 批量考勤对话框 -->
    <el-dialog v-model="batchDialogVisible" title="批量考勤" width="500px">
      <el-form ref="batchFormRef" :model="batchForm" :rules="batchRules" label-width="80px">
        <el-form-item label="班级" prop="classId">
          <el-select v-model="batchForm.classId" placeholder="选择班级" style="width: 100%" @change="loadClassChildren">
            <el-option v-for="cls in classes" :key="cls.id" :label="cls.className" :value="cls.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期" prop="date">
          <el-date-picker v-model="batchForm.date" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="batchForm.status">
            <el-radio :value="1">出勤</el-radio>
            <el-radio :value="0">缺勤</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="batchForm.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBatchRecord" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { attendanceApi, classApi, childApi } from '../api'

const records = ref([])
const classes = ref([])
const children = ref([])
const loading = ref(false)
const submitting = ref(false)

const searchClassId = ref('')
const searchDate = ref('')

const singleDialogVisible = ref(false)
const batchDialogVisible = ref(false)
const singleFormRef = ref()
const batchFormRef = ref()

const singleForm = reactive({
  childId: null,
  date: new Date().toISOString().slice(0, 10),
  status: 1,
  remark: ''
})

const batchForm = reactive({
  classId: null,
  date: new Date().toISOString().slice(0, 10),
  status: 1,
  remark: ''
})

const singleRules = {
  childId: [{ required: true, message: '请选择幼儿', trigger: 'change' }],
  date: [{ required: true, message: '请选择日期', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const batchRules = {
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }],
  date: [{ required: true, message: '请选择日期', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const stats = computed(() => {
  const total = records.value.length
  const present = records.value.filter(r => r.status === 1).length
  const absent = records.value.filter(r => r.status === 0).length
  const rate = total > 0 ? ((present / total) * 100).toFixed(1) : 0
  return { total, present, absent, rate }
})

const loadRecords = async () => {
  loading.value = true
  try {
    const params = {}
    if (searchClassId.value) params.classId = searchClassId.value
    if (searchDate.value) params.date = searchDate.value
    const res = await attendanceApi.getRecords(params)
    if (res.success) records.value = res.data || []
  } finally {
    loading.value = false
  }
}

const loadClasses = async () => {
  const res = await classApi.getAll()
  if (res.success) classes.value = res.data || []
}

const loadChildren = async () => {
  const res = await childApi.getAll()
  if (res.success) children.value = (res.data || []).filter(c => c.status === 1)
}

const loadClassChildren = async () => {
  if (!batchForm.classId) return
  const res = await childApi.getAll({ classId: batchForm.classId })
  if (res.success) children.value = (res.data || []).filter(c => c.status === 1)
}

const showSingleDialog = () => {
  Object.assign(singleForm, { childId: null, date: new Date().toISOString().slice(0, 10), status: 1, remark: '' })
  singleDialogVisible.value = true
  loadChildren()
}

const showBatchDialog = () => {
  Object.assign(batchForm, { classId: null, date: new Date().toISOString().slice(0, 10), status: 1, remark: '' })
  batchDialogVisible.value = true
}

const handleSingleRecord = async () => {
  await singleFormRef.value.validate()
  submitting.value = true
  try {
    const res = await attendanceApi.record(singleForm)
    if (res.success) {
      ElMessage.success('考勤记录成功')
      singleDialogVisible.value = false
      loadRecords()
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    submitting.value = false
  }
}

const handleBatchRecord = async () => {
  await batchFormRef.value.validate()
  submitting.value = true
  try {
    const res = await attendanceApi.batchRecord(batchForm)
    if (res.success) {
      ElMessage.success('批量考勤成功')
      batchDialogVisible.value = false
      loadRecords()
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadRecords()
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
