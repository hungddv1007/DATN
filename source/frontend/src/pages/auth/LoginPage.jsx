import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import MainLayout from '../../components/layout/MainLayout';
import './AuthPages.css';

const LoginPage = () => {
  const navigate = useNavigate();
  const { login } = useAuth();

  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const data = await login(formData.email, formData.password);
      
      // Chuyển hướng theo vai trò
      if (data.role === 'ADMIN') {
        navigate('/admin');
      } else if (data.role === 'PT') {
        navigate('/pt/dashboard');
      } else {
        navigate('/member/dashboard');
      }
    } catch (err) {
      const resData = err.response?.data;
      if (resData) {
        if (resData.message) {
          setError(resData.message);
        } else if (typeof resData === 'object') {
          const firstError = Object.values(resData)[0];
          setError(firstError || 'Email hoặc mật khẩu không đúng!');
        } else {
          setError('Email hoặc mật khẩu không đúng!');
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
            <h1>Đăng Nhập</h1>
            <p>Chào mừng trở lại! Vui lòng đăng nhập để tiếp tục.</p>
          </div>

          {error && <div className="auth-error">{error}</div>}

          <form onSubmit={handleSubmit} className="auth-form">
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
              <label htmlFor="password">Mật khẩu</label>
              <input
                id="password"
                type="password"
                name="password"
                placeholder="Nhập mật khẩu"
                value={formData.password}
                onChange={handleChange}
                required
              />
            </div>

            <button type="submit" className="btn-auth-submit" disabled={loading}>
              {loading ? 'Đang đăng nhập...' : 'ĐĂNG NHẬP'}
            </button>
          </form>

          <div className="auth-footer">
            <p>Chưa có tài khoản? <Link to="/register">Đăng ký ngay</Link></p>
          </div>
        </div>
      </div>
    </MainLayout>
  );
};

export default LoginPage;
