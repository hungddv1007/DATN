import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import PtLayout from '../../components/layout/PtLayout';
import api from '../../services/api';
import { Plus, Edit2, Trash2, Copy, BookOpen, Search, UserPlus } from 'lucide-react';
import '../admin/AdminManagement.css';

const PtTemplatesPage = () => {
  const navigate = useNavigate();
  const [routes, setRoutes] = useState([]);
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editingRoute, setEditingRoute] = useState(null);
  const [formData, setFormData] = useState({ name: '', isTemplate: true });
  const [activeTab, setActiveTab] = useState('templates');

  // Assign modal
  const [showAssignModal, setShowAssignModal] = useState(false);
  const [assignRouteId, setAssignRouteId] = useState(null);

  useEffect(() => { fetchData(); }, []);

  const fetchData = async () => {
    try {
      const [routesRes, membersRes] = await Promise.all([
        api.get('/pt/training-routes'),
        api.get('/pt/members')
      ]);
      setRoutes(routesRes.data);
      setMembers(membersRes.data);
    } catch (err) {
      console.error('Lỗi tải dữ liệu:', err);
    } finally {
      setLoading(false);
    }
  };

  const templates = routes.filter(r => r.isTemplate || r.template);
  const assigned = routes.filter(r => !(r.isTemplate || r.template));

  const filteredTemplates = templates.filter(r =>
    r.name?.toLowerCase().includes(searchTerm.toLowerCase())
  );
  const filteredAssigned = assigned.filter(r =>
    r.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    r.memberName?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const openCreate = () => {
    setEditingRoute(null);
    setFormData({ name: '', isTemplate: true });
    setShowModal(true);
  };

  const openEdit = (route) => {
    setEditingRoute(route);
    setFormData({ name: route.name, isTemplate: route.isTemplate || route.template || false });
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingRoute) {
        await api.put(`/pt/training-routes/${editingRoute.id}`, formData);
      } else {
        await api.post('/pt/training-routes', formData);
      }
      setShowModal(false);
      fetchData();
    } catch (err) {
      alert(err.response?.data?.message || 'Có lỗi xảy ra');
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Bạn có chắc muốn xóa lộ trình này?')) return;
    try {
      await api.delete(`/pt/training-routes/${id}`);
      fetchData();
    } catch (err) {
      alert(err.response?.data?.message || 'Có lỗi xảy ra');
    }
  };

  const handleClone = async (id) => {
    try {
      await api.post(`/pt/training-routes/${id}/clone`);
      fetchData();
    } catch (err) {
      alert(err.response?.data?.message || 'Có lỗi xảy ra');
    }
  };

  const openAssignModal = (routeId) => {
    setAssignRouteId(routeId);
    setShowAssignModal(true);
  };

  const handleAssign = async (memberId) => {
    try {
      await api.post(`/pt/training-routes/${assignRouteId}/assign/${memberId}`);
      setShowAssignModal(false);
      setAssignRouteId(null);
      fetchData();
    } catch (err) {
      alert(err.response?.data?.message || 'Có lỗi xảy ra');
    }
  };

  const handleComplete = async (routeId) => {
    if (!window.confirm('Đánh dấu lộ trình này là hoàn thành?')) return;
    try {
      await api.put(`/pt/training-routes/${routeId}/complete`);
      fetchData();
    } catch (err) {
      alert(err.response?.data?.message || 'Có lỗi xảy ra');
    }
  };

  const currentData = activeTab === 'templates' ? filteredTemplates : filteredAssigned;

  return (
    <PtLayout>
      <h1>Quản Lý Lộ Trình</h1>
      <p>Tạo lộ trình mẫu và gán cho học viên.</p>

      {/* Toolbar */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', flexWrap: 'wrap', gap: '12px' }}>
        <div style={{
          display: 'flex', alignItems: 'center', gap: '10px',
          background: 'rgba(30,41,59,0.7)', border: '1px solid rgba(255,255,255,0.06)',
          borderRadius: '8px', padding: '8px 16px', minWidth: '280px'
        }}>
          <Search size={18} style={{ color: '#64748b' }} />
          <input type="text" placeholder="Tìm kiếm lộ trình..." value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{ background: 'transparent', border: 'none', color: '#f1f5f9', outline: 'none', flex: 1, fontSize: '0.95rem' }}
          />
        </div>
        <button className="btn-primary-action" onClick={openCreate}>
          <Plus size={20} /> Tạo lộ trình mới
        </button>
      </div>

      {/* Tabs */}
      <div className="tab-buttons">
        <button className={`tab-btn ${activeTab === 'templates' ? 'active' : ''}`} onClick={() => setActiveTab('templates')}>
          Lộ trình mẫu ({templates.length})
        </button>
        <button className={`tab-btn ${activeTab === 'assigned' ? 'active' : ''}`} onClick={() => setActiveTab('assigned')}>
          Đã gán cho học viên ({assigned.length})
        </button>
      </div>

      {/* Table */}
      <div className="admin-table-container">
        {loading ? (
          <div style={{ textAlign: 'center', padding: '40px', color: '#94a3b8' }}>Đang tải...</div>
        ) : (
          <table className="admin-table">
            <thead>
              <tr>
                <th>Tên lộ trình</th>
                {activeTab === 'assigned' && <th>Học viên</th>}
                <th>Số buổi</th>
                <th>Trạng thái</th>
                <th>Ngày tạo</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {currentData.length === 0 ? (
                <tr><td colSpan={activeTab === 'assigned' ? 6 : 5} style={{ textAlign: 'center', color: '#64748b', padding: '40px' }}>
                  {searchTerm ? 'Không tìm thấy.' : (activeTab === 'templates' ? 'Chưa có lộ trình mẫu nào. Nhấn "Tạo lộ trình mới" để bắt đầu.' : 'Chưa gán lộ trình nào cho học viên.')}
                </td></tr>
              ) : (
                currentData.map(route => (
                  <tr key={route.id}>
                    <td style={{ fontWeight: '500' }}>
                      <BookOpen size={16} style={{ color: '#f97316', verticalAlign: 'middle', marginRight: '8px' }} />
                      <span onClick={() => navigate(`/pt/templates/${route.id}`)} style={{ cursor: 'pointer', transition: 'color 0.2s' }}
                        onMouseEnter={e => e.target.style.color = '#f97316'}
                        onMouseLeave={e => e.target.style.color = 'inherit'}>
                        {route.name}
                      </span>
                    </td>
                    {activeTab === 'assigned' && <td>{route.memberName || '—'}</td>}
                    <td>{route.totalSessions || 0} buổi</td>
                    <td>
                      <span className={`status-badge ${
                        route.status === 'TEMPLATE' ? 'status-pending' :
                        route.status === 'ASSIGNED' ? 'status-confirmed' :
                        route.status === 'COMPLETED' ? 'status-confirmed' : 'status-cancelled'
                      }`}>
                        {route.status === 'TEMPLATE' ? 'Mẫu' :
                         route.status === 'ASSIGNED' ? 'Đang tập' :
                         route.status === 'COMPLETED' ? 'Hoàn thành' : route.status}
                      </span>
                    </td>
                    <td style={{ fontSize: '0.9rem' }}>
                      {route.createdAt ? new Date(route.createdAt).toLocaleDateString('vi-VN') : '—'}
                    </td>
                    <td>
                      <div className="action-btns">
                        {activeTab === 'templates' && (
                          <button className="btn-icon" title="Gán cho học viên" onClick={() => openAssignModal(route.id)}
                            style={{ color: '#10b981', background: 'rgba(16,185,129,0.1)' }}>
                            <UserPlus size={16} />
                          </button>
                        )}
                        {activeTab === 'assigned' && route.status === 'ASSIGNED' && (
                          <button className="btn-icon confirm" title="Hoàn thành" onClick={() => handleComplete(route.id)}>
                            ✓
                          </button>
                        )}
                        <button className="btn-icon" title="Sửa" onClick={() => openEdit(route)} style={{ color: '#3b82f6', background: 'rgba(59,130,246,0.1)' }}><Edit2 size={16} /></button>
                        <button className="btn-icon" title="Nhân bản" onClick={() => handleClone(route.id)} style={{ color: '#a855f7', background: 'rgba(168,85,247,0.1)' }}><Copy size={16} /></button>
                        <button className="btn-icon cancel" title="Xóa" onClick={() => handleDelete(route.id)}><Trash2 size={16} /></button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        )}
      </div>

      {/* Modal: Tạo/Sửa */}
      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <h2>{editingRoute ? 'Sửa Lộ Trình' : 'Tạo Lộ Trình Mới'}</h2>
            <form onSubmit={handleSubmit}>
              <div>
                <label>Tên lộ trình</label>
                <input type="text" value={formData.name} onChange={e => setFormData({ ...formData, name: e.target.value })}
                  required placeholder="VD: Giảm mỡ 8 tuần" />
              </div>
              <div>
                <label>Loại</label>
                <select value={formData.isTemplate ? 'true' : 'false'} onChange={e => setFormData({ ...formData, isTemplate: e.target.value === 'true' })}>
                  <option value="true">Lộ trình mẫu (Template)</option>
                  <option value="false">Lộ trình thường</option>
                </select>
              </div>
              <div style={{ display: 'flex', gap: '12px', marginTop: '10px' }}>
                <button type="submit" className="btn-submit">{editingRoute ? 'Cập nhật' : 'Tạo mới'}</button>
                <button type="button" className="btn-cancel" onClick={() => setShowModal(false)}>Hủy</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal: Gán lộ trình cho học viên */}
      {showAssignModal && (
        <div className="modal-overlay" onClick={() => setShowAssignModal(false)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <h2>Gán Lộ Trình Cho Học Viên</h2>
            {members.length === 0 ? (
              <div style={{ color: '#94a3b8', textAlign: 'center', padding: '20px' }}>
                Chưa có học viên nào được giao cho bạn.
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                {members.map(m => (
                  <div key={m.memberId} style={{
                    display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                    padding: '12px 16px', background: 'rgba(15,23,42,0.5)',
                    border: '1px solid rgba(255,255,255,0.08)', borderRadius: '8px',
                    transition: 'border-color 0.2s'
                  }}
                  onMouseEnter={e => e.currentTarget.style.borderColor = 'rgba(249,115,22,0.4)'}
                  onMouseLeave={e => e.currentTarget.style.borderColor = 'rgba(255,255,255,0.08)'}
                  >
                    <div>
                      <div style={{ color: '#f1f5f9', fontWeight: '500' }}>{m.memberName}</div>
                      <div style={{ color: '#64748b', fontSize: '0.85rem', marginTop: '2px' }}>{m.packageName}</div>
                    </div>
                    <button className="btn-submit" onClick={() => handleAssign(m.memberId)}
                      style={{ padding: '6px 14px', fontSize: '0.85rem' }}>
                      Gán
                    </button>
                  </div>
                ))}
              </div>
            )}
            <div style={{ marginTop: '20px', textAlign: 'right' }}>
              <button className="btn-cancel" onClick={() => setShowAssignModal(false)}>Đóng</button>
            </div>
          </div>
        </div>
      )}
    </PtLayout>
  );
};

export default PtTemplatesPage;
