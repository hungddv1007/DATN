import React, { useState, useCallback, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import MainLayout from '../../components/layout/MainLayout';
import './AuthPages.css';

const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID;

const LoginPage = () => {
  const navigate = useNavigate();
  const { login, loginGoogle } = useAuth();

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

  // Google Sign-In callback
  const handleGoogleResponse = useCallback(async (response) => {
    setError('');
    setLoading(true);
    try {
      const data = await loginGoogle(response.credential);
      
      if (data.role === 'ADMIN') {
        navigate('/admin');
      } else if (data.role === 'PT') {
        navigate('/pt/dashboard');
      } else {
        navigate('/member/dashboard');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Đăng nhập Google thất bại!');
    } finally {
      setLoading(false);
    }
  }, [loginGoogle, navigate]);

  // Khởi tạo Google Sign-In
  useEffect(() => {
    if (window.google && GOOGLE_CLIENT_ID) {
      window.google.accounts.id.initialize({
        client_id: GOOGLE_CLIENT_ID,
        callback: handleGoogleResponse,
      });
    }
  }, [handleGoogleResponse]);

  const handleGoogleClick = () => {
    if (window.google && GOOGLE_CLIENT_ID) {
      window.google.accounts.id.prompt();
    } else {
      setError('Google Sign-In chưa sẵn sàng. Vui lòng thử lại sau.');
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

          <div className="auth-divider">hoặc</div>

          <button 
            type="button" 
            className="btn-google" 
            onClick={handleGoogleClick}
            disabled={loading}
          >
            <img src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg" alt="Google" />
            Đăng nhập bằng Google
          </button>

          <div className="auth-footer">
            <p>Chưa có tài khoản? <Link to="/register">Đăng ký ngay</Link></p>
          </div>
        </div>
      </div>
    </MainLayout>
  );
};

export default LoginPage;
