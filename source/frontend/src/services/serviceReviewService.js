import api from './api';

const serviceReviewService = {
  mine: async () => (await api.get('/member/service-reviews')).data,
  create: async data => (await api.post('/member/service-reviews', data)).data,
  adminAll: async () => (await api.get('/admin/service-reviews')).data,
  setFeatured: async (id, featured) => (
    await api.put(`/admin/service-reviews/${id}/featured`, { featured })
  ).data,
};

export default serviceReviewService;
