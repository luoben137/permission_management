<template>
  <div class="roles-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="handleAdd">新增角色</el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="角色名" />
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
        label-width="80px"
      >
        <el-form-item label="角色名" prop="name">
          <el-input v-model="formData.name" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" />
        </el-form-item>
        <el-form-item label="权限" prop="permissionIds">
          <el-select
            v-model="formData.permissionIds"
            multiple
            placeholder="请选择权限"
            style="width: 100%"
          >
            <el-option
              v-for="permission in permissions"
              :key="permission.id"
              :label="permission.name"
              :value="permission.id"
            />
          </el-select>
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
import { getRoleList, createRole, updateRole, deleteRole } from '@/api/role'
import { getPermissionList } from '@/api/permission'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const permissions = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增角色')
const formRef = ref(null)
const editingId = ref(null)

const pagination = reactive({
  page: 0,
  size: 10,
  total: 0
})

const formData = reactive({
  name: '',
  description: '',
  permissionIds: []
})

const formRules = {
  name: [
    { required: true, message: '请输入角色名', trigger: 'blur' }
  ]
}

const fetchData = async () => {
  loading.value = true
  try {
    const response = await getRoleList({
      page: pagination.page,
      size: pagination.size,
      sortBy: 'id',
      direction: 'DESC'
    })
    tableData.value = response.data.content
    pagination.total = response.data.totalElements
  } catch (error) {
    ElMessage.error('获取角色列表失败')
  } finally {
    loading.value = false
  }
}

const fetchPermissions = async () => {
  try {
    const response = await getPermissionList({ page: 0, size: 1000 })
    permissions.value = response.data.content
  } catch (error) {
    ElMessage.error('获取权限列表失败')
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增角色'
  editingId.value = null
  formData.name = ''
  formData.description = ''
  formData.permissionIds = []
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑角色'
  editingId.value = row.id
  formData.name = row.name
  formData.description = row.description
  formData.permissionIds = row.permissions ? row.permissions.map(p => p.id) : []
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (editingId.value) {
          await updateRole(editingId.value, formData)
          ElMessage.success('更新成功')
        } else {
          await createRole(formData)
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
    await ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
      type: 'warning'
    })
    await deleteRole(row.id)
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
  fetchPermissions()
})
</script>

<style scoped>
.roles-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

