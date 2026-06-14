/**
 * 无人机相关 API 接口
 * 封装所有无人机相关的 HTTP 请求
 */

// 导入配置好的 Axios 实例
import apiClient from './index'

/**
 * 无人机 API 接口
 */
const droneApi = {
    /**
     * 获取所有无人机列表
     * @returns {Promise<Array>} 无人机列表
     */
    getAll() {
        return apiClient.get('/drones')
    },

    /**
     * 根据ID获取单个无人机
     * @param {Number} id - 无人机ID
     * @returns {Promise<Object>} 无人机对象
     */
    getById(id) {
        return apiClient.get(`/drones/${id}`)
    },

    /**
     * 创建新无人机
     * @param {Object} droneData - 无人机数据
     * @returns {Promise<Object>} 创建的无人机对象
     */
    create(droneData) {
        return apiClient.post('/drones', droneData)
    },

    /**
     * 更新无人机信息
     * @param {Number} id - 无人机ID
     * @param {Object} droneData - 更新的无人机数据
     * @returns {Promise<Object>} 更新后的无人机对象
     */
    update(id, droneData) {
        return apiClient.put(`/drones/${id}`, droneData)
    },

    /**
     * 删除无人机
     * @param {Number} id - 无人机ID
     * @returns {Promise<void>}
     */
    delete(id) {
        return apiClient.delete(`/drones/${id}`)
    },

    /**
     * 根据名称搜索无人机
     * @param {String} name - 无人机名称
     * @returns {Promise<Array>} 匹配的无人机列表
     */
    searchByName(name) {
        return apiClient.get('/drones', { params: { name } })
    },

    /**
     * 根据型号搜索无人机
     * @param {String} model - 无人机型号
     * @returns {Promise<Array>} 匹配的无人机列表
     */
    searchByModel(model) {
        return apiClient.get('/drones', { params: { model } })
    }
}

// 导出无人机 API 接口
export default droneApi