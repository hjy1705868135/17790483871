/**
 * 商品服务模块 - productService.js
 * 
 * 提供商品相关的API调用接口
 * 
 * @author Frontend Team
 * @version 1.0.0
 */

import request from '../utils/request.js';

/**
 * 获取商品列表（支持分页和分类筛选）
 * @param {Object} params - 查询参数
 * @param {number} params.page - 当前页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.category - 商品分类
 * @param {string} params.keyword - 搜索关键词
 * @param {string} params.sortBy - 排序字段
 * @param {string} params.sortOrder - 排序顺序
 * @returns {Promise<Object>} 商品列表数据
 */
export async function getProductList(params) {
    try {
        const response = await request.get('/products', params, {
            mockHandler: 'getProductList',
            useCache: false // 禁用缓存，确保每次请求都是新请求
        });
        return response.data;
    } catch (error) {
        console.error('获取商品列表失败:', error);
        throw error;
    }
}

/**
 * 获取商品详情
 * @param {number} id - 商品ID
 * @returns {Promise<Object>} 商品详情
 */
export async function getProductDetail(id) {
    try {
        const response = await request.get('/products/detail', { id }, {
            mockHandler: 'getProductDetail'
        });
        return response.data;
    } catch (error) {
        console.error('获取商品详情失败:', error);
        throw error;
    }
}

/**
 * 获取商品分类列表
 * @returns {Promise<Array>} 分类列表
 */
export async function getCategories() {
    try {
        const response = await request.get('/categories', {}, {
            mockHandler: 'getCategories'
        });
        return response.data;
    } catch (error) {
        console.error('获取分类列表失败:', error);
        throw error;
    }
}

/**
 * 商品搜索
 * @param {Object} params - 搜索参数
 * @returns {Promise<Object>} 搜索结果
 */
export async function searchProducts(params) {
    try {
        const response = await request.get('/products/search', params, {
            mockHandler: 'getProductList'
        });
        return response.data;
    } catch (error) {
        console.error('商品搜索失败:', error);
        throw error;
    }
}
