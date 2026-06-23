import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { LayoutDashboard, Users, UserCircle, BookOpen, LogOut, Dumbbell, MessageSquare } from 'lucide-react';
import '../../pages/member/DashboardPage.css';

const PtLayout = ({ children }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const isActive = (path) => {
    if (path === '/pt/dashboard' && location.pathname === '/pt/dashboard') return 'active';
    if (path !== '/pt/dashboard' && location.pathname.startsWith(path)) return 'active';
    return '';
  };

  return (
    <div className="admin-page">
      <aside className="admin-sidebar">
        <div className="admin-sidebar-logo">
          <h2><Link to="/" style={{ textDecoration: 'none', color: 'inherit' }}><Dumbbell size={22} style={{ marginRight: '8px', verticalAlign: 'middle' }} />GymPro</Link></h2>
          <span>PT Workspace</span>
        </div>
        <ul className="admin-nav">
          <li><Link to="/pt/dashboard" className={isActive('/pt/dashboard')}><LayoutDashboard size={18} /> Tổng quan</Link></li>
          <li><Link to="/pt/profile" className={isActive('/pt/profile')}><UserCircle size={18} /> Hồ sơ của tôi</Link></li>
          <li><Link to="/pt/members" className={isActive('/pt/members')}><Users size={18} /> Học viên của tôi</Link></li>
          <li><Link to="/pt/reviews" className={isActive('/pt/reviews')}><MessageSquare size={18} /> Đánh giá</Link></li>
          <li style={{ marginTop: 'auto', borderTop: '1px solid rgba(255,255,255,0.1)', paddingTop: '10px' }}>
            <a href="#" onClick={(e) => { e.preventDefault(); handleLogout(); }}><LogOut size={18} /> Đăng xuất</a>
          </li>
        </ul>
      </aside>

      <main className="admin-main">
        {children}
      </main>
    </div>
  );
};

export default PtLayout;
