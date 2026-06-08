import React from 'react';
import { Link } from 'react-router-dom';
import { Globe, MessageCircle, Video, Camera, MapPin, Phone, Mail } from 'lucide-react';
import './Footer.css';

const Footer = () => {
  return (
    <footer className="main-footer">
      <div className="footer-container">
        <div className="footer-brand">
          <span className="logo-text">GymPro</span>
          <p>Hệ thống Quản lý & Tập luyện Chuyên nghiệp Nhất.</p>
          <div className="social-icons">
            <a href="#"><MessageCircle size={20} /></a>
            <a href="#"><Globe size={20} /></a>
            <a href="#"><Video size={20} /></a>
            <a href="#"><Camera size={20} /></a>
          </div>
        </div>
        
        <div className="footer-links">
          <h4>VỀ CHÚNG TÔI</h4>
          <Link to="/about">Giới thiệu</Link>
          <Link to="/pts">Huấn luyện viên</Link>
          <Link to="/gallery">Thư viện</Link>
          <Link to="/privacy">Chính sách bảo mật</Link>
        </div>
        
        <div className="footer-links">
          <h4>DỊCH VỤ</h4>
          <Link to="/packages">Gói Basic</Link>
          <Link to="/packages">Gói Premium</Link>
          <Link to="/packages">Gói VIP</Link>
          <Link to="/schedule">Lịch tập</Link>
        </div>
        
        <div className="footer-contact">
          <h4>LIÊN HỆ</h4>
          <p><MapPin size={16} /> <strong>Văn phòng:</strong> 123 Đường Gym, Quận 1, TP. HCM</p>
          <p><Phone size={16} /> <strong>Điện thoại:</strong> 1900 1234</p>
          <p><Mail size={16} /> <strong>Email:</strong> contact@gympro.vn</p>
        </div>
      </div>
      <div className="footer-bottom">
        <p>Copyright &copy; {new Date().getFullYear()} GymPro. All rights reserved.</p>
      </div>
    </footer>
  );
};

export default Footer;
