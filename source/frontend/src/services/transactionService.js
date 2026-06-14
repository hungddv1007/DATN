import api from './api';

const transactionService = {
  // Lấy tất cả giao dịch (có phân trang)
  getAllTransactions: async (page = 0, size = 20) => {
    const response = await api.get(`/admin/transactions?page=${page}&size=${size}`);
    return response.data;
  },

  // Lấy các giao dịch đang chờ duyệt
  getPendingTransactions: async (page = 0, size = 20) => {
    const response = await api.get(`/admin/transactions/pending?page=${page}&size=${size}`);
    return response.data;
  },

  // Duyệt giao dịch
  confirmTransaction: async (id) => {
    const response = await api.put(`/admin/transactions/${id}/confirm`);
    return response.data;
  },

  // Hủy giao dịch
  cancelTransaction: async (id) => {
    const response = await api.put(`/admin/transactions/${id}/cancel`);
    return response.data;
  }
};

export default transactionService;
