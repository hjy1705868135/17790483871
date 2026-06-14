/**
 * 无人机信息管理系统 - Vue3应用入口文件
 * 负责创建Vue应用实例并挂载到页面
 */

// 从Vue3库中导入createApp函数，用于创建Vue应用实例
import { createApp } from 'vue'

// 导入根组件App.vue，这是应用的主组件
import App from './App.vue'

// 创建Vue应用实例（以App.vue为根组件），并挂载到页面的#app元素上
// - createApp(App)：创建应用实例
// - .mount('#app')：将应用挂载到id为app的DOM元素
createApp(App).mount('#app')