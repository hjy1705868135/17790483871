<template>
  <div class="card">
    <div class="card-header">
      <h3 class="card-title">无人机列表</h3>
    </div>
    <div class="card-body">
      <!-- 搜索框 -->
      <div class="row mb-4">
        <div class="col-md-4">
          <input 
            type="text" 
            class="form-control" 
            placeholder="搜索编号、型号、制造商..." 
            v-model="keyword"
            @keyup.enter="loadData"
          />
        </div>
        <div class="col-md-2">
          <button class="btn btn-primary" @click="loadData">搜索</button>
        </div>
      </div>

      <!-- 表格 -->
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th>编号</th>
            <th>型号</th>
            <th>制造商</th>
            <th>最大飞行时间(分钟)</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="uav in list" :key="uav.id">
            <td>{{ uav.code }}</td>
            <td>{{ uav.model }}</td>
            <td>{{ uav.manufacturer }}</td>
            <td>{{ uav.maxFlightTimeMinutes }}</td>
            <td>
              <span 
                class="badge" 
                :class="getStatusClass(uav.status)"
              >
                {{ getStatusText(uav.status) }}
              </span>
            </td>
            <td>
              <button 
                class="btn btn-sm btn-info mr-1" 
                @click="$emit('view', uav)"
              >
                查看
              </button>
              <button 
                class="btn btn-sm btn-primary mr-1" 
                @click="$emit('edit', uav)"
              >
                编辑
              </button>
              <button 
                class="btn btn-sm btn-danger" 
                @click="$emit('delete', uav.id)"
              >
                删除
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 分页 -->
      <nav v-if="totalPages > 1" class="float-right">
        <ul class="pagination">
          <li class="page-item" :class="{ disabled: currentPage === 1 }">
            <button class="page-link" @click="prevPage">上一页</button>
          </li>
          <li 
            v-for="page in pageNumbers" 
            :key="page"
            class="page-item"
            :class="{ active: currentPage === page }"
          >
            <button class="page-link" @click="goToPage(page)">{{ page }}</button>
          </li>
          <li class="page-item" :class="{ disabled: currentPage === totalPages }">
            <button class="page-link" @click="nextPage">下一页</button>
          </li>
        </ul>
      </nav>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { uavApi } from '../utils/api'

const emit = defineEmits(['view', 'edit', 'delete'])

const list = ref([])
const currentPage = ref(1)
const totalPages = ref(1)
const keyword = ref('')

const pageNumbers = computed(() => {
  const pages = []
  for (let i = 1; i <= totalPages.value; i++) {
    pages.push(i)
  }
  return pages
})

const loadData = async () => {
  try {
    const response = await uavApi.list({
      page: currentPage.value,
      size: 10,
      keyword: keyword.value
    })
    list.value = response.data.data
    totalPages.value = response.data.totalPages
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    loadData()
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    loadData()
  }
}

const goToPage = (page) => {
  currentPage.value = page
  loadData()
}

const getStatusClass = (status) => {
  const classes = {
    'ACTIVE': 'badge-success',
    'INACTIVE': 'badge-secondary',
    'MAINTENANCE': 'badge-warning'
  }
  return classes[status] || 'badge-secondary'
}

const getStatusText = (status) => {
  const texts = {
    'ACTIVE': '运行中',
    'INACTIVE': '停用',
    'MAINTENANCE': '维护中'
  }
  return texts[status] || status
}

onMounted(() => {
  loadData()
})
</script>
