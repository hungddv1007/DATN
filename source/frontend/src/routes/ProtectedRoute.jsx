import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const ProtectedRoute = ({ children, allowedRoles }) => {
  const { user, isLoggedIn } = useAuth();

  // Chưa đăng nhập → về login
  if (!isLoggedIn) {
    return <Navigate to="/login" replace />;
  }

  // Đã đăng nhập nhưng không đúng role → về trang chủ
  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return <Navigate to="/" replace />;
  }

  return children;
};

export default ProtectedRoute;
