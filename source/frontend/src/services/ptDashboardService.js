import api from './api';

const ptDashboardService = {
  // Lấy thống kê của PT
  getDashboardStats: async () => {
    const response = await api.get('/pt/dashboard');
    return response.data;
  },

  // Lấy danh sách học viên đang gán cho PT này
  getAssignedMembers: async () => {
    const response = await api.get('/pt/members');
    return response.data;
  }
};

export default ptDashboardService;
