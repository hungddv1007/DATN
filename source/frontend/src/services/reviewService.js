import api from './api';

const reviewService = {
  getReviewsByPt: async (ptId) => {
    const response = await api.get(`/pt-profiles/${ptId}/reviews`);
    return response.data;
  },

  getMyReviews: async () => {
    const response = await api.get('/member/reviews');
    return response.data;
  },

  createReview: async (ptId, ratingStar, comment) => {
    const response = await api.post('/member/reviews', { ptId, ratingStar, comment });
    return response.data;
  },

  updateReview: async (reviewId, ratingStar, comment) => {
    const response = await api.put(`/member/reviews/${reviewId}`, { ratingStar, comment });
    return response.data;
  },

  deleteReview: async (reviewId) => {
    const response = await api.delete(`/member/reviews/${reviewId}`);
    return response.data;
  },
};

export default reviewService;
