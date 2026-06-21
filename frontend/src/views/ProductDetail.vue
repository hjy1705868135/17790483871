<template>
  <div class="product-detail">
    <div v-if="loading" class="product-detail__loading">
      <el-loading text="加载中..." />
    </div>
    
    <div v-else-if="error" class="product-detail__error">
      <el-alert type="error" :title="error" show-icon />
    </div>
    
    <div v-else class="product-detail__content">
      <div class="product-detail__main">
        <div class="product-detail__images">
          <div class="product-detail__main-image">
            <img :src="product.image" :alt="product.name" />
          </div>
          <div class="product-detail__thumbnails">
            <img 
              v-for="(img, index) in product.images" 
              :key="index"
              :src="img" 
              :alt="`${product.name} ${index + 1}`"
              class="product-detail__thumbnail"
              :class="{ active: activeThumbnail === index }"
              @click="activeThumbnail = index"
            />
          </div>
        </div>
        
        <div class="product-detail__info">
          <div class="product-detail__promotions">
            <PromotionTag 
              v-for="promo in product.promotions" 
              :key="promo.type"
              :type="promo.type"
              :text="promo.text"
              :discount="promo.discount"
            />
          </div>
          
          <h1 class="product-detail__name">{{ product.name }}</h1>
          
          <div class="product-detail__rating-row">
            <span class="product-detail__rating">⭐ {{ product.rating }}</span>
            <span class="product-detail__review-count">({{ product.reviewCount }}条评价)</span>
            <span class="product-detail__sales">已售 {{ product.sales }}件</span>
          </div>
          
          <div class="product-detail__price-section">
            <div class="product-detail__price">
              <span class="product-detail__currency">¥</span>
              <span class="product-detail__amount">{{ product.price }}</span>
            </div>
            <span v-if="product.originalPrice" class="product-detail__original-price">
              ¥{{ product.originalPrice }}
            </span>
            <span v-if="product.discount" class="product-detail__discount-tag">
              立省 ¥{{ product.originalPrice - product.price }}
            </span>
          </div>
          
          <CountdownTimer 
            v-if="product.flashSaleEndTime"
            :end-time="product.flashSaleEndTime"
            label="限时特惠"
            urgent-threshold="3600000"
            class="product-detail__countdown"
            @end="handleFlashSaleEnd"
          />
          
          <div class="product-detail__specs">
            <div class="product-detail__spec-row">
              <span class="product-detail__spec-label">品牌:</span>
              <span class="product-detail__spec-value">{{ product.brand }}</span>
            </div>
            <div class="product-detail__spec-row">
              <span class="product-detail__spec-label">规格:</span>
              <span class="product-detail__spec-value">{{ product.spec }}</span>
            </div>
            <div class="product-detail__spec-row">
              <span class="product-detail__spec-label">库存:</span>
              <span class="product-detail__spec-value" :class="{ low: product.stock < 10 }">
                {{ product.stock }}件
              </span>
            </div>
          </div>
          
          <div class="product-detail__actions">
            <div class="product-detail__quantity-select">
              <button 
                class="product-detail__qty-btn" 
                @click="decreaseQuantity"
                :disabled="quantity <= 1"
              >
                -
              </button>
              <span class="product-detail__qty-value">{{ quantity }}</span>
              <button 
                class="product-detail__qty-btn" 
                @click="increaseQuantity"
                :disabled="quantity >= product.stock"
              >
                +
              </button>
            </div>
            
            <el-button 
              type="primary" 
              size="large"
              :disabled="product.stock === 0"
              @click="addToCart"
            >
              加入购物车
            </el-button>
            
            <el-button 
              type="danger" 
              size="large"
              :disabled="product.stock === 0"
              @click="buyNow"
            >
              立即购买
            </el-button>
          </div>
        </div>
      </div>
      
      <div class="product-detail__tabs">
        <el-tabs v-model="activeTab" type="border-card">
          <el-tab-pane label="商品详情" name="detail">
            <div class="product-detail__tab-content">
              <img 
                v-for="(img, index) in product.detailImages" 
                :key="index"
                :src="img" 
                class="product-detail__detail-image"
              />
            </div>
          </el-tab-pane>
          <el-tab-pane label="用户评价" name="reviews">
            <div class="product-detail__tab-content">
              <div v-if="reviews.length === 0" class="product-detail__empty-reviews">
                <span class="product-detail__empty-icon">📝</span>
                <span>暂无评价</span>
              </div>
              <div v-else class="product-detail__reviews">
                <div 
                  v-for="review in reviews" 
                  :key="review.id" 
                  class="product-detail__review-item"
                >
                  <div class="product-detail__review-header">
                    <span class="product-detail__review-user">{{ review.username }}</span>
                    <span class="product-detail__review-rating">⭐ {{ review.rating }}</span>
                    <span class="product-detail__review-date">{{ review.createdAt }}</span>
                  </div>
                  <p class="product-detail__review-content">{{ review.content }}</p>
                  <div v-if="review.images" class="product-detail__review-images">
                    <img 
                      v-for="(img, idx) in review.images" 
                      :key="idx"
                      :src="img" 
                      class="product-detail__review-image"
                    />
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="商品参数" name="params">
            <div class="product-detail__tab-content">
              <div class="product-detail__params">
                <div 
                  v-for="(value, key) in product.params" 
                  :key="key" 
                  class="product-detail__param-row"
                >
                  <span class="product-detail__param-label">{{ key }}</span>
                  <span class="product-detail__param-value">{{ value }}</span>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useCartStore } from '../store'
import { productApi } from '../api'
import PromotionTag from '../components/PromotionTag.vue'
import CountdownTimer from '../components/CountdownTimer.vue'

const route = useRoute()
const cartStore = useCartStore()

const product = ref({})
const reviews = ref([])
const loading = ref(false)
const error = ref(null)
const quantity = ref(1)
const activeThumbnail = ref(0)
const activeTab = ref('detail')

const fetchProduct = async () => {
  loading.value = true
  error.value = null
  
  try {
    const productId = route.params.id
    product.value = await productApi.getProductDetail(productId)
    
    if (!product.value.images) {
      product.value.images = [product.value.image]
    }
    if (!product.value.promotions) {
      product.value.promotions = []
    }
  } catch (err) {
    error.value = '获取商品详情失败'
    console.error('Fetch product error:', err)
  } finally {
    loading.value = false
  }
}

const fetchReviews = async () => {
  try {
    reviews.value = [
      {
        id: 1,
        username: '用户***3',
        rating: 5,
        content: '商品质量很好，物流也很快，非常满意！',
        createdAt: '2024-01-15',
        images: []
      },
      {
        id: 2,
        username: '用户***8',
        rating: 4,
        content: '整体不错，性价比很高',
        createdAt: '2024-01-14',
        images: []
      }
    ]
  } catch (err) {
    console.error('Fetch reviews error:', err)
  }
}

const increaseQuantity = () => {
  if (quantity.value < product.value.stock) {
    quantity.value++
  }
}

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--
  }
}

const addToCart = () => {
  cartStore.addItem({
    ...product.value,
    quantity: quantity.value
  })
  ElMessage.success('已加入购物车')
}

const buyNow = () => {
  ElMessage.info('跳转到订单页面...')
}

const handleFlashSaleEnd = () => {
  ElMessage.warning('限时特惠已结束')
}

onMounted(() => {
  fetchProduct()
  fetchReviews()
})
</script>

<style scoped>
.product-detail {
  padding: 20px;
}

.product-detail__loading {
  display: flex;
  justify-content: center;
  padding: 60px;
}

.product-detail__error {
  max-width: 600px;
  margin: 40px auto;
}

.product-detail__content {
  max-width: 1200px;
  margin: 0 auto;
}

.product-detail__main {
  display: flex;
  gap: 32px;
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 24px;
}

.product-detail__images {
  flex: 1;
}

.product-detail__main-image {
  width: 400px;
  height: 400px;
  background: #f8f9fa;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 12px;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.product-detail__thumbnails {
  display: flex;
  gap: 8px;
}

.product-detail__thumbnail {
  width: 64px;
  height: 64px;
  border: 2px solid transparent;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  object-fit: cover;
  
  &.active {
    border-color: #409eff;
  }
}

.product-detail__info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.product-detail__promotions {
  display: flex;
  gap: 8px;
}

.product-detail__name {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0;
  line-height: 1.4;
}

.product-detail__rating-row {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #666;
}

.product-detail__rating {
  font-weight: 500;
}

.product-detail__price-section {
  display: flex;
  align-items: baseline;
  gap: 12px;
  padding: 16px;
  background: #fff5f5;
  border-radius: 8px;
}

.product-detail__price {
  display: flex;
  align-items: baseline;
  color: #ff4757;
}

.product-detail__currency {
  font-size: 18px;
  font-weight: 600;
}

.product-detail__amount {
  font-size: 36px;
  font-weight: 700;
}

.product-detail__original-price {
  font-size: 16px;
  color: #999;
  text-decoration: line-through;
}

.product-detail__discount-tag {
  padding: 4px 8px;
  background: #ff4757;
  color: #fff;
  font-size: 12px;
  border-radius: 4px;
}

.product-detail__specs {
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
  padding: 16px 0;
}

.product-detail__spec-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
}

.product-detail__spec-label {
  font-size: 14px;
  color: #999;
}

.product-detail__spec-value {
  font-size: 14px;
  color: #333;
  
  &.low {
    color: #ff4757;
    font-weight: 500;
  }
}

.product-detail__actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.product-detail__quantity-select {
  display: flex;
  align-items: center;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
}

.product-detail__qty-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: #fff;
  font-size: 18px;
  cursor: pointer;
  
  &:hover:not(:disabled) {
    background: #f5f5f5;
  }
  
  &:disabled {
    color: #ccc;
    cursor: not-allowed;
  }
}

.product-detail__qty-value {
  min-width: 48px;
  text-align: center;
  font-size: 14px;
}

.product-detail__tabs {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}

.product-detail__tab-content {
  padding: 20px;
}

.product-detail__detail-image {
  width: 100%;
  margin-bottom: 12px;
  border-radius: 4px;
}

.product-detail__empty-reviews {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px;
  color: #999;
  
  .product-detail__empty-icon {
    font-size: 48px;
    margin-bottom: 16px;
  }
}

.product-detail__reviews {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.product-detail__review-item {
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
  
  &:last-child {
    border-bottom: none;
  }
}

.product-detail__review-header {
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
}

.product-detail__review-user {
  font-weight: 500;
  color: #333;
}

.product-detail__review-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0;
}

.product-detail__review-images {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.product-detail__review-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.product-detail__params {
  display: flex;
  flex-direction: column;
}

.product-detail__param-row {
  display: flex;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
  
  &:last-child {
    border-bottom: none;
  }
}

.product-detail__param-label {
  width: 120px;
  font-size: 14px;
  color: #999;
}

.product-detail__param-value {
  font-size: 14px;
  color: #333;
}
</style>