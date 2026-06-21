import axios from 'axios'
import { ElMessage, ElLoading } from 'element-plus'
import { getToken, removeToken } from './auth'

const service = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

let loadingInstance = null

service.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    
    loadingInstance = ElLoading.service({
      lock: true,
      text: '加载中...',
      background: 'rgba(0, 0, 0, 0.5)'
    })
    
    return config
  },
  error => {
    if (loadingInstance) {
      loadingInstance.close()
    }
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    if (loadingInstance) {
      loadingInstance.close()
    }
    
    const res = response.data
    
    if (res.code !== 200) {
      ElMessage({
        message: res.message || '请求失败',
        type: 'error',
        duration: 3000
      })
      
      if (res.code === 401) {
        removeToken()
        window.location.href = '/login'
      }
      
      return Promise.reject(new Error(res.message || 'Error'))
    }
    
    return res.data
  },
  error => {
    if (loadingInstance) {
      loadingInstance.close()
    }
    
    let message = '请求失败'
    
    if (error.response) {
      const status = error.response.status
      
      switch (status) {
        case 401:
          message = '登录已过期，请重新登录'
          removeToken()
          setTimeout(() => {
            window.location.href = '/login'
          }, 1500)
          break
        case 403:
          message = '没有权限访问该资源'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = error.response.data?.message || `请求错误: ${status}`
      }
    } else if (error.request) {
      message = '网络超时，请检查网络连接'
    } else {
      message = error.message || '请求失败'
    }
    
    ElMessage({
      message,
      type: 'error',
      duration: 3000
    })
    
    return Promise.reject(error)
  }
)

export default service