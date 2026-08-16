import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import PtLayout from '../../components/layout/PtLayout';
import { SummaryCard, SummaryGrid } from '../../components/common/SummaryCards';
import { Users, MessageSquare, TrendingUp, Calendar, UserCircle } from 'lucide-react';
import { Link } from 'react-router-dom';
import ptDashboardService from '../../services/ptDashboardService';

const PtDashboard = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState({
    activeMembers: 0,
    totalReviews: 0
  });
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const statsData = await ptDashboardService.getDashboardStats(user?.email);
        setStats(statsData);
        
        const membersData = await ptDashboardService.getAssignedMembers();
        setMembers(membersData);
      } catch (error) {
        console.error("Lỗi tải dữ liệu PT:", error);
      } finally {
        setLoading(false);
      }
    };
    if (user?.email) {
      fetchData();
    }
  }, [user?.email]);

  return (
    <PtLayout>
      <div className="pt-dashboard-page" style={{ padding: '20px' }}>
        <h1 style={{ fontSize: '1.8rem', fontWeight: '800', marginBottom: '8px' }}>Tổng Quan PT</h1>
        <p style={{ color: '#94a3b8', marginBottom: '24px' }}>
          Xin chào, <strong style={{ color: '#f97316' }}>{user?.fullName || 'Huấn luyện viên'}</strong>! Chúc bạn một ngày làm việc hiệu quả.
        </p>

        {/* Stat Cards */}
        <SummaryGrid columns={3} ariaLabel="Tổng quan huấn luyện viên">
          <SummaryCard icon={Users} label="Học viên đang quản lý" value={stats.activeMembers} tone="blue" />
          <SummaryCard icon={MessageSquare} label="Lượt đánh giá" value={stats.totalReviews} tone="purple" />
          <SummaryCard icon={TrendingUp} label="Tỉ lệ hoàn thành lịch" value="100%" tone="green" />
        </SummaryGrid>

        {/* Quick Links */}
        <div style={{ display: 'flex', gap: '12px', marginBottom: '32px', flexWrap: 'wrap' }}>
          <Link to="/pt/members" className="tab-btn active" style={{ textDecoration: 'none', display: 'flex', alignItems: 'center', gap: '6px' }}>
            <Users size={16} /> Danh sách học viên
          </Link>
          <Link to="/pt/schedule" className="tab-btn" style={{ textDecoration: 'none', background: 'rgba(249, 115, 22, 0.1)', color: '#f97316', border: '1px solid rgba(249, 115, 22, 0.2)', display: 'flex', alignItems: 'center', gap: '6px' }}>
            <Calendar size={16} /> Xếp lịch huấn luyện
          </Link>
          <Link to="/pt/profile" className="tab-btn" style={{ textDecoration: 'none', display: 'flex', alignItems: 'center', gap: '6px' }}>
            <UserCircle size={16} /> Hồ sơ cá nhân
          </Link>
        </div>

        {/* Recent Members Table */}
        <div className="admin-table-container" style={{ background: 'rgba(30,41,59,0.6)', borderRadius: '12px', border: '1px solid rgba(255,255,255,0.05)', overflow: 'hidden' }}>
          <div style={{ padding: '16px 20px', borderBottom: '1px solid rgba(255,255,255,0.06)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <h3 style={{ color: '#f1f5f9', margin: 0, fontSize: '1.1rem', fontWeight: '700' }}>Học viên gần đây</h3>
            <Link to="/pt/members" style={{ color: '#f97316', fontSize: '0.9rem', textDecoration: 'none', fontWeight: '600' }}>Xem tất cả →</Link>
          </div>
          {loading ? (
            <div style={{ padding: '40px 20px', textAlign: 'center', color: '#64748b' }}>Đang tải danh sách học viên...</div>
          ) : members.length === 0 ? (
            <div style={{ padding: '40px 20px', textAlign: 'center', color: '#64748b' }}>
              Chưa có học viên nào được giao cho bạn.
            </div>
          ) : (
            <table className="admin-table" style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr style={{ background: 'rgba(15,23,42,0.3)', borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                  <th style={{ padding: '12px 16px', textAlign: 'left', color: '#94a3b8', fontSize: '0.85rem' }}>Họ tên</th>
                  <th style={{ padding: '12px 16px', textAlign: 'left', color: '#94a3b8', fontSize: '0.85rem' }}>Gói tập</th>
                  <th style={{ padding: '12px 16px', textAlign: 'left', color: '#94a3b8', fontSize: '0.85rem' }}>Thời hạn</th>
                  <th style={{ padding: '12px 16px', textAlign: 'left', color: '#94a3b8', fontSize: '0.85rem' }}>Lịch kèm</th>
                </tr>
              </thead>
              <tbody>
                {members.slice(0, 5).map(m => (
                  <tr key={m.membershipId} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                    <td style={{ padding: '12px 16px', fontWeight: '600', color: '#fff' }}>{m.memberName}</td>
                    <td style={{ padding: '12px 16px', color: '#cbd5e1' }}>{m.packageName}</td>
                    <td style={{ padding: '12px 16px', fontSize: '0.9rem', color: '#94a3b8' }}>
                      {m.startDate} – {m.endDate}
                    </td>
                    <td style={{ padding: '12px 16px' }}>
                      <span className={`status-badge ${m.isScheduled ? 'active' : 'paused'}`} style={{
                        background: m.isScheduled ? 'rgba(34,197,94,0.12)' : 'rgba(234,179,8,0.12)',
                        color: m.isScheduled ? '#4ade80' : '#facc15',
                        padding: '4px 8px', borderRadius: '6px', fontSize: '0.75rem', fontWeight: 'bold'
                      }}>
                        {m.isScheduled ? 'Đã xếp' : 'Chưa xếp'}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </PtLayout>
  );
};

export default PtDashboard;
