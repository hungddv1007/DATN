import React from 'react';
import './Header.css';

const Header = () => {
  return (
    <header className="main-header">
      <div className="header-container">
        <div className="logo">
          <span className="logo-text">GymPro</span>
        </div>
        <nav className="main-nav">
          <a href="#" className="nav-link">Trang chủ</a>
          <a href="#" className="nav-link">Gói tập</a>
          <a href="#" className="nav-link">Blog</a>
        </nav>
        <div className="header-actions">
          <button className="btn-login">Đăng nhập</button>
          <button className="btn-register">Tham gia ngay</button>
        </div>
      </div>
    </header>
  );
};

export default Header;
