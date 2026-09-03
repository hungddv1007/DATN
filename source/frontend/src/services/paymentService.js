import api from './api';

const paymentService = {
  getPublicPaymentInfo: async () => {
    const response = await api.get('/public/payment-info');
    return response.data;
  },
  initiateMomoPayment: async (transactionId) => {
    const response = await api.post(`/member/payments/momo/${transactionId}`);
    return response.data;
  },
  getMomoPayment: async (transactionId) => {
    const response = await api.get(`/member/payments/momo/${transactionId}`);
    return response.data;
  },
  refreshMomoPayment: async (transactionId) => {
    const response = await api.post(`/member/payments/momo/${transactionId}/refresh`);
    return response.data;
  },
  cancelMomoPayment: async (transactionId) => {
    await api.delete(`/member/payments/momo/${transactionId}`);
  },
};

export default paymentService;
