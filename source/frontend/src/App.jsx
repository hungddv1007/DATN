import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './routes/ProtectedRoute';

// Public Pages
import HomePage from './pages/public/HomePage';
import PackagesPage from './pages/public/PackagesPage';
import BlogListPage from './pages/public/BlogListPage';
import AboutPage from './pages/public/AboutPage';

// Auth Pages
import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';

// Member Pages
import MemberDashboard from './pages/member/MemberDashboard';

// PT Pages
import PtDashboard from './pages/pt/PtDashboard';

// Admin Pages
import AdminDashboard from './pages/admin/AdminDashboard';

// Profile Page
import ProfilePage from './pages/profile/ProfilePage';

import './index.css';

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* === Trang công khai === */}
          <Route path="/" element={<HomePage />} />
          <Route path="/packages" element={<PackagesPage />} />
          <Route path="/services" element={<PackagesPage />} />
          <Route path="/blog" element={<BlogListPage />} />
          <Route path="/about" element={<AboutPage />} />

          {/* === Auth === */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          {/* === Profile Chung (Ai đăng nhập cũng vào được) === */}
          <Route path="/profile" element={
            <ProtectedRoute>
              <ProfilePage />
            </ProtectedRoute>
          } />

          {/* === Member (cần đăng nhập + role MEMBER) === */}
          <Route path="/member/dashboard" element={
            <ProtectedRoute allowedRoles={['MEMBER']}>
              <MemberDashboard />
            </ProtectedRoute>
          } />

          {/* === PT (cần đăng nhập + role PT) === */}
          <Route path="/pt/dashboard" element={
            <ProtectedRoute allowedRoles={['PT']}>
              <PtDashboard />
            </ProtectedRoute>
          } />

          {/* === Admin (cần đăng nhập + role ADMIN) === */}
          <Route path="/admin" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <AdminDashboard />
            </ProtectedRoute>
          } />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
