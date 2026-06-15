import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './routes/ProtectedRoute';

// Public Pages
import HomePage from './pages/public/HomePage';
import PackagesPage from './pages/public/PackagesPage';
import BlogListPage from './pages/public/BlogListPage';
import AboutPage from './pages/public/AboutPage';
import PtListPage from './pages/public/PtListPage';

// Auth Pages
import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';

// Member Pages
import MemberDashboard from './pages/member/MemberDashboard';
import BuyPackagePage from './pages/member/BuyPackagePage';
import MemberTransactions from './pages/member/MemberTransactions';

// PT Pages
import PtDashboard from './pages/pt/PtDashboard';

// Admin Pages
import AdminDashboard from './pages/admin/AdminDashboard';
import TransactionsManagement from './pages/admin/TransactionsManagement';
import PackagesManagement from './pages/admin/PackagesManagement';
import UsersManagement from './pages/admin/UsersManagement';
import PromotionsManagement from './pages/admin/PromotionsManagement';
import BlogsManagement from './pages/admin/BlogsManagement';
import ExercisesManagement from './pages/admin/ExercisesManagement';

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
          <Route path="/pts" element={<PtListPage />} />

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
          <Route path="/member/buy-package" element={
            <ProtectedRoute allowedRoles={['MEMBER']}>
              <BuyPackagePage />
            </ProtectedRoute>
          } />
          <Route path="/member/transactions" element={
            <ProtectedRoute allowedRoles={['MEMBER']}>
              <MemberTransactions />
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
          <Route path="/admin/transactions" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <TransactionsManagement />
            </ProtectedRoute>
          } />
          <Route path="/admin/packages" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <PackagesManagement />
            </ProtectedRoute>
          } />
          <Route path="/admin/users" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <UsersManagement />
            </ProtectedRoute>
          } />
          <Route path="/admin/promotions" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <PromotionsManagement />
            </ProtectedRoute>
          } />
          <Route path="/admin/blogs" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <BlogsManagement />
            </ProtectedRoute>
          } />
          <Route path="/admin/exercises" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <ExercisesManagement />
            </ProtectedRoute>
          } />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
