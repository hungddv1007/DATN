import React from 'react';
import './Footer.css';

const Footer = () => {
  return (
    <footer className="main-footer">
      <div className="footer-container">
        <div className="footer-brand">
          <span className="logo-text">GymPro</span>
          <p>Nâng tầm sức mạnh, bứt phá giới hạn cùng các chuyên gia hàng đầu.</p>
        </div>
        <div className="footer-links">
          <div className="link-group">
            <h4>Dịch vụ</h4>
            <a href="#">Gói Basic</a>
            <a href="#">Gói Premium</a>
            <a href="#">Gói VIP</a>
          </div>
          <div className="link-group">
            <h4>Hỗ trợ</h4>
            <a href="#">Về chúng tôi</a>
            <a href="#">Liên hệ</a>
            <a href="#">Tuyển dụng</a>
          </div>
        </div>
      </div>
      <div className="footer-bottom">
        &copy; {new Date().getFullYear()} GymPro. Đồ án tốt nghiệp.
      </div>
    </footer>
  );
};

export default Footer;
