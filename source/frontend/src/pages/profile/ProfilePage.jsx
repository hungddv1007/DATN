import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import userService from '../../services/userService';
import fileService from '../../services/fileService';
import { resolveFileUrl } from '../../utils/fileUrl';
import MainLayout from '../../components/layout/MainLayout';
import { Camera, Save } from 'lucide-react';
import './ProfilePage.css';

const ProfilePage = () => {
  const { user, updateUser } = useAuth();
  
  const [formData, setFormData] = useState({
    fullName: '',
    phone: '',
    email: '',
    avatar: ''
  });
  
  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(false);
  const [pageLoading, setPageLoading] = useState(true);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  // Load profile từ backend API khi mở trang
  useEffect(() => {
    const loadProfile = async () => {
      try {
        const profile = await userService.getMyProfile();
        setFormData({
          fullName: profile.fullName || '',
          phone: profile.phone || '',
          email: profile.email || '',
          avatar: profile.avatar || ''
        });
      } catch (err) {
        // Nếu API lỗi thì dùng dữ liệu từ context làm fallback
        if (user) {
          setFormData({
            fullName: user.fullName || '',
            phone: user.phone || '',
            email: user.email || '',
            avatar: user.avatar || ''
          });
        }
      } finally {
        setPageLoading(false);
      }
    };
    loadProfile();
  }, [user]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleAvatarChange = async (e) => {
    const file = e.target.files[0];
    if (file) {
      try {
        setLoading(true);
        const data = await fileService.uploadFile(file);
        setFormData({ ...formData, avatar: data.fileUrl });
        setMessage('Tải ảnh lên thành công. Nhấn Lưu thay đổi để lưu.');
      } catch (err) {
        setError('Lỗi tải ảnh lên.');
      } finally {
        setLoading(false);
      }
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');
    setError('');

    // 1. Validate họ tên (chỉ được chứa chữ cái và khoảng trắng)
    const nameRegex = /^[\p{L}\s'-]+$/u;
    if (!nameRegex.test(formData.fullName)) {
      setError('Họ và tên chỉ được chứa chữ cái và khoảng trắng!');
      return;
    }

    // 2. Validate số điện thoại (phải đúng định dạng số điện thoại Việt Nam hoặc bỏ trống)
    if (formData.phone) {
      const phoneRegex = /^(0|84)(2(0[3-9]|1[0-6|8|9]|2[0-2|5-9]|3[2-9]|4[0-9]|5[1|2|4-9]|6[9]|7[0-7|9]|8[0-9]|9[0-4|6|7|9])|3[2-9]|5[5|6|8|9]|7[0|6-9]|8[0-6|8|9]|9[0-4|6-9])([0-9]{7})$/;
      if (!phoneRegex.test(formData.phone)) {
        setError('Số điện thoại không hợp lệ hoặc không đúng định dạng Việt Nam!');
        return;
      }
    }

    setLoading(true);
    
    try {
      const updatedProfile = await userService.updateMyProfile({
        fullName: formData.fullName,
        phone: formData.phone,
        avatar: formData.avatar
      });

      // Cập nhật AuthContext + localStorage để Header hiển thị đúng tên mới
      if (updateUser) {
        updateUser({
          ...user,
          fullName: updatedProfile.fullName,
          phone: updatedProfile.phone,
          avatar: updatedProfile.avatar
        });
      }

      setMessage('Cập nhật hồ sơ thành công!');
      setIsEditing(false);
    } catch (err) {
      const resData = err.response?.data;
      if (resData) {
        if (resData.message) {
          setError(resData.message);
        } else if (typeof resData === 'object') {
          const firstError = Object.values(resData)[0];
          setError(firstError || 'Cập nhật thất bại!');
        } else {
          setError('Cập nhật thất bại!');
        }
      } else {
        setError('Lỗi kết nối đến máy chủ!');
      }
    } finally {
      setLoading(false);
    }
  };

  if (pageLoading) {
    return (
      <MainLayout>
        <div className="profile-page">
          <div className="profile-container" style={{ textAlign: 'center', paddingTop: '120px' }}>
            <p style={{ color: '#94a3b8' }}>Đang tải hồ sơ...</p>
          </div>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout>
      <div className="profile-page">
        <div className="profile-container">
          <div className="profile-header">
            <h1>Hồ Sơ Cá Nhân</h1>
            <p>Quản lý thông tin tài khoản của bạn</p>
          </div>

          {message && <div className="profile-alert success">{message}</div>}
          {error && <div className="profile-alert error">{error}</div>}

          <div className="profile-card">
            <div className="profile-sidebar">
              <div className="avatar-wrapper">
                {formData.avatar ? (
                  <img src={resolveFileUrl(formData.avatar)} alt="Avatar" className="profile-avatar" />
                ) : (
                  <div className="avatar-placeholder avatar-initial">
                    {formData.fullName?.charAt(0)?.toUpperCase() || user?.fullName?.charAt(0)?.toUpperCase() || '?'}
                  </div>
                )}
                
                {isEditing && (
                  <label className="avatar-upload-btn">
                    <Camera size={18} />
                    <input type="file" accept="image/*" onChange={handleAvatarChange} hidden />
                  </label>
                )}
              </div>
              <h3 className="profile-name">{formData.fullName || user?.fullName}</h3>
              <span className="profile-role">Vai trò: {user?.role}</span>
            </div>

            <div className="profile-main">
              <div className="profile-main-header">
                <h2>Thông tin cơ bản</h2>
                {!isEditing && (
                  <button className="btn-edit" onClick={() => { setIsEditing(true); setMessage(''); setError(''); }}>Chỉnh sửa</button>
                )}
              </div>

              <form onSubmit={handleSubmit} className="profile-form">
                <div className="form-group">
                  <label>Họ và tên</label>
                  <input
                    type="text"
                    name="fullName"
                    value={formData.fullName}
                    onChange={handleChange}
                    disabled={!isEditing}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>Email</label>
                  <input
                    type="email"
                    name="email"
                    value={formData.email}
                    disabled
                    className="disabled-input"
                  />
                  <small>Email dùng để đăng nhập, không thể thay đổi.</small>
                </div>

                <div className="form-group">
                  <label>Số điện thoại</label>
                  <input
                    type="tel"
                    name="phone"
                    value={formData.phone}
                    onChange={handleChange}
                    disabled={!isEditing}
                  />
                </div>

                {user?.role === 'PT' && (
                  <div className="form-group">
                    <label>Giới thiệu PT</label>
                    <textarea 
                      disabled 
                      className="disabled-input" 
                      rows="3"
                      value="Vui lòng sử dụng trang Quản lý Hồ sơ PT riêng biệt để cập nhật Kinh nghiệm và Chuyên môn."
                    />
                  </div>
                )}

                {isEditing && (
                  <div className="form-actions">
                    <button type="button" className="btn-cancel" onClick={() => {
                      setIsEditing(false);
                      setError('');
                      // Reset form về dữ liệu gốc
                      setFormData({
                        fullName: user.fullName || '',
                        phone: user.phone || '',
                        email: user.email || '',
                        avatar: user.avatar || ''
                      });
                    }}>Hủy</button>
                    <button type="submit" className="btn-save" disabled={loading}>
                      <Save size={18} /> {loading ? 'Đang lưu...' : 'Lưu thay đổi'}
                    </button>
                  </div>
                )}
              </form>
            </div>
          </div>
        </div>
      </div>
    </MainLayout>
  );
};

export default ProfilePage;

