<template>
  <div class="order-detail">
    <div v-if="loading" class="order-detail__loading">
      <el-loading text="加载中..." />
    </div>
    
    <div v-else-if="error" class="order-detail__error">
      <el-alert type="error" :title="error" show-icon />
    </div>
    
    <div v-else class="order-detail__content">
      <div class="order-detail__header">
        <h2 class="order-detail__title">订单详情</h2>
        <span class="order-detail__status" :class="getStatusClass(order.status)">
          {{ getStatusText(order.status) }}
        </span>
      </div>
      
      <div class="order-detail__info">
        <div class="order-detail__info-item">
          <span class="order-detail__info-label">订单号:</span>
          <span class="order-detail__info-value">{{ order.orderNo }}</span>
        </div>
        <div class="order-detail__info-item">
          <span class="order-detail__info-label">创建时间:</span>
          <span class="order-detail__info-value">{{ order.createdAt }}</span>
        </div>
        <div class="order-detail__info-item">
          <span class="order-detail__info-label">支付方式:</span>
          <span class="order-detail__info-value">{{ order.paymentMethod }}</span>
        </div>
      </div>
      
      <div class="order-detail__section">
        <h3 class="order-detail__section-title">收货地址</h3>
        <div class="order-detail__address">
          <span class="order-detail__address-name">{{ order.shippingAddress.receiverName }}</span>
          <span class="order-detail__address-phone">{{ order.shippingAddress.phone }}</span>
          <span class="order-detail__address-detail">{{ order.shippingAddress.fullAddress }}</span>
        </div>
      </div>
      
      <div class="order-detail__section">
        <h3 class="order-detail__section-title">商品清单</h3>
        <div class="order-detail__products">
          <div 
            v-for="item in order.items" 
            :key="item.id" 
            class="order-detail__product-item"
          >
            <img :src="item.productImage" :alt="item.productName" class="order-detail__product-image" />
            <div class="order-detail__product-info">
              <span class="order-detail__product-name">{{ item.productName }}</span>
              <span class="order-detail__product-price">¥{{ item.price }}</span>
            </div>
            <div class="order-detail__product-actions">
              <span class="order-detail__product-quantity">x{{ item.quantity }}</span>
              <span class="order-detail__product-subtotal">¥{{ item.subtotal }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="order-detail__section">
        <h3 class="order-detail__section-title">订单金额</h3>
        <div class="order-detail__amount">
          <div class="order-detail__amount-item">
            <span class="order-detail__amount-label">商品金额:</span>
            <span class="order-detail__amount-value">¥{{ order.totalAmount }}</span>
          </div>
          <div class="order-detail__amount-item">
            <span class="order-detail__amount-label">运费:</span>
            <span class="order-detail__amount-value">¥0.00</span>
          </div>
          <div class="order-detail__amount-item order-detail__amount-total">
            <span class="order-detail__amount-label">实付金额:</span>
            <span class="order-detail__amount-value">¥{{ order.totalAmount }}</span>
          </div>
        </div>
      </div>
      
      <div class="order-detail__section">
        <h3 class="order-detail__section-title">订单状态</h3>
        <div class="order-detail__timeline">
          <div 
            v-for="(history, index) in order.statusHistory" 
            :key="history.id"
            class="order-detail__timeline-item"
            :class="{ 'order-detail__timeline-item--active': index === 0 }"
          >
            <div class="order-detail__timeline-dot"></div>
            <div class="order-detail__timeline-content">
              <span class="order-detail__timeline-status">{{ getStatusText(history.status) }}</span>
              <span class="order-detail__timeline-time">{{ history.createdAt }}</span>
              <span class="order-detail__timeline-operator">{{ history.operator }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="order-detail__actions">
        <el-button 
          v-if="order.status === 'PENDING_PAYMENT'"
          type="primary" 
          size="large"
          @click="handlePay"
        >
          立即支付
        </el-button>
        <el-button 
          v-if="order.status === 'SHIPPED'"
          type="primary" 
          size="large"
          @click="handleConfirm"
        >
          确认收货
        </el-button>
        <el-button 
          v-if="order.status === 'COMPLETED'"
          type="primary" 
          size="large"
          @click="handleReview"
        >
          评价商品
        </el-button>
        <el-button 
          v-if="order.status === 'PENDING_PAYMENT'"
          type="default" 
          size="large"
          @click="handleCancel"
        >
          取消订单
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { orderApi } from '../api'

const route = useRoute()

const order = ref({})
const loading = ref(false)
const error = ref(null)

const fetchOrder = async () => {
  loading.value = true
  error.value = null
  
  try {
    const orderId = route.params.id
    order.value = await orderApi.getOrderDetail(orderId)
    
    if (!order.value.items) {
      order.value.items = []
    }
    if (!order.value.statusHistory) {
      order.value.statusHistory = []
    }
  } catch (err) {
    error.value = '获取订单详情失败'
    console.error('Fetch order error:', err)
  } finally {
    loading.value = false
  }
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
    'PENDING_PAYMENT': 'order-detail__status--warning',
    'PAID': 'order-detail__status--info',
    'PROCESSING': 'order-detail__status--info',
    'SHIPPED': 'order-detail__status--primary',
    'DELIVERED': 'order-detail__status--success',
    'COMPLETED': 'order-detail__status--success',
    'CANCELLED': 'order-detail__status--default',
    'REFUNDING': 'order-detail__status--warning',
    'REFUNDED': 'order-detail__status--default'
  }
  return classMap[status] || ''
}

const handlePay = () => {
  ElMessage.info('跳转到支付页面...')
}

const handleConfirm = () => {
  ElMessage.info('确认收货成功')
}

const handleReview = () => {
  ElMessage.info('跳转到评价页面...')
}

const handleCancel = () => {
  ElMessage.info('订单已取消')
}

onMounted(() => {
  fetchOrder()
})
</script>

<style scoped>
.order-detail {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.order-detail__loading {
  display: flex;
  justify-content: center;
  padding: 40px;
}

.order-detail__error {
  margin-bottom: 16px;
}

.order-detail__content {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.order-detail__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.order-detail__title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.order-detail__status {
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

.order-detail__info {
  display: flex;
  gap: 24px;
  margin-bottom: 20px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.order-detail__info-item {
  display: flex;
  gap: 8px;
}

.order-detail__info-label {
  font-size: 14px;
  color: #999;
}

.order-detail__info-value {
  font-size: 14px;
  color: #333;
}

.order-detail__section {
  margin-bottom: 20px;
}

.order-detail__section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.order-detail__address {
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.order-detail__address-name {
  display: block;
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.order-detail__address-phone {
  display: block;
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.order-detail__address-detail {
  display: block;
  font-size: 14px;
  color: #666;
}

.order-detail__products {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
}

.order-detail__product-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
  
  &:last-child {
    border-bottom: none;
  }
}

.order-detail__product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.order-detail__product-info {
  flex: 1;
}

.order-detail__product-name {
  display: block;
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
}

.order-detail__product-price {
  font-size: 14px;
  color: #ff4757;
  font-weight: 500;
}

.order-detail__product-actions {
  text-align: right;
}

.order-detail__product-quantity {
  display: block;
  font-size: 14px;
  color: #999;
  margin-bottom: 4px;
}

.order-detail__product-subtotal {
  font-size: 16px;
  color: #ff4757;
  font-weight: 600;
}

.order-detail__amount {
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.order-detail__amount-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  
  &.order-detail__amount-total {
    border-top: 1px dashed #ddd;
    margin-top: 8px;
    padding-top: 16px;
    
    .order-detail__amount-label {
      font-size: 16px;
      font-weight: 600;
    }
    
    .order-detail__amount-value {
      font-size: 20px;
      font-weight: 700;
      color: #ff4757;
    }
  }
}

.order-detail__amount-label {
  font-size: 14px;
  color: #666;
}

.order-detail__amount-value {
  font-size: 14px;
  color: #333;
}

.order-detail__timeline {
  padding-left: 24px;
}

.order-detail__timeline-item {
  position: relative;
  padding-left: 24px;
  padding-bottom: 24px;
  
  &:last-child {
    padding-bottom: 0;
  }
  
  &--active .order-detail__timeline-dot {
    background: #409eff;
  }
  
  &--active .order-detail__timeline-content {
    color: #333;
  }
}

.order-detail__timeline-dot {
  position: absolute;
  left: -24px;
  top: 4px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #ddd;
  border: 2px solid #fff;
}

.order-detail__timeline-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
  color: #999;
}

.order-detail__timeline-status {
  font-size: 14px;
  font-weight: 500;
}

.order-detail__timeline-time {
  font-size: 12px;
}

.order-detail__timeline-operator {
  font-size: 12px;
}

.order-detail__actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}
</style>