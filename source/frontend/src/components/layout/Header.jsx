import React from 'react';
import { Link } from 'react-router-dom';
import './Header.css';

const Header = () => {
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
          <Link to="/login">
            <button className="btn-signin">SIGN IN</button>
          </Link>
        </div>
      </div>
    </header>
  );
};

export default Header;
