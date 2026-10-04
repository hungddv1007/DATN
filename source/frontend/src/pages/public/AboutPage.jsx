import React from 'react';
import { Target, Users, Award, Heart } from 'lucide-react';
import MainLayout from '../../components/layout/MainLayout';
import './AboutPage.css';

const AboutPage = () => {
  return (
    <MainLayout>
      <div className="about-page">
        <div className="about-hero">
          <h1>Về GymPro</h1>
          <p>Hệ thống quản lý phòng gym chuyên nghiệp hàng đầu Việt Nam.</p>
        </div>

        <div className="about-content">
          <div className="about-grid">
            <div className="about-card">
              <Target size={40} className="about-icon" />
              <h3>Sứ Mệnh</h3>
              <p>Mang đến trải nghiệm tập luyện chuyên nghiệp, khoa học và hiệu quả nhất cho mọi người.</p>
            </div>
            <div className="about-card">
              <Users size={40} className="about-icon" />
              <h3>Đội Ngũ PT</h3>
              <p>Hơn 20 huấn luyện viên được đào tạo bài bản, có chứng chỉ quốc tế, tận tâm với từng học viên.</p>
            </div>
            <div className="about-card">
              <Award size={40} className="about-icon" />
              <h3>Chất Lượng</h3>
              <p>Trang thiết bị hiện đại, nhập khẩu từ Mỹ và Châu Âu, bảo trì định kỳ đảm bảo an toàn.</p>
            </div>
            <div className="about-card">
              <Heart size={40} className="about-icon" />
              <h3>Cam Kết</h3>
              <p>Đồng hành cùng bạn trên hành trình chinh phục sức khỏe và vóc dáng lý tưởng.</p>
            </div>
          </div>
        </div>
      </div>
    </MainLayout>
  );
};

export default AboutPage;
