import React, { useState, useEffect } from 'react';
import packageService from '../../services/packageService';
import { Tag, Plus, Edit2, Trash2, ShieldAlert } from 'lucide-react';
import './DiscountsManagement.css';

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
  const [minDays, setMinDays] = useState(30);
  const [discountPercent, setDiscountPercent] = useState(5);
  const [submitting, setSubmitting] = useState(false);

  const loadData = async () => {
    try {
      const discountData = await packageService.getAdminDiscounts();
      setDiscounts(discountData);

      const pkgData = await packageService.getAllPackages(false); // get both active and inactive
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
    setMinDays(30);
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
    return <div className="admin-loading">Đang tải cấu hình chiết khấu...</div>;
  }

  return (
    <div className="discounts-management">
      <div className="admin-header-row">
        <div>
          <h1>Chiết Khấu Đăng Ký Dài Hạn</h1>
          <p className="admin-sub">Quản lý các mốc giảm giá tự động khi khách hàng đăng ký dài hạn (ví dụ: mua 3 tháng giảm 5%)</p>
        </div>
        <button className="btn-add-discount" onClick={handleOpenAdd}>
          <Plus size={18} /> Thêm mốc chiết khấu
        </button>
      </div>

      {error && <div className="admin-alert error">{error}</div>}
      {success && <div className="admin-alert success">{success}</div>}

      <div className="discounts-table-container">
        <table className="admin-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Gói tập áp dụng</th>
              <th>Đăng ký từ (ngày)</th>
              <th>Đăng ký từ (tháng tương ứng)</th>
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
                  <td><strong>{d.minDays} ngày</strong></td>
                  <td>~ {Math.round(d.minDays / 30 * 10) / 10} tháng</td>
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
                  <label htmlFor="minDaysInput">Số ngày đăng ký tối thiểu (ngày)</label>
                  <input
                    id="minDaysInput"
                    type="number"
                    min={1}
                    value={minDays}
                    onChange={(e) => setMinDays(parseInt(e.target.value) || 0)}
                    required
                  />
                  <small style={{ color: '#94a3b8', marginTop: '5px', display: 'block' }}>
                    Ví dụ: 90 ngày (~3 tháng), 180 ngày (~6 tháng), 365 ngày (~1 năm).
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
  );
};

export default DiscountsManagement;
