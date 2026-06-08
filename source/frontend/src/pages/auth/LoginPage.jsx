import React from 'react';
import { Link } from 'react-router-dom';

const LoginPage = () => {
  return (
    <div style={{ padding: '100px 20px', textAlign: 'center', color: '#fff' }}>
      <h1>Đăng Nhập</h1>
      <Link to="/" style={{ color: 'var(--primary)', textDecoration: 'none' }}>Quay lại trang chủ</Link>
    </div>
  );
};

export default LoginPage;
