/**
 * 异步请求工具模块 - request.js
 * 
 * 功能特性：
 * - 统一的请求拦截器与响应拦截器
 * - 错误处理机制（网络错误、超时、服务器错误等）
 * - 请求参数格式化与响应数据处理
 * - 支持请求取消功能
 * - 支持Mock数据模式
 * 
 * @author Frontend Team
 * @version 1.0.0
 */

// ============================================================
// 常量配置
// ============================================================

const CONFIG = {
    // API基础URL
    baseURL: 'http://localhost:8080/api',
    
    // 超时时间（毫秒）
    timeout: 30000,
    
    // 是否启用Mock数据
    useMock: true,
    
    // Mock延迟时间（毫秒）- 模拟网络请求延迟
    mockDelay: 500,
    
    // Content-Type类型
    contentTypes: {
        JSON: 'application/json',
        FORM: 'application/x-www-form-urlencoded',
        FORM_DATA: 'multipart/form-data'
    },
    
    // HTTP方法
    methods: {
        GET: 'GET',
        POST: 'POST',
        PUT: 'PUT',
        DELETE: 'DELETE',
        PATCH: 'PATCH'
    }
};

// ============================================================
// 响应状态码
// ============================================================

const RESPONSE_CODE = {
    SUCCESS: 200,           // 成功
    CREATED: 201,          // 创建成功
    NO_CONTENT: 204,       // 无内容
    BAD_REQUEST: 400,      // 请求参数错误
    UNAUTHORIZED: 401,     // 未授权
    FORBIDDEN: 403,        // 禁止访问
    NOT_FOUND: 404,        // 资源不存在
    TIMEOUT: 408,          // 请求超时
    SERVER_ERROR: 500,     // 服务器错误
    SERVICE_UNAVAILABLE: 503 // 服务不可用
};

// ============================================================
// 自定义异常类
// ============================================================

/**
 * 请求异常类
 */
class RequestException extends Error {
    constructor(message, code, status, response) {
        super(message);
        this.name = 'RequestException';
        this.code = code;
        this.status = status;
        this.response = response;
        this.timestamp = new Date().toISOString();
    }
}

/**
 * 网络异常类
 */
class NetworkException extends RequestException {
    constructor(message, status) {
        super(message, 'NETWORK_ERROR', status, null);
        this.name = 'NetworkException';
    }
}

/**
 * 超时异常类
 */
class TimeoutException extends RequestException {
    constructor(message) {
        super(message, 'TIMEOUT_ERROR', RESPONSE_CODE.TIMEOUT, null);
        this.name = 'TimeoutException';
    }
}

/**
 * 服务器异常类
 */
class ServerException extends RequestException {
    constructor(message, status, response) {
        super(message, 'SERVER_ERROR', status, response);
        this.name = 'ServerException';
    }
}

// ============================================================
// 请求拦截器管理器
// ============================================================

class InterceptorManager {
    constructor() {
        this.requestInterceptors = [];  // 请求拦截器队列
        this.responseInterceptors = []; // 响应拦截器队列
        this.interceptorId = 0;
    }

    /**
     * 添加请求拦截器
     * @param {Function} fulfilled - 成功回调
     * @param {Function} rejected - 失败回调
     * @returns {number} 拦截器ID
     */
    addRequestInterceptor(fulfilled, rejected) {
        const id = this.interceptorId++;
        this.requestInterceptors.push({
            id,
            fulfilled,
            rejected,
            synchronous: false
        });
        return id;
    }

    /**
     * 添加响应拦截器
     * @param {Function} fulfilled - 成功回调
     * @param {Function} rejected - 失败回调
     * @returns {number} 拦截器ID
     */
    addResponseInterceptor(fulfilled, rejected) {
        const id = this.interceptorId++;
        this.responseInterceptors.push({
            id,
            fulfilled,
            rejected,
            synchronous: false
        });
        return id;
    }

    /**
     * 移除请求拦截器
     * @param {number} id - 拦截器ID
     */
    removeRequestInterceptor(id) {
        this.requestInterceptors = this.requestInterceptors.filter(interceptor => interceptor.id !== id);
    }

    /**
     * 移除响应拦截器
     * @param {number} id - 拦截器ID
     */
    removeResponseInterceptor(id) {
        this.responseInterceptors = this.responseInterceptors.filter(interceptor => interceptor.id !== id);
    }

    /**
     * 清空所有拦截器
     */
    clearAll() {
        this.requestInterceptors = [];
        this.responseInterceptors = [];
    }

    /**
     * 执行请求拦截器链
     * @param {Object} config - 请求配置
     * @returns {Promise<Object>} 处理后的配置
     */
    async executeRequestInterceptors(config) {
        let processedConfig = { ...config };
        
        for (const interceptor of this.requestInterceptors) {
            try {
                processedConfig = await interceptor.fulfilled(processedConfig);
            } catch (error) {
                if (interceptor.rejected) {
                    await interceptor.rejected(error);
                } else {
                    throw error;
                }
            }
        }
        
        return processedConfig;
    }

    /**
     * 执行响应拦截器链
     * @param {Object} response - 响应数据
     * @returns {Promise<Object>} 处理后的响应
     */
    async executeResponseInterceptors(response) {
        let processedResponse = response;
        
        for (const interceptor of this.responseInterceptors) {
            try {
                processedResponse = await interceptor.fulfilled(processedResponse);
            } catch (error) {
                if (interceptor.rejected) {
                    processedResponse = await interceptor.rejected(error);
                } else {
                    throw error;
                }
            }
        }
        
        return processedResponse;
    }
}

// ============================================================
// 请求缓存管理
// ============================================================

class CacheManager {
    constructor() {
        this.cache = new Map();
        this.cacheTimeout = 5 * 60 * 1000; // 默认缓存5分钟
    }

    /**
     * 设置缓存
     * @param {string} key - 缓存键
     * @param {*} value - 缓存值
     * @param {number} ttl - 缓存时间（毫秒）
     */
    set(key, value, ttl = this.cacheTimeout) {
        const cacheItem = {
            value,
            expiry: Date.now() + ttl
        };
        this.cache.set(key, cacheItem);
    }

    /**
     * 获取缓存
     * @param {string} key - 缓存键
     * @returns {*} 缓存值
     */
    get(key) {
        const item = this.cache.get(key);
        if (!item) return null;
        
        if (Date.now() > item.expiry) {
            this.cache.delete(key);
            return null;
        }
        
        return item.value;
    }

    /**
     * 删除缓存
     * @param {string} key - 缓存键
     */
    delete(key) {
        this.cache.delete(key);
    }

    /**
     * 清空所有缓存
     */
    clear() {
        this.cache.clear();
    }

    /**
     * 生成缓存键
     * @param {string} url - 请求URL
     * @param {Object} params - 请求参数
     * @returns {string} 缓存键
     */
    generateKey(url, params) {
        return `${url}:${JSON.stringify(params || {})}`;
    }
}

// ============================================================
// 请求队列管理器
// ============================================================

class QueueManager {
    constructor() {
        this.queue = [];
        this.maxConcurrent = 6; // 最大并发数
        this.running = 0;
    }

    /**
     * 添加请求到队列
     * @param {Function} request - 请求函数
     * @returns {Promise} 请求Promise
     */
    add(request) {
        return new Promise((resolve, reject) => {
            this.queue.push({ request, resolve, reject });
            this.process();
        });
    }

    /**
     * 处理队列
     */
    async process() {
        while (this.running < this.maxConcurrent && this.queue.length > 0) {
            const { request, resolve, reject } = this.queue.shift();
            this.running++;
            
            request()
                .then(resolve)
                .catch(reject)
                .finally(() => {
                    this.running--;
                    this.process();
                });
        }
    }
}

// ============================================================
// 主请求工具类
// ============================================================

class Request {
    constructor() {
        // 初始化拦截器管理器
        this.interceptors = new InterceptorManager();
        
        // 初始化缓存管理器
        this.cache = new CacheManager();
        
        // 初始化请求队列
        this.queue = new QueueManager();
        
        // 请求控制器映射（用于取消请求）
        this.controllers = new Map();
        
        // 配置
        this.config = { ...CONFIG };
        
        // 初始化默认拦截器
        this._initDefaultInterceptors();
    }

    /**
     * 初始化默认拦截器
     * @private
     */
    _initDefaultInterceptors() {
        // 默认请求拦截器
        this.interceptors.addRequestInterceptor(
            (config) => {
                // 添加时间戳防止缓存
                if (config.method === CONFIG.methods.GET && !config.cache) {
                    config.params = {
                        ...config.params,
                        _t: Date.now()
                    };
                }
                
                // 添加Token（如果有）
                const token = localStorage.getItem('token');
                if (token && config.needToken !== false) {
                    config.headers = {
                        ...config.headers,
                        'Authorization': `Bearer ${token}`
                    };
                }
                
                console.log(`[Request] ${config.method} ${config.url}`, config);
                return config;
            },
            (error) => {
                console.error('[Request Interceptor Error]', error);
                return Promise.reject(error);
            }
        );

        // 默认响应拦截器
        this.interceptors.addResponseInterceptor(
            (response) => {
                console.log(`[Response] ${response.config.method} ${response.config.url}`, response);
                
                // 处理业务错误码
                if (response.data && response.data.code !== RESPONSE_CODE.SUCCESS) {
                    const error = new RequestException(
                        response.data.message || '业务处理失败',
                        response.data.code,
                        response.status,
                        response.data
                    );
                    throw error;
                }
                
                return response;
            },
            (error) => {
                console.error('[Response Interceptor Error]', error);
                
                // 处理错误响应
                if (error.response) {
                    const { status, data } = error.response;
                    
                    switch (status) {
                        case RESPONSE_CODE.UNAUTHORIZED:
                            // Token过期，跳转登录
                            localStorage.removeItem('token');
                            window.location.href = '/login';
                            throw new RequestException('登录已过期，请重新登录', 'UNAUTHORIZED', status, data);
                            
                        case RESPONSE_CODE.FORBIDDEN:
                            throw new RequestException('没有权限访问', 'FORBIDDEN', status, data);
                            
                        case RESPONSE_CODE.NOT_FOUND:
                            throw new RequestException('请求的资源不存在', 'NOT_FOUND', status, data);
                            
                        case RESPONSE_CODE.SERVER_ERROR:
                            throw new ServerException('服务器内部错误', status, data);
                            
                        default:
                            throw new RequestException(
                                data?.message || '请求失败',
                                'REQUEST_ERROR',
                                status,
                                data
                            );
                    }
                } else if (error.request) {
                    // 请求已发出但没有收到响应
                    if (error.code === 'ECONNABORTED') {
                        throw new TimeoutException('请求超时，请稍后重试');
                    }
                    throw new NetworkException('网络连接失败，请检查网络设置', 0);
                } else {
                    // 请求配置出错
                    throw new RequestException(error.message || '请求配置错误', 'CONFIG_ERROR', 0, null);
                }
            }
        );
    }

    /**
     * 创建取消令牌
     * @param {string} key - 请求标识
     * @returns {AbortController} 中止控制器
     */
    createCancelToken(key) {
        const controller = new AbortController();
        this.controllers.set(key, controller);
        return controller;
    }

    /**
     * 取消请求
     * @param {string} key - 请求标识
     */
    cancelRequest(key) {
        const controller = this.controllers.get(key);
        if (controller) {
            controller.abort();
            this.controllers.delete(key);
        }
    }

    /**
     * 取消所有请求
     */
    cancelAllRequests() {
        this.controllers.forEach(controller => controller.abort());
        this.controllers.clear();
    }

    /**
     * 生成请求唯一标识
     * @param {string} method - 请求方法
     * @param {string} url - 请求URL
     * @param {Object} params - 请求参数
     * @returns {string} 请求标识
     */
    _generateRequestKey(method, url, params) {
        return `${method}:${url}:${JSON.stringify(params || {})}`;
    }

    /**
     * 发送请求
     * @param {Object} options - 请求配置
     * @returns {Promise} 请求Promise
     */
    async request(options) {
        // 合并配置
        const config = {
            ...this.config,
            ...options,
            headers: {
                'Content-Type': CONFIG.contentTypes.JSON,
                ...this.config.headers,
                ...options.headers
            }
        };

        try {
            // 执行请求拦截器
            config = await this.interceptors.executeRequestInterceptors(config);
            
            // 生成请求标识
            const requestKey = this._generateRequestKey(config.method, config.url, config.params);
            
            // 创建取消令牌
            const controller = this.createCancelToken(requestKey);
            config.signal = controller.signal;

            // 检查缓存（仅GET请求）
            if (config.method === CONFIG.methods.GET && config.useCache) {
                const cacheKey = this.cache.generateKey(config.url, config.params);
                const cachedData = this.cache.get(cacheKey);
                if (cachedData) {
                    console.log('[Cache] Data retrieved from cache:', cacheKey);
                    return cachedData;
                }
            }

            // 发送请求
            let response;
            
            if (this.config.useMock && config.mock !== false) {
                // Mock模式
                response = await this._mockRequest(config);
            } else {
                // 真实请求
                response = await this._fetchRequest(config);
            }

            // 执行响应拦截器
            response = await this.interceptors.executeResponseInterceptors(response);

            // 缓存数据（如果有）
            if (config.method === CONFIG.methods.GET && config.useCache) {
                const cacheKey = this.cache.generateKey(config.url, config.params);
                this.cache.set(cacheKey, response, config.cacheTTL);
            }

            // 清理取消令牌
            this.controllers.delete(requestKey);

            return response;

        } catch (error) {
            // 清理取消令牌
            this.controllers.delete(requestKey);
            throw error;
        }
    }

    /**
     * Fetch请求实现
     * @param {Object} config - 请求配置
     * @returns {Promise<Object>} 响应数据
     * @private
     */
    async _fetchRequest(config) {
        // 构建URL
        let url = config.url;
        if (!url.startsWith('http')) {
            url = `${config.baseURL}${url}`;
        }

        // 添加查询参数
        if (config.params) {
            const searchParams = new URLSearchParams();
            Object.entries(config.params).forEach(([key, value]) => {
                if (value !== undefined && value !== null) {
                    if (Array.isArray(value)) {
                        value.forEach(v => searchParams.append(key, v));
                    } else {
                        searchParams.append(key, value);
                    }
                }
            });
            const queryString = searchParams.toString();
            if (queryString) {
                url += (url.includes('?') ? '&' : '?') + queryString;
            }
        }

        // 构建请求选项
        const requestOptions = {
            method: config.method,
            headers: config.headers,
            signal: config.signal,
            mode: 'cors',
            credentials: 'include'
        };

        // 添加请求体（非GET请求）
        if (config.method !== CONFIG.methods.GET && config.data) {
            if (config.headers['Content-Type'] === CONFIG.contentTypes.JSON) {
                requestOptions.body = JSON.stringify(config.data);
            } else {
                requestOptions.body = config.data;
            }
        }

        // 发送请求
        const response = await fetch(url, requestOptions);
        
        // 解析响应
        const contentType = response.headers.get('content-type');
        let data;
        
        if (contentType && contentType.includes('application/json')) {
            data = await response.json();
        } else {
            data = await response.text();
        }

        // 构建响应对象
        const result = {
            data,
            status: response.status,
            statusText: response.statusText,
            headers: response.headers,
            config: config
        };

        return result;
    }

    /**
     * Mock请求实现
     * @param {Object} config - 请求配置
     * @returns {Promise<Object>} Mock响应数据
     * @private
     */
    async _mockRequest(config) {
        // 模拟网络延迟
        await new Promise(resolve => setTimeout(resolve, this.config.mockDelay));

        // 调用Mock处理函数
        const mockHandler = MockHandlers[config.mockHandler];
        
        if (mockHandler) {
            const mockData = await mockHandler(config);
            return {
                data: mockData,
                status: RESPONSE_CODE.SUCCESS,
                statusText: 'OK',
                headers: new Map([['content-type', 'application/json']]),
                config: config
            };
        }

        throw new RequestException('Mock handler not found', 'MOCK_ERROR', 404, null);
    }

    /**
     * GET请求
     * @param {string} url - 请求URL
     * @param {Object} params - 查询参数
     * @param {Object} options - 其他配置
     * @returns {Promise}
     */
    get(url, params = {}, options = {}) {
        return this.request({
            method: CONFIG.methods.GET,
            url,
            params,
            ...options
        });
    }

    /**
     * POST请求
     * @param {string} url - 请求URL
     * @param {Object} data - 请求数据
     * @param {Object} options - 其他配置
     * @returns {Promise}
     */
    post(url, data = {}, options = {}) {
        return this.request({
            method: CONFIG.methods.POST,
            url,
            data,
            ...options
        });
    }

    /**
     * PUT请求
     * @param {string} url - 请求URL
     * @param {Object} data - 请求数据
     * @param {Object} options - 其他配置
     * @returns {Promise}
     */
    put(url, data = {}, options = {}) {
        return this.request({
            method: CONFIG.methods.PUT,
            url,
            data,
            ...options
        });
    }

    /**
     * DELETE请求
     * @param {string} url - 请求URL
     * @param {Object} params - 查询参数
     * @param {Object} options - 其他配置
     * @returns {Promise}
     */
    delete(url, params = {}, options = {}) {
        return this.request({
            method: CONFIG.methods.DELETE,
            url,
            params,
            ...options
        });
    }

    /**
     * PATCH请求
     * @param {string} url - 请求URL
     * @param {Object} data - 请求数据
     * @param {Object} options - 其他配置
     * @returns {Promise}
     */
    patch(url, data = {}, options = {}) {
        return this.request({
            method: CONFIG.methods.PATCH,
            url,
            data,
            ...options
        });
    }

    /**
     * 并发请求
     * @param {Array<Promise>} promises - Promise数组
     * @returns {Promise<Array>} 结果数组
     */
    all(promises) {
        return Promise.all(promises);
    }

    /**
     * 设置配置
     * @param {Object} newConfig - 新配置
     */
    setConfig(newConfig) {
        this.config = { ...this.config, ...newConfig };
    }

    /**
     * 启用Mock模式
     */
    enableMock() {
        this.config.useMock = true;
    }

    /**
     * 禁用Mock模式
     */
    disableMock() {
        this.config.useMock = false;
    }
}

// ============================================================
// Mock数据处理器
// ============================================================

const MockHandlers = {
    /**
     * 获取商品列表（支持分页和分类筛选）
     */
    async getProductList(config) {
        const { params = {} } = config;
        const { 
            page = 1, 
            pageSize = 10, 
            category = 'all',
            keyword = '',
            sortBy = 'createTime',
            sortOrder = 'desc'
        } = params;

        // 模拟商品数据
        const allProducts = generateMockProducts();
        
        // 分类筛选
        let filteredProducts = category === 'all' 
            ? allProducts 
            : allProducts.filter(p => p.category === category);
        
        // 关键词筛选
        if (keyword) {
            filteredProducts = filteredProducts.filter(p => 
                p.name.toLowerCase().includes(keyword.toLowerCase()) ||
                p.description.toLowerCase().includes(keyword.toLowerCase())
            );
        }
        
        // 排序
        filteredProducts.sort((a, b) => {
            const aValue = a[sortBy];
            const bValue = b[sortBy];
            if (sortOrder === 'asc') {
                return aValue > bValue ? 1 : -1;
            } else {
                return aValue < bValue ? 1 : -1;
            }
        });
        
        // 分页
        const total = filteredProducts.length;
        const totalPages = Math.ceil(total / pageSize);
        const startIndex = (page - 1) * pageSize;
        const endIndex = startIndex + pageSize;
        const products = filteredProducts.slice(startIndex, endIndex);

        console.log(`[Mock] Page ${page}, Category: ${category}, Total: ${total}`);

        return {
            code: 200,
            message: 'success',
            data: {
                list: products,
                pagination: {
                    current: page,
                    pageSize: pageSize,
                    total: total,
                    totalPages: totalPages
                }
            }
        };
    },

    /**
     * 获取商品详情
     */
    async getProductDetail(config) {
        const { params = {} } = config;
        const { id } = params;

        const products = generateMockProducts();
        const product = products.find(p => p.id === parseInt(id));

        if (!product) {
            return {
                code: 404,
                message: '商品不存在',
                data: null
            };
        }

        return {
            code: 200,
            message: 'success',
            data: product
        };
    },

    /**
     * 获取商品分类
     */
    async getCategories() {
        return {
            code: 200,
            message: 'success',
            data: [
                { id: 1, name: '电子产品', code: 'electronics', icon: '📱' },
                { id: 2, name: '服装鞋包', code: 'clothing', icon: '👕' },
                { id: 3, name: '家居用品', code: 'home', icon: '🏠' },
                { id: 4, name: '食品饮料', code: 'food', icon: '🍎' },
                { id: 5, name: '图书音像', code: 'books', icon: '📚' }
            ]
        };
    }
};

/**
 * 生成模拟商品数据
 * @returns {Array} 商品列表
 */
function generateMockProducts() {
    const categories = ['electronics', 'clothing', 'home', 'food', 'books'];
    const products = [];
    
    const productTemplates = [
        { name: 'iPhone 15 Pro Max', category: 'electronics', price: 9999, description: '苹果旗舰手机' },
        { name: 'MacBook Pro 14英寸', category: 'electronics', price: 15999, description: '专业笔记本电脑' },
        { name: 'AirPods Pro', category: 'electronics', price: 1899, description: '主动降噪无线耳机' },
        { name: 'iPad Air', category: 'electronics', price: 4799, description: '轻薄平板电脑' },
        { name: '华为Mate60 Pro', category: 'electronics', price: 6999, description: '华为旗舰手机' },
        { name: '小米手环8', category: 'electronics', price: 299, description: '智能运动手环' },
        { name: '索尼WH-1000XM5', category: 'electronics', price: 2699, description: '头戴式降噪耳机' },
        { name: '三星Galaxy S24 Ultra', category: 'electronics', price: 8999, description: '安卓旗舰手机' },
        { name: '联想ThinkPad X1', category: 'electronics', price: 12999, description: '商务笔记本电脑' },
        { name: '戴森V15吸尘器', category: 'home', price: 5499, description: '智能无线吸尘器' },
        { name: '飞利浦空气炸锅', category: 'home', price: 599, description: '多功能空气炸锅' },
        { name: '小米扫地机器人', category: 'home', price: 1999, description: '智能扫地机器人' },
        { name: '苏泊尔电饭煲', category: 'home', price: 399, description: '智能电饭煲' },
        { name: '九阳破壁机', category: 'home', price: 799, description: '多功能破壁机' },
        { name: '美的空调', category: 'home', price: 2999, description: '变频空调' },
        { name: 'Nike运动鞋', category: 'clothing', price: 899, description: '经典运动鞋' },
        { name: 'Adidas卫衣', category: 'clothing', price: 599, description: '运动休闲卫衣' },
        { name: 'Uniqlo羽绒服', category: 'clothing', price: 799, description: '轻便羽绒服' },
        { name: 'Levi's牛仔裤', category: 'clothing', price: 499, description: '经典牛仔裤' },
        { name: 'Coach手提包', category: 'clothing', price: 3999, description: '真皮手提包' },
        { name: '新西兰蜂蜜', category: 'food', price: 159, description: '纯天然蜂蜜500g' },
        { name: '龙井茶叶', category: 'food', price: 299, description: '西湖龙井250g' },
        { name: '三只松鼠坚果礼盒', category: 'food', price: 128, description: '混合坚果礼盒' },
        { name: '伊利纯牛奶', category: 'food', price: 68, description: '纯牛奶24盒装' },
        { name: '蒙牛酸奶', category: 'food', price: 45, description: '原味酸奶10盒装' },
        { name: '《活着》', category: 'books', price: 36, description: '余华经典小说' },
        { name: '《Python编程》', category: 'books', price: 89, description: 'Python入门书籍' },
        { name: '《人类简史》', category: 'books', price: 68, description: '历史科普读物' },
        { name: '《三体》全套', category: 'books', price: 128, description: '刘慈欣科幻小说' },
        { name: '《经济学原理》', category: 'books', price: 158, description: '经济学经典教材' }
    ];

    // 生成30个商品
    for (let i = 0; i < 30; i++) {
        const template = productTemplates[i % productTemplates.length];
        const id = i + 1;
        const now = new Date();
        
        products.push({
            id: id,
            name: template.name + (i > 30 ? `-${Math.floor(i / 30)}` : ''),
            category: template.category,
            categoryName: getCategoryName(template.category),
            price: template.price + (i * 10), // 每个商品价格略有不同
            originalPrice: Math.round(template.price * 1.2), // 原价
            description: template.description,
            image: `https://via.placeholder.com/300x300?text=${encodeURIComponent(template.name)}`,
            stock: Math.floor(Math.random() * 100) + 10,
            sales: Math.floor(Math.random() * 1000) + 100,
            rating: (4 + Math.random()).toFixed(1),
            createTime: new Date(now - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString(),
            updateTime: new Date(now - Math.random() * 7 * 24 * 60 * 60 * 1000).toISOString()
        });
    }

    return products;
}

/**
 * 获取分类名称
 * @param {string} code - 分类编码
 * @returns {string} 分类名称
 */
function getCategoryName(code) {
    const categoryMap = {
        'electronics': '电子产品',
        'clothing': '服装鞋包',
        'home': '家居用品',
        'food': '食品饮料',
        'books': '图书音像'
    };
    return categoryMap[code] || code;
}

// ============================================================
// 创建并导出默认实例
// ============================================================

const request = new Request();

// 导出
export default request;
export { request, Request, RequestException, NetworkException, TimeoutException, ServerException };
