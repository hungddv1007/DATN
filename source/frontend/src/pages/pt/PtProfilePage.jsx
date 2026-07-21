import React, { useState, useEffect } from 'react';
import PtLayout from '../../components/layout/PtLayout';
import api from '../../services/api';
import { Star, Award, UserCircle, Briefcase, FileText, Phone, Save, X } from 'lucide-react';
import '../admin/AdminManagement.css';

const PtProfilePage = () => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(false);
  const [formData, setFormData] = useState({
    fullName: '',
    phone: '',
    specialization: '',
    bio: '',
    certificates: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const res = await api.get('/pt/profile');
      setProfile(res.data);
      setFormData({
        fullName: res.data.fullName || '',
        phone: res.data.phone || '',
        specialization: res.data.specialization || '',
        bio: res.data.bio || '',
        certificates: res.data.certificates || ''
      });
    } catch (err) {
      console.error(err);
      setError('Lỗi khi tải thông tin hồ sơ.');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

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

    try {
      const res = await api.put('/pt/profile', formData);
      setProfile(res.data);
      setEditing(false);
      setSuccess('Cập nhật hồ sơ thành công!');
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError(err.response?.data?.message || 'Có lỗi xảy ra khi cập nhật.');
    }
  };

  const handleCancel = () => {
    // Reset form về dữ liệu gốc
    setFormData({
      fullName: profile?.fullName || '',
      phone: profile?.phone || '',
      specialization: profile?.specialization || '',
      bio: profile?.bio || '',
      certificates: profile?.certificates || ''
    });
    setEditing(false);
    setError('');
  };

  if (loading) {
    return (
      <PtLayout>
        <div style={{ textAlign: 'center', padding: '80px 20px', color: '#94a3b8' }}>Đang tải thông tin...</div>
      </PtLayout>
    );
  }

  return (
    <PtLayout>
      <h1>Hồ Sơ Của Tôi</h1>
      <p>Thông tin này sẽ được hiển thị cho khách hàng xem trên trang chủ.</p>

      {/* Alerts */}
      {error && (
        <div style={{ background: 'rgba(239,68,68,0.15)', border: '1px solid rgba(239,68,68,0.3)', color: '#fca5a5', padding: '12px 16px', borderRadius: '8px', marginBottom: '20px' }}>
          {error}
        </div>
      )}
      {success && (
        <div style={{ background: 'rgba(34,197,94,0.15)', border: '1px solid rgba(34,197,94,0.3)', color: '#86efac', padding: '12px 16px', borderRadius: '8px', marginBottom: '20px' }}>
          {success}
        </div>
      )}

      {/* Quick Stats */}
      <div className="admin-stats" style={{ gridTemplateColumns: 'repeat(3, 1fr)' }}>
        <div className="stat-card">
          <Star size={28} className="stat-icon" style={{ color: '#eab308' }} />
          <div className="stat-label">Đánh giá trung bình</div>
          <div className="stat-value">{profile?.ratingScore || '—'}</div>
        </div>
        <div className="stat-card">
          <UserCircle size={28} className="stat-icon" style={{ color: '#3b82f6' }} />
          <div className="stat-label">Học viên</div>
          <div className="stat-value">{profile?.totalMembers || 0}</div>
        </div>
        <div className="stat-card">
          <Award size={28} className="stat-icon" style={{ color: '#10b981' }} />
          <div className="stat-label">Lượt đánh giá</div>
          <div className="stat-value">{profile?.totalReviews || 0}</div>
        </div>
      </div>

      {/* Profile Info */}
      <div className="admin-table-container" style={{ marginTop: '0' }}>
        <div style={{ padding: '16px 20px', borderBottom: '1px solid rgba(255,255,255,0.06)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h3 style={{ color: '#f1f5f9', margin: 0 }}>Thông tin chuyên môn</h3>
          {!editing && (
            <button className="btn-submit" onClick={() => setEditing(true)} style={{ padding: '8px 16px', background: '#f97316', border: 'none', color: 'white', borderRadius: '8px', cursor: 'pointer', fontWeight: '600' }}>
              Chỉnh sửa
            </button>
          )}
        </div>

        <div style={{ padding: '24px' }}>
          {!editing ? (
            /* VIEW MODE */
            <div style={{ display: 'grid', gap: '20px' }}>
              <div>
                <div style={{ color: '#94a3b8', fontSize: '0.85rem', fontWeight: '600', textTransform: 'uppercase', letterSpacing: '0.04em', marginBottom: '6px', display: 'flex', alignItems: 'center', gap: '6px' }}>
                  <UserCircle size={16} /> Họ tên
                </div>
                <div style={{ color: '#e2e8f0', fontSize: '1rem' }}>{profile?.fullName || '—'}</div>
              </div>

              <div>
                <div style={{ color: '#94a3b8', fontSize: '0.85rem', fontWeight: '600', textTransform: 'uppercase', letterSpacing: '0.04em', marginBottom: '6px', display: 'flex', alignItems: 'center', gap: '6px' }}>
                  <Phone size={16} /> Số điện thoại
                </div>
                <div style={{ color: '#e2e8f0', fontSize: '1rem' }}>{profile?.phone || <span style={{ color: '#64748b', fontStyle: 'italic' }}>Chưa cập nhật</span>}</div>
              </div>

              <div>
                <div style={{ color: '#94a3b8', fontSize: '0.85rem', fontWeight: '600', textTransform: 'uppercase', letterSpacing: '0.04em', marginBottom: '6px', display: 'flex', alignItems: 'center', gap: '6px' }}>
                  <Briefcase size={16} /> Chuyên môn
                </div>
                <div style={{ color: '#e2e8f0', fontSize: '1rem' }}>{profile?.specialization || <span style={{ color: '#64748b', fontStyle: 'italic' }}>Chưa cập nhật</span>}</div>
              </div>

              <div>
                <div style={{ color: '#94a3b8', fontSize: '0.85rem', fontWeight: '600', textTransform: 'uppercase', letterSpacing: '0.04em', marginBottom: '6px', display: 'flex', alignItems: 'center', gap: '6px' }}>
                  <FileText size={16} /> Giới thiệu bản thân
                </div>
                <div style={{ color: '#e2e8f0', fontSize: '1rem', whiteSpace: 'pre-wrap', lineHeight: '1.6' }}>{profile?.bio || <span style={{ color: '#64748b', fontStyle: 'italic' }}>Chưa cập nhật</span>}</div>
              </div>

              <div>
                <div style={{ color: '#94a3b8', fontSize: '0.85rem', fontWeight: '600', textTransform: 'uppercase', letterSpacing: '0.04em', marginBottom: '6px', display: 'flex', alignItems: 'center', gap: '6px' }}>
                  <Award size={16} /> Bằng cấp / Chứng chỉ
                </div>
                <div style={{ color: '#e2e8f0', fontSize: '1rem', whiteSpace: 'pre-wrap', lineHeight: '1.6' }}>{profile?.certificates || <span style={{ color: '#64748b', fontStyle: 'italic' }}>Chưa cập nhật</span>}</div>
              </div>
            </div>
          ) : (
            /* EDIT MODE - using modal-content classes for consistent form styling */
            <form onSubmit={handleSubmit} className="modal-content" style={{ background: 'transparent', border: 'none', padding: 0, maxWidth: '100%', boxShadow: 'none' }}>
              <div>
                <label>Họ và tên</label>
                <input type="text" name="fullName" value={formData.fullName} onChange={handleChange} required />
              </div>

              <div>
                <label>Số điện thoại</label>
                <input type="text" name="phone" value={formData.phone} onChange={handleChange} placeholder="VD: 0901234567" />
              </div>

              <div>
                <label>Chuyên môn</label>
                <input type="text" name="specialization" value={formData.specialization} onChange={handleChange} placeholder="VD: Tăng cơ, Giảm mỡ, Yoga..." />
              </div>

              <div>
                <label>Giới thiệu bản thân</label>
                <textarea name="bio" value={formData.bio} onChange={handleChange} rows="4" placeholder="Viết vài dòng giới thiệu về bản thân và kinh nghiệm của bạn..." />
              </div>

              <div>
                <label>Bằng cấp / Chứng chỉ</label>
                <textarea name="certificates" value={formData.certificates} onChange={handleChange} rows="3" placeholder="VD: ACE Certified Personal Trainer, Chứng chỉ Yoga 200h..." />
              </div>

              <div style={{ display: 'flex', gap: '12px', marginTop: '10px' }}>
                <button type="submit" className="btn-submit">
                  <Save size={16} style={{ verticalAlign: 'middle', marginRight: '6px' }} />Lưu thay đổi
                </button>
                <button type="button" className="btn-cancel" onClick={handleCancel}>
                  <X size={16} style={{ verticalAlign: 'middle', marginRight: '6px' }} />Hủy
                </button>
              </div>
            </form>
          )}
        </div>
      </div>
    </PtLayout>
  );
};

export default PtProfilePage;
