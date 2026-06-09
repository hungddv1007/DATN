import React from 'react';
import { useAuth } from '../../context/AuthContext';
import MainLayout from '../../components/layout/MainLayout';
import { Users, Dumbbell, ClipboardList, MessageSquare } from 'lucide-react';
import '../member/DashboardPage.css';

const PtDashboard = () => {
  const { user } = useAuth();

  return (
    <MainLayout>
      <div className="dashboard-page">
        <div className="dashboard-header">
          <h1>Dashboard Huấn Luyện Viên</h1>
          <p>Xin chào, <strong>{user?.fullName || 'PT'}</strong>!</p>
        </div>

        <div className="dashboard-cards">
          <div className="dash-card">
            <Users size={32} className="dash-icon" />
            <h3>Học viên của bạn</h3>
            <p className="dash-value">0 học viên</p>
            <span className="dash-link">Xem danh sách →</span>
          </div>
          <div className="dash-card">
            <ClipboardList size={32} className="dash-icon" />
            <h3>Lộ trình tập</h3>
            <p className="dash-value">0 lộ trình</p>
            <span className="dash-link">Quản lý →</span>
          </div>
          <div className="dash-card">
            <Dumbbell size={32} className="dash-icon" />
            <h3>Buổi tập tuần này</h3>
            <p className="dash-value">0 buổi</p>
            <span className="dash-link">Xem lịch →</span>
          </div>
          <div className="dash-card">
            <MessageSquare size={32} className="dash-icon" />
            <h3>Ghi chú</h3>
            <p className="dash-value">0 ghi chú</p>
            <span className="dash-link">Xem tất cả →</span>
          </div>
        </div>
      </div>
    </MainLayout>
  );
};

export default PtDashboard;
