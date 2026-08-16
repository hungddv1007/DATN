import api from './api';

const membershipTransferService = {
  verifySender: async (data) => (await api.post('/member/membership-transfers/verify-sender', data)).data,
  create: async (data) => (await api.post('/member/membership-transfers', data)).data,
  incoming: async () => (await api.get('/member/membership-transfers/incoming')).data,
  outgoing: async () => (await api.get('/member/membership-transfers/outgoing')).data,
  sendAcceptOtp: async (id) => (await api.post(`/member/membership-transfers/${id}/send-accept-otp`)).data,
  accept: async (id, data) => (await api.post(`/member/membership-transfers/${id}/accept`, data)).data,
  reject: async (id) => (await api.post(`/member/membership-transfers/${id}/reject`)).data,
  cancel: async (id) => (await api.post(`/member/membership-transfers/${id}/cancel`)).data,
};
export default membershipTransferService;
