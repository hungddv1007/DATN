import api from './api';

const promotionService = {
  // Lấy danh sách (Admin)
  getAllPromotions: async () => {
    const response = await api.get('/admin/promotions');
    return response.data;
  },

  // Tạo mới
  createPromotion: async (data) => {
    const response = await api.post('/admin/promotions', data);
    return response.data;
  },

  // Sửa
  updatePromotion: async (id, data) => {
    const response = await api.put(`/admin/promotions/${id}`, data);
    return response.data;
  },

  // Ẩn/Hiện
  togglePromotionStatus: async (id) => {
    const response = await api.put(`/admin/promotions/${id}/toggle-status`);
    return response.data;
  },

  // Xóa vĩnh viễn
  deletePromotion: async (id) => {
    const response = await api.delete(`/admin/promotions/${id}`);
    return response.data;
  }
};

export default promotionService;
