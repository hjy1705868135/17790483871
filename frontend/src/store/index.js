import { createPinia, defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi } from '../api'
import { setToken, setRefreshToken, getToken, removeToken } from '../utils/auth'

// 创建 pinia 实例
const pinia = createPinia()

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null)
  const token = ref(getToken())
  
  const login = async (username, password) => {
    try {
      const response = await authApi.login({ username, password })
      setToken(response.accessToken)
      setRefreshToken(response.refreshToken)
      token.value = response.accessToken
      return response
    } catch (error) {
      throw error
    }
  }
  
  const logout = async () => {
    try {
      await authApi.logout()
    } catch (error) {
      console.error('Logout error:', error)
    } finally {
      removeToken()
      token.value = null
      userInfo.value = null
    }
  }
  
  const getUserInfo = async () => {
    try {
      const response = await authApi.getUserInfo()
      userInfo.value = response
      return response
    } catch (error) {
      throw error
    }
  }
  
  return {
    userInfo,
    token,
    login,
    logout,
    getUserInfo
  }
})

export const useCartStore = defineStore('cart', () => {
  const items = ref([])
  
  const addItem = (product) => {
    const existingItem = items.value.find(item => item.id === product.id)
    if (existingItem) {
      existingItem.quantity++
    } else {
      items.value.push({ ...product, quantity: 1 })
    }
  }
  
  const removeItem = (productId) => {
    items.value = items.value.filter(item => item.id !== productId)
  }
  
  const updateQuantity = (productId, quantity) => {
    const item = items.value.find(item => item.id === productId)
    if (item) {
      if (quantity <= 0) {
        removeItem(productId)
      } else {
        item.quantity = quantity
      }
    }
  }
  
  const clearCart = () => {
    items.value = []
  }
  
  const totalItems = () => {
    return items.value.reduce((sum, item) => sum + item.quantity, 0)
  }
  
  const totalPrice = () => {
    return items.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
  }
  
  return {
    items,
    addItem,
    removeItem,
    updateQuantity,
    clearCart,
    totalItems,
    totalPrice
  }
})

// 默认导出 pinia 实例
export default pinia