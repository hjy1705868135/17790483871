<template>
  <div class="order-list">
    <div class="order-list__header">
      <h2 class="order-list__title">我的订单</h2>
      <div class="order-list__tabs">
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane label="全部" name="all" />
          <el-tab-pane label="待付款" name="PENDING_PAYMENT" />
          <el-tab-pane label="待发货" name="PAID" />
          <el-tab-pane label="待收货" name="SHIPPED" />
          <el-tab-pane label="已完成" name="COMPLETED" />
        </el-tabs>
      </div>
    </div>
    
    <div v-if="loading" class="order-list__loading">
      <el-loading text="加载中..." />
    </div>
    
    <div v-else-if="error" class="order-list__error">
      <el-alert type="error" :title="error" show-icon />
    </div>
    
    <div v-else-if="orders.length === 0" class="order-list__empty">
      <div class="order-list__empty-icon">📦</div>
      <div class="order-list__empty-text">暂无订单</div>
    </div>
    
    <div v-else class="order-list__content">
      <div v-for="order in orders" :key="order.id" class="order-list__item">
        <div class="order-list__item-header">
          <span class="order-list__order-no">订单号: {{ order.orderNo }}</span>
          <span class="order-list__status" :class="getStatusClass(order.status)">
            {{ getStatusText(order.status) }}
          </span>
        </div>
        
        <div class="order-list__item-products">
          <div 
            v-for="item in order.items" 
            :key="item.id" 
            class="order-list__product-item"
            @click="goToProductDetail(item.productId)"
          >
            <img :src="item.productImage" :alt="item.productName" class="order-list__product-image" />
            <div class="order-list__product-info">
              <span class="order-list__product-name">{{ item.productName }}</span>
              <span class="order-list__product-spec">规格: {{ item.quantity }}件</span>
              <span class="order-list__product-price">¥{{ item.price }}</span>
            </div>
          </div>
        </div>
        
        <div class="order-list__item-footer">
          <span class="order-list__total">
            共{{ order.itemCount }}件商品 合计: 
            <span class="order-list__total-price">¥{{ order.totalAmount }}</span>
          </span>
          <div class="order-list__actions">
            <el-button 
              v-if="order.status === 'PENDING_PAYMENT'"
              type="primary" 
              size="small"
              @click="handlePay(order.id)"
            >
              立即支付
            </el-button>
            <el-button 
              v-if="order.status === 'SHIPPED'"
              type="primary" 
              size="small"
              @click="handleConfirm(order.id)"
            >
              确认收货
            </el-button>
            <el-button 
              v-if="order.status === 'COMPLETED'"
              type="text" 
              size="small"
              @click="handleReview(order.id)"
            >
              评价
            </el-button>
          </div>
        </div>
      </div>
    </div>
    
    <div v-if="total > pageSize" class="order-list__pagination">
      <el-pagination
        :current-page="page"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { orderApi } from '../api'

const router = useRouter()

const orders = ref([])
const loading = ref(false)
const error = ref(null)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const activeTab = ref('all')

const fetchOrders = async () => {
  loading.value = true
  error.value = null
  
  try {
    const params = {
      page: page.value,
      size: pageSize.value,
      status: activeTab.value === 'all' ? undefined : activeTab.value
    }
    
    const response = await orderApi.getOrders(params)
    orders.value = response.records || []
    total.value = response.total || 0
  } catch (err) {
    error.value = '获取订单列表失败'
    console.error('Fetch orders error:', err)
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  page.value = 1
  fetchOrders()
}

const handlePageChange = (currentPage) => {
  page.value = currentPage
  fetchOrders()
}

const getStatusText = (status) => {
  const statusMap = {
    'PENDING_PAYMENT': '待付款',
    'PAID': '待发货',
    'PROCESSING': '处理中',
    'SHIPPED': '待收货',
    'DELIVERED': '已送达',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消',
    'REFUNDING': '退款中',
    'REFUNDED': '已退款'
  }
  return statusMap[status] || status
}

const getStatusClass = (status) => {
  const classMap = {
    'PENDING_PAYMENT': 'order-list__status--warning',
    'PAID': 'order-list__status--info',
    'PROCESSING': 'order-list__status--info',
    'SHIPPED': 'order-list__status--primary',
    'DELIVERED': 'order-list__status--success',
    'COMPLETED': 'order-list__status--success',
    'CANCELLED': 'order-list__status--default',
    'REFUNDING': 'order-list__status--warning',
    'REFUNDED': 'order-list__status--default'
  }
  return classMap[status] || ''
}

const goToProductDetail = (productId) => {
  router.push(`/products/${productId}`)
}

const handlePay = (orderId) => {
  ElMessage.info(`支付订单 ${orderId}`)
}

const handleConfirm = (orderId) => {
  ElMessage.info(`确认收货 ${orderId}`)
}

const handleReview = (orderId) => {
  ElMessage.info(`评价订单 ${orderId}`)
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-list {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.order-list__header {
  margin-bottom: 20px;
}

.order-list__title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #333;
}

.order-list__tabs {
  background: #fff;
  border-radius: 8px;
  padding: 8px;
}

.order-list__loading {
  display: flex;
  justify-content: center;
  padding: 40px;
}

.order-list__error {
  margin-bottom: 16px;
}

.order-list__empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 20px;
  background: #fff;
  border-radius: 8px;
}

.order-list__empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.order-list__empty-text {
  font-size: 16px;
  color: #999;
}

.order-list__content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-list__item {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
}

.order-list__item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.order-list__order-no {
  font-size: 14px;
  color: #666;
}

.order-list__status {
  font-size: 14px;
  font-weight: 500;
  padding: 4px 12px;
  border-radius: 4px;
  
  &--warning {
    background: #fff7e6;
    color: #d46b08;
  }
  
  &--info {
    background: #e6f7ff;
    color: #1890ff;
  }
  
  &--primary {
    background: #f0f5ff;
    color: #597ef7;
  }
  
  &--success {
    background: #f6ffed;
    color: #52c41a;
  }
  
  &--default {
    background: #f5f5f5;
    color: #666;
  }
}

.order-list__item-products {
  margin-bottom: 12px;
}

.order-list__product-item {
  display: flex;
  gap: 12px;
  padding: 8px 0;
  cursor: pointer;
  
  &:hover {
    background: #fafafa;
  }
}

.order-list__product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.order-list__product-info {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  flex: 1;
}

.order-list__product-name {
  font-size: 14px;
  color: #333;
}

.order-list__product-spec {
  font-size: 12px;
  color: #999;
}

.order-list__product-price {
  font-size: 14px;
  color: #ff4757;
  font-weight: 500;
}

.order-list__item-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.order-list__total {
  font-size: 14px;
  color: #666;
}

.order-list__total-price {
  font-size: 18px;
  color: #ff4757;
  font-weight: 600;
}

.order-list__pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>