import api from './api';

const membershipService = {
  registerPackage: async (data) => {
    const response = await api.post('/member/memberships', data);
    return response.data;
  },

  getCurrentMembership: async () => {
    const response = await api.get('/member/memberships/current');
    return response.data;
  },

  getMembershipHistory: async () => {
    const response = await api.get('/member/memberships/history');
    return response.data;
  },

  // MỚI: Hủy đăng ký gói đang chờ xác nhận
  cancelMembership: async (id) => {
    const response = await api.put(`/member/memberships/${id}/cancel`);
    return response.data;
  }
};

export default membershipService;