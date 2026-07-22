import api from './api';

const notificationService = {
  // Lấy danh sách thông báo của tôi (phân trang)
  getMyNotifications: async (page = 0, size = 10) => {
    const response = await api.get('/notifications', {
      params: { page, size }
    });
    return response.data;
  },

  // Lấy danh sách thông báo chưa đọc
  getUnreadNotifications: async () => {
    const response = await api.get('/notifications/unread');
    return response.data;
  },

  // Lấy số lượng thông báo chưa đọc
  getUnreadCount: async () => {
    const response = await api.get('/notifications/unread/count');
    return response.data;
  },

  // Đánh dấu 1 thông báo là đã đọc
  markAsRead: async (id) => {
    const response = await api.put(`/notifications/${id}/read`);
    return response.data;
  },

  // Đánh dấu tất cả là đã đọc
  markAllAsRead: async () => {
    const response = await api.put('/notifications/read-all');
    return response.data;
  },

  // Xóa thông báo
  deleteNotification: async (id) => {
    const response = await api.delete(`/notifications/${id}`);
    return response.data;
  }
};

export default notificationService;
