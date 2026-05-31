import React from 'react';
import { Check, X } from 'lucide-react';
import './PackagesSection.css';

const PackagesSection = () => {
  return (
    <section className="packages-section">
      <div className="container">
        <h2 className="section-title">HỆ THỐNG GÓI TẬP</h2>
        
        <div className="packages-grid">
          {/* BASIC */}
          <div className="package-card basic-card">
            <h3 className="package-name">BASIC</h3>
            <div className="package-price">Giá: 500k/tháng</div>
            <ul className="package-features">
              <li><Check className="check-icon" /> Tập tự do</li>
              <li><Check className="check-icon" /> Xem blog/video công khai</li>
              <li className="disabled"><X className="x-icon" /> PT kèm</li>
              <li className="disabled"><X className="x-icon" /> Xem lộ trình</li>
            </ul>
            <button className="btn-package btn-basic">ĐĂNG KÝ GÓI</button>
          </div>

          {/* PREMIUM */}
          <div className="package-card premium-card">
            <h3 className="package-name">PREMIUM</h3>
            <div className="package-price">Giá: 2000k/tháng</div>
            <ul className="package-features">
              <li><Check className="check-icon" /> PT gán ngẫu nhiên</li>
              <li><Check className="check-icon" /> Xem lộ trình</li>
              <li><Check className="check-icon" /> Điểm danh buổi tập</li>
              <li><Check className="check-icon" /> Đánh giá PT</li>
              <li><Check className="check-icon" /> Free: Khăn, Nước</li>
            </ul>
            <button className="btn-package btn-premium">ĐĂNG KÝ GÓI</button>
          </div>

          {/* VIP */}
          <div className="package-card vip-card">
            <div className="special-badge">SPECIAL</div>
            <h3 className="package-name">VIP</h3>
            <div className="package-price">Giá: 5000k/tháng</div>
            <ul className="package-features">
              <li><Check className="check-icon-vip" /> Được CHỌN PT</li>
              <li><Check className="check-icon-vip" /> Khẩu phần ăn do PT lên</li>
              <li><Check className="check-icon-vip" /> Điểm danh buổi tập</li>
              <li><Check className="check-icon-vip" /> Đánh giá PT</li>
              <li><Check className="check-icon-vip" /> Free: Khăn, Nước</li>
            </ul>
            <button className="btn-package btn-vip">ĐĂNG KÝ GÓI</button>
          </div>
        </div>
      </div>
    </section>
  );
};

export default PackagesSection;
