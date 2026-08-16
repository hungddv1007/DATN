import api from './api';

const membershipService = {
  // Hội viên đăng ký gói tập mới
  registerPackage: async (data) => {
    const response = await api.post('/member/memberships', data);
    return response.data;
  },

  previewPurchase: async (packageId, days, promotionCode, referralCode) => {
    const response = await api.get('/member/memberships/preview/purchase', {
      params: { packageId, days, promotionCode: promotionCode || undefined, referralCode: referralCode || undefined }
    });
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
  },

  // Gia hạn gói tập
  renewMembership: async (data) => {
    const response = await api.post('/member/memberships/renew', data);
    return response.data;
  },

  // Nâng cấp gói tập
  upgradeMembership: async (data) => {
    const response = await api.post('/member/memberships/upgrade', data);
    return response.data;
  },

  // Bảo lưu gói tập
  pauseMembership: async (data) => {
    const response = await api.post('/member/memberships/pause', data);
    return response.data;
  },

  // Hủy bảo lưu gói tập
  resumeMembership: async () => {
    const response = await api.post('/member/memberships/resume');
    return response.data;
  },

  // Hủy gói tập
  // Xem trước giá gia hạn
  previewRenew: async (days) => {
    const response = await api.get(`/member/memberships/preview/renew`, { params: { days } });
    return response.data;
  },

  // Xem trước giá nâng cấp
  previewUpgrade: async (packageId, extraDays) => {
    const response = await api.get(`/member/memberships/preview/upgrade`, {
      params: { packageId, extraDays: extraDays || 0 }
    });
    return response.data;
  }
};

export default membershipService;
