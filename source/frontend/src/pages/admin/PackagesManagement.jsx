import React, { useState, useEffect } from 'react';
import AdminLayout from '../../components/layout/AdminLayout';
import packageService from '../../services/packageService';
import { Edit, Trash2, Plus, Eye, EyeOff } from 'lucide-react';
import './AdminManagement.css';

const PackagesManagement = () => {
  const [packages, setPackages] = useState([]);
  const [loading, setLoading] = useState(true);

  // Modal state
  const [showModal, setShowModal] = useState(false);
  const [editingPkg, setEditingPkg] = useState(null);
  
  // Form state
  const [formData, setFormData] = useState({
    name: '',
    dailyPrice: '',
    minDays: '',
    maxHoldTimes: 1,
    holdReturnPercent: 80,
    description: '',
    hasPt: false,
    canChoosePt: false,
    hasMealPlan: false
  });

  const fetchPackages = async () => {
    setLoading(true);
    try {
      const data = await packageService.getAllPackages(false); // Admin gets all (including hidden)
      setPackages(data);
    } catch (error) {
      console.error('Lỗi tải gói tập:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPackages();
  }, []);

  const handleAddNew = () => {
    setEditingPkg(null);
    setFormData({
      name: '', dailyPrice: '', minDays: '', maxHoldTimes: 1, holdReturnPercent: 80, description: '',
      hasPt: false, canChoosePt: false, hasMealPlan: false
    });
    setShowModal(true);
  };

  const handleEdit = (pkg) => {
    setEditingPkg(pkg.id);
    setFormData({ ...pkg });
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Bạn có chắc chắn muốn xóa vĩnh viễn gói tập này? (Khuyến nghị: Chỉ nên Ẩn đi)')) return;
    try {
      await packageService.deletePackage(id);
      alert('Xóa thành công!');
      fetchPackages();
    } catch (error) {
      alert(error.response?.data?.message || 'Lỗi khi xóa gói tập');
    }
  };

  const handleToggleStatus = async (id, currentStatus) => {
    const action = currentStatus ? 'Ẩn' : 'Hiện';
    if (!window.confirm(`Bạn có chắc chắn muốn ${action} gói tập này?`)) return;
    try {
      await packageService.togglePackageStatus(id);
      alert(`${action} thành công!`);
      fetchPackages();
    } catch (error) {
      alert(error.response?.data?.message || `Lỗi khi ${action} gói tập`);
    }
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingPkg) {
        await packageService.updatePackage(editingPkg, formData);
        alert('Cập nhật thành công!');
      } else {
        await packageService.createPackage(formData);
        alert('Thêm mới thành công!');
      }
      setShowModal(false);
      fetchPackages();
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

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
  };

  return (
    <AdminLayout>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h1>Quản lý Gói Tập</h1>
        <button 
          onClick={handleAddNew}
          style={{
            display: 'flex', alignItems: 'center', gap: '8px',
            background: '#f97316', color: 'white', border: 'none',
            padding: '10px 20px', borderRadius: '8px', cursor: 'pointer', fontWeight: 'bold'
          }}
        >
          <Plus size={20} /> Thêm gói mới
        </button>
      </div>

      <div className="admin-table-container">
        <table className="admin-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Tên Gói</th>
              <th>Giá Tiền/Ngày</th>
              <th>Tối thiểu</th>
              <th>Bảo lưu</th>
              <th>Tính năng</th>
              <th>Trạng thái</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan="8" style={{ textAlign: 'center' }}>Đang tải...</td></tr>
            ) : packages.length === 0 ? (
              <tr><td colSpan="8" style={{ textAlign: 'center' }}>Không có gói tập nào</td></tr>
            ) : (
              packages.map(pkg => (
                <tr key={pkg.id} style={{ opacity: pkg.isActive ? 1 : 0.5 }}>
                  <td>{pkg.id}</td>
                  <td style={{ fontWeight: 'bold', color: '#f1f5f9' }}>{pkg.name}</td>
                  <td style={{ color: '#f97316', fontWeight: 'bold' }}>{formatCurrency(pkg.dailyPrice)}</td>
                  <td>{pkg.minDays} ngày</td>
                  <td>{pkg.maxHoldTimes} lần ({pkg.holdReturnPercent}%)</td>
                  <td>
                    <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                      {pkg.hasPt && <span className="status-badge" style={{ background: 'rgba(59,130,246,0.2)', color: '#60a5fa' }}>PT Kèm</span>}
                      {pkg.canChoosePt && <span className="status-badge" style={{ background: 'rgba(168,85,247,0.2)', color: '#c084fc' }}>Chọn PT</span>}
                      {pkg.hasMealPlan && <span className="status-badge" style={{ background: 'rgba(34,197,94,0.2)', color: '#4ade80' }}>Dinh dưỡng</span>}
                    </div>
                  </td>
                  <td>
                    {pkg.isActive 
                      ? <span className="status-badge status-confirmed">Đang hiện</span> 
                      : <span className="status-badge" style={{ background: 'rgba(148,163,184,0.2)', color: '#94a3b8' }}>Đã ẩn</span>
                    }
                  </td>
                  <td>
                    <div className="action-btns">
                      <button className="btn-icon" style={{ color: '#60a5fa', background: 'rgba(59,130,246,0.1)' }} onClick={() => handleEdit(pkg)} title="Sửa">
                        <Edit size={18} />
                      </button>
                      <button className="btn-icon" style={{ color: '#eab308', background: 'rgba(234,179,8,0.1)' }} onClick={() => handleToggleStatus(pkg.id, pkg.isActive)} title={pkg.isActive ? "Ẩn gói này" : "Hiện gói này"}>
                        {pkg.isActive ? <EyeOff size={18} /> : <Eye size={18} />}
                      </button>
                      <button className="btn-icon cancel" onClick={() => handleDelete(pkg.id)} title="Xóa vĩnh viễn">
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
            background: '#1e293b', padding: '30px', borderRadius: '12px', width: '90%', maxWidth: '600px',
            border: '2px solid #3b82f6', boxShadow: '0 25px 50px rgba(0,0,0,0.5)'
          }}>
            <h2 style={{ color: '#ffffff', marginBottom: '20px', borderBottom: '1px solid rgba(255,255,255,0.2)', paddingBottom: '10px' }}>
              {editingPkg ? 'Cập nhật Gói Tập' : 'Thêm Gói Tập Mới'}
            </h2>
            <form onSubmit={handleSave} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
              <div>
                <label style={{ display: 'block', marginBottom: '5px', color: '#ffffff', fontWeight: 'bold' }}>Tên gói</label>
                <input 
                  required
                  type="text" 
                  value={formData.name} 
                  onChange={e => setFormData({...formData, name: e.target.value})}
                  style={{ width: '100%', padding: '12px', background: 'rgba(15,23,42,0.6)', border: '1px solid rgba(255,255,255,0.2)', color: 'white', borderRadius: '8px' }}
                />
              </div>
              
              <div style={{ display: 'flex', gap: '15px' }}>
                <div style={{ flex: 1 }}>
                  <label style={{ display: 'block', marginBottom: '5px', color: '#ffffff', fontWeight: 'bold' }}>Giá/Ngày (VNĐ)</label>
                  <input 
                    required
                    type="number" 
                    value={formData.dailyPrice} 
                    onChange={e => setFormData({...formData, dailyPrice: e.target.value})}
                    style={{ width: '100%', padding: '12px', background: 'rgba(15,23,42,0.6)', border: '1px solid rgba(255,255,255,0.2)', color: 'white', borderRadius: '8px' }}
                  />
                </div>
                <div style={{ flex: 1 }}>
                  <label style={{ display: 'block', marginBottom: '5px', color: '#ffffff', fontWeight: 'bold' }}>Ngày tối thiểu</label>
                  <input 
                    required
                    type="number" 
                    value={formData.minDays} 
                    onChange={e => setFormData({...formData, minDays: e.target.value})}
                    style={{ width: '100%', padding: '12px', background: 'rgba(15,23,42,0.6)', border: '1px solid rgba(255,255,255,0.2)', color: 'white', borderRadius: '8px' }}
                  />
                </div>
              </div>

              <div style={{ display: 'flex', gap: '15px' }}>
                <div style={{ flex: 1 }}>
                  <label style={{ display: 'block', marginBottom: '5px', color: '#ffffff', fontWeight: 'bold' }}>Số lần bảo lưu tối đa</label>
                  <input 
                    required
                    type="number" 
                    value={formData.maxHoldTimes} 
                    onChange={e => setFormData({...formData, maxHoldTimes: e.target.value})}
                    style={{ width: '100%', padding: '12px', background: 'rgba(15,23,42,0.6)', border: '1px solid rgba(255,255,255,0.2)', color: 'white', borderRadius: '8px' }}
                  />
                </div>
                <div style={{ flex: 1 }}>
                  <label style={{ display: 'block', marginBottom: '5px', color: '#ffffff', fontWeight: 'bold' }}>Phần trăm hoàn ({'%'})</label>
                  <input 
                    required
                    type="number" 
                    value={formData.holdReturnPercent} 
                    onChange={e => setFormData({...formData, holdReturnPercent: e.target.value})}
                    style={{ width: '100%', padding: '12px', background: 'rgba(15,23,42,0.6)', border: '1px solid rgba(255,255,255,0.2)', color: 'white', borderRadius: '8px' }}
                  />
                </div>
              </div>

              <div>
                <label style={{ display: 'block', marginBottom: '5px', color: '#ffffff', fontWeight: 'bold' }}>Mô tả</label>
                <textarea 
                  value={formData.description} 
                  onChange={e => setFormData({...formData, description: e.target.value})}
                  style={{ width: '100%', padding: '12px', background: 'rgba(15,23,42,0.6)', border: '1px solid rgba(255,255,255,0.2)', color: 'white', borderRadius: '8px', height: '80px' }}
                />
              </div>

              <div style={{ display: 'flex', gap: '20px', marginTop: '10px' }}>
                <label style={{ display: 'flex', alignItems: 'center', gap: '8px', color: '#ffffff', fontWeight: 'bold' }}>
                  <input type="checkbox" checked={formData.hasPt} onChange={e => setFormData({...formData, hasPt: e.target.checked})} />
                  Có PT hướng dẫn
                </label>
                <label style={{ display: 'flex', alignItems: 'center', gap: '8px', color: '#ffffff', fontWeight: 'bold' }}>
                  <input type="checkbox" checked={formData.canChoosePt} onChange={e => setFormData({...formData, canChoosePt: e.target.checked})} />
                  Được chọn PT
                </label>
                <label style={{ display: 'flex', alignItems: 'center', gap: '8px', color: '#ffffff', fontWeight: 'bold' }}>
                  <input type="checkbox" checked={formData.hasMealPlan} onChange={e => setFormData({...formData, hasMealPlan: e.target.checked})} />
                  Có Meal Plan
                </label>
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

export default PackagesManagement;
