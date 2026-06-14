import api from './api';

const userService = {
  // Lấy hồ sơ cá nhân
  getMyProfile: async () => {
    const response = await api.get('/users/profile');
    return response.data;
  },

  // Cập nhật hồ sơ cá nhân
  updateMyProfile: async (data) => {
    const response = await api.put('/users/profile', data);
    return response.data;
  },

  // ===== ADMIN API =====
  // Lấy danh sách tất cả người dùng
  getAllUsers: async () => {
    const response = await api.get('/admin/users');
    return response.data;
  },

  // Khóa / Mở khóa tài khoản
  toggleUserStatus: async (id) => {
    const response = await api.put(`/admin/users/${id}/toggle-status`);
    return response.data;
  }
};

export default userService;
