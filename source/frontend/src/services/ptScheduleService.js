import api from './api';

const ptScheduleService = {
  // PT lấy toàn bộ lịch huấn luyện của mình
  getPtSchedules: async () => {
    const response = await api.get('/pt/schedules');
    return response.data;
  },

  // PT lấy lịch tập của 1 member cụ thể
  getPtMemberSchedule: async (memberId) => {
    const response = await api.get(`/pt/schedules/member/${memberId}`);
    return response.data;
  },

  // PT lưu/cập nhật thời khóa biểu cho học viên
  saveMemberSchedule: async (data) => {
    const response = await api.post('/pt/schedules', data);
    return response.data;
  },

  // Học viên xem lịch biểu kèm PT của mình
  getMemberSchedule: async () => {
    const response = await api.get('/member/schedule');
    return response.data;
  }
};

export default ptScheduleService;
