import api from './api';

const paymentService = {
  getPublicPaymentInfo: async () => {
    const response = await api.get('/public/payment-info');
    return response.data;
  },
};

export default paymentService;
