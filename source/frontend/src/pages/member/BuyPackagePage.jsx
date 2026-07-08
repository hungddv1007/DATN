import React, { useState, useEffect, useMemo } from 'react';
import { useLocation, useNavigate, Link } from 'react-router-dom';
import MainLayout from '../../components/layout/MainLayout';
import packageService from '../../services/packageService';
import ptService from '../../services/ptService';
import membershipService from '../../services/membershipService';
import discountService from '../../services/discountService';
import { CreditCard, Banknote, CheckCircle, Info } from 'lucide-react';
import './BuyPackagePage.css';

const BuyPackagePage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  
  const queryParams = new URLSearchParams(location.search);
  const pkgId = parseInt(queryParams.get('pkgId'), 10);

  const [gymPackage, setGymPackage] = useState(null);
  const [currentMembership, setCurrentMembership] = useState(null);
  const [pts, setPts] = useState([]);
  const [discounts, setDiscounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  const [durationDays, setDurationDays] = useState(0);
  const [promoCode, setPromoCode] = useState('');
  const [paymentMethod, setPaymentMethod] = useState('BANK'); 
  const [selectedPtId, setSelectedPtId] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [successData, setSuccessData] = useState(null);
  
  const [upgradeMode, setUpgradeMode] = useState('UPGRADE_ONLY'); // UPGRADE_ONLY or UPGRADE_RENEW

  useEffect(() => {
    if (!pkgId) {
      setError('Không tìm thấy mã gói tập. Vui lòng quay lại trang Gói tập.');
      setLoading(false);
      return;
    }

    const fetchData = async () => {
      try {
        const pkgData = await packageService.getPackageById(pkgId);
        if (pkgData.isActive === false) {
          setError('Gói tập này hiện đã ngừng cung cấp.');
          setGymPackage(null);
          return;
        }

        setGymPackage(pkgData);
        setDurationDays(pkgData.minDays > 0 ? pkgData.minDays : 30);
        
        const [discountData, currentMem] = await Promise.all([
          discountService.getPublicDiscounts().catch(() => []),
          membershipService.getMyCurrentMembership().catch(() => null)
        ]);
        
        setDiscounts(discountData);
        setCurrentMembership(currentMem);

        if (pkgData.canChoosePt) {
          const ptsData = await ptService.getAllPtProfiles();
          setPts(ptsData);
        }
      } catch (err) {
        setError('Lỗi tải thông tin gói tập. Có thể gói tập không tồn tại.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [pkgId]);

  const getActionType = () => {
    if (!currentMembership) return 'REGISTER';
    if (currentMembership.packageId === pkgId) return 'RENEW';
    return upgradeMode;
  };

  const actionType = getActionType();

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (actionType !== 'UPGRADE_ONLY' && durationDays < gymPackage.minDays) {
      setError(`Số ngày đăng ký tối thiểu cho gói này là ${gymPackage.minDays} ngày.`);
      return;
    }

    if (gymPackage?.canChoosePt && !selectedPtId && actionType !== 'UPGRADE_ONLY') {
      setError('Vui lòng chọn một Huấn luyện viên cá nhân!');
      return;
    }
    
    setSubmitting(true);
    setError('');

    try {
      let data;
      const payload = {
        packageId: pkgId,
        durationDays: actionType === 'UPGRADE_ONLY' ? null : parseInt(durationDays),
        promotionCode: promoCode || null,
        paymentMethod: paymentMethod,
        ptId: selectedPtId ? parseInt(selectedPtId) : null
      };

      if (actionType === 'REGISTER') data = await membershipService.registerPackage(payload);
      else if (actionType === 'RENEW') data = await membershipService.renewPackage(payload);
      else if (actionType === 'UPGRADE_ONLY') data = await membershipService.upgradePackage(payload);
      else if (actionType === 'UPGRADE_RENEW') data = await membershipService.upgradeAndRenewPackage(payload);
      
      setSuccessData(data);
    } catch (err) {
      const resData = err.response?.data;
      if (resData && typeof resData === 'object') {
        const firstError = resData.message || Object.values(resData)[0];
        setError(firstError || 'Giao dịch thất bại. Vui lòng thử lại.');
      } else {
        setError('Lỗi kết nối máy chủ!');
      }
    } finally {
      setSubmitting(false);
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
  };

  const pricing = useMemo(() => {
    if (!gymPackage) return { original: 0, discountPercent: 0, final: 0, proration: null };
    
    // Nếu chỉ Nâng cấp
    if (actionType === 'UPGRADE_ONLY') {
      // Tính toán gần đúng ở client, hoặc tốt nhất là nên gọi API previewUpgrade, nhưng để nhanh ta ước lượng
      if (!currentMembership) return { final: 0 };
      const today = new Date();
      const end = new Date(currentMembership.endDate);
      const remaining = Math.max(0, Math.ceil((end - today) / (1000 * 60 * 60 * 24)));
      
      const credit = currentMembership.dailyPrice * remaining;
      const newCost = gymPackage.dailyPrice * remaining;
      let upgradeCost = newCost - credit;
      if (upgradeCost < 0) upgradeCost = 0;

      return {
        original: newCost,
        discountPercent: 0,
        final: upgradeCost,
        proration: { remaining, credit, newCost }
      };
    }

    // Các trường hợp khác: có durationDays
    const original = gymPackage.dailyPrice * durationDays;
    let applicableDiscount = 0;
    
    for (const d of discounts) {
      if (d.packageId === null || d.packageId === gymPackage.id) {
        if (durationDays >= d.minDays && d.discountPercent > applicableDiscount) {
          applicableDiscount = d.discountPercent;
        }
      }
    }
    
    let finalAmount = original * (1 - applicableDiscount / 100);

    let proration = null;
    if (actionType === 'UPGRADE_RENEW') {
      const today = new Date();
      const end = new Date(currentMembership.endDate);
      const remaining = Math.max(0, Math.ceil((end - today) / (1000 * 60 * 60 * 24)));
      
      const credit = currentMembership.dailyPrice * remaining;
      const newCost = gymPackage.dailyPrice * remaining;
      let upgradeCost = newCost - credit;
      if (upgradeCost < 0) upgradeCost = 0;

      finalAmount += upgradeCost;
      proration = { remaining, credit, newCost, upgradeCost };
    }

    return {
      original,
      discountPercent: applicableDiscount,
      final: finalAmount,
      proration
    };
  }, [gymPackage, durationDays, discounts, currentMembership, actionType]);

  if (loading) {
    return (
      <MainLayout>
        <div className="buy-package-page">
          <p style={{ color: '#94a3b8', marginTop: '100px', textAlign: 'center' }}>Đang tải thông tin gói tập...</p>
        </div>
      </MainLayout>
    );
  }

  if (successData) {
    return (
      <MainLayout>
        <div className="buy-package-page">
          <div className="buy-package-container success-container">
            <CheckCircle size={80} color="#22c55e" style={{ margin: '0 auto 20px' }} />
            <h2>Giao dịch thành công!</h2>
            <p>
              Bạn đã {actionType === 'REGISTER' ? 'đăng ký' : (actionType === 'RENEW' ? 'gia hạn' : 'nâng cấp')} gói <strong>{successData.packageName}</strong>.<br/>
              Mã giao dịch: <strong>#{successData.transactionId}</strong><br/>
              Tổng tiền cần thanh toán: <strong>{formatCurrency(successData.finalAmount)}</strong>
            </p>
            {successData.finalAmount > 0 && (
              <div style={{ background: 'rgba(255,255,255,0.05)', padding: '20px', borderRadius: '8px', marginBottom: '30px', textAlign: 'left' }}>
                <p style={{ margin: 0, color: '#f1f5f9' }}>
                  Vui lòng thanh toán tại quầy lễ tân hoặc chuyển khoản theo thông tin sau:<br/><br/>
                  Ngân hàng: <strong>Vietcombank</strong><br/>
                  STK: <strong>1234567890</strong><br/>
                  Chủ TK: <strong>GYMPRO VN</strong><br/>
                  Nội dung: <strong>GYMPRO {successData.transactionId}</strong>
                </p>
              </div>
            )}
            <p style={{ color: '#f97316', fontSize: '0.9rem' }}>
              Thay đổi sẽ được áp dụng ngay sau khi Admin xác nhận thanh toán.
            </p>
            <Link to="/member/membership" className="btn-dashboard">Quản lý Gói tập</Link>
          </div>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout>
      <div className="buy-package-page">
        <div className="buy-package-container">
          <div className="buy-header">
            <h1>
              {actionType === 'REGISTER' && 'Đăng Ký Gói Tập'}
              {actionType === 'RENEW' && 'Gia Hạn Gói Tập'}
              {(actionType === 'UPGRADE_ONLY' || actionType === 'UPGRADE_RENEW') && 'Nâng Cấp Gói Tập'}
            </h1>
            <p>Hoàn tất giao dịch để tiếp tục hành trình thay đổi bản thân</p>
          </div>

          {error && <div className="buy-alert error">{error}</div>}

          {gymPackage && (
            <div className="package-summary">
              <div className="summary-info">
                <h3>Gói {gymPackage.name}</h3>
                <p>{gymPackage.description}</p>
                <div style={{ marginTop: '10px', fontSize: '0.9rem', color: '#94a3b8' }}>
                  <Info size={14} style={{ display: 'inline', verticalAlign: 'middle', marginRight: '4px' }}/>
                  Giá gốc: {formatCurrency(gymPackage.dailyPrice)}/ngày
                  {gymPackage.minDays > 0 && ` (Tối thiểu ${gymPackage.minDays} ngày)`}
                </div>
              </div>
            </div>
          )}

          {currentMembership && currentMembership.packageId !== gymPackage?.id && (
            <div style={{ background: 'rgba(59,130,246,0.1)', border: '1px solid #3b82f6', padding: '15px', borderRadius: '8px', marginBottom: '20px' }}>
              <p style={{ color: '#60a5fa', margin: '0 0 10px 0', fontWeight: 'bold' }}>Bạn đang sử dụng gói: {currentMembership.packageName}</p>
              <div style={{ display: 'flex', gap: '20px' }}>
                <label style={{ display: 'flex', alignItems: 'center', gap: '8px', color: 'white' }}>
                  <input type="radio" checked={upgradeMode === 'UPGRADE_ONLY'} onChange={() => setUpgradeMode('UPGRADE_ONLY')} />
                  Chỉ nâng cấp số ngày còn lại (Không mua thêm ngày)
                </label>
                <label style={{ display: 'flex', alignItems: 'center', gap: '8px', color: 'white' }}>
                  <input type="radio" checked={upgradeMode === 'UPGRADE_RENEW'} onChange={() => setUpgradeMode('UPGRADE_RENEW')} />
                  Nâng cấp & Mua thêm ngày
                </label>
              </div>
            </div>
          )}

          {gymPackage && (
            <form className="buy-form" onSubmit={handleSubmit}>
              
              {actionType !== 'UPGRADE_ONLY' && (
                <div className="form-group">
                  <label>Thời gian đăng ký thêm (Ngày)</label>
                  <input 
                    type="number" 
                    min={gymPackage.minDays || 1}
                    value={durationDays}
                    onChange={(e) => setDurationDays(Number(e.target.value))}
                    required
                  />
                </div>
              )}

              <div className="form-group">
                <label>Mã khuyến mãi (Nếu có)</label>
                <input 
                  type="text" 
                  placeholder="Nhập mã giảm giá..." 
                  value={promoCode}
                  onChange={(e) => setPromoCode(e.target.value)}
                />
              </div>

              {gymPackage.canChoosePt && actionType !== 'UPGRADE_ONLY' && (
                <div className="form-group">
                  <label>Chọn Huấn luyện viên cá nhân (PT)</label>
                  <select 
                    value={selectedPtId} 
                    onChange={(e) => setSelectedPtId(e.target.value)}
                    required
                    style={{ width: '100%', padding: '12px', borderRadius: '8px', border: '1px solid rgba(255,255,255,0.1)', background: 'rgba(15,23,42,0.5)', color: 'white', marginBottom: '15px' }}
                  >
                    <option value="">-- Vui lòng chọn một Huấn luyện viên --</option>
                    {pts.map(pt => (
                      <option key={pt.userId} value={pt.userId}>
                        {pt.user?.fullName} - {pt.specialization} (Đánh giá: {pt.ratingScore || 'Chưa có'}/5)
                      </option>
                    ))}
                  </select>
                </div>
              )}

              <div className="form-group">
                <label>Phương thức thanh toán</label>
                <div className="payment-methods">
                  <div 
                    className={`payment-method-card ${paymentMethod === 'BANK' ? 'selected' : ''}`}
                    onClick={() => setPaymentMethod('BANK')}
                  >
                    <CreditCard size={28} style={{ marginBottom: '8px' }} />
                    <div>Chuyển khoản</div>
                  </div>
                  <div 
                    className={`payment-method-card ${paymentMethod === 'CASH' ? 'selected' : ''}`}
                    onClick={() => setPaymentMethod('CASH')}
                  >
                    <Banknote size={28} style={{ marginBottom: '8px' }} />
                    <div>Tiền mặt tại quầy</div>
                  </div>
                </div>
              </div>

              <div className="total-section">
                <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                  <span className="total-label">Tổng thanh toán dự kiến:</span>
                  {actionType !== 'UPGRADE_ONLY' && pricing.discountPercent > 0 && (
                    <span style={{ fontSize: '0.9rem', color: '#4ade80' }}>
                      Được giảm {pricing.discountPercent}% cho số ngày mua mới
                    </span>
                  )}
                  {pricing.proration && (
                    <div style={{ fontSize: '0.9rem', color: '#94a3b8', marginTop: '5px' }}>
                      Proration (Số ngày còn lại: {pricing.proration.remaining} ngày):<br/>
                      - Khấu trừ gói cũ: {formatCurrency(pricing.proration.credit)}<br/>
                      - Phí gói mới: {formatCurrency(pricing.proration.newCost)}<br/>
                      - Phí nâng cấp: <strong style={{ color: '#f97316' }}>{formatCurrency(actionType === 'UPGRADE_ONLY' ? pricing.final : pricing.proration.upgradeCost)}</strong>
                    </div>
                  )}
                </div>
                <div style={{ textAlign: 'right' }}>
                  <div className="total-amount" style={{ color: '#f97316' }}>
                    {formatCurrency(pricing.final)}
                  </div>
                </div>
              </div>

              <button type="submit" className="btn-submit-buy" disabled={submitting}>
                {submitting ? 'Đang xử lý...' : 'XÁC NHẬN GIAO DỊCH'}
              </button>
            </form>
          )}

          {!gymPackage && !error && (
            <div style={{ textAlign: 'center' }}>
              <Link to="/packages" className="btn-dashboard">Quay lại trang Gói tập</Link>
            </div>
          )}
        </div>
      </div>
    </MainLayout>
  );
};

export default BuyPackagePage;
