import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Tự động gắn JWT token vào mỗi request
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    const isPublicAuthRequest = config.url?.startsWith('/auth/');

    // Không gửi JWT cũ vào các API đăng nhập/đăng ký công khai.
    // Nếu token đã hết hạn, JwtAuthenticationFilter có thể chặn yêu cầu
    // trước khi backend kịp xác thực thông tin đăng nhập mới.
    if (token && !isPublicAuthRequest) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Xử lý lỗi 401 (token hết hạn) → tự động logout
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Không tự động logout/reload nếu đang gọi API đăng nhập (vì 401 lúc này là do sai mật khẩu)
      const isLoginRequest = error.config.url.includes('/auth/login');
      
      if (!isLoginRequest) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        alert('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
        window.location.href = '/login?expired=true';
      }
    }
    return Promise.reject(error);
  }
);

export default api;
