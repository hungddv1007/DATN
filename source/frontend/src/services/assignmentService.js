import api from './api';

const assignmentService = {
  // Lấy tất cả phân công của PT
  getAll: async () => {
    const response = await api.get('/pt/assignments');
    return response.data;
  },

  // Gán lộ trình cho member
  assign: async (data) => {
    const response = await api.post('/pt/assignments', data);
    return response.data;
  },

  // Thay đổi trạng thái
  changeStatus: async (assignmentId, status) => {
    const response = await api.put(`/pt/assignments/${assignmentId}/status`, { status });
    return response.data;
  }
};

export default assignmentService;
