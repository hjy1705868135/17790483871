/**
 * API 基础配置文件
 * 创建并配置 Axios 实例
 */

// 导入 Axios HTTP 客户端
import axios from 'axios'

// 创建 Axios 实例
const apiClient = axios.create({
    // 基础 URL，所有请求都会基于此路径
    baseURL: '/api',
    
    // 请求超时时间（毫秒）
    timeout: 10000,
    
    // 请求头配置
    headers: {
        'Content-Type': 'application/json'
    }
})

// 请求拦截器：在发送请求前执行
apiClient.interceptors.request.use(
    (config) => {
        // 可以在这里添加 token 等认证信息
        // const token = localStorage.getItem('token')
        // if (token) {
        //     config.headers.Authorization = `Bearer ${token}`
        // }
        console.log(`[API请求] ${config.method.toUpperCase()} ${config.url}`)
        return config
    },
    (error) => {
        // 请求错误处理
        console.error('[API请求错误]', error)
        return Promise.reject(error)
    }
)

// 响应拦截器：在收到响应后执行
apiClient.interceptors.response.use(
    (response) => {
        // 响应成功处理
        console.log(`[API响应] ${response.config.url}`, response.data)
        return response
    },
    (error) => {
        // 响应错误处理
        console.error('[API响应错误]', error)
        
        // 统一错误处理
        if (error.response) {
            // 服务器返回了错误状态码
            switch (error.response.status) {
                case 400:
                    console.error('请求参数错误')
                    break
                case 401:
                    console.error('未授权，请登录')
                    break
                case 403:
                    console.error('拒绝访问')
                    break
                case 404:
                    console.error('请求的资源不存在')
                    break
                case 500:
                    console.error('服务器内部错误')
                    break
                default:
                    console.error(`未知错误: ${error.response.status}`)
            }
        } else if (error.request) {
            // 请求已发送但没有收到响应
            console.error('网络错误，请检查网络连接')
        } else {
            // 请求配置错误
            console.error('请求配置错误', error.message)
        }
        
        return Promise.reject(error)
    }
)

// 导出配置好的 Axios 实例
export default apiClient