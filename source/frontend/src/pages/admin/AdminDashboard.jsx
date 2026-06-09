import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Users, Package, CreditCard, BarChart3, FileText, Dumbbell, Tag, LayoutDashboard, LogOut } from 'lucide-react';
import '../member/DashboardPage.css';

const AdminDashboard = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <div className="admin-page">
      <aside className="admin-sidebar">
        <div className="admin-sidebar-logo">
          <h2><Link to="/" style={{ textDecoration: 'none', color: 'inherit' }}>GymPro</Link></h2>
          <span>Admin Panel</span>
        </div>
        <ul className="admin-nav">
          <li><Link to="/admin" className="active"><LayoutDashboard size={18} /> Tổng quan</Link></li>
          <li><Link to="/admin/users"><Users size={18} /> Quản lý Users</Link></li>
          <li><Link to="/admin/packages"><Package size={18} /> Gói tập</Link></li>
          <li><Link to="/admin/promotions"><Tag size={18} /> Khuyến mãi</Link></li>
          <li><Link to="/admin/transactions"><CreditCard size={18} /> Giao dịch</Link></li>
          <li><Link to="/admin/blogs"><FileText size={18} /> Bài viết</Link></li>
          <li><Link to="/admin/exercises"><Dumbbell size={18} /> Bài tập</Link></li>
          <li><Link to="/admin/statistics"><BarChart3 size={18} /> Thống kê</Link></li>
          <li><a href="#" onClick={handleLogout}><LogOut size={18} /> Đăng xuất</a></li>
        </ul>
      </aside>

      <main className="admin-main">
        <h1>Tổng Quan</h1>
        <p>Chào mừng, {user?.fullName || 'Admin'}!</p>

        <div className="admin-stats">
          <div className="stat-card">
            <Users size={28} className="stat-icon" />
            <div className="stat-label">Tổng người dùng</div>
            <div className="stat-value">--</div>
          </div>
          <div className="stat-card">
            <Package size={28} className="stat-icon" />
            <div className="stat-label">Đăng ký tháng này</div>
            <div className="stat-value">--</div>
          </div>
          <div className="stat-card">
            <CreditCard size={28} className="stat-icon" />
            <div className="stat-label">Doanh thu tháng</div>
            <div className="stat-value">--</div>
          </div>
          <div className="stat-card">
            <BarChart3 size={28} className="stat-icon" />
            <div className="stat-label">PT đang hoạt động</div>
            <div className="stat-value">--</div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default AdminDashboard;
