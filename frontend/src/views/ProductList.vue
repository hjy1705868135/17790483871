<template>
  <div class="product-list">
    <div class="product-list__header">
      <div class="product-list__filters">
        <div class="product-list__filter-group">
          <span class="product-list__filter-label">分类:</span>
          <el-select 
            v-model="filters.category" 
            placeholder="全部分类"
            class="product-list__filter-select"
            @change="handleFilterChange"
          >
            <el-option label="全部分类" value="" />
            <el-option 
              v-for="category in categories" 
              :key="category.id" 
              :label="category.name" 
              :value="category.id" 
            />
          </el-select>
        </div>
        
        <div class="product-list__filter-group">
          <span class="product-list__filter-label">排序:</span>
          <el-select 
            v-model="filters.sortBy" 
            placeholder="默认排序"
            class="product-list__filter-select"
            @change="handleFilterChange"
          >
            <el-option label="默认排序" value="default" />
            <el-option label="价格从低到高" value="price_asc" />
            <el-option label="价格从高到低" value="price_desc" />
            <el-option label="销量优先" value="sales" />
            <el-option label="最新上架" value="newest" />
          </el-select>
        </div>
        
        <div class="product-list__filter-group">
          <span class="product-list__filter-label">价格区间:</span>
          <el-input 
            v-model="filters.minPrice" 
            placeholder="最低价" 
            type="number"
            class="product-list__price-input"
            @change="handleFilterChange"
          />
          <span class="product-list__price-separator">-</span>
          <el-input 
            v-model="filters.maxPrice" 
            placeholder="最高价" 
            type="number"
            class="product-list__price-input"
            @change="handleFilterChange"
          />
        </div>
        
        <el-button 
          type="primary" 
          size="small"
          @click="handleFilterChange"
        >
          筛选
        </el-button>
        
        <el-button 
          size="small"
          @click="resetFilters"
        >
          重置
        </el-button>
      </div>
    </div>
    
    <div class="product-list__toolbar">
      <span class="product-list__result-count">共 {{ total }} 件商品</span>
      <div class="product-list__view-toggle">
        <button 
          class="product-list__view-btn" 
          :class="{ active: viewMode === 'grid' }"
          @click="viewMode = 'grid'"
        >
          📦
        </button>
        <button 
          class="product-list__view-btn" 
          :class="{ active: viewMode === 'list' }"
          @click="viewMode = 'list'"
        >
          📋
        </button>
      </div>
    </div>
    
    <div v-if="loading" class="product-list__loading">
      <el-loading text="加载中..." />
    </div>
    
    <div v-else-if="error" class="product-list__error">
      <el-alert type="error" :title="error" show-icon />
    </div>
    
    <div v-else-if="products.length === 0" class="product-list__empty">
      <div class="product-list__empty-icon">📭</div>
      <div class="product-list__empty-text">暂无商品</div>
    </div>
    
    <div 
      class="product-list__grid" 
      :class="{ 'product-list__grid--list': viewMode === 'list' }"
    >
      <ProductCard 
        v-for="product in products" 
        :key="product.id" 
        :product="product"
        @click="handleProductClick"
      />
    </div>
    
    <div v-if="total > pageSize" class="product-list__pagination">
      <el-pagination
        :current-page="page"
        :page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { productApi } from '../api'
import ProductCard from '../components/ProductCard.vue'

const router = useRouter()

const products = ref([])
const categories = ref([])
const loading = ref(false)
const error = ref(null)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const viewMode = ref('grid')

const filters = reactive({
  category: '',
  sortBy: 'default',
  minPrice: '',
  maxPrice: ''
})

const fetchProducts = async () => {
  loading.value = true
  error.value = null
  
  try {
    const params = {
      page: page.value,
      size: pageSize.value,
      categoryId: filters.category || undefined,
      sortBy: filters.sortBy,
      minPrice: filters.minPrice || undefined,
      maxPrice: filters.maxPrice || undefined
    }
    
    const response = await productApi.getProducts(params)
    products.value = response.records || []
    total.value = response.total || 0
  } catch (err) {
    error.value = '获取商品列表失败'
    console.error('Fetch products error:', err)
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    categories.value = await productApi.getCategories()
  } catch (err) {
    console.error('Fetch categories error:', err)
  }
}

const handleFilterChange = () => {
  page.value = 1
  fetchProducts()
}

const resetFilters = () => {
  filters.category = ''
  filters.sortBy = 'default'
  filters.minPrice = ''
  filters.maxPrice = ''
  page.value = 1
  fetchProducts()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  page.value = 1
  fetchProducts()
}

const handlePageChange = (currentPage) => {
  page.value = currentPage
  fetchProducts()
}

const handleProductClick = (product) => {
  router.push(`/products/${product.id}`)
}

onMounted(() => {
  fetchCategories()
  fetchProducts()
})
</script>

<style scoped>
.product-list {
  padding: 20px;
}

.product-list__header {
  background: #fff;
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 16px;
}

.product-list__filters {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.product-list__filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.product-list__filter-label {
  font-size: 14px;
  color: #666;
}

.product-list__filter-select {
  width: 140px;
}

.product-list__price-input {
  width: 80px;
}

.product-list__price-separator {
  color: #999;
}

.product-list__toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0 4px;
}

.product-list__result-count {
  font-size: 14px;
  color: #666;
}

.product-list__view-toggle {
  display: flex;
  gap: 4px;
}

.product-list__view-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 4px;
  background: #f5f5f5;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.2s;
  
  &:hover {
    background: #e8e8e8;
  }
  
  &.active {
    background: #409eff;
    color: #fff;
  }
}

.product-list__loading {
  display: flex;
  justify-content: center;
  padding: 40px;
}

.product-list__error {
  margin-bottom: 16px;
}

.product-list__empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background: #fff;
  border-radius: 8px;
}

.product-list__empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.product-list__empty-text {
  font-size: 16px;
  color: #999;
}

.product-list__grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
  
  &--list {
    grid-template-columns: 1fr;
    
    .product-card {
      flex-direction: row;
      
      .product-card__image-wrapper {
        width: 200px;
        padding-top: 0;
        height: 200px;
      }
      
      .product-card__content {
        flex: 1;
        justify-content: center;
      }
    }
  }
}

.product-list__pagination {
  display: flex;
  justify-content: center;
}
</style>