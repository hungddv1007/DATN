import React, { useState, useEffect, useRef } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import notificationService from '../../services/notificationService';
import { Bell, CheckCheck, Trash2, X } from 'lucide-react';
import './Header.css';

const Header = () => {
  const { user, isLoggedIn, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [showDropdown, setShowDropdown] = useState(false);
  const [showNotifDropdown, setShowNotifDropdown] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);
  const [notifications, setNotifications] = useState([]);
  const [notifLoading, setNotifLoading] = useState(false);

  const notifRef = useRef(null);

  // Lấy số lượng chưa đọc khi component mount & khi đăng nhập
  const fetchUnreadCount = async () => {
    if (!isLoggedIn) return;
    try {
      const data = await notificationService.getUnreadCount();
      setUnreadCount(data.unreadCount || 0);
    } catch (err) {
      console.log('Chưa thể lấy số lượng thông báo');
    }
  };

  useEffect(() => {
    fetchUnreadCount();
    const interval = setInterval(fetchUnreadCount, 30000); // Polling mỗi 30s
    return () => clearInterval(interval);
  }, [isLoggedIn]);

  // Click ngoài dropdown thì tự đóng
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (notifRef.current && !notifRef.current.contains(e.target)) {
        setShowNotifDropdown(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleToggleNotif = async () => {
    const nextState = !showNotifDropdown;
    setShowNotifDropdown(nextState);
    if (nextState) {
      setNotifLoading(true);
      try {
        const data = await notificationService.getMyNotifications(0, 15);
        setNotifications(data.content || []);
      } catch (err) {
        console.error('Lỗi tải danh sách thông báo:', err);
      } finally {
        setNotifLoading(false);
      }
    }
  };

  const handleMarkAsRead = async (id) => {
    try {
      await notificationService.markAsRead(id);
      setNotifications(prev => prev.map(n => n.id === id ? { ...n, isRead: true } : n));
      fetchUnreadCount();
    } catch (err) {
      console.error('Lỗi đánh dấu đã đọc:', err);
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await notificationService.markAllAsRead();
      setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
      setUnreadCount(0);
    } catch (err) {
      console.error('Lỗi đánh dấu tất cả đã đọc:', err);
    }
  };

  const handleDeleteNotif = async (id, e) => {
    e.stopPropagation();
    try {
      await notificationService.deleteNotification(id);
      setNotifications(prev => prev.filter(n => n.id !== id));
      fetchUnreadCount();
    } catch (err) {
      console.error('Lỗi xóa thông báo:', err);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const getDashboardLink = () => {
    if (!user) return '/';
    if (user.role === 'ADMIN') return '/admin';
    if (user.role === 'PT') return '/pt/dashboard';
    return '/member/dashboard';
  };

  return (
    <header className="main-header">
      <div className="header-container">
        <div className="logo">
          <Link to="/" className="logo-text" style={{ textDecoration: 'none' }}>GymPro</Link>
        </div>
        <nav className="main-nav">
          <Link to="/" className={`nav-link ${location.pathname === '/' ? 'active' : ''}`}>HOME</Link>
          <Link to="/about" className={`nav-link ${location.pathname === '/about' ? 'active' : ''}`}>ABOUT</Link>
          <Link to="/services" className={`nav-link ${location.pathname === '/services' ? 'active' : ''}`}>SERVICES</Link>
          <Link to="/pts" className={`nav-link ${location.pathname === '/pts' ? 'active' : ''}`}>PTs</Link>
          <Link to="/blog" className={`nav-link ${location.pathname === '/blog' ? 'active' : ''}`}>BLOG</Link>
        </nav>
        <div className="header-actions">
          {isLoggedIn ? (
            <div className="header-user-wrapper">
              {/* NOTIFICATION BELL ICON */}
              <div className="notif-bell-container" ref={notifRef}>
                <button
                  className="notif-bell-btn"
                  onClick={handleToggleNotif}
                  title="Thông báo"
                >
                  <Bell size={20} />
                  {unreadCount > 0 && (
                    <span className="notif-badge">
                      {unreadCount > 99 ? '99+' : unreadCount}
                    </span>
                  )}
                </button>

                {/* NOTIFICATION DROPDOWN */}
                {showNotifDropdown && (
                  <div className="notif-dropdown">
                    <div className="notif-dropdown-header">
                      <span className="notif-header-title">Thông Báo</span>
                      {notifications.some(n => !n.isRead) && (
                        <button className="notif-readall-btn" onClick={handleMarkAllAsRead}>
                          <CheckCheck size={13} /> Đọc tất cả
                        </button>
                      )}
                    </div>

                    <div className="notif-dropdown-body">
                      {notifLoading ? (
                        <div className="notif-state-text">Đang tải thông báo...</div>
                      ) : notifications.length === 0 ? (
                        <div className="notif-state-text">Bạn không có thông báo nào.</div>
                      ) : (
                        notifications.map(n => (
                          <div
                            key={n.id}
                            className={`notif-dropdown-item ${!n.isRead ? 'unread' : ''}`}
                            onClick={() => !n.isRead && handleMarkAsRead(n.id)}
                          >
                            <div className="notif-item-main">
                              <div className="notif-item-title-row">
                                {!n.isRead && <span className="notif-dot" />}
                                <span className="notif-title-text">{n.title}</span>
                              </div>
                              <p className="notif-msg-text">{n.message}</p>
                              <span className="notif-time-text">
                                {n.createdAt ? new Date(n.createdAt).toLocaleString('vi-VN') : ''}
                              </span>
                            </div>
                            <button
                              className="notif-item-del"
                              title="Xóa"
                              onClick={(e) => handleDeleteNotif(n.id, e)}
                            >
                              <Trash2 size={13} />
                            </button>
                          </div>
                        ))
                      )}
                    </div>
                  </div>
                )}
              </div>

              {/* USER PROFILE DROPDOWN */}
              <div 
                className="user-menu"
                onMouseEnter={() => setShowDropdown(true)}
                onMouseLeave={() => setShowDropdown(false)}
              >
                {user.avatar ? (
                  <img 
                    src={user.avatar} 
                    alt="Avatar" 
                    className="header-avatar"
                  />
                ) : (
                  <div className="header-avatar header-avatar-fallback">
                    {user.fullName?.charAt(0)?.toUpperCase() || '?'}
                  </div>
                )}
                <span className="user-greeting">
                  Xin chào, <strong>{user.fullName}</strong> ▾
                </span>
                
                {showDropdown && (
                  <div className="user-dropdown">
                    <Link to={getDashboardLink()} className="dropdown-item">Dashboard</Link>
                    <Link to="/profile" className="dropdown-item">Hồ sơ cá nhân</Link>
                    <div className="dropdown-divider"></div>
                    <div className="dropdown-item text-danger" onClick={handleLogout}>Đăng xuất</div>
                  </div>
                )}
              </div>
            </div>
          ) : (
            <div className="auth-buttons">
              <Link to="/login">
                <button className="btn-signin">SIGN IN</button>
              </Link>
              <Link to="/register" className="link-signup">Sign up</Link>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};

export default Header;
