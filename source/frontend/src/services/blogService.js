import api from './api';

const blogService = {
  // === PUBLIC (không cần đăng nhập) ===
  
  // Lấy danh sách blog đã publish (public)
  getPublicBlogs: async () => {
    const response = await api.get('/blogs');
    return response.data;
  },

  // Lấy 1 blog theo ID (public)
  getPublicBlogById: async (id) => {
    const response = await api.get(`/blogs/${id}`);
    return response.data;
  },

  // === ADMIN ===

  // Lấy danh sách (Admin)
  getAllBlogs: async () => {
    const response = await api.get('/admin/blogs');
    return response.data;
  },

  // Tạo mới
  createBlog: async (data) => {
    const response = await api.post('/admin/blogs', data);
    return response.data;
  },

  // Sửa
  updateBlog: async (id, data) => {
    const response = await api.put(`/admin/blogs/${id}`, data);
    return response.data;
  },

  // Ẩn/Hiện
  toggleBlogStatus: async (id) => {
    const response = await api.put(`/admin/blogs/${id}/toggle-status`);
    return response.data;
  },

  // Xóa vĩnh viễn
  deleteBlog: async (id) => {
    const response = await api.delete(`/admin/blogs/${id}`);
    return response.data;
  }
};

export default blogService;
