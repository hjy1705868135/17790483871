import request from '../utils/request'

export const authApi = {
  login(data) {
    return request({
      url: '/v1/auth/login',
      method: 'post',
      data
    })
  },
  
  refreshToken(data) {
    return request({
      url: '/v1/auth/refresh',
      method: 'post',
      data
    })
  },
  
  logout() {
    return request({
      url: '/v1/auth/logout',
      method: 'post'
    })
  },
  
  getUserInfo() {
    return request({
      url: '/v1/auth/me',
      method: 'get'
    })
  }
}

export const orderApi = {
  createOrder(data) {
    return request({
      url: '/v1/orders',
      method: 'post',
      data
    })
  },
  
  getOrderDetail(orderId) {
    return request({
      url: `/v1/orders/${orderId}`,
      method: 'get'
    })
  },
  
  getOrders(params) {
    return request({
      url: '/v1/orders',
      method: 'get',
      params
    })
  },
  
  updateOrderStatus(orderId, data) {
    return request({
      url: `/v1/orders/${orderId}/status`,
      method: 'put',
      data
    })
  },
  
  deleteOrder(orderId) {
    return request({
      url: `/v1/orders/${orderId}`,
      method: 'delete'
    })
  }
}

export const productApi = {
  getProducts(params) {
    return request({
      url: '/v1/products',
      method: 'get',
      params
    })
  },
  
  getProductDetail(productId) {
    return request({
      url: `/v1/products/${productId}`,
      method: 'get'
    })
  },
  
  getCategories() {
    return request({
      url: '/v1/categories',
      method: 'get'
    })
  }
}