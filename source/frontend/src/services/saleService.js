import api from './api';

const saleService = {
  dashboard: async () => (await api.get('/sale/dashboard')).data,
  setOnline: async (online) => (await api.put('/sale/availability', null, { params: { online } })).data,
  codes: async () => (await api.get('/sale/codes')).data,
  createCode: async (data) => (await api.post('/sale/codes', data)).data,
  setCodeActive: async (id, value) => (await api.put(`/sale/codes/${id}/active`, null, { params: { value } })).data,
  commissions: async () => (await api.get('/sale/commissions')).data,
  chats: async () => (await api.get('/sale/chats')).data,
  claimNext: async () => (await api.post('/sale/chats/claim-next')).data,
  messages: async (id) => (await api.get(`/sale/chats/${id}/messages`)).data,
  sendMessage: async (id, message) => (await api.post(`/sale/chats/${id}/messages`, { message })).data,
  closeChat: async (id) => (await api.post(`/sale/chats/${id}/close`)).data,
};
export default saleService;
