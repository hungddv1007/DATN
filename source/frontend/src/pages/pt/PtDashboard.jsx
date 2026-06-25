import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import PtLayout from '../../components/layout/PtLayout';
import { Users, ClipboardList, MessageSquare, TrendingUp } from 'lucide-react';
import { Link } from 'react-router-dom';
import api from '../../services/api';

const PtDashboard = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState({
    activeMembers: 0,
    totalReviews: 0
  });
  const [members, setMembers] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [statsRes, membersRes] = await Promise.all([
          api.get('/pt/dashboard'),
          api.get('/pt/members')
        ]);
        setStats(statsRes.data);
        setMembers(membersRes.data);
      } catch (error) {
        console.error("Lỗi tải dữ liệu PT:", error);
      }
    };
    fetchData();
  }, []);

  return (
    <PtLayout>
      <h1>Tổng Quan</h1>
      <p>Xin chào, <strong style={{ color: '#f97316' }}>{user?.fullName || 'PT'}</strong>! Chúc bạn một ngày làm việc hiệu quả.</p>

      {/* Stat Cards */}
      <div className="admin-stats">
        <div className="stat-card">
          <Users size={28} className="stat-icon" />
          <div className="stat-label">Học viên đang quản lý</div>
          <div className="stat-value">{stats.activeMembers}</div>
        </div>

        <div className="stat-card">
          <MessageSquare size={28} className="stat-icon" />
          <div className="stat-label">Lượt đánh giá</div>
          <div className="stat-value">{stats.totalReviews}</div>
        </div>
        <div className="stat-card">
          <TrendingUp size={28} className="stat-icon" />
          <div className="stat-label">Tỉ lệ phản hồi</div>
          <div className="stat-value">{stats.totalReviews > 0 ? '100%' : '—'}</div>
        </div>
      </div>

      {/* Quick Links */}
      <div style={{ display: 'flex', gap: '12px', marginBottom: '32px' }}>
        <Link to="/pt/members" className="tab-btn active" style={{ textDecoration: 'none' }}>
          <Users size={16} style={{ marginRight: '6px', verticalAlign: 'middle' }} />Xem học viên
        </Link>
        <Link to="/pt/profile" className="tab-btn" style={{ textDecoration: 'none' }}>
          Cập nhật hồ sơ
        </Link>

      </div>

      {/* Recent Members Table */}
      <div className="admin-table-container">
        <div style={{ padding: '16px 20px', borderBottom: '1px solid rgba(255,255,255,0.06)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h3 style={{ color: '#f1f5f9', margin: 0 }}>Học viên gần đây</h3>
          <Link to="/pt/members" style={{ color: '#f97316', fontSize: '0.9rem', textDecoration: 'none' }}>Xem tất cả →</Link>
        </div>
        {members.length === 0 ? (
          <div style={{ padding: '40px 20px', textAlign: 'center', color: '#64748b' }}>
            Chưa có học viên nào được giao cho bạn.
          </div>
        ) : (
          <table className="admin-table">
            <thead>
              <tr>
                <th>Họ tên</th>
                <th>Gói tập</th>
                <th>Thời hạn</th>
                <th>Trạng thái</th>
              </tr>
            </thead>
            <tbody>
              {members.slice(0, 5).map(m => (
                <tr key={m.membershipId}>
                  <td style={{ fontWeight: '500' }}>{m.memberName}</td>
                  <td>{m.packageName}</td>
                  <td style={{ fontSize: '0.9rem' }}>
                    {new Date(m.startDate).toLocaleDateString('vi-VN')} – {new Date(m.endDate).toLocaleDateString('vi-VN')}
                  </td>
                  <td>
                    <span className={`status-badge ${m.status === 'ACTIVE' ? 'status-confirmed' : 'status-cancelled'}`}>
                      {m.status === 'ACTIVE' ? 'Đang hoạt động' : m.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </PtLayout>
  );
};

export default PtDashboard;
