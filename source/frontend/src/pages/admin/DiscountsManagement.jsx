import React, { useState, useEffect } from 'react';
import AdminLayout from '../../components/layout/AdminLayout';
import packageService from '../../services/packageService';
import { Tag, Plus, Edit2, Trash2 } from 'lucide-react';
import './DiscountsManagement.css';

// Các mốc thời gian cố định
const DURATION_MILESTONES = [
  { days: 1,    label: '1 ngày' },
  { days: 7,    label: '1 tuần' },
  { days: 30,   label: '1 tháng' },
  { days: 90,   label: '3 tháng' },
  { days: 180,  label: '6 tháng' },
  { days: 365,  label: '1 năm' },
  { days: 730,  label: '2 năm' },
];

const getDurationLabel = (days) => {
  const found = DURATION_MILESTONES.find(m => m.days === days);
  return found ? found.label : `${days} ngày`;
};

const DiscountsManagement = () => {
  const [discounts, setDiscounts] = useState([]);
  const [packages, setPackages] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Form / Modal state
  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState(null);
  
  const [pkgId, setPkgId] = useState('');
  const [minDays, setMinDays] = useState(90);
  const [discountPercent, setDiscountPercent] = useState(5);
  const [submitting, setSubmitting] = useState(false);

  const loadData = async () => {
    try {
      const discountData = await packageService.getAdminDiscounts();
      setDiscounts(discountData);

      const pkgData = await packageService.getAllPackages(false);
      setPackages(pkgData);
    } catch (err) {
      setError('Lỗi tải dữ liệu chiết khấu');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleOpenAdd = () => {
    setEditingId(null);
    setPkgId('');
    setMinDays(90);
    setDiscountPercent(5);
    setError('');
    setSuccess('');
    setShowModal(true);
  };

  const handleOpenEdit = (discount) => {
    setEditingId(discount.id);
    setPkgId(discount.packageId || '');
    setMinDays(discount.minDays);
    setDiscountPercent(discount.discountPercent);
    setError('');
    setSuccess('');
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Bạn có chắc chắn muốn xóa mốc chiết khấu này không?")) return;
    try {
      await packageService.deleteDiscount(id);
      setSuccess('Xóa mốc chiết khấu thành công');
      loadData();
    } catch (err) {
      setError(err.response?.data?.message || 'Xóa thất bại');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError('');
    setSuccess('');

    const payload = {
      packageId: pkgId ? parseInt(pkgId) : null,
      minDays: parseInt(minDays),
      discountPercent: parseInt(discountPercent)
    };

    try {
      if (editingId) {
        await packageService.updateDiscount(editingId, payload);
        setSuccess('Cập nhật mốc chiết khấu thành công');
      } else {
        await packageService.createDiscount(payload);
        setSuccess('Thêm mốc chiết khấu thành công');
      }
      setShowModal(false);
      loadData();
    } catch (err) {
      setError(err.response?.data?.message || 'Có lỗi xảy ra khi lưu');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <AdminLayout>
        <div className="admin-loading">Đang tải cấu hình chiết khấu...</div>
      </AdminLayout>
    );
  }

  return (
    <AdminLayout>
      <div className="discounts-management">
        <div className="admin-header-row">
          <div>
            <h1>Chiết Khấu Đăng Ký Dài Hạn</h1>
            <p className="admin-sub">Quản lý giảm giá theo mốc thời gian: 1 ngày, 1 tuần, 1 tháng, 3 tháng, 6 tháng, 1 năm, 2 năm</p>
          </div>
          <button className="btn-add-discount" onClick={handleOpenAdd}>
            <Plus size={18} /> Thêm mốc chiết khấu
          </button>
        </div>

        {error && <div className="admin-alert error">{error}</div>}
        {success && <div className="admin-alert success">{success}</div>}

        {/* Visual Milestones Overview */}
        <div className="milestones-overview">
          <h3><Tag size={18} /> Tổng quan các mốc giảm giá hiện tại</h3>
          <div className="milestones-grid">
            {DURATION_MILESTONES.map((ms) => {
              // Lấy các discount áp dụng cho mốc này (global - packageId null)
              const globalDiscount = discounts.find(d => d.packageId === null && d.minDays === ms.days);
              const specificDiscounts = discounts.filter(d => d.packageId !== null && d.minDays === ms.days);
              const hasDiscount = globalDiscount || specificDiscounts.length > 0;
              
              return (
                <div key={ms.days} className={`ms-overview-card ${hasDiscount ? 'has-discount' : ''}`}>
                  <div className="ms-ov-label">{ms.label}</div>
                  <div className="ms-ov-days">{ms.days} ngày</div>
                  {globalDiscount ? (
                    <div className="ms-ov-discount">-{globalDiscount.discountPercent}%</div>
                  ) : specificDiscounts.length > 0 ? (
                    <div className="ms-ov-discount partial">Riêng gói</div>
                  ) : (
                    <div className="ms-ov-none">Chưa cấu hình</div>
                  )}
                </div>
              );
            })}
          </div>
        </div>

        <div className="discounts-table-container">
          <table className="admin-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Gói tập áp dụng</th>
                <th>Mốc thời gian</th>
                <th>Số ngày tương ứng</th>
                <th>Phần trăm giảm giá</th>
                <th style={{ textAlign: 'center' }}>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {discounts.length > 0 ? (
                discounts.map(d => (
                  <tr key={d.id}>
                    <td>#{d.id}</td>
                    <td>
                      <span className={`pkg-badge ${d.packageId ? 'specific' : 'all'}`}>
                        {d.packageName}
                      </span>
                    </td>
                    <td><strong>{getDurationLabel(d.minDays)}</strong></td>
                    <td>{d.minDays} ngày</td>
                    <td className="discount-pct-cell">-{d.discountPercent}%</td>
                    <td style={{ textAlign: 'center' }}>
                      <div className="table-actions">
                        <button className="btn-edit" onClick={() => handleOpenEdit(d)}>
                          <Edit2 size={16} /> Sửa
                        </button>
                        <button className="btn-delete" onClick={() => handleDelete(d.id)}>
                          <Trash2 size={16} /> Xóa
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', color: '#94a3b8' }}>Chưa có cấu hình chiết khấu nào. Hãy tạo mới!</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* Modal Thêm/Sửa */}
        {showModal && (
          <div className="admin-modal-overlay">
            <div className="admin-modal">
              <div className="modal-header">
                <h2>{editingId ? 'Sửa mốc chiết khấu' : 'Thêm mốc chiết khấu mới'}</h2>
                <button className="btn-close" onClick={() => setShowModal(false)}>×</button>
              </div>
              <form onSubmit={handleSubmit}>
                <div className="modal-body">
                  <div className="form-group">
                    <label htmlFor="packageSelect">Gói tập áp dụng</label>
                    <select
                      id="packageSelect"
                      value={pkgId}
                      onChange={(e) => setPkgId(e.target.value)}
                    >
                      <option value="">Tất cả gói tập (Mặc định)</option>
                      {packages.map(p => (
                        <option key={p.id} value={p.id}>{p.name}</option>
                      ))}
                    </select>
                    <small style={{ color: '#94a3b8', marginTop: '5px', display: 'block' }}>
                      Chọn gói tập cụ thể hoặc để trống để áp dụng chung cho tất cả gói.
                    </small>
                  </div>

                  <div className="form-group">
                    <label htmlFor="milestoneSelect">Mốc thời gian</label>
                    <select
                      id="milestoneSelect"
                      value={minDays}
                      onChange={(e) => setMinDays(parseInt(e.target.value))}
                    >
                      {DURATION_MILESTONES.map(ms => (
                        <option key={ms.days} value={ms.days}>{ms.label} ({ms.days} ngày)</option>
                      ))}
                    </select>
                    <small style={{ color: '#94a3b8', marginTop: '5px', display: 'block' }}>
                      Chọn mốc thời gian để áp dụng chiết khấu. Hội viên đăng ký mốc này trở lên sẽ được giảm giá.
                    </small>
                  </div>

                  <div className="form-group">
                    <label htmlFor="pctInput">Phần trăm giảm giá (%)</label>
                    <input
                      id="pctInput"
                      type="number"
                      min={1}
                      max={100}
                      value={discountPercent}
                      onChange={(e) => setDiscountPercent(parseInt(e.target.value) || 0)}
                      required
                    />
                  </div>
                </div>
                <div className="modal-footer">
                  <button type="button" className="btn-cancel" onClick={() => setShowModal(false)}>Hủy</button>
                  <button type="submit" className="btn-save" disabled={submitting}>
                    {submitting ? 'Đang xử lý...' : 'Lưu thiết lập'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </AdminLayout>
  );
};

export default DiscountsManagement;
