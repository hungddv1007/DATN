import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import MainLayout from '../../components/layout/MainLayout';
import { Package, CreditCard, Bell, User, Dumbbell, Star } from 'lucide-react';
import './DashboardPage.css';

const MemberDashboard = () => {
  const { user } = useAuth();

  return (
    <MainLayout>
      <div className="dashboard-page">
        <div className="dashboard-header">
          <h1>Dashboard Hội Viên</h1>
          <p>Xin chào, <strong>{user?.fullName || 'Hội viên'}</strong>! Chúc bạn có buổi tập hiệu quả.</p>
        </div>

        <div className="dashboard-cards">
          <div className="dash-card">
            <Package size={32} className="dash-icon" />
            <h3>Gói tập hiện tại</h3>
            <p className="dash-value">Chưa đăng ký</p>
            <Link to="/packages" className="dash-link">Xem gói tập →</Link>
          </div>
          <div className="dash-card">
            <CreditCard size={32} className="dash-icon" />
            <h3>Giao dịch</h3>
            <p className="dash-value">0 giao dịch</p>
            <span className="dash-link">Xem lịch sử →</span>
          </div>
          <div className="dash-card">
            <Dumbbell size={32} className="dash-icon" />
            <h3>Lộ trình tập</h3>
            <p className="dash-value">Chưa có lộ trình</p>
            <span className="dash-link">Xem chi tiết →</span>
          </div>
          <div className="dash-card">
            <Star size={32} className="dash-icon" />
            <h3>PT của bạn</h3>
            <p className="dash-value">Chưa được gán</p>
            <span className="dash-link">Xem hồ sơ PT →</span>
          </div>
          <div className="dash-card">
            <Bell size={32} className="dash-icon" />
            <h3>Thông báo</h3>
            <p className="dash-value">0 thông báo mới</p>
            <span className="dash-link">Xem tất cả →</span>
          </div>
          <div className="dash-card">
            <User size={32} className="dash-icon" />
            <h3>Hồ sơ cá nhân</h3>
            <p className="dash-value">{user?.email || ''}</p>
            <span className="dash-link">Chỉnh sửa →</span>
          </div>
        </div>
      </div>
    </MainLayout>
  );
};

export default MemberDashboard;
