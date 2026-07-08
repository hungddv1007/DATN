import api from './api';

const packageService = {
  // Lấy tất cả gói tập
  getAllPackages: async (activeOnly = true) => {
    // Nếu gọi từ public activeOnly = true, gọi từ Admin activeOnly = false
    const response = await api.get(`/packages?activeOnly=${activeOnly}`);
    return response.data;
  },

  // Lấy chi tiết gói tập
  getPackageById: async (id) => {
    const response = await api.get(`/packages/${id}`);
    return response.data;
  },

  // (Admin) Tạo gói tập
  createPackage: async (data) => {
    const response = await api.post('/packages', data);
    return response.data;
  },

  // (Admin) Cập nhật gói tập
  updatePackage: async (id, data) => {
    const response = await api.put(`/packages/${id}`, data);
    return response.data;
  },

  // (Admin) Xóa gói tập
  deletePackage: async (id) => {
    const response = await api.delete(`/packages/${id}`);
    return response.data;
  },

  // (Admin) Thay đổi trạng thái gói tập
  togglePackageStatus: async (id) => {
    const response = await api.put(`/packages/${id}/toggle-status`);
    return response.data;
  },

  // ================================================================
  // DISCOUNTS (CHIẾT KHẤU DÀI HẠN)
  // ================================================================

  getPublicDiscounts: async () => {
    const response = await api.get('/public/discounts');
    return response.data;
  },

  getAdminDiscounts: async () => {
    const response = await api.get('/admin/discounts');
    return response.data;
  },

  createDiscount: async (data) => {
    const response = await api.post('/admin/discounts', data);
    return response.data;
  },

  updateDiscount: async (id, data) => {
    const response = await api.put(`/admin/discounts/${id}`, data);
    return response.data;
  },

  deleteDiscount: async (id) => {
    const response = await api.delete(`/admin/discounts/${id}`);
    return response.data;
  }
};

export default packageService;
