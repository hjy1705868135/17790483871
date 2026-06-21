<template>
  <div class="home">
    <div class="home__banner">
      <div class="home__banner-content">
        <h1 class="home__banner-title">欢迎来到电商购物平台</h1>
        <p class="home__banner-subtitle">发现优质商品，享受便捷购物体验</p>
        <el-button type="primary" size="large" @click="goToProducts">
          立即选购
        </el-button>
      </div>
    </div>
    
    <div class="home__categories">
      <h2 class="home__section-title">热门分类</h2>
      <div class="home__category-grid">
        <div 
          v-for="category in categories" 
          :key="category.id" 
          class="home__category-item"
          @click="goToProductsByCategory(category.id)"
        >
          <div class="home__category-icon">{{ category.icon }}</div>
          <span class="home__category-name">{{ category.name }}</span>
        </div>
      </div>
    </div>
    
    <div class="home__flash-sale">
      <div class="home__flash-sale-header">
        <span class="home__flash-sale-icon">⚡</span>
        <h2 class="home__section-title">限时闪购</h2>
        <CountdownTimer 
          :end-time="flashSaleEndTime" 
          label="距结束"
          class="home__flash-countdown"
        />
      </div>
      <div class="home__product-scroll">
        <div class="home__product-list">
          <ProductCard 
            v-for="product in flashSaleProducts" 
            :key="product.id" 
            :product="product"
            @click="goToProductDetail"
          />
        </div>
      </div>
    </div>
    
    <div class="home__hot-products">
      <h2 class="home__section-title">热销商品</h2>
      <div class="home__product-grid">
        <ProductCard 
          v-for="product in hotProducts" 
          :key="product.id" 
          :product="product"
          @click="goToProductDetail"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { productApi } from '../api'
import ProductCard from '../components/ProductCard.vue'
import CountdownTimer from '../components/CountdownTimer.vue'

const router = useRouter()

const categories = ref([])
const flashSaleProducts = ref([])
const hotProducts = ref([])

const flashSaleEndTime = ref(Date.now() + 3600000 * 2)

const fetchCategories = async () => {
  try {
    categories.value = await productApi.getCategories()
  } catch (err) {
    console.error('Fetch categories error:', err)
    categories.value = [
      { id: 1, name: '电子产品', icon: '📱' },
      { id: 2, name: '服装鞋帽', icon: '👔' },
      { id: 3, name: '美妆护肤', icon: '💄' },
      { id: 4, name: '家居用品', icon: '🏠' },
      { id: 5, name: '食品零食', icon: '🍎' },
      { id: 6, name: '图书文具', icon: '📚' }
    ]
  }
}

const fetchProducts = async () => {
  try {
    const params = { page: 1, size: 8, sortBy: 'sales' }
    const response = await productApi.getProducts(params)
    hotProducts.value = response.records || []
    
    flashSaleProducts.value = hotProducts.value.slice(0, 4).map(p => ({
      ...p,
      flashSaleEndTime: Date.now() + 3600000 * 2,
      promotions: [{ type: 'flash', text: '闪购' }]
    }))
  } catch (err) {
    console.error('Fetch products error:', err)
    hotProducts.value = [
      {
        id: 1,
        name: '无线蓝牙耳机',
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=wireless%20bluetooth%20earbuds%20white%20background&image_size=square',
        price: 199,
        originalPrice: 299,
        discount: 33,
        sales: 2580,
        rating: 4.8,
        promotions: [{ type: 'sale', text: '限时折扣', discount: 33 }]
      },
      {
        id: 2,
        name: '智能手表',
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=smart%20watch%20white%20background&image_size=square',
        price: 899,
        originalPrice: 1299,
        discount: 31,
        sales: 1890,
        rating: 4.7,
        promotions: [{ type: 'sale', text: '限时折扣', discount: 31 }]
      },
      {
        id: 3,
        name: '轻薄笔记本电脑',
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=laptop%20computer%20white%20background&image_size=square',
        price: 4999,
        originalPrice: 5999,
        discount: 17,
        sales: 1250,
        rating: 4.9,
        promotions: [{ type: 'hot', text: '热销' }]
      },
      {
        id: 4,
        name: '机械键盘',
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=mechanical%20keyboard%20white%20background&image_size=square',
        price: 399,
        originalPrice: 499,
        discount: 20,
        sales: 3200,
        rating: 4.6,
        promotions: [{ type: 'hot', text: '热销' }]
      },
      {
        id: 5,
        name: '无线鼠标',
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=wireless%20mouse%20white%20background&image_size=square',
        price: 129,
        originalPrice: 169,
        discount: 24,
        sales: 4500,
        rating: 4.5,
        promotions: [{ type: 'sale', text: '限时折扣', discount: 24 }]
      },
      {
        id: 6,
        name: '4K显示器',
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=4k%20monitor%20white%20background&image_size=square',
        price: 1999,
        originalPrice: 2499,
        discount: 20,
        sales: 890,
        rating: 4.8,
        promotions: [{ type: 'sale', text: '限时折扣', discount: 20 }]
      },
      {
        id: 7,
        name: '游戏耳机',
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=gaming%20headset%20white%20background&image_size=square',
        price: 599,
        originalPrice: 799,
        discount: 25,
        sales: 1560,
        rating: 4.7,
        promotions: [{ type: 'hot', text: '热销' }]
      },
      {
        id: 8,
        name: '便携音箱',
        image: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=portable%20speaker%20white%20background&image_size=square',
        price: 299,
        originalPrice: 399,
        discount: 25,
        sales: 2100,
        rating: 4.6,
        promotions: [{ type: 'sale', text: '限时折扣', discount: 25 }]
      }
    ]
    
    flashSaleProducts.value = hotProducts.value.slice(0, 4).map(p => ({
      ...p,
      flashSaleEndTime: Date.now() + 3600000 * 2,
      promotions: [{ type: 'flash', text: '闪购' }]
    }))
  }
}

const goToProducts = () => {
  router.push('/products')
}

const goToProductsByCategory = (categoryId) => {
  router.push(`/products?category=${categoryId}`)
}

const goToProductDetail = (product) => {
  router.push(`/products/${product.id}`)
}

onMounted(() => {
  fetchCategories()
  fetchProducts()
})
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: #f5f5f5;
}

.home__banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 80px 20px;
  text-align: center;
  color: #fff;
}

.home__banner-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 16px;
}

.home__banner-subtitle {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 24px;
}

.home__section-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 20px;
  text-align: center;
  color: #333;
}

.home__categories {
  padding: 40px 20px;
  background: #fff;
}

.home__category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.home__category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

.home__category-icon {
  font-size: 32px;
}

.home__category-name {
  font-size: 14px;
  color: #333;
}

.home__flash-sale {
  padding: 40px 20px;
  background: linear-gradient(135deg, #ff4757 0%, #ff6b81 100%);
}

.home__flash-sale .home__section-title {
  color: #fff;
}

.home__flash-sale-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-bottom: 20px;
}

.home__flash-sale-icon {
  font-size: 28px;
}

.home__product-scroll {
  overflow-x: auto;
  padding-bottom: 10px;
}

.home__product-list {
  display: flex;
  gap: 16px;
  min-width: max-content;
}

.home__product-list .product-card {
  width: 220px;
  flex-shrink: 0;
}

.home__hot-products {
  padding: 40px 20px;
  background: #f5f5f5;
}

.home__product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
  max-width: 1200px;
  margin: 0 auto;
}
</style>