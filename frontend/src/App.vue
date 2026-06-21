<template>
  <div class="app">
    <header class="app-header">
      <div class="app-header__logo" @click="goToHome">🛒</div>
      <nav class="app-header__nav">
        <a href="/" class="app-header__nav-item" :class="{ active: currentRoute === '/' }">首页</a>
        <a href="/products" class="app-header__nav-item" :class="{ active: currentRoute === '/products' }">商品列表</a>
        <a href="/orders" class="app-header__nav-item" :class="{ active: currentRoute.startsWith('/orders') }">我的订单</a>
      </nav>
      <div class="app-header__actions">
        <button class="app-header__cart-btn" @click="goToCart">
          🛒
          <span v-if="cartCount > 0" class="app-header__cart-badge">{{ cartCount }}</span>
        </button>
        <button 
          v-if="isLoggedIn" 
          class="app-header__user-btn"
          @click="handleLogout"
        >
          退出登录
        </button>
        <a v-else href="/login" class="app-header__user-btn">
          登录
        </a>
      </div>
    </header>
    
    <main class="app-main">
      <router-view />
    </main>
    
    <footer class="app-footer">
      <div class="app-footer__content">
        <p>© 2024 电商购物平台 版权所有</p>
        <p>客服热线: 400-888-8888</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore, useCartStore } from './store'
import { getToken } from './utils/auth'

const router = useRouter()
const route = useRoute()

const userStore = useUserStore()
const cartStore = useCartStore()

const currentRoute = ref('/')
const isLoggedIn = ref(false)
const cartCount = ref(0)

const goToHome = () => {
  router.push('/')
}

const goToCart = () => {
  ElMessage.info('购物车功能开发中...')
}

const handleLogout = async () => {
  try {
    await userStore.logout()
    ElMessage.success('退出成功')
    router.push('/login')
  } catch (error) {
    console.error('Logout error:', error)
  }
}

watch(() => route.path, (newPath) => {
  currentRoute.value = newPath
})

onMounted(() => {
  currentRoute.value = route.path
  isLoggedIn.value = !!getToken()
  
  cartCount.value = cartStore.totalItems()
  
  if (isLoggedIn.value) {
    userStore.getUserInfo().catch(err => {
      console.error('Get user info error:', err)
    })
  }
})
</script>

<style scoped>
.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.app-header__logo {
  font-size: 32px;
  cursor: pointer;
}

.app-header__nav {
  display: flex;
  gap: 32px;
}

.app-header__nav-item {
  font-size: 16px;
  color: #333;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 4px;
  transition: all 0.3s;
  
  &:hover {
    background: #f5f5f5;
  }
  
  &.active {
    background: #f0f5ff;
    color: #409eff;
    font-weight: 500;
  }
}

.app-header__actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.app-header__cart-btn {
  position: relative;
  width: 40px;
  height: 40px;
  border: none;
  background: #f5f5f5;
  border-radius: 50%;
  font-size: 20px;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    background: #e8e8e8;
  }
}

.app-header__cart-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  min-width: 18px;
  height: 18px;
  padding: 0 4px;
  background: #ff4757;
  color: #fff;
  font-size: 12px;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.app-header__user-btn {
  padding: 8px 16px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  text-decoration: none;
  
  &:hover {
    background: #66b1ff;
  }
}

.app-main {
  flex: 1;
  padding-top: 60px;
}

.app-footer {
  background: #333;
  color: #fff;
  padding: 24px;
  text-align: center;
}

.app-footer__content {
  display: flex;
  justify-content: center;
  gap: 40px;
  font-size: 14px;
}
</style>