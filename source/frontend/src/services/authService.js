import api from './api';

const authService = {
  // Đăng nhập
  login: async (email, password) => {
    const response = await api.post('/auth/login', { email, password });
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data));
    }
    return response.data;
  },

  // Đăng ký
  register: async (data) => {
    const response = await api.post('/auth/register', data);
    return response.data;
  },

  // Gửi OTP
  sendOtp: async (email, phone) => {
    const response = await api.post('/auth/send-otp', { email, phone });
    return response.data;
  },

  // Đăng nhập bằng Google
  loginWithGoogle: async (idToken) => {
    const response = await api.post('/auth/google', { idToken });
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data));
    }
    return response.data;
  },

  // Lấy Google Client ID từ backend
  getGoogleClientId: async () => {
    const response = await api.get('/auth/google/client-id');
    return response.data.clientId;
  },

  // Đăng xuất
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  // Lấy user hiện tại từ localStorage
  getCurrentUser: () => {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  },

  // Kiểm tra đã đăng nhập chưa
  isLoggedIn: () => {
    return !!localStorage.getItem('token');
  },
};

export default authService;
