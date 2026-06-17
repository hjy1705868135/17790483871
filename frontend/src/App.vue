<template>
  <div class="min-vh-100 bg-light">
    <!-- 导航栏 -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
      <div class="container">
        <a class="navbar-brand" href="#">无人机信息管理系统</a>
        <div class="navbar-nav">
          <a class="nav-link active" href="#" @click="showList = true; showForm = false; selectedUav = null">
            无人机列表
          </a>
          <a class="nav-link" href="#" @click="openAddForm">
            新增无人机
          </a>
        </div>
      </div>
    </nav>

    <!-- 主内容区 -->
    <div class="container mt-4">
      <!-- 列表页面 -->
      <UavList 
        v-if="showList" 
        @edit="openEditForm" 
        @view="openDetail"
        @delete="handleDelete"
      />

      <!-- 表单页面 -->
      <UavForm 
        v-if="showForm" 
        :uav="selectedUav" 
        @submit="handleSubmit" 
        @cancel="showList = true; showForm = false; selectedUav = null"
      />

      <!-- 详情弹窗 -->
      <UavDetail 
        v-if="showDetail" 
        :uav="selectedUav" 
        @close="showDetail = false"
        @edit="openEditFormFromDetail"
      />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import UavList from './components/UavList.vue'
import UavForm from './components/UavForm.vue'
import UavDetail from './components/UavDetail.vue'
import { uavApi } from './utils/api'

const showList = ref(true)
const showForm = ref(false)
const showDetail = ref(false)
const selectedUav = ref(null)

const openAddForm = () => {
  selectedUav.value = null
  showList.value = false
  showForm.value = true
}

const openEditForm = (uav) => {
  selectedUav.value = { ...uav }
  showList.value = false
  showForm.value = true
}

const openDetail = (uav) => {
  selectedUav.value = uav
  showDetail.value = true
}

const openEditFormFromDetail = () => {
  showDetail.value = false
  showForm.value = true
}

const handleSubmit = async (data) => {
  try {
    if (selectedUav.value) {
      await uavApi.update(selectedUav.value.id, data)
    } else {
      await uavApi.create(data)
    }
    alert('操作成功！')
    showForm.value = false
    showList.value = true
    selectedUav.value = null
  } catch (error) {
    alert('操作失败：' + (error.response?.data?.message || error.message))
  }
}

const handleDelete = async (id) => {
  if (!confirm('确定要删除该无人机吗？')) return
  try {
    await uavApi.delete(id)
    alert('删除成功！')
  } catch (error) {
    alert('删除失败：' + (error.response?.data?.message || error.message))
  }
}
</script>
