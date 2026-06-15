import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { Users, Package, CreditCard, BarChart3 } from 'lucide-react';
import AdminLayout from '../../components/layout/AdminLayout';
import statisticsService from '../../services/statisticsService';

const AdminDashboard = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState({
    totalUsers: 0,
    newRegistrationsThisMonth: 0,
    monthlyRevenue: 0,
    activePTs: 0
  });

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const data = await statisticsService.getOverview();
        setStats(data);
      } catch (error) {
        console.error('Lỗi tải thống kê:', error);
      }
    };
    fetchStats();
  }, []);

  return (
    <AdminLayout>
      <h1>Tổng Quan</h1>
      <p>Chào mừng, {user?.fullName || 'Admin'}!</p>

      <div className="admin-stats">
        <div className="stat-card">
          <Users size={28} className="stat-icon" />
          <div className="stat-label">Tổng người dùng</div>
          <div className="stat-value">{stats.totalUsers}</div>
        </div>
        <div className="stat-card">
          <Package size={28} className="stat-icon" />
          <div className="stat-label">Đăng ký tháng này</div>
          <div className="stat-value">{stats.newRegistrationsThisMonth}</div>
        </div>
        <div className="stat-card">
          <CreditCard size={28} className="stat-icon" />
          <div className="stat-label">Doanh thu tháng (VNĐ)</div>
          <div className="stat-value">{stats.monthlyRevenue.toLocaleString('vi-VN')} đ</div>
        </div>
        <div className="stat-card">
          <BarChart3 size={28} className="stat-icon" />
          <div className="stat-label">PT đang hoạt động</div>
          <div className="stat-value">{stats.activePTs}</div>
        </div>
      </div>
    </AdminLayout>
  );
};

export default AdminDashboard;
