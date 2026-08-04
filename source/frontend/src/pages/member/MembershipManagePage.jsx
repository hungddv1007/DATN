import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import MainLayout from '../../components/layout/MainLayout';
import membershipService from '../../services/membershipService';
import packageService from '../../services/packageService';
import ptService from '../../services/ptService';
import { getMembershipPriceDisplay } from '../../utils/membershipPriceDisplay';
import { Calendar, RefreshCw, ChevronUp, Pause, Play, AlertTriangle, ShieldCheck } from 'lucide-react';
import './MembershipManagePage.css';

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

const MembershipManagePage = () => {
  const [membership, setMembership] = useState(null);
  const [history, setHistory] = useState([]);
  const [packages, setPackages] = useState([]);
  const [pts, setPts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState('');
  
  // Modals state
  const [showRenewModal, setShowRenewModal] = useState(false);
  const [showUpgradeModal, setShowUpgradeModal] = useState(false);
  
  // Form state
  const [renewDays, setRenewDays] = useState(30);
  const [renewPromo, setRenewPromo] = useState('');
  const [renewPayMethod, setRenewPayMethod] = useState('BANK');
  const [renewPreview, setRenewPreview] = useState(null);
  const [renewPreviewError, setRenewPreviewError] = useState('');

  const [upgradePkgId, setUpgradePkgId] = useState('');
  const [upgradeExtraDays, setUpgradeExtraDays] = useState(0);
  const [upgradePromo, setUpgradePromo] = useState('');
  const [upgradePtId, setUpgradePtId] = useState('');
  const [upgradePayMethod, setUpgradePayMethod] = useState('BANK');
  const [upgradePreview, setUpgradePreview] = useState(null);
  const [upgradePreviewError, setUpgradePreviewError] = useState('');

  const fetchMembershipData = async () => {
    try {
      const currentData = await membershipService.getCurrentMembership();
      setMembership(currentData);
    } catch (err) {
      setMembership(null);
    }

    try {
      const histData = await membershipService.getMembershipHistory();
      setHistory(histData);
    } catch (err) {
      console.error('Lỗi tải lịch sử giao dịch', err);
    }

    try {
      const pkgsData = await packageService.getAllPackages();
      setPackages(pkgsData);
      
      const ptsData = await ptService.getAllPtProfiles();
      setPts(ptsData);
    } catch (err) {
      console.error('Lỗi tải danh sách cấu hình', err);
    }
    
    setLoading(false);
  };

  useEffect(() => {
    fetchMembershipData();
  }, []);

  // Preview gia hạn
  useEffect(() => {
    if (!showRenewModal) return;
    const triggerPreview = async () => {
      setRenewPreviewError('');
      try {
        const preview = await membershipService.previewRenew(renewDays);
        setRenewPreview(preview);
      } catch (err) {
        setRenewPreview(null);
        setRenewPreviewError(err.response?.data?.message || 'Không thể xem trước giá gia hạn');
      }
    };
    const debounce = setTimeout(triggerPreview, 300);
    return () => clearTimeout(debounce);
  }, [renewDays, showRenewModal]);

  // Preview nâng cấp
  useEffect(() => {
    if (!showUpgradeModal || !upgradePkgId) {
      setUpgradePreview(null);
      return;
    }
    const triggerPreview = async () => {
      setUpgradePreviewError('');
      try {
        const preview = await membershipService.previewUpgrade(upgradePkgId, upgradeExtraDays);
        setUpgradePreview(preview);
      } catch (err) {
        setUpgradePreview(null);
        setUpgradePreviewError(err.response?.data?.message || 'Không thể xem trước giá nâng cấp');
      }
    };
    const debounce = setTimeout(triggerPreview, 300);
    return () => clearTimeout(debounce);
  }, [upgradePkgId, upgradeExtraDays, showUpgradeModal]);

  const handlePause = async () => {
    if (!window.confirm("Bạn có chắc chắn muốn bảo lưu gói tập không? Lịch tập của bạn sẽ được tạm dừng tính ngày.")) return;
    setActionLoading(true);
    setError('');
    try {
      await membershipService.pauseMembership();
      alert("Bảo lưu gói tập thành công!");
      fetchMembershipData();
    } catch (err) {
      setError(err.response?.data?.message || 'Bảo lưu thất bại');
    } finally {
      setActionLoading(false);
    }
  };

  const handleResume = async () => {
    if (!window.confirm("Bạn có chắc chắn muốn kích hoạt lại gói tập không?")) return;
    setActionLoading(true);
    setError('');
    try {
      await membershipService.resumeMembership();
      alert("Kích hoạt lại gói tập thành công!");
      fetchMembershipData();
    } catch (err) {
      setError(err.response?.data?.message || 'Kích hoạt lại thất bại');
    } finally {
      setActionLoading(false);
    }
  };

  const handleCancel = async () => {
    if (!window.confirm("CẢNH BÁO: Hủy gói tập sẽ không được hoàn tiền và không thể khôi phục. Bạn có chắc chắn muốn hủy gói không?")) return;
    setActionLoading(true);
    setError('');
    try {
      await membershipService.cancelMembership();
      alert("Hủy gói tập thành công!");
      fetchMembershipData();
    } catch (err) {
      setError(err.response?.data?.message || 'Hủy gói thất bại');
    } finally {
      setActionLoading(false);
    }
  };

  const handleRenewSubmit = async (e) => {
    e.preventDefault();
    setActionLoading(true);
    setError('');
    try {
      const data = await membershipService.renewMembership({
        durationDays: parseInt(renewDays),
        promotionCode: renewPromo || null,
        paymentMethod: renewPayMethod
      });
      alert(`Đăng ký gia hạn thành công! Mã giao dịch: #${data.transactionId}`);
      setShowRenewModal(false);
      fetchMembershipData();
    } catch (err) {
      setError(err.response?.data?.message || 'Gia hạn thất bại');
    } finally {
      setActionLoading(false);
    }
  };

  const handleUpgradeSubmit = async (e) => {
    e.preventDefault();
    setActionLoading(true);
    setError('');
    try {
      const selectedPkg = packages.find(p => p.id === parseInt(upgradePkgId));
      if (selectedPkg?.canChoosePt && !upgradePtId) {
        setError("Vui lòng chọn Huấn luyện viên cá nhân (PT) cho gói nâng cấp!");
        setActionLoading(false);
        return;
      }
      const data = await membershipService.upgradeMembership({
        newPackageId: parseInt(upgradePkgId),
        extraDays: parseInt(upgradeExtraDays) || 0,
        promotionCode: upgradePromo || null,
        paymentMethod: upgradePayMethod,
        ptId: upgradePtId ? parseInt(upgradePtId) : null
      });
      alert(`Đăng ký nâng cấp thành công! Mã giao dịch: #${data.transactionId}`);
      setShowUpgradeModal(false);
      fetchMembershipData();
    } catch (err) {
      setError(err.response?.data?.message || 'Nâng cấp thất bại');
    } finally {
      setActionLoading(false);
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
  };

  if (loading) {
    return (
      <MainLayout>
        <div className="membership-manage-page">
          <p style={{ color: '#94a3b8', marginTop: '100px', textAlign: 'center' }}>Đang tải thông tin gói tập của bạn...</p>
        </div>
      </MainLayout>
    );
  }

  const upgradeOptions = packages.filter(p => membership && p.dailyPrice > membership.dailyPrice && p.isActive);
  const priceDisplay = getMembershipPriceDisplay(membership, history);

  return (
    <MainLayout>
      <div className="membership-manage-page">
        <div className="manage-header">
          <h1>Quản Lý Gói Tập</h1>
          <p>Xem thông tin chi tiết, gia hạn, nâng cấp hoặc bảo lưu gói tập của bạn</p>
        </div>

        {error && <div className="manage-alert error">{error}</div>}

        <div className="manage-grid">
          {/* Cột Trái: Gói tập hiện tại */}
          <div className="membership-card-section">
            <div className="card-header">
              <h2>Gói tập hiện tại</h2>
              {membership && (
                <span className={`status-badge ${membership.status.toLowerCase()}`}>
                  {membership.status === 'ACTIVE'
                    ? 'Đang hoạt động'
                    : membership.status === 'PAUSED'
                      ? 'Đang bảo lưu'
                      : membership.status === 'PENDING'
                        ? 'Chờ thanh toán'
                        : 'Đã kết thúc'}
                </span>
              )}
            </div>

            {membership ? (
              <div className="membership-card-body">
                <div className="pkg-title-row">
                  <h3>Gói {membership.packageName}</h3>
                  <div className="pkg-price-badge">
                    <span className="pkg-price-value">
                      {formatCurrency(priceDisplay.amount)}
                      <small>/{priceDisplay.unit}</small>
                    </span>
                    <span className="pkg-price-caption">{priceDisplay.caption}</span>
                  </div>
                </div>

                <div className="info-list">
                  {membership.status === 'PENDING' && (
                    <div className="manage-alert">
                      Gói tập chỉ được kích hoạt sau khi giao dịch được admin xác nhận.
                    </div>
                  )}
                  <div className="info-item">
                    <Calendar size={18} />
                    <div>
                      <span className="label">Thời hạn gói:</span>
                      <span className="value">{membership.startDate} đến {membership.endDate} ({membership.durationDays} ngày)</span>
                    </div>
                  </div>
                  {membership.ptName && (
                    <div className="info-item">
                      <ShieldCheck size={18} />
                      <div>
                        <span className="label">Huấn luyện viên kèm 1-1:</span>
                        <span className="value">{membership.ptName}</span>
                      </div>
                    </div>
                  )}
                  {membership.status === 'ACTIVE' && (
                    <div className="info-item">
                      <RefreshCw size={18} />
                      <div>
                        <span className="label">Số ngày còn lại:</span>
                        <span className="value" style={{ color: '#22c55e', fontWeight: 'bold' }}>{membership.remainingDays} ngày</span>
                      </div>
                    </div>
                  )}
                  {membership.status === 'PAUSED' && (
                    <div className="info-item">
                      <Pause size={18} />
                      <div>
                        <span className="label">Bảo lưu bắt đầu từ:</span>
                        <span className="value" style={{ color: '#f97316' }}>{membership.pausedAt}</span>
                      </div>
                    </div>
                  )}
                  <div className="info-item">
                    <Pause size={18} />
                    <div>
                      <span className="label">Số lượt bảo lưu đã dùng:</span>
                      <span className="value">{membership.holdCount} / {membership.maxHoldTimes} lần</span>
                    </div>
                  </div>
                  {membership.totalHoldDays > 0 && (
                    <div className="info-item">
                      <Pause size={18} />
                      <div>
                        <span className="label">Tổng số ngày đã bảo lưu:</span>
                        <span className="value">{membership.totalHoldDays} ngày</span>
                      </div>
                    </div>
                  )}
                </div>

                {/* Nút thao tác */}
                <div className="action-buttons">
                  {membership.status === 'ACTIVE' && (
                    <>
                      <button className="btn-manage-action btn-renew" onClick={() => setShowRenewModal(true)}>
                        <RefreshCw size={16} /> Gia hạn gói
                      </button>
                      {upgradeOptions.length > 0 && (
                        <button className="btn-manage-action btn-upgrade" onClick={() => setShowUpgradeModal(true)}>
                          <ChevronUp size={16} /> Nâng cấp gói
                        </button>
                      )}
                      {membership.maxHoldTimes > 0 && membership.holdCount < membership.maxHoldTimes && (
                        <button className="btn-manage-action btn-pause" onClick={handlePause} disabled={actionLoading}>
                          <Pause size={16} /> Bảo lưu gói
                        </button>
                      )}
                      <button className="btn-manage-action btn-cancel" onClick={handleCancel} disabled={actionLoading}>
                        <AlertTriangle size={16} /> Hủy gói tập
                      </button>
                    </>
                  )}

                  {membership.status === 'PAUSED' && (
                    <button className="btn-manage-action btn-resume" onClick={handleResume} disabled={actionLoading}>
                      <Play size={16} /> Kích hoạt lại
                    </button>
                  )}
                </div>
              </div>
            ) : (
              <div className="no-membership">
                <p>Bạn chưa đăng ký gói tập nào hoặc gói tập đã hết hạn.</p>
                <Link to="/packages" className="btn-buy-now">ĐĂNG KÝ GÓI TẬP NGAY</Link>
              </div>
            )}
          </div>

          {/* Cột Phải: Lịch sử giao dịch */}
          <div className="history-section">
            <h2>Lịch sử đăng ký / giao dịch</h2>
            <div className="history-list">
              {history.length > 0 ? (
                history.map((h, i) => (
                  <div className="history-item" key={i}>
                    <div className="item-title">
                      <strong>{h.packageName}</strong>
                      <span className={`tx-status ${h.transactionStatus?.toLowerCase()}`}>
                        {h.transactionStatus === 'PENDING' ? 'Chờ duyệt' : h.transactionStatus === 'CONFIRMED' ? 'Thành công' : 'Đã hủy'}
                      </span>
                    </div>
                    <div className="item-meta">
                      <span>Loại: {h.transactionType === 'NEW' ? 'Đăng ký mới' : h.transactionType === 'RENEW' ? 'Gia hạn' : 'Nâng cấp'}</span>
                      <span>Ngày: {h.createdAt ? new Date(h.createdAt).toLocaleDateString('vi-VN') : ''}</span>
                    </div>
                    <div className="item-price">
                      <span>Thời hạn: {h.durationDays} ngày ({h.startDate} - {h.endDate})</span>
                      <strong className="final-price">{formatCurrency(h.finalAmount)}</strong>
                    </div>
                  </div>
                ))
              ) : (
                <p style={{ color: '#94a3b8', fontSize: '0.9rem' }}>Chưa có lịch sử giao dịch.</p>
              )}
            </div>
          </div>
        </div>

        {/* Modal Gia hạn */}
        {showRenewModal && (
          <div className="manage-modal-overlay">
            <div className="manage-modal">
              <div className="modal-header">
                <h2>Gia Hạn Gói Tập</h2>
                <button className="btn-close-modal" onClick={() => setShowRenewModal(false)}>×</button>
              </div>
              <form onSubmit={handleRenewSubmit}>
                <div className="modal-body">
                  {renewPreviewError && <div className="manage-alert error">{renewPreviewError}</div>}
                  
                  <div className="form-group">
                    <label>Chọn thời hạn gia hạn</label>
                    <select
                      value={renewDays}
                      onChange={(e) => setRenewDays(parseInt(e.target.value))}
                      required
                    >
                      {DURATION_MILESTONES.map(ms => (
                        <option key={ms.days} value={ms.days}>{ms.label} ({ms.days} ngày)</option>
                      ))}
                    </select>
                  </div>

                  <div className="form-group">
                    <label>Mã khuyến mãi (Nếu có)</label>
                    <input 
                      type="text" 
                      placeholder="Mã giảm giá..." 
                      value={renewPromo}
                      onChange={(e) => setRenewPromo(e.target.value)}
                    />
                  </div>

                  <div className="form-group">
                    <label>Phương thức thanh toán</label>
                    <select value={renewPayMethod} onChange={(e) => setRenewPayMethod(e.target.value)}>
                      <option value="BANK">Chuyển khoản ngân hàng</option>
                      <option value="CASH">Tiền mặt tại quầy</option>
                    </select>
                  </div>

                  {renewPreview && (
                    <div className="price-preview-box">
                      <div className="preview-row">
                        <span>Giá gốc ({renewDays} ngày):</span>
                        <span>{formatCurrency(renewPreview.grossAmount)}</span>
                      </div>
                      {renewPreview.longTermDiscount > 0 && (
                        <div className="preview-row discount">
                          <span>Chiết khấu dài hạn (-{renewPreview.longTermDiscount}%):</span>
                          <span>-{formatCurrency(renewPreview.grossAmount - renewPreview.afterDiscount)}</span>
                        </div>
                      )}
                      <div className="preview-row total">
                        <span>Phải trả:</span>
                        <span>{formatCurrency(renewPreview.finalAmount)}</span>
                      </div>
                    </div>
                  )}
                </div>
                <div className="modal-footer">
                  <button type="button" className="btn-cancel-modal" onClick={() => setShowRenewModal(false)}>Hủy</button>
                  <button type="submit" className="btn-confirm-modal" disabled={actionLoading}>
                    {actionLoading ? 'Đang xử lý...' : 'XÁC NHẬN GIA HẠN'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* Modal Nâng cấp */}
        {showUpgradeModal && (
          <div className="manage-modal-overlay">
            <div className="manage-modal">
              <div className="modal-header">
                <h2>Nâng Cấp Gói Tập (PRORATION)</h2>
                <button className="btn-close-modal" onClick={() => setShowUpgradeModal(false)}>×</button>
              </div>
              <form onSubmit={handleUpgradeSubmit}>
                <div className="modal-body">
                  {upgradePreviewError && <div className="manage-alert error">{upgradePreviewError}</div>}
                  
                  <div className="form-group">
                    <label>Chọn gói tập mới</label>
                    <select 
                      value={upgradePkgId} 
                      onChange={(e) => setUpgradePkgId(e.target.value)}
                      required
                    >
                      <option value="">-- Chọn gói tập cao cấp hơn --</option>
                      {upgradeOptions.map(p => (
                        <option key={p.id} value={p.id}>{p.name} ({formatCurrency(p.dailyPrice)}/ngày)</option>
                      ))}
                    </select>
                  </div>

                  {upgradePkgId && packages.find(p => p.id === parseInt(upgradePkgId))?.canChoosePt && (
                    <div className="form-group">
                      <label>Chọn Huấn luyện viên cá nhân (PT)</label>
                      <select 
                        value={upgradePtId} 
                        onChange={(e) => setUpgradePtId(e.target.value)}
                        required
                      >
                        <option value="">-- Vui lòng chọn một Huấn luyện viên --</option>
                        {pts.map(pt => (
                          <option key={pt.id} value={pt.id}>
                            {pt.fullName} - {pt.specialization} (Đánh giá: {pt.ratingScore || 'Chưa có'}/5)
                          </option>
                        ))}
                      </select>
                    </div>
                  )}

                  <div className="form-group">
                    <label>Gia hạn thêm (Tùy chọn)</label>
                    <select
                      value={upgradeExtraDays}
                      onChange={(e) => setUpgradeExtraDays(parseInt(e.target.value))}
                    >
                      <option value={0}>Không gia hạn thêm (chỉ nâng cấp)</option>
                      {DURATION_MILESTONES.map(ms => (
                        <option key={ms.days} value={ms.days}>{ms.label} ({ms.days} ngày)</option>
                      ))}
                    </select>
                    <small style={{ color: '#94a3b8' }}>
                      Chọn thời hạn muốn gia hạn thêm sau khi nâng cấp hoặc giữ "Không gia hạn" để chỉ nâng cấp số ngày còn lại.
                    </small>
                  </div>

                  <div className="form-group">
                    <label>Mã khuyến mãi (Nếu có)</label>
                    <input 
                      type="text" 
                      placeholder="Mã giảm giá..." 
                      value={upgradePromo}
                      onChange={(e) => setUpgradePromo(e.target.value)}
                    />
                  </div>

                  <div className="form-group">
                    <label>Phương thức thanh toán</label>
                    <select value={upgradePayMethod} onChange={(e) => setUpgradePayMethod(e.target.value)}>
                      <option value="BANK">Chuyển khoản ngân hàng</option>
                      <option value="CASH">Tiền mặt tại quầy</option>
                    </select>
                  </div>

                  {upgradePreview && (
                    <div className="price-preview-box">
                      <div className="preview-row">
                        <span>Giá gói mới ({upgradePreview.totalNewDays} ngày):</span>
                        <span>{formatCurrency(upgradePreview.grossAmount)}</span>
                      </div>
                      {upgradePreview.longTermDiscount > 0 && (
                        <div className="preview-row discount">
                          <span>Chiết khấu dài hạn (-{upgradePreview.longTermDiscount}%):</span>
                          <span>-{formatCurrency(upgradePreview.grossAmount - upgradePreview.afterDiscount)}</span>
                        </div>
                      )}
                      <div className="preview-row credit">
                        <span>Credit trừ lại (số ngày chưa dùng gói cũ):</span>
                        <span>-{formatCurrency(upgradePreview.credit)}</span>
                      </div>
                      <div className="preview-row total">
                        <span>Phải trả thêm:</span>
                        <span>{formatCurrency(upgradePreview.finalAmount)}</span>
                      </div>
                    </div>
                  )}
                </div>
                <div className="modal-footer">
                  <button type="button" className="btn-cancel-modal" onClick={() => setShowUpgradeModal(false)}>Hủy</button>
                  <button type="submit" className="btn-confirm-modal" disabled={actionLoading}>
                    {actionLoading ? 'Đang xử lý...' : 'XÁC NHẬN NÂN CẤP'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </MainLayout>
  );
};

export default MembershipManagePage;
