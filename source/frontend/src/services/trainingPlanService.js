import api from './api';

const trainingPlanService = {
  // Lấy tất cả lộ trình của PT
  getAll: async () => {
    const response = await api.get('/pt/plans');
    return response.data;
  },

  // Lấy chi tiết lộ trình
  getDetail: async (planId) => {
    const response = await api.get(`/pt/plans/${planId}`);
    return response.data;
  },

  // Tạo lộ trình mới
  create: async (data) => {
    const response = await api.post('/pt/plans', data);
    return response.data;
  },

  // Sửa lộ trình
  update: async (planId, data) => {
    const response = await api.put(`/pt/plans/${planId}`, data);
    return response.data;
  },

  // Xoá lộ trình
  delete: async (planId) => {
    const response = await api.delete(`/pt/plans/${planId}`);
    return response.data;
  },

  // Nhân bản lộ trình
  clone: async (planId) => {
    const response = await api.post(`/pt/plans/${planId}/clone`);
    return response.data;
  },
};

export default trainingPlanService;
