/**
 * Vite 构建工具配置文件
 * 用于配置 Vue3 项目的开发服务器和构建选项
 */

// 从 Vite 导入 defineConfig 函数，用于定义配置对象
import { defineConfig } from 'vite'

// 导入 Vue 插件，使 Vite 能够处理 .vue 文件
import vue from '@vitejs/plugin-vue'

// 导出配置对象
export default defineConfig({
  // 配置插件数组
  plugins: [vue()],
  
  // 开发服务器配置
  server: {
    // 开发服务器端口号
    port: 5173,
    
    // 代理配置：用于解决前端开发时的跨域问题
    proxy: {
      // 将 /api 开头的请求代理到后端服务器
      '/api': {
        // 后端服务器地址
        target: 'http://localhost:8086',
        // 是否改变请求头中的 origin 字段
        changeOrigin: true,
        // 是否验证 SSL 证书（开发环境设为 false）
        secure: false
      }
    }
  }
})