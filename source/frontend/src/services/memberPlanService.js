import api from './api';

const memberPlanService = {
  // Lấy lộ trình đang active của member
  getActivePlan: async () => {
    const response = await api.get('/member/plans/active');
    return response.data; // trả về null hoặc 204 no content nếu không có
  }
};

export default memberPlanService;
