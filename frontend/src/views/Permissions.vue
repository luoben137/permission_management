<template>
  <div class="permissions-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>权限点管理</span>
          <el-button type="primary" @click="handleAdd">新增权限点</el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="权限名称" />
        <el-table-column prop="code" label="权限代码" />
        <el-table-column prop="url" label="URL" />
        <el-table-column prop="method" label="方法" width="100" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
        style="margin-top: 20px"
      />
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="权限名称" prop="name">
          <el-input v-model="formData.name" />
        </el-form-item>
        <el-form-item label="权限代码" prop="code">
          <el-input v-model="formData.code" />
        </el-form-item>
        <el-form-item label="URL" prop="url">
          <el-input v-model="formData.url" />
        </el-form-item>
        <el-form-item label="方法" prop="method">
          <el-select v-model="formData.method" placeholder="请选择方法">
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
            <el-option label="*" value="*" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getPermissionList, createPermission, updatePermission, deletePermission } from '@/api/permission'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增权限点')
const formRef = ref(null)
const editingId = ref(null)

const pagination = reactive({
  page: 0,
  size: 10,
  total: 0
})

const formData = reactive({
  name: '',
  code: '',
  url: '',
  method: '',
  description: ''
})

const formRules = {
  name: [
    { required: true, message: '请输入权限名称', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入权限代码', trigger: 'blur' }
  ],
  url: [
    { required: true, message: '请输入URL', trigger: 'blur' }
  ]
}

const fetchData = async () => {
  loading.value = true
  try {
    const response = await getPermissionList({
      page: pagination.page,
      size: pagination.size,
      sortBy: 'id',
      direction: 'DESC'
    })
    tableData.value = response.data.content
    pagination.total = response.data.totalElements
  } catch (error) {
    ElMessage.error('获取权限列表失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增权限点'
  editingId.value = null
  formData.name = ''
  formData.code = ''
  formData.url = ''
  formData.method = ''
  formData.description = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑权限点'
  editingId.value = row.id
  formData.name = row.name
  formData.code = row.code
  formData.url = row.url
  formData.method = row.method
  formData.description = row.description
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (editingId.value) {
          await updatePermission(editingId.value, formData)
          ElMessage.success('更新成功')
        } else {
          await createPermission(formData)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该权限点吗？', '提示', {
      type: 'warning'
    })
    await deletePermission(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.permissions-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

