import React from 'react';
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
          <a href="#">Giới thiệu</a>
          <a href="#">Huấn luyện viên</a>
          <a href="#">Thư viện</a>
          <a href="#">Chính sách bảo mật</a>
        </div>
        
        <div className="footer-links">
          <h4>DỊCH VỤ</h4>
          <a href="#">Gói Basic</a>
          <a href="#">Gói Premium</a>
          <a href="#">Gói VIP</a>
          <a href="#">Lịch tập</a>
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
