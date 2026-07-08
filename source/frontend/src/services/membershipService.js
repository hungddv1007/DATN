import api from './api';

const membershipService = {
  // Lấy gói tập hiện tại
  getMyCurrentMembership: async () => {
    const response = await api.get('/member/membership/current');
    return response.data;
  },

  // Lấy lịch sử
  getMyMembershipHistory: async () => {
    const response = await api.get('/member/membership/history');
    return response.data;
  },

  // 1. Đăng ký
  registerPackage: async (data) => {
    const response = await api.post('/member/membership/register', data);
    return response.data;
  },

  // 2. Gia hạn
  renewPackage: async (data) => {
    const response = await api.post('/member/membership/renew', data);
    return response.data;
  },

  // 3. Nâng cấp
  upgradePackage: async (data) => {
    const response = await api.post('/member/membership/upgrade', data);
    return response.data;
  },

  // 4. Nâng cấp & Gia hạn
  upgradeAndRenewPackage: async (data) => {
    const response = await api.post('/member/membership/upgrade-renew', data);
    return response.data;
  },

  // 5. Bảo lưu
  pauseMembership: async () => {
    const response = await api.post('/member/membership/pause');
    return response.data;
  },

  // 6. Huỷ bảo lưu
  resumeMembership: async () => {
    const response = await api.post('/member/membership/resume');
    return response.data;
  },

  // 7. Hủy gói
  cancelMembership: async () => {
    const response = await api.post('/member/membership/cancel');
    return response.data;
  },

  // Previews
  previewUpgrade: async (packageId) => {
    const response = await api.get(`/member/membership/preview-upgrade?packageId=${packageId}`);
    return response.data;
  },

  previewRenew: async (packageId, days) => {
    const response = await api.get(`/member/membership/preview-renew?packageId=${packageId}&days=${days}`);
    return response.data;
  }
};

export default membershipService;
