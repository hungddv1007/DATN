import api from './api';

const discountService = {
  getPublicDiscounts: async () => {
    const response = await api.get('/public/discounts');
    return response.data;
  },

  getAllDiscounts: async () => {
    const response = await api.get('/admin/discounts');
    return response.data;
  },

  createDiscount: async (data) => {
    const response = await api.post('/admin/discounts', data);
    return response.data;
  },

  updateDiscount: async (id, data) => {
    const response = await api.put(`/admin/discounts/${id}`, data);
    return response.data;
  },

  deleteDiscount: async (id) => {
    const response = await api.delete(`/admin/discounts/${id}`);
    return response.data;
  }
};

export default discountService;
