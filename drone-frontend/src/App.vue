<template>
    <!-- 应用根容器 -->
    <div class="app-container">
        <!-- 页面头部区域：包含logo、标题、日期和状态指示 -->
        <header class="cyber-header">
            <div class="header-content">
                <!-- 左侧：logo和标题 -->
                <div class="header-left">
                    <div class="logo-icon">
                        <!-- SVG无人机图标 -->
                        <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                            <path d="M12 2L2 7l10 5 10-5-10-5z"/>
                            <path d="M2 17l10 5 10-5"/>
                            <path d="M2 12l10 5 10-5"/>
                        </svg>
                    </div>
                    <div class="header-title">
                        <h1>DRONE SYSTEM</h1>
                        <p class="subtitle">无人机信息管理系统</p>
                    </div>
                </div>
                <!-- 右侧：日期和在线状态 -->
                <div class="header-right">
                    <!-- 显示当前日期 -->
                    <div class="cyber-date">{{ new Date().toLocaleDateString('zh-CN') }}</div>
                    <div class="status-indicator">
                        <span class="status-dot"></span>
                        <span>在线</span>
                    </div>
                </div>
            </div>
        </header>

        <!-- 主内容区域 -->
        <main class="main-content">
            <!-- 工具栏：添加按钮和搜索框 -->
            <div class="toolbar">
                <!-- 添加无人机按钮 -->
                <button @click="openAddModal" class="cyber-btn cyber-btn-primary">
                    <span class="btn-icon">+</span>
                    添加无人机
                </button>
                <!-- 搜索区域 -->
                <div class="search-wrapper">
                    <!-- 搜索类型选择：按名称或型号搜索 -->
                    <select v-model="searchType" class="search-select">
                        <option value="name">名称</option>
                        <option value="model">型号</option>
                    </select>
                    <div class="search-input-wrapper">
                        <span class="search-icon">🔍</span>
                        <input v-model="searchKeyword" type="text" class="search-input" placeholder="搜索..."/>
                    </div>
                </div>
            </div>

            <!-- 统计卡片区域：显示无人机数量统计 -->
            <div class="stats-grid">
                <!-- 总无人机数 -->
                <div class="stat-card">
                    <div class="stat-icon">📦</div>
                    <div class="stat-content">
                        <div class="stat-value">{{ filteredDrones.length }}</div>
                        <div class="stat-label">总无人机数</div>
                    </div>
                </div>
                <!-- 正常运行数量 -->
                <div class="stat-card">
                    <div class="stat-icon">✅</div>
                    <div class="stat-content">
                        <div class="stat-value">{{ filteredDrones.filter(d => d.status === 'ACTIVE').length }}</div>
                        <div class="stat-label">正常运行</div>
                    </div>
                </div>
                <!-- 已停用数量 -->
                <div class="stat-card">
                    <div class="stat-icon">⏸️</div>
                    <div class="stat-content">
                        <div class="stat-value">{{ filteredDrones.filter(d => d.status === 'INACTIVE').length }}</div>
                        <div class="stat-label">已停用</div>
                    </div>
                </div>
            </div>

            <!-- 数据面板：无人机列表表格 -->
            <div class="data-panel">
                <div class="panel-header">
                    <h2>无人机列表</h2>
                    <span class="cyber-badge">{{ filteredDrones.length }} 架无人机</span>
                </div>
                <div class="panel-body">
                    <table class="cyber-table">
                        <thead>
                            <tr>
                                <th>名称</th>
                                <th>型号</th>
                                <th>制造商</th>
                                <th>重量</th>
                                <th>最大高度</th>
                                <th>续航</th>
                                <th>速度</th>
                                <th>状态</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            <!-- 遍历无人机列表 -->
                            <tr v-for="drone in filteredDrones" :key="drone.id" class="table-row">
                                <td class="cell-name">{{ drone.name }}</td>
                                <td>{{ drone.model }}</td>
                                <td>{{ drone.manufacturer }}</td>
                                <td>{{ drone.weight }} kg</td>
                                <td>{{ drone.maxAltitude }} m</td>
                                <td>{{ drone.maxFlightTime }} min</td>
                                <td>{{ drone.maxSpeed }} km/h</td>
                                <td>
                                    <!-- 根据状态显示不同样式的标签 -->
                                    <span :class="['status-tag', drone.status === 'ACTIVE' ? 'status-active' : 'status-inactive']">
                                        {{ drone.status === 'ACTIVE' ? '正常' : '停用' }}
                                    </span>
                                </td>
                                <td>
                                    <div class="action-btns">
                                        <!-- 编辑按钮 -->
                                        <button @click="openEditModal(drone)" class="cyber-btn cyber-btn-sm cyber-btn-secondary">编辑</button>
                                        <!-- 删除按钮 -->
                                        <button @click="deleteDrone(drone.id)" class="cyber-btn cyber-btn-sm cyber-btn-danger">删除</button>
                                    </div>
                                </td>
                            </tr>
                            <!-- 空列表提示 -->
                            <tr v-if="filteredDrones.length === 0" class="empty-row">
                                <td colspan="9" class="empty-message">
                                    <div class="empty-icon">🚁</div>
                                    <div>暂无无人机数据</div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </main>

        <!-- 模态框：添加/编辑无人机表单 -->
        <!-- 使用Teleport将模态框挂载到body，避免样式嵌套问题 -->
        <Teleport to="body">
            <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
                <div class="modal-container">
                    <div class="modal-header">
                        <h3 class="modal-title">{{ isEdit ? '编辑无人机' : '添加无人机' }}</h3>
                        <button type="button" class="modal-close" @click="closeModal">×</button>
                    </div>
                    <!-- 可滚动的模态框内容区域 -->
                    <div class="modal-body-scrollable">
                        <form @submit.prevent="saveDrone" class="cyber-form">
                            <div class="form-grid">
                                <!-- 无人机名称 -->
                                <div class="form-group">
                                    <label>无人机名称 <span class="required">*</span></label>
                                    <input v-model="form.name" type="text" class="cyber-input" required />
                                </div>
                                <!-- 型号 -->
                                <div class="form-group">
                                    <label>型号 <span class="required">*</span></label>
                                    <input v-model="form.model" type="text" class="cyber-input" required />
                                </div>
                                <!-- 制造商 -->
                                <div class="form-group">
                                    <label>制造商 <span class="required">*</span></label>
                                    <input v-model="form.manufacturer" type="text" class="cyber-input" required />
                                </div>
                                <!-- 重量 -->
                                <div class="form-group">
                                    <label>重量(kg) <span class="required">*</span></label>
                                    <input v-model="form.weight" type="number" step="0.01" min="0" class="cyber-input" required />
                                </div>
                                <!-- 最大高度 -->
                                <div class="form-group">
                                    <label>最大高度(m) <span class="required">*</span></label>
                                    <input v-model="form.maxAltitude" type="number" min="0" class="cyber-input" required />
                                </div>
                                <!-- 续航时间 -->
                                <div class="form-group">
                                    <label>续航时间(min) <span class="required">*</span></label>
                                    <input v-model="form.maxFlightTime" type="number" min="0" class="cyber-input" required />
                                </div>
                                <!-- 最大速度 -->
                                <div class="form-group">
                                    <label>最大速度(km/h) <span class="required">*</span></label>
                                    <input v-model="form.maxSpeed" type="number" step="0.1" min="0" class="cyber-input" required />
                                </div>
                                <!-- 状态选择 -->
                                <div class="form-group">
                                    <label>状态</label>
                                    <select v-model="form.status" class="cyber-select">
                                        <option value="ACTIVE">正常运行</option>
                                        <option value="INACTIVE">已停用</option>
                                    </select>
                                </div>
                                <!-- 描述（跨两列） -->
                                <div class="form-group full-width">
                                    <label>描述</label>
                                    <textarea v-model="form.description" class="cyber-textarea" rows="3"></textarea>
                                </div>
                            </div>
                            <!-- AI生成按钮区域 -->
                            <div class="form-actions">
                                <button type="button" @click="generateAIAttributes" class="cyber-btn cyber-btn-outline">
                                    🤖 AI 自动生成属性
                                </button>
                            </div>
                        </form>
                    </div>
                    <!-- 模态框底部按钮 -->
                    <div class="modal-footer">
                        <button type="button" class="cyber-btn cyber-btn-outline" @click="closeModal">取消</button>
                        <button type="button" class="cyber-btn cyber-btn-primary" @click="saveDrone">保存</button>
                    </div>
                </div>
            </div>
        </Teleport>
    </div>
</template>

<script setup>
/**
 * 无人机信息管理系统 - Vue3主组件
 * 技术栈：Vue3 Composition API + Axios
 * 
 * 功能：
 * 1. 无人机列表展示和管理（CRUD操作）
 * 2. 无人机信息的添加、编辑、删除
 * 3. 按名称/型号搜索无人机
 * 4. AI自动生成无人机属性
 * 5. 赛博朋克视觉效果
 */

// 从Vue3库中导入所需的响应式API和生命周期钩子
import { 
    ref,            // 创建响应式基本类型数据（如字符串、数字、布尔值）
    reactive,       // 创建响应式对象（适用于复杂对象）
    computed,       // 创建计算属性（根据依赖自动更新）
    onMounted,      // 组件挂载到DOM后执行的生命周期钩子
    watch           // 监听响应式数据变化，执行回调函数
} from 'vue'

// 导入无人机API接口
import droneApi from './api/drone'

// ========== 响应式数据定义 ==========

// 无人机列表数据（数组类型）
const drones = ref([])

// 控制模态框显示/隐藏（true表示显示，false表示隐藏）
const showModal = ref(false)

// 标识当前是编辑模式还是添加模式（true=编辑，false=添加）
const isEdit = ref(false)

// 搜索关键词
const searchKeyword = ref('')

// 搜索类型：name（按名称搜索）或 model（按型号搜索）
const searchType = ref('name')

// 表单数据（使用reactive创建响应式对象）
const form = reactive({
    id: null,              // 无人机ID（新增时为null，编辑时有值）
    name: '',              // 无人机名称（必填）
    model: '',             // 无人机型号（必填）
    manufacturer: '',      // 制造商名称
    weight: '',            // 重量（单位：kg）
    maxAltitude: '',       // 最大飞行高度（单位：米）
    maxFlightTime: '',     // 续航时间（单位：分钟）
    maxSpeed: '',          // 最大速度（单位：km/h）
    status: 'ACTIVE',      // 状态：ACTIVE（正常）/ INACTIVE（停用）
    description: ''        // 描述信息
})

// ========== 计算属性 ==========

/**
 * 过滤后的无人机列表
 * 根据搜索关键词和搜索类型动态过滤无人机列表
 * 计算属性会自动响应依赖数据的变化
 */
const filteredDrones = computed(() => {
    // 如果没有搜索关键词，直接返回全部列表
    if (!searchKeyword.value) return drones.value
    
    // 将关键词转为小写，实现不区分大小写搜索
    const keyword = searchKeyword.value.toLowerCase()
    
    // 根据搜索类型进行过滤
    return drones.value.filter(drone => {
        if (searchType.value === 'name') {
            // 按名称搜索：检查无人机名称是否包含关键词
            return drone.name.toLowerCase().includes(keyword)
        } else {
            // 按型号搜索：检查无人机型号是否包含关键词
            return drone.model.toLowerCase().includes(keyword)
        }
    })
})

// ========== 方法定义 ==========

/**
 * 加载无人机列表
 * 从后端API获取所有无人机数据
 * 使用async/await语法处理异步请求
 */
const loadDrones = async () => {
    try {
        // 使用droneApi发送GET请求获取无人机列表
        const response = await droneApi.getAll()
        // 将响应数据赋值给drones数组
        drones.value = response.data
    } catch (error) {
        // 捕获异常，输出错误日志并提示用户
        console.error('加载失败:', error)
        alert('加载无人机列表失败')
    }
}

/**
 * 打开添加无人机的模态框
 * 重置表单数据，设置为添加模式
 */
const openAddModal = () => {
    // 设置为添加模式（非编辑模式）
    isEdit.value = false
    // 重置表单所有字段为空或默认值
    Object.assign(form, {
        id: null, name: '', model: '', manufacturer: '',
        weight: '', maxAltitude: '', maxFlightTime: '',
        maxSpeed: '', status: 'ACTIVE', description: ''
    })
    // 显示模态框
    showModal.value = true
    // 给body添加modal-open类，防止页面背景滚动
    document.body.classList.add('modal-open')
}

/**
 * 打开编辑无人机的模态框
 * 将选中的无人机数据填充到表单
 * @param {Object} drone - 要编辑的无人机对象
 */
const openEditModal = (drone) => {
    // 设置为编辑模式
    isEdit.value = true
    // 将无人机数据复制到表单中
    Object.assign(form, drone)
    // 显示模态框
    showModal.value = true
    // 给body添加modal-open类，防止页面背景滚动
    document.body.classList.add('modal-open')
}

/**
 * 关闭模态框
 */
const closeModal = () => {
    // 隐藏模态框
    showModal.value = false
    // 移除body的modal-open类，恢复页面滚动
    document.body.classList.remove('modal-open')
}

/**
 * 保存无人机（新增或更新）
 * 根据isEdit判断是新增还是更新操作
 * 使用async/await语法处理异步请求
 */
const saveDrone = async () => {
    try {
        // 构建要提交的数据对象
        const droneData = {
            ...form,  // 使用展开运算符复制表单所有字段
            // 将字符串类型的数值转换为正确的类型
            weight: parseFloat(form.weight) || 0,           // 转换为浮点数
            maxAltitude: parseInt(form.maxAltitude) || 0,   // 转换为整数
            maxFlightTime: parseInt(form.maxFlightTime) || 0, // 转换为整数
            maxSpeed: parseFloat(form.maxSpeed) || 0        // 转换为浮点数
        }
        
        // 根据模式选择对应的API方法
        if (isEdit.value) {
            // 编辑模式：使用update方法更新现有无人机
            await droneApi.update(form.id, droneData)
        } else {
            // 添加模式：使用create方法创建新无人机
            await droneApi.create(droneData)
        }
        
        // 保存成功后关闭模态框
        closeModal()
        // 重新加载无人机列表，显示最新数据
        loadDrones()
    } catch (error) {
        // 捕获异常，输出错误日志并提示用户
        console.error('保存失败:', error)
        // 优先显示后端返回的错误信息，否则显示默认错误信息
        alert('保存失败: ' + (error.response?.data?.message || error.message))
    }
}

/**
 * 删除无人机
 * @param {Number} id - 要删除的无人机ID
 */
const deleteDrone = async (id) => {
    // 先弹出确认框，确认后再执行删除
    if (!confirm('确定删除此无人机吗？')) return
    
    try {
        // 使用droneApi的delete方法删除指定ID的无人机
        await droneApi.delete(id)
        // 删除成功后重新加载列表，更新显示
        loadDrones()
    } catch (error) {
        // 捕获异常，输出错误日志并提示用户
        console.error('删除失败:', error)
        alert('删除失败')
    }
}

/**
 * AI自动生成无人机属性
 * 使用预设的制造商、型号、名称列表随机生成数据
 * 方便用户快速添加示例数据
 */
const generateAIAttributes = () => {
    // 预设的制造商列表（国内知名无人机厂商）
    const manufacturers = ['大疆创新', '极飞科技', '亿航智能', '昊翔YUNEEC', '零度智控']
    // 预设的型号前缀列表
    const models = ['Phantom X', 'Mavic Cyber', 'Inspire Neo', 'Matrice Alpha', 'Mini Pro']
    // 预设的名称前缀列表（科幻风格）
    const names = ['幻影', '猎鹰', '幽灵', '风暴', '雷霆', '暗影', '极光', '量子']
    
    // 随机选择制造商
    form.manufacturer = manufacturers[Math.floor(Math.random() * manufacturers.length)]
    // 随机选择型号前缀并添加随机数字（1-100）
    form.model = models[Math.floor(Math.random() * models.length)] + ' ' + (Math.floor(Math.random() * 100) + 1)
    // 随机选择名称前缀并添加随机数字（0-999）
    form.name = names[Math.floor(Math.random() * names.length)] + '-' + Math.floor(Math.random() * 1000)
    // 随机生成重量（0.8-4.8kg），保留2位小数
    form.weight = (Math.random() * 4 + 0.8).toFixed(2)
    // 随机生成最大高度（2000-8000米）
    form.maxAltitude = Math.floor(Math.random() * 6000) + 2000
    // 随机生成续航时间（20-60分钟）
    form.maxFlightTime = Math.floor(Math.random() * 40) + 20
    // 随机生成最大速度（70-130 km/h），保留1位小数
    form.maxSpeed = (Math.random() * 60 + 70).toFixed(1)
    // 设置描述信息
    form.description = 'AI生成 - 高性能无人机系统'
}

// ========== 监听与生命周期 ==========

/**
 * 监听模态框显示状态变化
 * 当模态框显示时，隐藏页面滚动条；关闭时恢复
 */
watch(showModal, (newVal) => {
    document.body.style.overflow = newVal ? 'hidden' : ''
})

/**
 * 组件挂载后执行
 * 页面加载完成后自动加载无人机列表
 */
onMounted(() => {
    loadDrones()
})
</script>

<style>
/* ===== 全局基础样式重置 ===== */
* {
    margin: 0;           /* 清除默认外边距 */
    padding: 0;          /* 清除默认内边距 */
    box-sizing: border-box; /* 盒模型设置为border-box */
}

/* ===== HTML和Body样式 ===== */
html, body {
    height: 100%;        /* 高度占满视口 */
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; /* 设置字体 */
    background: linear-gradient(135deg, #0a0a15 0%, #1a1a2e 50%, #16213e 100%); /* 赛博朋克深色渐变背景 */
    color: #ffffff;      /* 默认文字颜色为白色 */
}

#app {
    min-height: 100%;    /* 最小高度占满视口 */
}

/* ===== 应用容器样式 ===== */
.app-container {
    min-height: 100vh;   /* 最小高度占满视口 */
    padding-bottom: 2rem; /* 底部留白 */
}

/* ===== 头部样式 ===== */
.cyber-header {
    background: linear-gradient(135deg, rgba(0, 0, 0, 0.8), rgba(30, 25, 55, 0.9));
    border-bottom: 1px solid rgba(0, 255, 255, 0.3);
    padding: 1rem 0;
    position: relative;
}

/* 头部底部发光线条 */
.cyber-header::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: linear-gradient(90deg, transparent, #00ffff, transparent);
}

.header-content {
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 2rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.header-left {
    display: flex;
    align-items: center;
    gap: 1rem;
}

.logo-icon {
    color: #00ffff;
    filter: drop-shadow(0 0 10px rgba(0, 255, 255, 0.5));
}

.header-title h1 {
    font-size: 1.5rem;
    font-weight: 700;
    color: #00ffff;
    text-shadow: 0 0 20px rgba(0, 255, 255, 0.6);
    margin: 0;
    letter-spacing: 2px;
}

.header-title .subtitle {
    font-size: 0.85rem;
    color: rgba(255, 255, 255, 0.6);
    margin: 2px 0 0 0;
}

.header-right {
    display: flex;
    align-items: center;
    gap: 1.5rem;
}

.cyber-date {
    font-size: 0.85rem;
    color: rgba(0, 255, 255, 0.8);
    font-family: monospace;
}

.status-indicator {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 0.85rem;
    color: #00ff88;
}

.status-dot {
    width: 8px;
    height: 8px;
    background: #00ff88;
    border-radius: 50%;
    box-shadow: 0 0 10px #00ff88;
    animation: pulse 2s infinite;
}

/* 脉冲动画 */
@keyframes pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.5; }
}

/* ===== 主内容区域 ===== */
.main-content {
    max-width: 1400px;
    width: 100%;
    margin: 0 auto;
    padding: 2rem;
}

/* ===== 工具栏 ===== */
.toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
    flex-wrap: wrap;
    gap: 1rem;
}

/* ===== 搜索框 ===== */
.search-wrapper {
    display: flex;
    align-items: center;
    background: rgba(0, 0, 0, 0.3);
    border: 1px solid rgba(0, 255, 255, 0.3);
    border-radius: 8px;
    overflow: hidden;
}

.search-select {
    padding: 10px 14px;
    background: rgba(0, 255, 255, 0.1);
    border: none;
    border-right: 1px solid rgba(0, 255, 255, 0.3);
    color: #00ffff;
    font-size: 0.85rem;
    cursor: pointer;
    outline: none;
}

.search-input-wrapper {
    display: flex;
    align-items: center;
    padding: 0 14px;
}

.search-icon {
    margin-right: 8px;
    opacity: 0.6;
}

.search-input {
    background: transparent;
    border: none;
    padding: 10px 0;
    color: #fff;
    font-size: 0.9rem;
    outline: none;
    width: 200px;
}

.search-input::placeholder {
    color: rgba(255, 255, 255, 0.4);
}

/* ===== 统计卡片 ===== */
.stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 1rem;
    margin-bottom: 1.5rem;
}

.stat-card {
    background: linear-gradient(145deg, rgba(0, 0, 0, 0.3), rgba(30, 25, 55, 0.4));
    border: 1px solid rgba(0, 255, 255, 0.2);
    border-radius: 12px;
    padding: 1.2rem;
    display: flex;
    align-items: center;
    gap: 1rem;
    transition: all 0.3s ease;
}

.stat-card:hover {
    border-color: rgba(0, 255, 255, 0.5);
    box-shadow: 0 0 20px rgba(0, 255, 255, 0.2);
    transform: translateY(-2px);
}

.stat-icon {
    font-size: 2rem;
}

.stat-content {
    flex: 1;
}

.stat-value {
    font-size: 1.5rem;
    font-weight: 700;
    color: #00ffff;
    text-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
}

.stat-label {
    font-size: 0.85rem;
    color: rgba(255, 255, 255, 0.6);
}

/* ===== 数据面板 ===== */
.data-panel {
    background: linear-gradient(145deg, rgba(0, 0, 0, 0.3), rgba(30, 25, 55, 0.4));
    border: 1px solid rgba(0, 255, 255, 0.2);
    border-radius: 12px;
    overflow: hidden;
}

.panel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem 1.5rem;
    border-bottom: 1px solid rgba(0, 255, 255, 0.2);
    background: rgba(0, 255, 255, 0.03);
}

.panel-header h2 {
    font-size: 1.1rem;
    font-weight: 600;
    color: #fff;
    margin: 0;
}

.cyber-badge {
    font-size: 0.8rem;
    color: #00ffff;
    background: rgba(0, 255, 255, 0.15);
    padding: 4px 12px;
    border-radius: 20px;
    border: 1px solid rgba(0, 255, 255, 0.3);
}

/* ===== 表格样式 ===== */
.cyber-table {
    width: 100%;
    border-collapse: collapse;
}

.cyber-table thead {
    background: rgba(0, 0, 0, 0.3);
}

.cyber-table th {
    padding: 1rem 1.5rem;
    text-align: left;
    font-size: 0.8rem;
    font-weight: 600;
    color: #00ffff;
    text-transform: uppercase;
    letter-spacing: 1px;
    border-bottom: 1px solid rgba(0, 255, 255, 0.2);
}

.cyber-table tbody tr {
    border-bottom: 1px solid rgba(0, 255, 255, 0.1);
    transition: all 0.2s ease;
}

.cyber-table tbody tr:hover {
    background: rgba(0, 255, 255, 0.05);
}

.cyber-table td {
    padding: 1rem 1.5rem;
    font-size: 0.85rem;
    color: rgba(255, 255, 255, 0.8);
}

.cell-name {
    font-weight: 500;
    color: #fff;
}

.empty-row {
    background: rgba(0, 0, 0, 0.1);
}

.empty-message {
    padding: 3rem;
    text-align: center;
    color: rgba(255, 255, 255, 0.4);
}

.empty-icon {
    font-size: 3rem;
    margin-bottom: 0.5rem;
}

/* ===== 状态标签 ===== */
.status-tag {
    display: inline-block;
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 0.75rem;
    font-weight: 500;
}

.status-active {
    background: rgba(0, 255, 136, 0.2);
    color: #00ff88;
    border: 1px solid rgba(0, 255, 136, 0.3);
}

.status-inactive {
    background: rgba(255, 100, 100, 0.2);
    color: #ff6464;
    border: 1px solid rgba(255, 100, 100, 0.3);
}

/* ===== 操作按钮容器 ===== */
.action-btns {
    display: flex;
    gap: 6px;
}

/* ===== 赛博朋克按钮基础样式 ===== */
.cyber-btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding: 12px 24px;
    font-size: 0.9rem;
    font-weight: 500;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.22s cubic-bezier(0.2, 0, 0.2, 1);
    text-decoration: none;
    position: relative;
    overflow: hidden;
    outline: none;
}

/* 按钮顶部高光描边 */
.cyber-btn::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.5), transparent);
    opacity: 0.6;
    transition: opacity 0.22s ease;
}

.cyber-btn:hover::before {
    opacity: 1;
}

/* 主要按钮样式 */
.cyber-btn-primary {
    background: linear-gradient(135deg, rgba(0, 255, 255, 0.2), rgba(0, 200, 255, 0.3));
    color: #00ffff;
    border: 1px solid rgba(0, 255, 255, 0.5);
    box-shadow: 0 0 15px rgba(0, 255, 255, 0.2);
}

.cyber-btn-primary:hover {
    background: linear-gradient(135deg, rgba(0, 255, 255, 0.3), rgba(0, 200, 255, 0.4));
    transform: translateY(-2px);
    box-shadow: 0 0 25px rgba(0, 255, 255, 0.4), 0 4px 12px rgba(0, 0, 0, 0.2);
    border-color: rgba(0, 255, 255, 0.8);
}

.cyber-btn-primary:active {
    transform: translateY(0) scale(0.97);
    box-shadow: 0 0 10px rgba(0, 255, 255, 0.3);
}

/* 次要按钮样式 */
.cyber-btn-secondary {
    background: rgba(255, 255, 255, 0.05);
    color: #fff;
    border: 1px solid rgba(255, 255, 255, 0.2);
}

.cyber-btn-secondary:hover {
    background: rgba(255, 255, 255, 0.1);
    border-color: rgba(0, 255, 255, 0.5);
    box-shadow: 0 0 15px rgba(0, 255, 255, 0.2);
}

/* 危险按钮样式 */
.cyber-btn-danger {
    background: rgba(255, 100, 100, 0.15);
    color: #ff6464;
    border: 1px solid rgba(255, 100, 100, 0.4);
}

.cyber-btn-danger:hover {
    background: rgba(255, 100, 100, 0.25);
    border-color: rgba(255, 100, 100, 0.6);
    box-shadow: 0 0 15px rgba(255, 100, 100, 0.3);
}

/* 轮廓按钮样式 */
.cyber-btn-outline {
    background: transparent;
    color: #00ffff;
    border: 1px solid rgba(0, 255, 255, 0.5);
}

.cyber-btn-outline:hover {
    background: rgba(0, 255, 255, 0.1);
    box-shadow: 0 0 15px rgba(0, 255, 255, 0.3);
}

/* 小按钮样式 */
.cyber-btn-sm {
    padding: 6px 14px;
    font-size: 0.8rem;
}

.btn-icon {
    font-size: 1.1rem;
    margin-right: 6px;
}

/* ===== 表单样式 ===== */
.cyber-form {
    margin: 0;
}

.form-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 1rem;
}

.form-group {
    display: flex;
    flex-direction: column;
}

.form-group.full-width {
    grid-column: span 2;
}

.form-group label {
    font-size: 0.85rem;
    color: rgba(255, 255, 255, 0.8);
    margin-bottom: 0.5rem;
}

.required {
    color: #ff6464;
}

.cyber-input, .cyber-select, .cyber-textarea {
    padding: 10px 14px;
    background: rgba(0, 0, 0, 0.3);
    border: 1px solid rgba(0, 255, 255, 0.3);
    border-radius: 8px;
    color: #fff;
    font-size: 0.9rem;
    transition: all 0.2s ease;
    outline: none;
}

.cyber-input:focus, .cyber-select:focus, .cyber-textarea:focus {
    border-color: #00ffff;
    box-shadow: 0 0 15px rgba(0, 255, 255, 0.3);
}

.cyber-textarea {
    resize: vertical;
    min-height: 80px;
}

.form-actions {
    margin-top: 1.5rem;
    padding-top: 1rem;
    border-top: 1px solid rgba(0, 255, 255, 0.2);
}

/* ===== 模态框样式 ===== */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.8);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
    backdrop-filter: blur(5px);
}

.modal-container {
    background: linear-gradient(145deg, rgba(0, 0, 0, 0.9), rgba(30, 25, 55, 0.95));
    border: 1px solid rgba(0, 255, 255, 0.3);
    border-radius: 12px;
    width: 90%;
    max-width: 600px;
    max-height: 90vh;
    overflow: hidden;
    box-shadow: 0 0 30px rgba(0, 255, 255, 0.2);
    display: flex;
    flex-direction: column;
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem 1.5rem;
    border-bottom: 1px solid rgba(0, 255, 255, 0.2);
    background: rgba(0, 255, 255, 0.03);
}

.modal-title {
    font-size: 1.2rem;
    font-weight: 600;
    color: #00ffff;
    text-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
    margin: 0;
}

.modal-close {
    background: none;
    border: none;
    color: rgba(255, 255, 255, 0.6);
    font-size: 1.5rem;
    cursor: pointer;
    padding: 0 0.5rem;
    transition: all 0.2s ease;
}

.modal-close:hover {
    color: #00ffff;
}

.modal-body-scrollable {
    flex: 1;
    padding: 1.5rem;
    overflow-y: auto;
    max-height: calc(100vh - 200px);
}

.modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
    padding: 1rem 1.5rem;
    border-top: 1px solid rgba(0, 255, 255, 0.2);
    background: rgba(0, 0, 0, 0.2);
}

/* ===== 响应式设计（移动端适配） ===== */
@media (max-width: 768px) {
    .main-content {
        padding: 1rem;
    }
    .header-content {
        padding: 0 1rem;
        flex-direction: column;
        gap: 1rem;
    }
    .header-title h1 {
        font-size: 1.2rem;
    }
    .toolbar {
        flex-direction: column;
        align-items: stretch;
    }
    .search-wrapper {
        width: 100%;
    }
    .search-input {
        width: 100%;
    }
    .stats-grid {
        grid-template-columns: 1fr;
    }
    .form-grid {
        grid-template-columns: 1fr;
    }
    .form-group.full-width {
        grid-column: span 1;
    }
}
</style>