import api from './api';

const ptScheduleService = {
  // === PT APIs ===
  getPtSchedules: async () => {
    const response = await api.get('/pt/schedules');
    return response.data;
  },

  getPtSchedulesForMember: async (memberId) => {
    const response = await api.get(`/pt/schedules/member/${memberId}`);
    return response.data;
  },

  createSchedule: async (data) => {
    const response = await api.post('/pt/schedules', data);
    return response.data;
  },

  deleteSchedule: async (id) => {
    const response = await api.delete(`/pt/schedules/${id}`);
    return response.data;
  },

  // === MEMBER APIs ===
  getMemberSchedules: async () => {
    const response = await api.get('/member/schedules');
    return response.data;
  }
};

export default ptScheduleService;
