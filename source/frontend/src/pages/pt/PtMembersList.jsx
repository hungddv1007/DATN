import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import PtLayout from '../../components/layout/PtLayout';
import api from '../../services/api';
import { Search, Eye, ClipboardList, Users } from 'lucide-react';
import '../admin/AdminManagement.css';

const PtMembersList = () => {
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    fetchMembers();
  }, []);

  const fetchMembers = async () => {
    try {
      const response = await api.get('/pt/members');
      setMembers(response.data);
    } catch (error) {
      console.error('Error fetching members:', error);
    } finally {
      setLoading(false);
    }
  };

  const filteredMembers = members.filter(member =>
    member.memberName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    member.memberEmail?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    (member.memberPhone && member.memberPhone.includes(searchTerm))
  );

  return (
    <PtLayout>
      <h1>Học Viên Của Tôi</h1>
      <p>Quản lý danh sách học viên được giao cho bạn.</p>

      {/* Toolbar */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
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
      <div className="admin-table-container">
        {loading ? (
          <div style={{ textAlign: 'center', padding: '40px', color: '#94a3b8' }}>Đang tải danh sách...</div>
        ) : filteredMembers.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '40px', color: '#64748b' }}>
            {searchTerm ? 'Không tìm thấy học viên phù hợp.' : 'Chưa có học viên nào được giao cho bạn.'}
          </div>
        ) : (
          <table className="admin-table">
            <thead>
              <tr>
                <th>Họ tên</th>
                <th>Liên hệ</th>
                <th>Gói tập</th>
                <th>Thời hạn</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {filteredMembers.map(member => (
                <tr key={member.membershipId}>
                  <td style={{ fontWeight: '500' }}>{member.memberName}</td>
                  <td>
                    <div>{member.memberEmail}</div>
                    <div style={{ fontSize: '0.85rem', color: '#64748b', marginTop: '2px' }}>{member.memberPhone || '—'}</div>
                  </td>
                  <td>
                    <span className="status-badge status-pending" style={{ background: 'rgba(59,130,246,0.15)', color: '#93c5fd', borderColor: 'rgba(59,130,246,0.3)' }}>
                      {member.packageName}
                    </span>
                  </td>
                  <td style={{ fontSize: '0.9rem' }}>
                    {new Date(member.startDate).toLocaleDateString('vi-VN')} – {new Date(member.endDate).toLocaleDateString('vi-VN')}
                  </td>
                  <td>
                    <span className={`status-badge ${member.status === 'ACTIVE' ? 'status-confirmed' : 'status-cancelled'}`}>
                      {member.status === 'ACTIVE' ? 'Đang hoạt động' : member.status}
                    </span>
                  </td>
                  <td>
                    <div className="action-btns">
                      <button className="btn-icon confirm" title="Xem chi tiết" onClick={() => navigate(`/pt/members/${member.memberId}`)}>
                        <Eye size={18} />
                      </button>

                    </div>
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

export default PtMembersList;
