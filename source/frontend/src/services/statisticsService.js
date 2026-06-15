import api from './api';

const statisticsService = {
  getOverview: async () => {
    const response = await api.get('/admin/statistics/overview');
    return response.data;
  }
};

export default statisticsService;
