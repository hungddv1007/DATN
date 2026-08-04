import React, { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import MainLayout from '../../components/layout/MainLayout';
import authService from '../../services/authService';
import { Mail, KeyRound, ShieldCheck, ArrowLeft, CheckCircle } from 'lucide-react';
import './AuthPages.css';

const STEPS = {
  EMAIL: 'EMAIL',
  OTP: 'OTP',
  NEW_PASSWORD: 'NEW_PASSWORD',
  SUCCESS: 'SUCCESS',
};

const ForgotPasswordPage = () => {
  const [step, setStep] = useState(STEPS.EMAIL);
  const [email, setEmail] = useState('');
  const [otp, setOtp] = useState(['', '', '', '', '', '']);
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [countdown, setCountdown] = useState(0);

  const otpRefs = useRef([]);

  // Countdown timer for resend
  useEffect(() => {
    if (countdown <= 0) return;
    const timer = setInterval(() => setCountdown(c => c - 1), 1000);
    return () => clearInterval(timer);
  }, [countdown]);

  // Step 1: Send OTP
  const handleSendOtp = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await authService.forgotPassword(email);
      setSuccess(res.message);
      setStep(STEPS.OTP);
      setCountdown(60);
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Có lỗi xảy ra!');
    } finally {
      setLoading(false);
    }
  };

  // Resend OTP
  const handleResendOtp = async () => {
    if (countdown > 0) return;
    setError('');
    setLoading(true);
    try {
      await authService.forgotPassword(email);
      setSuccess('Đã gửi lại mã OTP mới!');
      setCountdown(60);
      setOtp(['', '', '', '', '', '']);
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể gửi lại OTP');
    } finally {
      setLoading(false);
    }
  };

  // OTP input handlers
  const handleOtpChange = (index, value) => {
    if (value.length > 1) value = value.slice(-1);
    if (!/^\d*$/.test(value)) return;

    const newOtp = [...otp];
    newOtp[index] = value;
    setOtp(newOtp);

    // Auto-focus next
    if (value && index < 5) {
      otpRefs.current[index + 1]?.focus();
    }
  };

  const handleOtpKeyDown = (index, e) => {
    if (e.key === 'Backspace' && !otp[index] && index > 0) {
      otpRefs.current[index - 1]?.focus();
    }
  };

  const handleOtpPaste = (e) => {
    e.preventDefault();
    const pastedData = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, 6);
    if (pastedData.length === 6) {
      setOtp(pastedData.split(''));
      otpRefs.current[5]?.focus();
    }
  };

  // Step 2: Verify OTP → Go to password step
  const handleVerifyOtp = async (e) => {
    e.preventDefault();
    const otpString = otp.join('');
    if (otpString.length !== 6) {
      setError('Vui lòng nhập đủ 6 chữ số OTP');
      return;
    }

    setError('');
    setSuccess('');
    setLoading(true);
    try {
      const res = await authService.verifyForgotPasswordOtp(email, otpString);
      setSuccess(res.message);
      setStep(STEPS.NEW_PASSWORD);
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Mã OTP không đúng hoặc đã hết hạn');
    } finally {
      setLoading(false);
    }
  };

  // Step 3: Reset password
  const handleResetPassword = async (e) => {
    e.preventDefault();
    setError('');

    if (newPassword.length < 6) {
      setError('Mật khẩu mới phải có ít nhất 6 ký tự!');
      return;
    }
    if (newPassword !== confirmPassword) {
      setError('Mật khẩu xác nhận không khớp!');
      return;
    }

    setLoading(true);
    try {
      const res = await authService.resetPassword(email, otp.join(''), newPassword, confirmPassword);
      setSuccess(res.message);
      setStep(STEPS.SUCCESS);
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Có lỗi xảy ra!');
    } finally {
      setLoading(false);
    }
  };

  const getStepIndicator = () => {
    const steps = [
      { key: STEPS.EMAIL, label: 'Email', icon: <Mail size={16} /> },
      { key: STEPS.OTP, label: 'OTP', icon: <ShieldCheck size={16} /> },
      { key: STEPS.NEW_PASSWORD, label: 'Mật khẩu mới', icon: <KeyRound size={16} /> },
    ];
    const currentIdx = steps.findIndex(s => s.key === step);

    return (
      <div className="fp-steps">
        {steps.map((s, i) => (
          <React.Fragment key={s.key}>
            <div className={`fp-step ${i <= currentIdx ? 'active' : ''} ${i < currentIdx ? 'done' : ''}`}>
              <div className="fp-step-icon">{i < currentIdx ? <CheckCircle size={16} /> : s.icon}</div>
              <span className="fp-step-label">{s.label}</span>
            </div>
            {i < steps.length - 1 && <div className={`fp-step-line ${i < currentIdx ? 'active' : ''}`} />}
          </React.Fragment>
        ))}
      </div>
    );
  };

  return (
    <MainLayout>
      <div className="auth-page">
        <div className="auth-card" style={{ maxWidth: step === STEPS.SUCCESS ? 520 : 480 }}>
          {step !== STEPS.SUCCESS && (
            <>
              <div className="auth-header">
                <h1>Quên mật khẩu</h1>
                <p>Đặt lại mật khẩu qua email đã đăng ký</p>
              </div>
              {getStepIndicator()}
            </>
          )}

          {error && <div className="auth-error">{error}</div>}
          {success && step !== STEPS.SUCCESS && <div className="auth-success">{success}</div>}

          {/* Step 1: Email */}
          {step === STEPS.EMAIL && (
            <form onSubmit={handleSendOtp} className="auth-form">
              <div className="form-group">
                <label htmlFor="fp-email">Email đã đăng ký</label>
                <input
                  id="fp-email"
                  type="email"
                  placeholder="example@gmail.com"
                  value={email}
                  onChange={(e) => { setEmail(e.target.value); setError(''); }}
                  required
                  autoFocus
                />
              </div>
              <button type="submit" className="btn-auth-submit" disabled={loading}>
                {loading ? 'Đang gửi...' : 'GỬI MÃ OTP'}
              </button>
              <div className="auth-footer" style={{ borderTop: 'none', marginTop: '12px', paddingTop: '8px' }}>
                <p><Link to="/login"><ArrowLeft size={14} style={{ verticalAlign: 'middle' }} /> Quay lại đăng nhập</Link></p>
              </div>
            </form>
          )}

          {/* Step 2: OTP */}
          {step === STEPS.OTP && (
            <form onSubmit={handleVerifyOtp} className="auth-form">
              <p style={{ color: '#94a3b8', fontSize: '0.9rem', textAlign: 'center', marginBottom: '16px' }}>
                Mã OTP 6 chữ số đã được gửi đến <strong style={{ color: '#f97316' }}>{email}</strong>
              </p>
              <div className="fp-otp-inputs" onPaste={handleOtpPaste}>
                {otp.map((digit, i) => (
                  <input
                    key={i}
                    ref={(el) => otpRefs.current[i] = el}
                    type="text"
                    inputMode="numeric"
                    maxLength={1}
                    className="fp-otp-input"
                    value={digit}
                    onChange={(e) => handleOtpChange(i, e.target.value)}
                    onKeyDown={(e) => handleOtpKeyDown(i, e)}
                    autoFocus={i === 0}
                  />
                ))}
              </div>
              <button type="submit" className="btn-auth-submit" disabled={loading}>
                {loading ? 'Đang xác minh...' : 'XÁC NHẬN'}
              </button>
              <div style={{ textAlign: 'center', marginTop: '12px' }}>
                {countdown > 0 ? (
                  <span style={{ color: '#64748b', fontSize: '0.85rem' }}>
                    Gửi lại mã sau <strong style={{ color: '#f97316' }}>{countdown}s</strong>
                  </span>
                ) : (
                  <button
                    type="button"
                    onClick={handleResendOtp}
                    style={{ background: 'none', border: 'none', color: '#f97316', cursor: 'pointer', fontWeight: 600, fontSize: '0.9rem', fontFamily: 'inherit' }}
                    disabled={loading}
                  >
                    Gửi lại mã OTP
                  </button>
                )}
              </div>
            </form>
          )}

          {/* Step 3: New Password */}
          {step === STEPS.NEW_PASSWORD && (
            <form onSubmit={handleResetPassword} className="auth-form">
              <div className="form-group">
                <label htmlFor="fp-newpwd">Mật khẩu mới</label>
                <input
                  id="fp-newpwd"
                  type="password"
                  placeholder="Tối thiểu 6 ký tự"
                  value={newPassword}
                  onChange={(e) => { setNewPassword(e.target.value); setError(''); }}
                  required
                  autoFocus
                />
              </div>
              <div className="form-group">
                <label htmlFor="fp-confirmpwd">Xác nhận mật khẩu mới</label>
                <input
                  id="fp-confirmpwd"
                  type="password"
                  placeholder="Nhập lại mật khẩu"
                  value={confirmPassword}
                  onChange={(e) => { setConfirmPassword(e.target.value); setError(''); }}
                  required
                />
              </div>
              <button type="submit" className="btn-auth-submit" disabled={loading}>
                {loading ? 'Đang xử lý...' : 'ĐẶT LẠI MẬT KHẨU'}
              </button>
            </form>
          )}

          {/* Step 4: Success */}
          {step === STEPS.SUCCESS && (
            <div style={{ textAlign: 'center', padding: '20px 0' }}>
              <CheckCircle size={72} color="#22c55e" style={{ marginBottom: '20px' }} />
              <h2 style={{ color: '#22c55e', fontSize: '1.6rem', marginBottom: '12px' }}>Đặt lại mật khẩu thành công!</h2>
              <p style={{ color: '#cbd5e1', fontSize: '1rem', marginBottom: '30px', lineHeight: 1.6 }}>
                Bạn có thể đăng nhập với mật khẩu mới ngay bây giờ.
              </p>
              <Link to="/login" className="btn-auth-submit" style={{ display: 'inline-block', textDecoration: 'none', textAlign: 'center', padding: '14px 40px' }}>
                ĐĂNG NHẬP NGAY
              </Link>
            </div>
          )}
        </div>
      </div>
    </MainLayout>
  );
};

export default ForgotPasswordPage;
