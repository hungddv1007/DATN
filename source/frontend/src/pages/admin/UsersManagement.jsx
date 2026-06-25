import React, { useState, useEffect } from 'react';
import AdminLayout from '../../components/layout/AdminLayout';
import userService from '../../services/userService';
import { Lock, Unlock, ShieldAlert, CheckCircle, Search, Filter } from 'lucide-react';
import './AdminManagement.css';

const UsersManagement = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Filtering
  const [searchTerm, setSearchTerm] = useState('');
  const [roleFilter, setRoleFilter] = useState('ALL');

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const data = await userService.getAllUsers();
      // Loại bỏ chính Admin đang đăng nhập nếu cần, nhưng để hiển thị cũng không sao
      setUsers(data);
    } catch (error) {
      console.error('Lỗi tải danh sách người dùng:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleToggleStatus = async (id, currentStatus) => {
    const action = currentStatus ? 'Khóa' : 'Mở khóa';
    if (!window.confirm(`Bạn có chắc chắn muốn ${action} tài khoản này?`)) return;
    try {
      await userService.toggleUserStatus(id);
      alert(`${action} thành công!`);
      fetchUsers();
    } catch (error) {
      alert(error.response?.data?.message || `Lỗi khi ${action} tài khoản`);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return '';
    const d = new Date(dateString);
    return d.toLocaleDateString('vi-VN');
  };

  const getRoleBadgeStyle = (role) => {
    switch (role) {
      case 'ADMIN': return { background: 'rgba(239, 68, 68, 0.2)', color: '#fca5a5' };
      case 'PT': return { background: 'rgba(168, 85, 247, 0.2)', color: '#d8b4fe' };
      default: return { background: 'rgba(59, 130, 246, 0.2)', color: '#93c5fd' };
    }
  };

  // Lọc dữ liệu hiển thị
  const filteredUsers = users.filter(user => {
    const matchSearch = user.email.toLowerCase().includes(searchTerm.toLowerCase()) || 
                        user.fullName.toLowerCase().includes(searchTerm.toLowerCase());
    const matchRole = roleFilter === 'ALL' || user.role === roleFilter;
    return matchSearch && matchRole;
  });

  return (
    <AdminLayout>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h1>Quản lý Người Dùng</h1>
      </div>

      {/* Thanh công cụ tìm kiếm và lọc */}
      <div style={{ 
        display: 'flex', gap: '15px', marginBottom: '20px', 
        background: 'rgba(30, 41, 59, 0.7)', padding: '15px', borderRadius: '12px', border: '1px solid rgba(255,255,255,0.06)'
      }}>
        <div style={{ flex: 1, position: 'relative' }}>
          <Search size={18} style={{ position: 'absolute', left: '12px', top: '10px', color: '#64748b' }} />
          <input 
            type="text" 
            placeholder="Tìm kiếm theo Tên hoặc Email..." 
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{ 
              width: '100%', padding: '10px 10px 10px 40px', 
              background: '#0f172a', border: '1px solid rgba(255,255,255,0.1)', 
              color: 'white', borderRadius: '8px', outline: 'none'
            }}
          />
        </div>
        
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <Filter size={18} color="#64748b" />
          <select 
            value={roleFilter} 
            onChange={(e) => setRoleFilter(e.target.value)}
            style={{ 
              padding: '10px', background: '#0f172a', border: '1px solid rgba(255,255,255,0.1)', 
              color: 'white', borderRadius: '8px', outline: 'none', cursor: 'pointer'
            }}
          >
            <option value="ALL">Tất cả Vai trò</option>
            <option value="MEMBER">Hội Viên</option>
            <option value="PT">Huấn luyện viên (PT)</option>
            <option value="ADMIN">Quản trị viên</option>
          </select>
        </div>
      </div>

      <div className="admin-table-container">
        <table className="admin-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Họ tên</th>
              <th>Thông tin liên hệ</th>
              <th>Vai trò</th>
              <th>Ngày tham gia</th>
              <th>Trạng thái</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan="7" style={{ textAlign: 'center' }}>Đang tải...</td></tr>
            ) : filteredUsers.length === 0 ? (
              <tr><td colSpan="7" style={{ textAlign: 'center' }}>Không tìm thấy người dùng</td></tr>
            ) : (
              filteredUsers.map(user => (
                <tr key={user.id} style={{ opacity: user.status ? 1 : 0.6 }}>
                  <td>{user.id}</td>
                  <td>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                      <div style={{ 
                        width: '35px', height: '35px', borderRadius: '50%', 
                        overflow: 'hidden', flexShrink: 0,
                        border: '2px solid #f97316'
                      }}>
                        {user.avatar ? (
                          <img src={user.avatar} alt="avatar" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                        ) : (
                          <div style={{ 
                            width: '100%', height: '100%', 
                            background: 'linear-gradient(135deg, #f97316, #ea580c)',
                            display: 'flex', alignItems: 'center', justifyContent: 'center',
                            color: '#fff', fontWeight: '700', fontSize: '0.95rem'
                          }}>
                            {user.fullName?.charAt(0)?.toUpperCase() || '?'}
                          </div>
                        )}
                      </div>
                      <strong style={{ color: '#f1f5f9' }}>{user.fullName}</strong>
                    </div>
                  </td>
                  <td>
                    <div style={{ fontSize: '0.9rem' }}>{user.email}</div>
                    <div style={{ fontSize: '0.85rem', color: '#94a3b8' }}>{user.phone || 'Chưa cập nhật SĐT'}</div>
                  </td>
                  <td>
                    <span className="status-badge" style={getRoleBadgeStyle(user.role)}>
                      {user.role}
                    </span>
                  </td>
                  <td>{formatDate(user.createdAt)}</td>
                  <td>
                    {user.status ? (
                      <span className="status-badge" style={{ background: 'rgba(34, 197, 94, 0.1)', color: '#4ade80' }}>
                        <CheckCircle size={14} style={{ marginRight: '4px', verticalAlign: 'middle' }} /> Đang hoạt động
                      </span>
                    ) : (
                      <span className="status-badge" style={{ background: 'rgba(239, 68, 68, 0.1)', color: '#f87171' }}>
                        <ShieldAlert size={14} style={{ marginRight: '4px', verticalAlign: 'middle' }} /> Bị khóa
                      </span>
                    )}
                  </td>
                  <td>
                    {/* Không cho Admin khóa chính mình */}
                    {user.role !== 'ADMIN' && (
                      <div className="action-btns">
                        <button 
                          className={`btn-icon ${user.status ? 'cancel' : 'confirm'}`} 
                          onClick={() => handleToggleStatus(user.id, user.status)} 
                          title={user.status ? "Khóa tài khoản" : "Mở khóa"}
                        >
                          {user.status ? <Lock size={18} /> : <Unlock size={18} />}
                        </button>
                      </div>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </AdminLayout>
  );
};

export default UsersManagement;
