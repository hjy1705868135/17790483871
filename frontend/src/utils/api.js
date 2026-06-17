import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

export const uavApi = {
  list: (params) => api.get('/uav/list', { params }),
  get: (id) => api.get(`/uav/${id}`),
  create: (data) => api.post('/uav', data),
  update: (id, data) => api.put(`/uav/${id}`, data),
  delete: (id) => api.delete(`/uav/${id}`)
}

export default api
