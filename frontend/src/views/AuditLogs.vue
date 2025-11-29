<template>
  <div class="audit-logs-container">
    <el-card>
      <template #header>
        <span>审计日志</span>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="操作类型">
          <el-select v-model="searchForm.action" placeholder="请选择" clearable>
            <el-option label="CREATE" value="CREATE" />
            <el-option label="UPDATE" value="UPDATE" />
            <el-option label="DELETE" value="DELETE" />
            <el-option label="LOGIN" value="LOGIN" />
            <el-option label="LOGOUT" value="LOGOUT" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input v-model="searchForm.userId" placeholder="请输入用户ID" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="action" label="操作" width="100" />
        <el-table-column prop="resourceType" label="资源类型" width="120" />
        <el-table-column prop="resourceId" label="资源ID" width="100" />
        <el-table-column prop="details" label="详情" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP" width="120" />
        <el-table-column prop="createdAt" label="时间" width="180" />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAuditLogList } from '@/api/audit'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])

const pagination = reactive({
  page: 0,
  size: 10,
  total: 0
})

const searchForm = reactive({
  action: '',
  userId: null
})

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }
    if (searchForm.action) {
      params.action = searchForm.action
    }
    if (searchForm.userId) {
      params.userId = parseInt(searchForm.userId)
    }

    const response = await getAuditLogList(params)
    tableData.value = response.data.content
    pagination.total = response.data.totalElements
  } catch (error) {
    ElMessage.error('获取审计日志失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 0
  fetchData()
}

const handleReset = () => {
  searchForm.action = ''
  searchForm.userId = null
  pagination.page = 0
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.audit-logs-container {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}
</style>

