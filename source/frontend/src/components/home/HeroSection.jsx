import React from 'react';
import './HeroSection.css';

const HeroSection = () => {
  return (
    <section className="hero-section">
      <div className="hero-overlay"></div>
      <div className="hero-content">
        <h1 className="hero-headline">GYMPRO - ĐÁNH THỨC SỨC MẠNH TIỀM ẨN.</h1>
        <p className="hero-subheadline">Hệ thống Quản lý & Tập luyện Chuyên nghiệp Nhất.</p>
        <div className="hero-buttons">
          <button className="btn-register-now">ĐĂNG KÝ NGAY</button>
          <button className="btn-explore">KHÁM PHÁ CÁC GÓI TẬP</button>
        </div>
      </div>
    </section>
  );
};

export default HeroSection;
