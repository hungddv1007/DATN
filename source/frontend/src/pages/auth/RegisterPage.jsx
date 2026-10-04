import React, { useState, useCallback, useEffect, useRef } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import authService from '../../services/authService';
import MainLayout from '../../components/layout/MainLayout';
import './AuthPages.css';

const RegisterPage = () => {
  const navigate = useNavigate();
  const { loginGoogle } = useAuth();
  const googleButtonRef = useRef(null);

  const [formData, setFormData] = useState({
    email: '',
    password: '',
    confirmPassword: '',
    fullName: '',
    phone: '',
    otp: '',
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const [step, setStep] = useState(1);
  const [countdown, setCountdown] = useState(0);
  const [googleClientId, setGoogleClientId] = useState('');

  // Lấy Google Client ID từ backend
  useEffect(() => {
    const fetchClientId = async () => {
      try {
        const id = await authService.getGoogleClientId();
        setGoogleClientId(id);
      } catch (err) {
        console.error('Không thể lấy Google Client ID từ backend', err);
      }
    };
    fetchClientId();
  }, []);

  // Xử lý đếm ngược OTP
  React.useEffect(() => {
    let timer;
    if (countdown > 0) {
      timer = setTimeout(() => setCountdown(countdown - 1), 1000);
    }
    return () => clearTimeout(timer);
  }, [countdown]);

  // Bước 1: Gửi thông tin (chưa có OTP) để kiểm tra hợp lệ và gửi mã
  const handleRegisterPhase1 = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (formData.password !== formData.confirmPassword) {
      setError('Mật khẩu xác nhận không khớp!');
      return;
    }
    if (formData.password.length < 6) {
      setError('Mật khẩu phải có ít nhất 6 ký tự!');
      return;
    }

    // Unicode Letter hỗ trợ đầy đủ chữ tiếng Việt có dấu và thống nhất với backend.
    const nameRegex = /^[\p{L}\s'-]+$/u;
    if (!nameRegex.test(formData.fullName)) {
      setError('Họ và tên chỉ được chứa chữ cái và khoảng trắng!');
      return;
    }

    // Validate số điện thoại (phải đúng định dạng số điện thoại Việt Nam)
    const phoneRegex = /^(0|84)(2(0[3-9]|1[0-6|8|9]|2[0-2|5-9]|3[2-9]|4[0-9]|5[1|2|4-9]|6[9]|7[0-7|9]|8[0-9]|9[0-4|6|7|9])|3[2-9]|5[5|6|8|9]|7[0|6-9]|8[0-6|8|9]|9[0-4|6-9])([0-9]{7})$/;
    if (!phoneRegex.test(formData.phone)) {
      setError('Số điện thoại không hợp lệ hoặc không đúng định dạng Việt Nam!');
      return;
    }

    setLoading(true);

    try {
      // Gọi API send-otp, API này sẽ kiểm tra trùng lặp email/phone và gửi OTP
      await authService.sendOtp(formData.email, formData.phone);
      setSuccess('Mã OTP đã được gửi đến email của bạn! Mã có hiệu lực trong 5 phút.');
      setStep(2); // Chuyển sang bước nhập OTP
      setCountdown(300); // 5 phút = 300 giây
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể gửi mã OTP. Vui lòng thử lại!');
    } finally {
      setLoading(false);
    }
  };

  // Bước 2: Xác nhận OTP và tạo tài khoản chính thức
  const handleRegisterPhase2 = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!formData.otp) {
      setError('Vui lòng nhập mã OTP!');
      return;
    }

    setLoading(true);

    try {
      const data = await authService.register(formData);
      setSuccess(data.message || 'Tạo tài khoản thành công!');
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
        setError('Đăng ký thất bại. Vui lòng thử lại!');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setError('');
  };

  const formatTime = (seconds) => {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m}:${s < 10 ? '0' : ''}${s}`;
  };

  // Google Sign-In callback
  const handleGoogleResponse = useCallback(async (response) => {
    setError('');
    setLoading(true);
    try {
      const data = await loginGoogle(response.credential);
      setSuccess('Đăng ký bằng Google thành công!');
      if (data.role === 'ADMIN') {
        navigate('/admin');
      } else if (data.role === 'PT') {
        navigate('/pt/dashboard');
      } else {
        navigate('/member/dashboard');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Đăng ký Google thất bại!');
    } finally {
      setLoading(false);
    }
  }, [loginGoogle, navigate]);

  useEffect(() => {
    if (window.google && googleClientId && googleButtonRef.current) {
      window.google.accounts.id.initialize({
        client_id: googleClientId,
        callback: handleGoogleResponse,
      });

      window.google.accounts.id.renderButton(googleButtonRef.current, {
        theme: 'outline',
        size: 'large',
        width: '100%',
        text: 'signup_with',
      });
    }
  }, [handleGoogleResponse, googleClientId]);

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

          {step === 1 ? (
            <form onSubmit={handleRegisterPhase1} className="auth-form">
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
                {loading ? 'Đang kiểm tra...' : 'ĐĂNG KÝ'}
              </button>
            </form>
          ) : (
            <form onSubmit={handleRegisterPhase2} className="auth-form">
              <div style={{ textAlign: 'center', marginBottom: '20px', color: '#94a3b8' }}>
                Vui lòng kiểm tra email <strong>{formData.email}</strong> để lấy mã xác thực gồm 6 chữ số.
              </div>
              
              <div className="form-group" style={{ textAlign: 'center' }}>
                <label htmlFor="otp" style={{ fontSize: '1.1rem', marginBottom: '10px' }}>Mã OTP</label>
                <input
                  id="otp"
                  type="text"
                  name="otp"
                  placeholder="------"
                  value={formData.otp}
                  onChange={handleChange}
                  maxLength="6"
                  required
                  style={{ fontSize: '2rem', letterSpacing: '0.5rem', textAlign: 'center', padding: '15px' }}
                />
                <div style={{ marginTop: '10px', color: countdown > 0 ? '#10b981' : '#ef4444', fontWeight: '500' }}>
                  {countdown > 0 ? `Thời gian còn lại: ${formatTime(countdown)}` : 'Mã OTP đã hết hạn!'}
                </div>
              </div>

              <button type="submit" className="btn-auth-submit" disabled={loading || countdown === 0}>
                {loading ? 'Đang xác nhận...' : 'XÁC NHẬN'}
              </button>

              <div style={{ marginTop: '15px', textAlign: 'center' }}>
                <button 
                  type="button" 
                  onClick={() => setStep(1)} 
                  style={{ background: 'transparent', border: 'none', color: '#3b82f6', cursor: 'pointer', textDecoration: 'underline' }}
                >
                  Quay lại sửa thông tin
                </button>
              </div>
            </form>
          )}

          {step === 1 && (
            <>
              <div className="auth-divider">hoặc</div>
              <div ref={googleButtonRef} style={{ marginTop: '15px' }}></div>
            </>
          )}


          <div className="auth-footer">
            <p>Đã có tài khoản? <Link to="/login">Đăng nhập</Link></p>
          </div>
        </div>
      </div>
    </MainLayout>
  );
};

export default RegisterPage;
