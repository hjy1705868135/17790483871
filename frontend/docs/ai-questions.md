# AI提问记录

本文档记录了在完成电商平台前端异步请求功能开发过程中，向AI助手提出的问题及其解决方案。

---

## 问题1：前端异步请求工具设计

### 问题描述
如何设计一个前端异步请求工具模块，需要包含哪些核心功能？

### 上下文
- 需要支持分页查询和分类筛选
- 需要完善的错误处理
- 需要支持Mock数据
- 需要请求拦截器和响应拦截器

### AI回复
设计一个完整的请求工具应该包含以下核心功能：

1. **拦截器机制**
   - 请求拦截器：添加Token、参数格式化、时间戳防止缓存
   - 响应拦截器：统一错误处理、业务错误码识别

2. **错误处理**
   - 网络错误（断网、CORS）
   - 超时错误
   - HTTP错误（4xx、5xx）
   - 业务逻辑错误

3. **请求取消**
   - AbortController
   - 请求标识管理

4. **缓存机制**
   - 内存缓存
   - 缓存过期时间

5. **Mock数据支持**
   - 模拟网络延迟
   - 模拟真实请求行为

---

## 问题2：Fetch API与Axios的选择

### 问题描述
为什么选择Fetch API而不是Axios？

### 上下文
- 需要轻量级解决方案
- 不希望引入过多依赖
- 需要ES6+原生支持

### AI回复

**选择Fetch API的理由：**

| 方面 | Fetch API | Axios |
|------|-----------|-------|
| 依赖 | 无（浏览器原生） | 需要安装 |
| 大小 | ~0 KB | ~14 KB |
| 语法 | Promise原生 | 包装的Promise |
| 取消请求 | AbortController | CancelToken |
| 进度 | 无内置 | 有内置 |
| 错误处理 | 仅网络错误 | 所有错误 |

**Fetch API优势：**
1. **无需依赖**：浏览器原生支持，减少包体积
2. **ES6+标准**：符合现代前端开发趋势
3. **流式响应**：支持ReadableStream
4. **底层控制**：更灵活的配置能力

**Fetch API劣势：**
1. 需要手动处理JSON解析
2. 不支持请求进度
3. 错误处理不如Axios完善（需要额外封装）

**结论**：对于本项目，选择Fetch API是合理的，可以在不增加依赖的情况下满足所有需求。

---

## 问题3：如何实现请求拦截器

### 问题描述
如何实现类似Axios的请求/响应拦截器机制？

### AI回复

采用**责任链模式（Chain of Responsibility Pattern）**：

```javascript
class InterceptorManager {
    constructor() {
        this.requestInterceptors = [];
        this.responseInterceptors = [];
    }
    
    // 添加请求拦截器
    addRequestInterceptor(fulfilled, rejected) {
        const id = this.interceptorId++;
        this.requestInterceptors.push({ id, fulfilled, rejected });
        return id;
    }
    
    // 执行拦截器链
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
}
```

**执行流程：**
```
请求配置
    ↓
拦截器1 (fulfilled) → 修改配置
    ↓
拦截器2 (fulfilled) → 添加Token
    ↓
拦截器3 (fulfilled) → 添加时间戳
    ↓
发送请求
    ↓
接收响应
    ↓
拦截器3 (response.fulfilled) → 处理数据
    ↓
拦截器2 (response.fulfilled) → 检查错误码
    ↓
拦截器1 (response.fulfilled) → 统一格式化
    ↓
返回结果
```

---

## 问题4：如何实现请求取消功能

### 问题描述
如何实现类似Axios的请求取消功能？

### AI回复

使用 **AbortController**（W3C标准）：

```javascript
class Request {
    constructor() {
        this.controllers = new Map(); // 存储取消控制器
    }
    
    // 创建取消令牌
    createCancelToken(key) {
        const controller = new AbortController();
        this.controllers.set(key, controller);
        return controller;
    }
    
    // 取消请求
    cancelRequest(key) {
        const controller = this.controllers.get(key);
        if (controller) {
            controller.abort();
            this.controllers.delete(key);
        }
    }
    
    // 发送请求时关联信号
    async request(options) {
        const requestKey = this._generateRequestKey(options);
        const controller = this.createCancelToken(requestKey);
        
        try {
            const response = await fetch(options.url, {
                method: options.method,
                signal: controller.signal // 关键：关联AbortSignal
            });
            return response;
        } catch (error) {
            if (error.name === 'AbortError') {
                throw new CancelException('请求已取消');
            }
            throw error;
        }
    }
}
```

**使用示例：**
```javascript
// 创建请求
const key = 'fetch-products-1';
request.get('/products', {}, { requestKey: key });

// 取消请求
request.cancelRequest(key);
```

---

## 问题5：如何设计Mock数据系统

### 问题描述
如何设计一个Mock数据系统，模拟真实的异步请求行为？

### AI回复

设计Mock数据系统需要考虑：

1. **延迟模拟**：模拟真实网络延迟
2. **数据筛选**：根据参数返回不同数据
3. **分页支持**：正确处理分页逻辑
4. **错误模拟**：模拟各种错误场景

```javascript
const MockHandlers = {
    async getProductList(config) {
        // 1. 模拟网络延迟
        await new Promise(resolve => setTimeout(resolve, 500));
        
        // 2. 解析参数
        const { page = 1, pageSize = 10, category } = config.params || {};
        
        // 3. 数据筛选
        let filtered = allProducts;
        if (category && category !== 'all') {
            filtered = filtered.filter(p => p.category === category);
        }
        
        // 4. 分页计算
        const total = filtered.length;
        const startIndex = (page - 1) * pageSize;
        const products = filtered.slice(startIndex, startIndex + pageSize);
        
        // 5. 返回格式化数据
        return {
            code: 200,
            message: 'success',
            data: {
                list: products,
                pagination: { current: page, pageSize, total }
            }
        };
    }
};
```

**关键点：**
- 使用 `setTimeout` 模拟延迟
- 返回与真实API一致的响应格式
- 正确实现分页逻辑
- 支持各种查询参数

---

## 问题6：如何处理错误和异常

### 问题描述
如何设计前端的错误处理机制？

### AI回复

采用**分层错误处理**：

```javascript
// 1. 自定义异常类
class RequestException extends Error {
    constructor(message, code, status) {
        super(message);
        this.code = code;
        this.status = status;
    }
}

class NetworkException extends RequestException {
    constructor(message) {
        super(message, 'NETWORK_ERROR', 0);
    }
}

class TimeoutException extends RequestException {
    constructor(message) {
        super(message, 'TIMEOUT_ERROR', 408);
    }
}

// 2. 响应拦截器统一处理
addResponseInterceptor(
    (response) => response,
    (error) => {
        if (error.response) {
            // HTTP错误
            switch (error.response.status) {
                case 401: // Token过期
                    localStorage.removeItem('token');
                    window.location.href = '/login';
                    break;
                case 403:
                    throw new RequestException('无权限', 'FORBIDDEN', 403);
                case 404:
                    throw new RequestException('资源不存在', 'NOT_FOUND', 404);
            }
        } else if (error.request) {
            // 网络错误
            if (error.code === 'ECONNABORTED') {
                throw new TimeoutException('请求超时');
            }
            throw new NetworkException('网络连接失败');
        }
        
        throw error;
    }
);

// 3. 业务层错误处理
try {
    const response = await request.get('/api/products');
} catch (error) {
    if (error instanceof RequestException) {
        showError(error.message);
    } else {
        showError('未知错误');
    }
}
```

---

## 问题7：如何实现防抖和节流

### 问题描述
搜索功能需要防抖，如何实现？

### AI回复

```javascript
// 防抖函数
function debounce(func, wait = 500) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// 使用
const handleSearch = debounce((keyword) => {
    state.keyword = keyword;
    state.currentPage = 1;
    loadProducts();
}, 500);

// 绑定事件
searchInput.addEventListener('input', (e) => {
    handleSearch(e.target.value);
});
```

**防抖vs节流：**
- **防抖（debounce）**：最后一次触发后等待N秒执行
- **节流（throttle）**：每隔N秒执行一次

---

## 问题8：如何设计分页组件

### 问题描述
如何实现一个完整的分页组件？

### AI回复

分页组件设计要点：

```javascript
// 1. 状态管理
const state = {
    currentPage: 1,
    pageSize: 10,
    totalPages: 1,
    total: 0
};

// 2. 计算属性
const startIndex = (state.currentPage - 1) * state.pageSize;
const endIndex = startIndex + state.pageSize;
const hasPrev = state.currentPage > 1;
const hasNext = state.currentPage < state.totalPages;

// 3. 翻页处理
function goToPage(page) {
    if (page < 1 || page > state.totalPages) return;
    state.currentPage = page;
    loadProducts();
}

function prevPage() {
    if (hasPrev) goToPage(state.currentPage - 1);
}

function nextPage() {
    if (hasNext) goToPage(state.currentPage + 1);
}

// 4. 渲染
paginationInfo.textContent = `第 ${currentPage} / ${totalPages} 页`;
prevButton.disabled = !hasPrev;
nextButton.disabled = !hasNext;
```

---

## 问题9：如何实现请求日志

### 问题描述
如何实现类似Chrome DevTools的请求日志面板？

### AI回复

```javascript
// 日志管理
class LogManager {
    constructor() {
        this.logs = [];
        this.maxLogs = 50; // 最多保留50条
    }
    
    addLog(method, url, params, status, error = null) {
        const log = {
            id: `log-${Date.now()}-${Math.random()}`,
            method,
            url,
            params,
            status,
            error,
            time: new Date().toLocaleTimeString()
        };
        
        this.logs.unshift(log);
        
        // 限制数量
        if (this.logs.length > this.maxLogs) {
            this.logs = this.logs.slice(0, this.maxLogs);
        }
        
        this.render();
    }
    
    render() {
        const html = this.logs.map(log => `
            <div class="log-item">
                <div class="log-time">${log.time}</div>
                <div>
                    <span class="log-method ${log.method.toLowerCase()}">${log.method}</span>
                    <span class="log-url">${log.url}</span>
                    <span class="log-status ${log.status}">${log.status}</span>
                </div>
                ${Object.keys(log.params).length ? `
                    <div class="log-params">${JSON.stringify(log.params)}</div>
                ` : ''}
            </div>
        `).join('');
        
        document.getElementById('logList').innerHTML = html;
    }
}
```

---

## 问题10：ES6模块化最佳实践

### 问题描述
如何正确使用ES6模块化？

### AI回复

**导出方式：**

```javascript
// 具名导出（多个）
export const CONSTANT = 'value';
export function getData() { ... }

// 默认导出（一个）
export default class Request { ... }

// 混合导出
export const A = 1;
export default class Default { ... }
```

**导入方式：**

```javascript
// 导入默认导出
import Request from './request.js';

// 导入具名导出
import { CONSTANT, getData } from './utils.js';

// 导入所有
import * as Utils from './utils.js';

// 导入并重命名
import { CONSTANT as VALUE } from './utils.js';

// 同时导入默认和具名
import Default, { NAMED } from './module.js';
```

**最佳实践：**
1. 一个文件只做一件事
2. 使用**index.js**作为模块入口
3. 相对路径导入不需要.js后缀
4. 使用**barrel pattern**（桶模式）组织导出

---

## 问题11：如何处理跨域请求

### 问题描述
开发环境中如何处理跨域问题？

### AI回复

**方案1：开发服务器代理**

```javascript
// vite.config.js
export default {
    server: {
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true
            }
        }
    }
};
```

**方案2：CORS头部**

后端添加CORS头：
```java
// Spring Boot
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:8000")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true);
    }
}
```

**方案3：JSONP（仅GET）**

```javascript
function jsonp(url, callback) {
    const callbackName = 'jsonp_callback_' + Date.now();
    window[callbackName] = callback;
    
    const script = document.createElement('script');
    script.src = `${url}?callback=${callbackName}`;
    document.body.appendChild(script);
    
    script.onload = () => document.body.removeChild(script);
}
```

**推荐**：方案1（开发环境）+ 方案2（生产环境）

---

## 问题12：如何优化大量数据渲染

### 问题描述
商品列表有30+商品，如何优化渲染性能？

### AI回复

**方案1：虚拟列表（Virtual Scrolling）**

只渲染可见区域的元素：

```javascript
// 简单实现
class VirtualList {
    constructor(container, items, itemHeight) {
        this.container = container;
        this.items = items;
        this.itemHeight = itemHeight;
        this.visibleCount = Math.ceil(container.height / itemHeight);
    }
    
    render(scrollTop) {
        const startIndex = Math.floor(scrollTop / this.itemHeight);
        const visibleItems = this.items.slice(
            startIndex, 
            startIndex + this.visibleCount + 1
        );
        
        // 渲染可见项
        // 添加paddingTop/paddingBottom模拟滚动
    }
}
```

**方案2：分页（当前使用）**

将数据分页，每次只渲染10条：

```javascript
const pageSize = 10;
const totalPages = Math.ceil(total / pageSize);
const currentItems = allItems.slice((page - 1) * pageSize, page * pageSize);
```

**方案3：懒加载（Intersection Observer）**

滚动到可视区域才加载图片：

```javascript
const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            const img = entry.target;
            img.src = img.dataset.src; // 真实URL
            observer.unobserve(img);
        }
    });
});
```

**本项目选择**：分页方案，简单高效，满足需求。

---

## 总结

通过以上问题的解决，完成了：

### 核心功能
1. **请求工具模块**
   - Fetch API封装
   - 拦截器机制
   - 错误处理
   - 请求取消
   - 缓存管理

2. **Mock数据系统**
   - 30个模拟商品
   - 5个分类
   - 真实分页逻辑
   - 500ms延迟模拟

3. **商品列表功能**
   - 分页查询
   - 分类筛选
   - 关键词搜索
   - 多字段排序

### 技术亮点
- ✅ 责任链模式的拦截器
- ✅ AbortController请求取消
- ✅ 防抖优化搜索性能
- ✅ 请求日志实时显示
- ✅ ES6+模块化设计
- ✅ 分层错误处理

### 项目成果
- 📦 完整的请求工具（~700行代码）
- 🛒 可用的商品列表页面
- 📝 详细的实现文档
- 🧪 完整的测试方案

---

**AI工具使用总结：**

在整个项目开发过程中，AI助手提供了以下帮助：

1. **架构设计指导**：提供了请求工具的完整架构方案
2. **技术选型建议**：解释了Fetch API vs Axios的权衡
3. **代码模式指导**：提供了拦截器、错误处理等设计模式
4. **性能优化建议**：防抖、虚拟列表等优化方案
5. **最佳实践分享**：ES6模块化、错误处理等编码规范

AI作为开发助手的价值在于：
- 提供技术方案参考
- 解释技术原理
- 分享最佳实践
- 代码审查建议
