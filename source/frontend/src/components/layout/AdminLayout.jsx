import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Users, Package, CreditCard, FileText, Dumbbell, Tag, LayoutDashboard, LogOut, Percent, Bell, Menu, X } from 'lucide-react';
import '../../pages/member/DashboardPage.css';

const AdminLayout = ({ children }) => {
  const { logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  useEffect(() => {
    setMobileMenuOpen(false);
  }, [location.pathname]);

  useEffect(() => {
    if (!mobileMenuOpen) return undefined;
    const previousOverflow = document.body.style.overflow;
    document.body.style.overflow = 'hidden';
    return () => {
      document.body.style.overflow = previousOverflow;
    };
  }, [mobileMenuOpen]);

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const isActive = (path) => {
    if (path === '/admin' && location.pathname === '/admin') return 'active';
    if (path !== '/admin' && location.pathname.startsWith(path)) return 'active';
    return '';
  };

  return (
    <div className="admin-page">
      <header className="workspace-mobile-header">
        <button
          type="button"
          className="workspace-menu-toggle"
          aria-label={mobileMenuOpen ? 'Đóng menu quản trị' : 'Mở menu quản trị'}
          aria-expanded={mobileMenuOpen}
          onClick={() => setMobileMenuOpen((current) => !current)}
        >
          {mobileMenuOpen ? <X size={22} /> : <Menu size={22} />}
        </button>
        <Link to="/admin" className="workspace-mobile-brand">GymPro</Link>
        <span className="workspace-mobile-role">Admin</span>
      </header>

      {mobileMenuOpen && (
        <button
          type="button"
          className="workspace-sidebar-backdrop"
          aria-label="Đóng menu quản trị"
          onClick={() => setMobileMenuOpen(false)}
        />
      )}

      <aside className={`admin-sidebar ${mobileMenuOpen ? 'mobile-open' : ''}`}>
        <div className="admin-sidebar-logo">
          <h2><Link to="/" style={{ textDecoration: 'none', color: 'inherit' }}>GymPro</Link></h2>
          <span>Admin Panel</span>
        </div>
        <ul className="admin-nav">
          <li><Link to="/admin" className={isActive('/admin')}><LayoutDashboard size={18} /> Tổng quan</Link></li>
          <li><Link to="/admin/transactions" className={isActive('/admin/transactions')}><CreditCard size={18} /> Giao dịch</Link></li>
          <li><Link to="/admin/packages" className={isActive('/admin/packages')}><Package size={18} /> Gói tập</Link></li>
          <li><Link to="/admin/promotions" className={isActive('/admin/promotions')}><Tag size={18} /> Khuyến mãi</Link></li>
          <li><Link to="/admin/discounts" className={isActive('/admin/discounts')}><Percent size={18} /> Chiết khấu</Link></li>
          <li><Link to="/admin/users" className={isActive('/admin/users')}><Users size={18} /> Quản lý Users</Link></li>
          <li><Link to="/admin/blogs" className={isActive('/admin/blogs')}><FileText size={18} /> Bài viết</Link></li>
          <li><Link to="/admin/exercises" className={isActive('/admin/exercises')}><Dumbbell size={18} /> Bài tập</Link></li>
          <li><Link to="/admin/notifications" className={isActive('/admin/notifications')}><Bell size={18} /> Gửi thông báo</Link></li>
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

export default AdminLayout;
