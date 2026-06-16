import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import MainLayout from '../../components/layout/MainLayout';
import membershipService from '../../services/membershipService';
import { Package, CreditCard, Bell, User, Dumbbell, Star } from 'lucide-react';
import './DashboardPage.css';

const MemberDashboard = () => {
  const { user } = useAuth();
  const [membership, setMembership] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const currentData = await membershipService.getCurrentMembership();
        setMembership(currentData);
      } catch (error) {
        console.log('User has no active membership or error occurred');
      } finally {
        setLoading(false);
      }
    };
    fetchDashboardData();
  }, []);

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
            {loading ? (
              <p className="dash-value">Đang tải...</p>
            ) : membership ? (
              <p className="dash-value" style={{ color: '#22c55e' }}>{membership.packageName}</p>
            ) : (
              <p className="dash-value">Chưa đăng ký</p>
            )}
            <Link to="/packages" className="dash-link">Xem gói tập →</Link>
          </div>
          <div className="dash-card">
            <CreditCard size={32} className="dash-icon" />
            <h3>Giao dịch</h3>
            <p className="dash-value">
              {membership && membership.transactionStatus === 'PENDING' 
                ? 'Đang chờ duyệt' 
                : 'Đã thanh toán'}
            </p>
            <Link to="/member/transactions" className="dash-link">Xem lịch sử →</Link>
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
            {loading ? (
              <p className="dash-value">Đang tải...</p>
            ) : membership?.ptName ? (
              <p className="dash-value" style={{ color: '#f97316' }}>{membership.ptName}</p>
            ) : (
              <p className="dash-value">Chưa được gán</p>
            )}
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
            <Link to="/profile" className="dash-link">Chỉnh sửa →</Link>
          </div>
        </div>
      </div>
    </MainLayout>
  );
};

export default MemberDashboard;
