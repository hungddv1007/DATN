import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import './Header.css';

const Header = () => {
  const { user, isLoggedIn, logout } = useAuth();
  const navigate = useNavigate();
  const [showDropdown, setShowDropdown] = useState(false);

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
          <Link to="/" className="nav-link active">HOME</Link>
          <Link to="/about" className="nav-link">ABOUT</Link>
          <Link to="/services" className="nav-link">SERVICES</Link>
          <Link to="/pts" className="nav-link">PTs</Link>
          <Link to="/blog" className="nav-link">BLOG</Link>
        </nav>
        <div className="header-actions">
          {isLoggedIn ? (
            <div 
              className="user-menu"
              onMouseEnter={() => setShowDropdown(true)}
              onMouseLeave={() => setShowDropdown(false)}
            >
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
