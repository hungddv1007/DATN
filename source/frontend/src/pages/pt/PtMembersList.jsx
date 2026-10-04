import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import PtLayout from '../../components/layout/PtLayout';
import ptDashboardService from '../../services/ptDashboardService';
import { Search, Eye, Calendar, Utensils, Users } from 'lucide-react';
import '../admin/AdminManagement.css';

const PtMembersList = () => {
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const navigate = useNavigate();

  const fetchMembers = useCallback(async () => {
    try {
      const data = await ptDashboardService.getAssignedMembers();
      setMembers(data);
    } catch (error) {
      console.error('Error fetching members:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchMembers();
  }, [fetchMembers]);

  const filteredMembers = members.filter(member =>
    member.memberName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    member.memberEmail?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    (member.memberPhone && member.memberPhone.includes(searchTerm))
  );

  return (
    <PtLayout>
      <div className="pt-members-list-page" style={{ padding: '20px' }}>
        <h1 style={{ fontSize: '1.8rem', fontWeight: '800', marginBottom: '8px' }}>Học Viên Của Tôi</h1>
        <p style={{ color: '#94a3b8', marginBottom: '24px' }}>Quản lý danh sách học viên và thiết lập lịch trình huấn luyện chi tiết.</p>

        {/* Toolbar */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', gap: '15px', flexWrap: 'wrap' }}>
          <div style={{
            display: 'flex', alignItems: 'center', gap: '10px',
            background: 'rgba(30,41,59,0.7)', border: '1px solid rgba(255,255,255,0.06)',
            borderRadius: '8px', padding: '8px 16px', flex: '1', maxWidth: '400px'
          }}>
            <Search size={18} style={{ color: '#64748b' }} />
            <input
              type="text"
              placeholder="Tìm kiếm theo tên, email, sđt..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              style={{
                background: 'transparent', border: 'none', color: '#f1f5f9',
                outline: 'none', flex: 1, fontSize: '0.95rem'
              }}
            />
          </div>
          <div style={{ color: '#94a3b8', fontSize: '0.9rem' }}>
            <Users size={16} style={{ verticalAlign: 'middle', marginRight: '6px' }} />
            Tổng: <strong style={{ color: '#f1f5f9' }}>{filteredMembers.length}</strong> học viên
          </div>
        </div>

        {/* Table */}
        <div className="admin-table-container" style={{ background: 'rgba(30,41,59,0.6)', borderRadius: '12px', border: '1px solid rgba(255,255,255,0.05)', overflow: 'hidden' }}>
          {loading ? (
            <div style={{ textAlign: 'center', padding: '40px', color: '#94a3b8' }}>Đang tải danh sách học viên...</div>
          ) : filteredMembers.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '40px', color: '#64748b' }}>
              {searchTerm ? 'Không tìm thấy học viên phù hợp.' : 'Chưa có học viên nào được giao cho bạn.'}
            </div>
          ) : (
            <table className="admin-table" style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr style={{ background: 'rgba(15,23,42,0.3)', borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                  <th style={{ padding: '16px', textAlign: 'left', color: '#94a3b8', fontSize: '0.85rem', textTransform: 'uppercase' }}>Họ tên</th>
                  <th style={{ padding: '16px', textAlign: 'left', color: '#94a3b8', fontSize: '0.85rem', textTransform: 'uppercase' }}>Liên hệ</th>
                  <th style={{ padding: '16px', textAlign: 'left', color: '#94a3b8', fontSize: '0.85rem', textTransform: 'uppercase' }}>Gói tập</th>
                  <th style={{ padding: '16px', textAlign: 'left', color: '#94a3b8', fontSize: '0.85rem', textTransform: 'uppercase' }}>Thời hạn</th>
                  <th style={{ padding: '16px', textAlign: 'left', color: '#94a3b8', fontSize: '0.85rem', textTransform: 'uppercase' }}>Trạng thái lịch</th>
                  <th style={{ padding: '16px', textAlign: 'center', color: '#94a3b8', fontSize: '0.85rem', textTransform: 'uppercase' }}>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {filteredMembers.map(member => (
                  <tr key={member.membershipId} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                    <td style={{ padding: '16px', fontWeight: '600', color: '#fff' }}>{member.memberName}</td>
                    <td style={{ padding: '16px' }}>
                      <div style={{ color: '#e2e8f0' }}>{member.memberEmail}</div>
                      <div style={{ fontSize: '0.85rem', color: '#64748b', marginTop: '2px' }}>{member.memberPhone || '—'}</div>
                    </td>
                    <td style={{ padding: '16px' }}>
                      <span className="status-badge" style={{ background: 'rgba(59,130,246,0.12)', color: '#60a5fa', padding: '4px 10px', borderRadius: '6px', fontSize: '0.8rem', fontWeight: 'bold' }}>
                        {member.packageName}
                      </span>
                    </td>
                    <td style={{ padding: '16px', fontSize: '0.9rem', color: '#cbd5e1' }}>
                      {member.startDate} – {member.endDate}
                    </td>
                    <td style={{ padding: '16px' }}>
                      <span className={`status-badge ${member.isScheduled ? 'active' : 'paused'}`} style={{
                        background: member.isScheduled ? 'rgba(34,197,94,0.12)' : 'rgba(234,179,8,0.12)',
                        color: member.isScheduled ? '#4ade80' : '#facc15',
                        padding: '4px 10px', borderRadius: '6px', fontSize: '0.8rem', fontWeight: 'bold'
                      }}>
                        {member.isScheduled ? '✓ Đã xếp lịch' : '⚠ Chưa xếp lịch'}
                      </span>
                    </td>
                    <td style={{ padding: '16px' }}>
                      <div style={{ display: 'flex', gap: '8px', justifyContent: 'center' }}>
                        <button 
                          className="btn-icon confirm" 
                          title="Xem chi tiết hồ sơ" 
                          onClick={() => navigate(`/pt/members/${member.memberId}`)}
                          style={{ background: 'rgba(255,255,255,0.05)', border: 'none', color: '#fff', padding: '6px', borderRadius: '6px', cursor: 'pointer' }}
                        >
                          <Eye size={16} />
                        </button>
                        
                        <button 
                          onClick={() => navigate(`/pt/schedule?memberId=${member.memberId}`)}
                          title={member.isScheduled ? "Sửa lịch trình huấn luyện" : "Xếp lịch trình huấn luyện"}
                          style={{
                            background: member.isScheduled ? 'rgba(255,255,255,0.05)' : '#f97316',
                            border: member.isScheduled ? '1px solid rgba(255,255,255,0.1)' : 'none',
                            color: '#fff', padding: '6px 12px', borderRadius: '6px', cursor: 'pointer',
                            display: 'flex', alignItems: 'center', gap: '5px', fontSize: '0.85rem', fontWeight: 'bold'
                          }}
                        >
                          <Calendar size={16} />
                          {member.isScheduled ? 'Sửa lịch' : 'Xếp lịch'}
                        </button>

                        <button 
                          title="Thiết lập / Xem khẩu phần ăn" 
                          onClick={() => navigate(`/pt/members/${member.memberId}?tab=diet`, { state: { tab: 'diet' } })}
                          style={{
                            background: 'rgba(249,115,22,0.12)',
                            border: '1px solid rgba(249,115,22,0.3)',
                            color: '#f97316', padding: '6px 10px', borderRadius: '6px', cursor: 'pointer',
                            display: 'flex', alignItems: 'center', gap: '4px', fontSize: '0.85rem', fontWeight: 'bold'
                          }}
                        >
                          <Utensils size={16} />
                        </button>
                      </div>
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

export default PtMembersList;
