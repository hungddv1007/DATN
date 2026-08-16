import api from './api';

export const getPolicy = async (type) => (await api.get(`/public/policies/${type}`)).data;
export const getPolicies = async () => (await api.get('/public/policies')).data;
