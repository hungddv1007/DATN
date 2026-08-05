import React, { useState, useEffect } from 'react';
import AdminLayout from '../../components/layout/AdminLayout';
import promotionService from '../../services/promotionService';
import packageService from '../../services/packageService';
import { Edit, Trash2, Plus, Eye, EyeOff, Tag } from 'lucide-react';
import AdminPagination from '../../components/admin/AdminPagination';
import useClientPagination from '../../hooks/useClientPagination';
import './AdminManagement.css';

const PromotionsManagement = () => {
  const [promotions, setPromotions] = useState([]);
  const [packages, setPackages] = useState([]);
  const [loading, setLoading] = useState(true);
  const { page, setPage, totalPages, pageItems: visiblePromotions } = useClientPagination(promotions);
  
  // Modal state
  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState({
    code: '',
    discountPercent: 10,
    packageId: '', // rỗng = áp dụng cho mọi gói
    startDate: '',
    endDate: '',
    maxUsage: 100
  });

  const fetchData = async () => {
    setLoading(true);
    try {
      const [promoData, pkgData] = await Promise.all([
        promotionService.getAllPromotions(),
        packageService.getAllPackages(false)
      ]);
      setPromotions(promoData);
      setPackages(pkgData);
    } catch (error) {
      console.error('Lỗi tải dữ liệu:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleAddNew = () => {
    setEditingId(null);
    setFormData({
      code: '',
      discountPercent: 10,
      packageId: '',
      startDate: new Date().toISOString().split('T')[0],
      endDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0], // +30 days
      maxUsage: 100
    });
    setShowModal(true);
  };

  const handleEdit = (promo) => {
    setEditingId(promo.id);
    setFormData({
      code: promo.code,
      discountPercent: promo.discountPercent,
      packageId: promo.packageId || '',
      startDate: promo.startDate,
      endDate: promo.endDate,
      maxUsage: promo.maxUsage || ''
    });
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Bạn có chắc chắn muốn xóa vĩnh viễn khuyến mãi này? (Khuyến nghị: Chỉ nên Ẩn)')) return;
    try {
      await promotionService.deletePromotion(id);
      alert('Xóa thành công!');
      fetchData();
    } catch (error) {
      alert(error.response?.data?.message || 'Lỗi khi xóa khuyến mãi');
    }
  };

  const handleToggleStatus = async (id, currentStatus) => {
    const action = currentStatus ? 'Ẩn' : 'Hiện';
    if (!window.confirm(`Bạn có chắc chắn muốn ${action} khuyến mãi này?`)) return;
    try {
      await promotionService.togglePromotionStatus(id);
      alert(`${action} thành công!`);
      fetchData();
    } catch (error) {
      alert(error.response?.data?.message || `Lỗi khi ${action} khuyến mãi`);
    }
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        ...formData,
        packageId: formData.packageId ? parseInt(formData.packageId) : null,
        maxUsage: formData.maxUsage ? parseInt(formData.maxUsage) : null
      };

      if (editingId) {
        await promotionService.updatePromotion(editingId, payload);
        alert('Cập nhật thành công!');
      } else {
        await promotionService.createPromotion(payload);
        alert('Thêm mới thành công!');
      }
      setShowModal(false);
      fetchData();
    } catch (error) {
      alert(error.response?.data?.message || 'Có lỗi xảy ra');
    }
  };

  const getStatusBadgeClass = (status) => {
    switch (status) {
      case 'Đang diễn ra': return 'status-confirmed';
      case 'Đã kết thúc': return 'status-cancelled';
      case 'Sắp diễn ra': return 'status-pending';
      case 'Đã ẩn': return '';
      default: return 'status-pending';
    }
  };

  return (
    <AdminLayout>
      <div className="admin-page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h1>Quản lý Khuyến Mãi</h1>
        <button 
          onClick={handleAddNew}
          style={{
            display: 'flex', alignItems: 'center', gap: '8px',
            background: '#f97316', color: 'white', border: 'none',
            padding: '10px 20px', borderRadius: '8px', cursor: 'pointer', fontWeight: 'bold'
          }}
        >
          <Plus size={20} /> Tạo mã KM
        </button>
      </div>

      <div className="admin-table-container">
        <table className="admin-table">
          <thead>
            <tr>
              <th>Mã Code</th>
              <th>Giảm giá</th>
              <th>Áp dụng cho</th>
              <th>Hiệu lực</th>
              <th>Lượt dùng</th>
              <th>Trạng thái</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan="7" style={{ textAlign: 'center' }}>Đang tải...</td></tr>
            ) : promotions.length === 0 ? (
              <tr><td colSpan="7" style={{ textAlign: 'center' }}>Không có khuyến mãi nào</td></tr>
            ) : (
              visiblePromotions.map(promo => (
                <tr key={promo.id} style={{ opacity: promo.isActive ? 1 : 0.5 }}>
                  <td>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      <Tag size={16} color="#f97316" />
                      <strong style={{ color: '#f1f5f9', letterSpacing: '1px' }}>{promo.code}</strong>
                    </div>
                  </td>
                  <td style={{ color: '#ef4444', fontWeight: 'bold', fontSize: '1.1rem' }}>
                    {promo.discountPercent}%
                  </td>
                  <td>{promo.packageName}</td>
                  <td style={{ fontSize: '0.9rem' }}>
                    Từ: <span style={{ color: '#94a3b8' }}>{new Date(promo.startDate).toLocaleDateString('vi-VN')}</span><br/>
                    Đến: <span style={{ color: '#94a3b8' }}>{new Date(promo.endDate).toLocaleDateString('vi-VN')}</span>
                  </td>
                  <td>
                    <div style={{ background: '#1e293b', padding: '4px 8px', borderRadius: '4px', display: 'inline-block', fontSize: '0.85rem' }}>
                      <span style={{ color: '#38bdf8' }}>{promo.currentUsage || 0}</span> / 
                      <span style={{ color: '#94a3b8' }}> {promo.maxUsage || '∞'}</span>
                    </div>
                  </td>
                  <td>
                    <span className={`status-badge ${getStatusBadgeClass(promo.status)}`}
                          style={promo.status === 'Đã ẩn' ? { background: 'rgba(148,163,184,0.2)', color: '#94a3b8' } : {}}
                    >
                      {promo.status}
                    </span>
                  </td>
                  <td>
                    <div className="action-btns">
                      <button className="btn-icon" style={{ color: '#60a5fa', background: 'rgba(59,130,246,0.1)' }} onClick={() => handleEdit(promo)} title="Sửa">
                        <Edit size={18} />
                      </button>
                      <button className="btn-icon" style={{ color: '#eab308', background: 'rgba(234,179,8,0.1)' }} onClick={() => handleToggleStatus(promo.id, promo.isActive)} title={promo.isActive ? "Ẩn khuyến mãi" : "Hiện khuyến mãi"}>
                        {promo.isActive ? <EyeOff size={18} /> : <Eye size={18} />}
                      </button>
                      <button className="btn-icon cancel" onClick={() => handleDelete(promo.id)} title="Xóa vĩnh viễn">
                        <Trash2 size={18} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
        <AdminPagination page={page} totalPages={totalPages} onPageChange={setPage} />
      </div>

      {/* Modal Thêm/Sửa */}
      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)} style={{ backdropFilter: 'blur(8px)', zIndex: 10000 }}>
          <div className="modal-content" style={{ maxWidth: '600px', background: '#1e293b', border: '2px solid #3b82f6', boxShadow: '0 25px 50px rgba(0,0,0,0.5)' }} onClick={e => e.stopPropagation()}>
            <h2 style={{ marginBottom: '20px', color: '#ffffff', borderBottom: '1px solid rgba(255,255,255,0.2)', paddingBottom: '10px' }}>
              {editingId ? 'Sửa Khuyến Mãi' : 'Thêm Khuyến Mãi Mới'}
            </h2>
            
            <form onSubmit={handleSave} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
              <div style={{ display: 'flex', gap: '15px' }}>
                <div style={{ flex: 1 }}>
                  <label style={{ color: '#ffffff', fontWeight: 'bold', display: 'block', marginBottom: '8px' }}>Mã Code (VIẾT HOA)</label>
                  <input 
                    type="text" 
                    value={formData.code} 
                    onChange={e => setFormData({...formData, code: e.target.value.toUpperCase()})}
                    required 
                    placeholder="VD: SUMMER2026"
                    style={{ width: '100%', padding: '12px', background: 'rgba(15,23,42,0.6)', color: 'white', border: '1px solid rgba(255,255,255,0.2)', borderRadius: '8px' }}
                  />
                </div>
                <div style={{ width: '120px' }}>
                  <label style={{ color: '#ffffff', fontWeight: 'bold', display: 'block', marginBottom: '8px' }}>% Giảm giá</label>
                  <input 
                    type="number" 
                    min="1" max="100"
                    value={formData.discountPercent} 
                    onChange={e => setFormData({...formData, discountPercent: e.target.value})}
                    required 
                    style={{ width: '100%', padding: '12px', background: 'rgba(15,23,42,0.6)', color: 'white', border: '1px solid rgba(255,255,255,0.2)', borderRadius: '8px' }}
                  />
                </div>
              </div>

              <div>
                <label style={{ color: '#ffffff', fontWeight: 'bold', display: 'block', marginBottom: '8px' }}>Áp dụng cho gói tập</label>
                <select 
                  value={formData.packageId} 
                  onChange={e => setFormData({...formData, packageId: e.target.value})}
                  style={{ width: '100%', padding: '12px', background: 'rgba(15,23,42,0.6)', color: 'white', border: '1px solid rgba(255,255,255,0.2)', borderRadius: '8px' }}
                >
                  <option value="">Tất cả các gói</option>
                  {packages.map(pkg => (
                    <option key={pkg.id} value={pkg.id}>{pkg.name}</option>
                  ))}
                </select>
              </div>

              <div style={{ display: 'flex', gap: '15px' }}>
                <div style={{ flex: 1 }}>
                  <label style={{ color: '#ffffff', fontWeight: 'bold', display: 'block', marginBottom: '8px' }}>Ngày bắt đầu</label>
                  <input 
                    type="date" 
                    value={formData.startDate} 
                    onChange={e => setFormData({...formData, startDate: e.target.value})}
                    required 
                    style={{ width: '100%', padding: '12px', background: 'rgba(15,23,42,0.6)', color: 'white', border: '1px solid rgba(255,255,255,0.2)', borderRadius: '8px' }}
                  />
                </div>
                <div style={{ flex: 1 }}>
                  <label style={{ color: '#ffffff', fontWeight: 'bold', display: 'block', marginBottom: '8px' }}>Ngày kết thúc</label>
                  <input 
                    type="date" 
                    value={formData.endDate} 
                    onChange={e => setFormData({...formData, endDate: e.target.value})}
                    required 
                    style={{ width: '100%', padding: '12px', background: 'rgba(15,23,42,0.6)', color: 'white', border: '1px solid rgba(255,255,255,0.2)', borderRadius: '8px' }}
                  />
                </div>
              </div>

              <div>
                <label style={{ color: '#ffffff', fontWeight: 'bold', display: 'block', marginBottom: '8px' }}>Số lượt dùng tối đa (Để trống nếu không giới hạn)</label>
                <input 
                  type="number" 
                  min="1"
                  value={formData.maxUsage} 
                  onChange={e => setFormData({...formData, maxUsage: e.target.value})}
                  style={{ width: '100%', padding: '12px', background: 'rgba(15,23,42,0.6)', color: 'white', border: '1px solid rgba(255,255,255,0.2)', borderRadius: '8px' }}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '15px', marginTop: '20px' }}>
                <button type="button" onClick={() => setShowModal(false)} className="btn-cancel" style={{ padding: '12px 24px', fontWeight: 'bold', borderRadius: '8px' }}>Hủy</button>
                <button type="submit" className="btn-submit" style={{ padding: '12px 24px', fontWeight: 'bold', borderRadius: '8px', background: 'linear-gradient(to right, #f97316, #ea580c)', boxShadow: '0 4px 6px rgba(249, 115, 22, 0.3)' }}>Lưu thay đổi</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </AdminLayout>
  );
};

export default PromotionsManagement;
