import React from 'react';
import './Header.css';

const Header = () => {
  return (
    <header className="main-header">
      <div className="header-container">
        <div className="logo">
          <span className="logo-text">GymPro</span>
        </div>
        <nav className="main-nav">
          <a href="#" className="nav-link active">HOME</a>
          <a href="#" className="nav-link">ABOUT</a>
          <a href="#" className="nav-link">SERVICES</a>
          <a href="#" className="nav-link">PTs</a>
          <a href="#" className="nav-link">BLOG</a>
        </nav>
        <div className="header-actions">
          <button className="btn-signin">SIGN IN</button>
        </div>
      </div>
    </header>
  );
};

export default Header;
