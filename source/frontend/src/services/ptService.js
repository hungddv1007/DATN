import api from './api';

const ptService = {
  // Lấy danh sách tất cả hồ sơ PT công khai
  getAllPtProfiles: async () => {
    const response = await api.get('/pt-profiles');
    return response.data;
  },

  // Lấy chi tiết hồ sơ PT theo userId
  getPtProfileByUserId: async (userId) => {
    const response = await api.get(`/pt-profiles/${userId}`);
    return response.data;
  }
};

export default ptService;
