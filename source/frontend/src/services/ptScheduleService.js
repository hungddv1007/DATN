import api from './api';

const ptScheduleService = {
  // PT lấy lịch theo tuần
  getPtSchedules: async (weekStart) => {
    const response = await api.get('/pt/schedules', { params: { weekStart } });
    return response.data;
  },

  // PT tạo buổi tập mới (hỗ trợ recurring)
  createSchedule: async (data) => {
    const response = await api.post('/pt/schedules', data);
    return response.data;
  },

  // PT sửa 1 buổi tập
  updateSchedule: async (id, data) => {
    const response = await api.put(`/pt/schedules/${id}`, data);
    return response.data;
  },

  completeSchedule: async (id, data) => (await api.post(`/pt/schedules/${id}/complete`, data)).data,
  markNoShow: async (id) => (await api.post(`/pt/schedules/${id}/no-show`)).data,
  getTrainingStats: async (memberId, from, to) => (await api.get(`/pt/members/${memberId}/training-stats`, { params: { from, to } })).data,

  // PT xóa 1 hoặc cả nhóm buổi tập
  deleteSchedule: async (id, deleteAll = false, notify = false) => {
    const response = await api.delete(`/pt/schedules/${id}`, {
      params: { deleteAll, notify }
    });
    return response.data;
  },

  // Học viên xem lịch theo tuần
  getMemberSchedule: async (weekStart) => {
    const response = await api.get('/member/schedule', { params: { weekStart } });
    return response.data;
  }
};

export default ptScheduleService;
