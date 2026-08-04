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
  },

  // ADMIN/PT gửi thông báo tới người dùng hợp lệ theo phân quyền backend
  sendNotification: async (userId, title, message) => {
    const response = await api.post('/notifications/send', { userId, title, message });
    return response.data;
  },

  // ADMIN/PT gửi cùng một thông báo tới nhiều người trong một request
  sendNotifications: async (userIds, title, message) => {
    const response = await api.post('/notifications/send-bulk', { userIds, title, message });
    return response.data;
  }
};

export default notificationService;
