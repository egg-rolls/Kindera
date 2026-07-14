<template>
  <div class="transfers-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>调班管理</span>
          <el-button type="primary" @click="showTransferDialog">
            <el-icon><Switch /></el-icon> 发起调班
          </el-button>
        </div>
      </template>

      <!-- 调班记录表格 -->
      <el-table :data="transfers" stripe border style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="childName" label="幼儿姓名" width="120" />
        <el-table-column prop="oldClassName" label="原班级" width="120" />
        <el-table-column prop="newClassName" label="目标班级" width="120" />
        <el-table-column prop="transferDate" label="调班日期" width="120" />
        <el-table-column prop="reason" label="调班原因" min-width="200" />
        <el-table-column prop="operatorName" label="操作人" width="100" />
      </el-table>
    </el-card>

    <!-- 发起调班对话框 -->
    <el-dialog v-model="dialogVisible" title="发起调班" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="幼儿" prop="childId">
          <el-select v-model="form.childId" filterable placeholder="选择幼儿" style="width: 100%" @change="onChildChange">
            <el-option v-for="child in children" :key="child.id" :label="`${child.name} (${child.className || ''})`" :value="child.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="当前班级">
          <el-input :value="currentClassName" disabled />
        </el-form-item>
        <el-form-item label="目标班级" prop="toClassId">
          <el-select v-model="form.toClassId" placeholder="选择目标班级" style="width: 100%">
            <el-option
              v-for="cls in availableClasses"
              :key="cls.id"
              :label="`${cls.className}（${cls.currentCount}/${cls.maxCount}人）`"
              :value="cls.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="调班原因" prop="reason">
          <el-input v-model="form.reason" type="textarea" :rows="3" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { transferApi, classApi, childApi } from '../api'

const transfers = ref([])
const classes = ref([])
const children = ref([])
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const formRef = ref()

const form = reactive({
  childId: null,
  toClassId: null,
  reason: ''
})

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    const payload = {
      childId: form.childId,
      newClassId: form.toClassId,
      operatorId: user.id || 1,
      remark: form.reason
    }
    const res = await transferApi.transfer(payload)
    if (res.success) {
      ElMessage.success('调班成功')
      dialogVisible.value = false
      loadTransfers()
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    submitting.value = false
  }
}

const rules = {
  childId: [{ required: true, message: '请选择幼儿', trigger: 'change' }],
  toClassId: [{ required: true, message: '请选择目标班级', trigger: 'change' }],
  reason: [{ required: true, message: '请输入调班原因', trigger: 'blur' }]
}

const currentClassName = computed(() => {
  if (!form.childId) return ''
  const child = children.value.find(c => c.id === form.childId)
  return child?.className || ''
})

const availableClasses = computed(() => {
  if (!form.childId) return classes.value
  const child = children.value.find(c => c.id === form.childId)
  if (!child) return classes.value
  return classes.value.filter(cls => cls.id !== child.classId)
})

const loadTransfers = async () => {
  loading.value = true
  try {
    const res = await transferApi.getAll()
    if (res.success) transfers.value = res.data || []
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

const showTransferDialog = () => {
  Object.assign(form, { childId: null, toClassId: null, reason: '' })
  dialogVisible.value = true
  loadChildren()
}

const onChildChange = () => {
  form.toClassId = null
}

onMounted(() => {
  loadTransfers()
  loadClasses()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
