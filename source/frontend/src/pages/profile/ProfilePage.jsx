import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import MainLayout from '../../components/layout/MainLayout';
import { Camera, Save, User as UserIcon } from 'lucide-react';
import './ProfilePage.css';

const ProfilePage = () => {
  const { user } = useAuth();
  
  const [formData, setFormData] = useState({
    fullName: '',
    phone: '',
    email: '',
    avatar: ''
  });
  
  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  // Load user info into form
  useEffect(() => {
    if (user) {
      setFormData({
        fullName: user.fullName || '',
        phone: user.phone || '',
        email: user.email || '',
        avatar: user.avatar || ''
      });
    }
  }, [user]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleAvatarChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const imageUrl = URL.createObjectURL(file);
      setFormData({ ...formData, avatar: imageUrl });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage('');
    
    // TODO: Gọi API cập nhật User. 
    // Hiện tại Backend chưa có API PUT /api/users/{id}
    setTimeout(() => {
      setMessage('Lưu thành công! (Lưu ý: Backend chưa có API cập nhật User nên thông tin sẽ không lưu vĩnh viễn khi F5)');
      setIsEditing(false);
      setLoading(false);
    }, 1000);
  };

  return (
    <MainLayout>
      <div className="profile-page">
        <div className="profile-container">
          <div className="profile-header">
            <h1>Hồ Sơ Cá Nhân</h1>
            <p>Quản lý thông tin tài khoản của bạn</p>
          </div>

          {message && <div className="profile-alert success">{message}</div>}

          <div className="profile-card">
            <div className="profile-sidebar">
              <div className="avatar-wrapper">
                {formData.avatar ? (
                  <img src={formData.avatar} alt="Avatar" className="profile-avatar" />
                ) : (
                  <div className="avatar-placeholder">
                    <UserIcon size={60} color="#cbd5e1" />
                  </div>
                )}
                
                {isEditing && (
                  <label className="avatar-upload-btn">
                    <Camera size={18} />
                    <input type="file" accept="image/*" onChange={handleAvatarChange} hidden />
                  </label>
                )}
              </div>
              <h3 className="profile-name">{user?.fullName}</h3>
              <span className="profile-role">Vai trò: {user?.role}</span>
            </div>

            <div className="profile-main">
              <div className="profile-main-header">
                <h2>Thông tin cơ bản</h2>
                {!isEditing && (
                  <button className="btn-edit" onClick={() => setIsEditing(true)}>Chỉnh sửa</button>
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
                    disabled // Không cho đổi email
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
                      value="Vui lòng sử dụng API/trang Quản lý Hồ sơ PT riêng biệt để cập nhật Kinh nghiệm và Chuyên môn."
                    />
                  </div>
                )}

                {isEditing && (
                  <div className="form-actions">
                    <button type="button" className="btn-cancel" onClick={() => {
                      setIsEditing(false);
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
