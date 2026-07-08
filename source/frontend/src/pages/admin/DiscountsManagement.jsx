import React, { useState, useEffect } from 'react';
import AdminLayout from '../../components/layout/AdminLayout';
import discountService from '../../services/discountService';
import packageService from '../../services/packageService';
import { Edit, Trash2, Plus } from 'lucide-react';
import './AdminManagement.css';

const DiscountsManagement = () => {
  const [discounts, setDiscounts] = useState([]);
  const [packages, setPackages] = useState([]);
  const [loading, setLoading] = useState(true);

  // Modal state
  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState(null);
  
  // Form state
  const [formData, setFormData] = useState({
    packageId: '', // rỗng = áp dụng cho tất cả
    minDays: '',
    discountPercent: ''
  });

  const fetchData = async () => {
    setLoading(true);
    try {
      const [discountsData, packagesData] = await Promise.all([
        discountService.getAllDiscounts(),
        packageService.getAllPackages(false)
      ]);
      setDiscounts(discountsData);
      setPackages(packagesData);
    } catch (error) {
      console.error('Lỗi tải chiết khấu:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleAddNew = () => {
    setEditingId(null);
    setFormData({ packageId: '', minDays: '', discountPercent: '' });
    setShowModal(true);
  };

  const handleEdit = (d) => {
    setEditingId(d.id);
    setFormData({ 
      packageId: d.packageId || '', 
      minDays: d.minDays, 
      discountPercent: d.discountPercent 
    });
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Bạn có chắc chắn muốn xóa chiết khấu này?')) return;
    try {
      await discountService.deleteDiscount(id);
      alert('Xóa thành công!');
      fetchData();
    } catch (error) {
      alert(error.response?.data?.message || 'Lỗi khi xóa chiết khấu');
    }
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        packageId: formData.packageId ? parseInt(formData.packageId) : null,
        minDays: parseInt(formData.minDays),
        discountPercent: parseFloat(formData.discountPercent)
      };

      if (editingId) {
        await discountService.updateDiscount(editingId, payload);
        alert('Cập nhật thành công!');
      } else {
        await discountService.createDiscount(payload);
        alert('Thêm mới thành công!');
      }
      setShowModal(false);
      fetchData();
    } catch (error) {
      const resData = error.response?.data;
      if (resData && typeof resData === 'object') {
        const firstError = resData.message || Object.values(resData)[0];
        alert(firstError || 'Có lỗi xảy ra');
      } else {
        alert('Lỗi kết nối máy chủ!');
      }
    }
  };

  const getPackageName = (pkgId) => {
    if (!pkgId) return 'TẤT CẢ CÁC GÓI';
    const pkg = packages.find(p => p.id === pkgId);
    return pkg ? pkg.name : `Gói ID: ${pkgId}`;
  };

  return (
    <AdminLayout>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h1>Quản lý Chiết Khấu Gia Hạn</h1>
        <button 
          onClick={handleAddNew}
          style={{
            display: 'flex', alignItems: 'center', gap: '8px',
            background: '#f97316', color: 'white', border: 'none',
            padding: '10px 20px', borderRadius: '8px', cursor: 'pointer', fontWeight: 'bold'
          }}
        >
          <Plus size={20} /> Thêm chiết khấu
        </button>
      </div>

      <div className="admin-table-container">
        <table className="admin-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Áp dụng cho</th>
              <th>Số ngày tối thiểu</th>
              <th>Chiết khấu (%)</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan="5" style={{ textAlign: 'center' }}>Đang tải...</td></tr>
            ) : discounts.length === 0 ? (
              <tr><td colSpan="5" style={{ textAlign: 'center' }}>Không có chiết khấu nào</td></tr>
            ) : (
              discounts.map(d => (
                <tr key={d.id}>
                  <td>{d.id}</td>
                  <td style={{ fontWeight: 'bold', color: d.packageId ? '#60a5fa' : '#4ade80' }}>
                    {getPackageName(d.packageId)}
                  </td>
                  <td style={{ color: '#f1f5f9' }}>{d.minDays} ngày</td>
                  <td style={{ color: '#f97316', fontWeight: 'bold' }}>{d.discountPercent}%</td>
                  <td>
                    <div className="action-btns">
                      <button className="btn-icon" style={{ color: '#60a5fa', background: 'rgba(59,130,246,0.1)' }} onClick={() => handleEdit(d)} title="Sửa">
                        <Edit size={18} />
                      </button>
                      <button className="btn-icon cancel" onClick={() => handleDelete(d.id)} title="Xóa">
                        <Trash2 size={18} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Modal Form */}
      {showModal && (
        <div style={{
          position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
          background: 'rgba(15, 23, 42, 0.8)', backdropFilter: 'blur(8px)', zIndex: 1000,
          display: 'flex', alignItems: 'center', justifyContent: 'center'
        }} onClick={() => setShowModal(false)}>
          <div onClick={e => e.stopPropagation()} style={{
            background: '#1e293b', padding: '30px', borderRadius: '12px', width: '90%', maxWidth: '500px',
            border: '2px solid #3b82f6', boxShadow: '0 25px 50px rgba(0,0,0,0.5)'
          }}>
            <h2 style={{ color: '#ffffff', marginBottom: '20px', borderBottom: '1px solid rgba(255,255,255,0.2)', paddingBottom: '10px' }}>
              {editingId ? 'Cập nhật Chiết khấu' : 'Thêm Chiết khấu'}
            </h2>
            <form onSubmit={handleSave} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
              
              <div>
                <label style={{ display: 'block', marginBottom: '5px', color: '#ffffff', fontWeight: 'bold' }}>Áp dụng cho gói tập</label>
                <select 
                  value={formData.packageId} 
                  onChange={e => setFormData({...formData, packageId: e.target.value})}
                  style={{ width: '100%', padding: '12px', background: 'rgba(15,23,42,0.6)', border: '1px solid rgba(255,255,255,0.2)', color: 'white', borderRadius: '8px' }}
                >
                  <option value="">-- TẤT CẢ CÁC GÓI --</option>
                  {packages.map(p => (
                    <option key={p.id} value={p.id}>{p.name}</option>
                  ))}
                </select>
              </div>

              <div>
                <label style={{ display: 'block', marginBottom: '5px', color: '#ffffff', fontWeight: 'bold' }}>Số ngày tối thiểu để được giảm</label>
                <input 
                  required
                  type="number" 
                  min="1"
                  value={formData.minDays} 
                  onChange={e => setFormData({...formData, minDays: e.target.value})}
                  style={{ width: '100%', padding: '12px', background: 'rgba(15,23,42,0.6)', border: '1px solid rgba(255,255,255,0.2)', color: 'white', borderRadius: '8px' }}
                />
              </div>

              <div>
                <label style={{ display: 'block', marginBottom: '5px', color: '#ffffff', fontWeight: 'bold' }}>Phần trăm giảm giá (%)</label>
                <input 
                  required
                  type="number" 
                  min="0" max="100" step="0.1"
                  value={formData.discountPercent} 
                  onChange={e => setFormData({...formData, discountPercent: e.target.value})}
                  style={{ width: '100%', padding: '12px', background: 'rgba(15,23,42,0.6)', border: '1px solid rgba(255,255,255,0.2)', color: 'white', borderRadius: '8px' }}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '15px', marginTop: '20px' }}>
                <button type="button" onClick={() => setShowModal(false)} style={{ padding: '12px 24px', background: 'transparent', border: '1px solid #475569', color: 'white', borderRadius: '8px', cursor: 'pointer', fontWeight: 'bold' }}>Hủy</button>
                <button type="submit" style={{ padding: '12px 24px', background: 'linear-gradient(to right, #f97316, #ea580c)', boxShadow: '0 4px 6px rgba(249, 115, 22, 0.3)', border: 'none', color: 'white', borderRadius: '8px', cursor: 'pointer', fontWeight: 'bold' }}>Lưu thay đổi</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </AdminLayout>
  );
};

export default DiscountsManagement;
