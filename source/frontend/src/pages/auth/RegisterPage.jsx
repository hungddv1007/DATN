import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import authService from '../../services/authService';
import MainLayout from '../../components/layout/MainLayout';
import './AuthPages.css';

const RegisterPage = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    email: '',
    password: '',
    confirmPassword: '',
    fullName: '',
    phone: '',
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    // Validate phía frontend trước khi gửi
    if (formData.password !== formData.confirmPassword) {
      setError('Mật khẩu xác nhận không khớp!');
      return;
    }
    if (formData.password.length < 6) {
      setError('Mật khẩu phải có ít nhất 6 ký tự!');
      return;
    }

    setLoading(true);

    try {
      const data = await authService.register(formData);
      setSuccess(data.message || 'Đăng ký thành công!');
      // Chuyển sang trang login sau 2 giây
      setTimeout(() => navigate('/login'), 2000);
    } catch (err) {
      const resData = err.response?.data;
      if (resData) {
        if (resData.message) {
          setError(resData.message);
        } else if (typeof resData === 'object') {
          // Lấy thông báo lỗi đầu tiên từ map các lỗi validation
          const firstError = Object.values(resData)[0];
          setError(firstError || 'Đăng ký thất bại. Vui lòng thử lại!');
        } else {
          setError('Đăng ký thất bại. Vui lòng thử lại!');
        }
      } else {
        setError('Lỗi kết nối đến máy chủ!');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <MainLayout>
      <div className="auth-page">
        <div className="auth-card">
          <div className="auth-header">
            <h1>Đăng Ký</h1>
            <p>Tạo tài khoản mới để bắt đầu hành trình tập luyện.</p>
          </div>

          {error && <div className="auth-error">{error}</div>}
          {success && <div className="auth-success">{success}</div>}

          <form onSubmit={handleSubmit} className="auth-form">
            <div className="form-group">
              <label htmlFor="fullName">Họ và tên</label>
              <input
                id="fullName"
                type="text"
                name="fullName"
                placeholder="Nguyễn Văn A"
                value={formData.fullName}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="email">Email</label>
              <input
                id="email"
                type="email"
                name="email"
                placeholder="example@gmail.com"
                value={formData.email}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="phone">Số điện thoại</label>
              <input
                id="phone"
                type="tel"
                name="phone"
                placeholder="0901234567"
                value={formData.phone}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="password">Mật khẩu</label>
              <input
                id="password"
                type="password"
                name="password"
                placeholder="Ít nhất 6 ký tự"
                value={formData.password}
                onChange={handleChange}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="confirmPassword">Xác nhận mật khẩu</label>
              <input
                id="confirmPassword"
                type="password"
                name="confirmPassword"
                placeholder="Nhập lại mật khẩu"
                value={formData.confirmPassword}
                onChange={handleChange}
                required
              />
            </div>

            <button type="submit" className="btn-auth-submit" disabled={loading}>
              {loading ? 'Đang đăng ký...' : 'ĐĂNG KÝ'}
            </button>
          </form>

          <div className="auth-footer">
            <p>Đã có tài khoản? <Link to="/login">Đăng nhập</Link></p>
          </div>
        </div>
      </div>
    </MainLayout>
  );
};

export default RegisterPage;
