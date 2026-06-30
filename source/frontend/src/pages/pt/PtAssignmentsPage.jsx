import React, { useState, useEffect } from 'react';
import PtLayout from '../../components/layout/PtLayout';
import assignmentService from '../../services/assignmentService';
import { Search } from 'lucide-react';
import './PtWorkoutManager.css';

const PtAssignmentsPage = () => {
  const [assignments, setAssignments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  const loadData = async () => {
    try {
      const data = await assignmentService.getAll();
      setAssignments(data);
    } catch (err) {
      console.error(err);
      alert('Lỗi tải danh sách phân công!');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, []);

  const handleStatusChange = async (id, status) => {
    try {
      await assignmentService.changeStatus(id, status);
      loadData(); // Tải lại để cập nhật
    } catch (err) {
      alert('Lỗi thay đổi trạng thái!');
    }
  };

  const filtered = assignments.filter(a => 
    !search || 
    a.memberName.toLowerCase().includes(search.toLowerCase()) || 
    a.planTitle.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <PtLayout>
      <div className="pt-workout-manager">
        <div className="topbar-actions">
          <div className="topbar-title">Quản lý phân công</div>
        </div>

        <div className="search-bar">
          <div className="search-wrap">
            <Search size={16} />
            <input 
              className="search-input" 
              placeholder="Tìm theo tên member hoặc lộ trình..." 
              value={search}
              onChange={e => setSearch(e.target.value)}
            />
          </div>
        </div>

        {loading ? (
          <div style={{ textAlign: 'center', padding: '40px', color: 'var(--text3)' }}>Đang tải...</div>
        ) : filtered.length === 0 ? (
           <div className="empty">
            <div className="empty-icon">👥</div>
            <div className="empty-title">Chưa có phân công nào</div>
          </div>
        ) : (
          <div style={{ background: 'var(--card)', border: '1px solid var(--border)', borderRadius: 'var(--radius)', overflow: 'hidden' }}>
            <table className="assignment-table">
              <thead>
                <tr>
                  <th>Member</th>
                  <th>Lộ trình</th>
                  <th>Bắt đầu</th>
                  <th>Trạng thái</th>
                  <th>Ghi chú</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {filtered.map(a => {
                  const sc = a.status === 'ACTIVE' ? '#10b981' : a.status === 'PAUSED' ? '#f59e0b' : '#94a3b8';
                  const sl = a.status === 'ACTIVE' ? 'Đang tập' : a.status === 'PAUSED' ? 'Tạm dừng' : 'Hoàn thành';
                  return (
                    <tr key={a.id}>
                      <td>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                          <div className="member-avatar" style={{ width: '30px', height: '30px', fontSize: '11px' }}>
                            {a.memberAvatar ? <img src={`http://localhost:8080/api/public/uploads/${a.memberAvatar}`} alt=""/> : a.memberName.charAt(0)}
                          </div>
                          <div>
                            <div style={{ fontWeight: '600', fontSize: '13px' }}>{a.memberName}</div>
                          </div>
                        </div>
                      </td>
                      <td style={{ fontWeight: '500' }}>{a.planTitle}</td>
                      <td style={{ color: 'var(--text2)', fontSize: '13px' }}>{a.startDate}</td>
                      <td>
                        <span style={{ display: 'inline-flex', alignItems: 'center' }}>
                          <span className="status-dot" style={{ background: sc }}></span>
                          <span style={{ fontSize: '13px' }}>{sl}</span>
                        </span>
                      </td>
                      <td style={{ color: 'var(--text2)', fontSize: '13px' }}>{a.note || '—'}</td>
                      <td>
                        <div style={{ display: 'flex', gap: '4px' }}>
                          {a.status === 'ACTIVE' && <button className="btn btn-sm btn-outline" onClick={() => handleStatusChange(a.id, 'PAUSED')}>Tạm dừng</button>}
                          {a.status === 'PAUSED' && <button className="btn btn-sm btn-outline" onClick={() => handleStatusChange(a.id, 'ACTIVE')}>Tiếp tục</button>}
                          {a.status !== 'COMPLETED' && <button className="btn btn-sm btn-outline" onClick={() => handleStatusChange(a.id, 'COMPLETED')}>Hoàn thành</button>}
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </PtLayout>
  );
};

export default PtAssignmentsPage;
