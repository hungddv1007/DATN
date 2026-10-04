import React, { createContext, useContext, useState, useEffect } from 'react';
import authService from '../services/authService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Khi app load lần đầu, đọc user từ localStorage
  useEffect(() => {
    const savedUser = authService.getCurrentUser();
    if (savedUser) {
      setUser(savedUser);
    }
    setLoading(false);
  }, []);

  // Đăng nhập
  const login = async (email, password) => {
    const data = await authService.login(email, password);
    setUser(data);
    return data;
  };

  // Đăng nhập bằng Google
  const loginGoogle = async (idToken) => {
    const data = await authService.loginWithGoogle(idToken);
    setUser(data);
    return data;
  };

  // Đăng xuất
  const logout = () => {
    authService.logout();
    setUser(null);
  };

  // Cập nhật thông tin user (sau khi sửa profile)
  const updateUser = (updatedData) => {
    setUser(updatedData);
    localStorage.setItem('user', JSON.stringify(updatedData));
  };

  const value = {
    user,
    loading,
    login,
    loginGoogle,
    logout,
    updateUser,
    isLoggedIn: !!user,
  };

  return (
    <AuthContext.Provider value={value}>
      {!loading && children}
    </AuthContext.Provider>
  );
};

// Hook tiện ích để dùng ở bất kỳ component nào
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth phải được sử dụng bên trong AuthProvider');
  }
  return context;
};

export default AuthContext;
