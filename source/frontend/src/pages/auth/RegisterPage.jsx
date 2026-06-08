import React from 'react';
import { Link } from 'react-router-dom';

const RegisterPage = () => {
  return (
    <div style={{ padding: '100px 20px', textAlign: 'center', color: '#fff' }}>
      <h1>Đăng Ký</h1>
      <p>Trang này sẽ liên kết với Backend bằng biến tiếng Anh (email, password, confirmPassword, fullName, phone)</p>
      <Link to="/" style={{ color: 'var(--primary)', textDecoration: 'none' }}>Quay lại trang chủ</Link>
    </div>
  );
};

export default RegisterPage;
