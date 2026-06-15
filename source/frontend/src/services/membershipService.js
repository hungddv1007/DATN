import api from './api';

const membershipService = {
  // Hội viên đăng ký gói tập mới
  registerPackage: async (data) => {
    const response = await api.post('/member/memberships', data);
    return response.data;
  },

  // Lấy gói tập hiện tại của hội viên
  getCurrentMembership: async () => {
    const response = await api.get('/member/memberships/current');
    return response.data;
  },

  // Lấy lịch sử đăng ký của hội viên
  getMembershipHistory: async () => {
    const response = await api.get('/member/memberships/history');
    return response.data;
  }
};

export default membershipService;
