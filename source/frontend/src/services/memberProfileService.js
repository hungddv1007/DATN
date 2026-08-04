import api from './api';

const memberProfileService = {
  getMyPhysicalProfile: async () => {
    const response = await api.get('/member/profile/physical');
    return response.data;
  },

  updateMyPhysicalProfile: async (data) => {
    const response = await api.put('/member/profile/physical', data);
    return response.data;
  },

  getAssignedMemberPhysicalProfile: async (memberId) => {
    const response = await api.get(`/pt/member-profiles/${memberId}`);
    return response.data;
  },
};

export default memberProfileService;
