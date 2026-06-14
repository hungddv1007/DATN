import api from './api';

const exerciseService = {
  // Lấy danh sách (Admin)
  getAllExercises: async () => {
    const response = await api.get('/admin/exercises');
    return response.data;
  },

  // Tạo mới
  createExercise: async (data) => {
    const response = await api.post('/admin/exercises', data);
    return response.data;
  },

  // Sửa
  updateExercise: async (id, data) => {
    const response = await api.put(`/admin/exercises/${id}`, data);
    return response.data;
  },

  // Xóa vĩnh viễn
  deleteExercise: async (id) => {
    const response = await api.delete(`/admin/exercises/${id}`);
    return response.data;
  }
};

export default exerciseService;
