import React, { useState, useEffect } from 'react';
import { useLocation, Link } from 'react-router-dom';
import MainLayout from '../../components/layout/MainLayout';
import PolicyDialog from '../../components/common/PolicyDialog';
import packageService from '../../services/packageService';
import ptService from '../../services/ptService';
import membershipService from '../../services/membershipService';
import paymentService from '../../services/paymentService';
import { getPolicy } from '../../services/policyService';
import { CreditCard, Banknote, CheckCircle, Clock, Tag } from 'lucide-react';
import './BuyPackagePage.css';

// Các mốc thời gian cố định
const DURATION_MILESTONES = [
  { key: '1D',   days: 1,    label: '1 ngày' },
  { key: '1W',   days: 7,    label: '1 tuần' },
  { key: '1M',   days: 30,   label: '1 tháng' },
  { key: '3M',   days: 90,   label: '3 tháng' },
  { key: '6M',   days: 180,  label: '6 tháng' },
  { key: '1Y',   days: 365,  label: '1 năm' },
  { key: '2Y',   days: 730,  label: '2 năm' },
];

const BuyPackagePage = () => {
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const pkgId = queryParams.get('pkgId');

  const [gymPackage, setGymPackage] = useState(null);
  const [pts, setPts] = useState([]);
  const [discounts, setDiscounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  const [selectedMilestone, setSelectedMilestone] = useState('1M'); // mặc định 1 tháng
  const [discountCode, setDiscountCode] = useState('');
  const [resolvedCodeType, setResolvedCodeType] = useState(null);
  const [terms, setTerms] = useState(null);
  const [acceptedTerms, setAcceptedTerms] = useState(false);
  const [showTerms, setShowTerms] = useState(false);
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [paymentMethod, setPaymentMethod] = useState('BANK');
  const [selectedPtId, setSelectedPtId] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [successData, setSuccessData] = useState(null);
  const [paymentInfo, setPaymentInfo] = useState(null);
  const [purchasePreview, setPurchasePreview] = useState(null);

  useEffect(() => {
    if (!pkgId) {
      setError('Không tìm thấy mã gói tập. Vui lòng quay lại trang Gói tập.');
      setLoading(false);
      return;
    }

    const fetchInitialData = async () => {
      try {
        const pkgData = await packageService.getPackageById(pkgId);
        
        if (pkgData.isActive === false) {
          setError('Gói tập này hiện đã ngừng cung cấp.');
          setGymPackage(null);
          return;
        }

        setGymPackage(pkgData);
        
        if (pkgData.canChoosePt) {
          const ptsData = await ptService.getAllPtProfiles();
          setPts(ptsData);
        }

        const [discountData, publicPaymentInfo, termsData] = await Promise.all([
          packageService.getPublicDiscounts(),
          paymentService.getPublicPaymentInfo(),
          getPolicy('MEMBERSHIP_TERMS'),
        ]);
        setDiscounts(discountData);
        setPaymentInfo(publicPaymentInfo);
        setTerms(termsData);
      } catch (err) {
        setError('Lỗi tải thông tin gói tập. Có thể gói tập không tồn tại.');
      } finally {
        setLoading(false);
      }
    };

    fetchInitialData();
  }, [pkgId]);

  const getDurationDays = () => {
    const milestone = DURATION_MILESTONES.find(m => m.key === selectedMilestone);
    return milestone ? milestone.days : 30;
  };

  const getDiscountForDays = (days) => {
    if (!gymPackage) return 0;
    const pkgDiscounts = discounts.filter(d => d.packageId === null || d.packageId === gymPackage.id);
    const applicable = pkgDiscounts.filter(d => d.minDays <= days);
    return applicable.length > 0 ? Math.max(...applicable.map(d => d.discountPercent)) : 0;
  };

  const submitPurchase = async () => {
    if (gymPackage?.canChoosePt && !selectedPtId) {
      setError('Vui lòng chọn một Huấn luyện viên cá nhân!');
      return;
    }
    
    setSubmitting(true);
    setError('');

    try {
      const data = await membershipService.registerPackage({
        packageId: parseInt(pkgId),
        durationDays: getDurationDays(),
        promotionCode: resolvedCodeType === 'PROMOTION' ? discountCode.trim() : null,
        referralCode: resolvedCodeType === 'SALE_REFERRAL' ? discountCode.trim() : null,
        acceptedTerms,
        termsVersionId: terms.id,
        paymentMethod: paymentMethod,
        ptId: selectedPtId ? parseInt(selectedPtId) : null
      });
      setSuccessData(data);
    } catch (err) {
      const resData = err.response?.data;
      if (resData && typeof resData === 'object') {
        const firstError = resData.message || Object.values(resData)[0];
        setError(firstError || 'Đăng ký thất bại. Vui lòng thử lại.');
      } else {
        setError('Lỗi kết nối máy chủ!');
      }
    } finally {
      setSubmitting(false);
    }
  };

  const reviewPurchase = async (e) => {
    e.preventDefault();
    if (!acceptedTerms) { setError('Bạn phải đọc và đồng ý Điều khoản thành viên.'); return; }
    if (gymPackage?.canChoosePt && !selectedPtId) { setError('Vui lòng chọn một Huấn luyện viên cá nhân!'); return; }
    setError('');
    setSubmitting(true);
    try {
      let preview;
      const normalizedCode = discountCode.trim().toUpperCase();
      if (!normalizedCode) {
        preview = await membershipService.previewPurchase(parseInt(pkgId), getDurationDays());
        setResolvedCodeType(null);
      } else {
        try {
          preview = await membershipService.previewPurchase(
            parseInt(pkgId), getDurationDays(), normalizedCode, null,
          );
          setResolvedCodeType('PROMOTION');
        } catch (promotionError) {
          try {
            preview = await membershipService.previewPurchase(
              parseInt(pkgId), getDurationDays(), null, normalizedCode,
            );
            setResolvedCodeType('SALE_REFERRAL');
          } catch {
            const invalidCodeError = new Error('Mã giảm giá không hợp lệ, đã hết hạn hoặc không áp dụng cho gói này.');
            invalidCodeError.cause = promotionError;
            throw invalidCodeError;
          }
        }
      }
      setPurchasePreview(preview);
      setShowConfirmation(true);
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Không thể kiểm tra mã và giá thanh toán.');
    } finally {
      setSubmitting(false);
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
  };

  const durationDays = getDurationDays();
  const gross = gymPackage ? gymPackage.dailyPrice * durationDays : 0;
  const discountPercent = getDiscountForDays(durationDays);
  const afterDiscount = gross * (1 - discountPercent / 100);

  if (loading) {
    return (
      <MainLayout>
        <div className="buy-package-page">
          <p style={{ color: '#94a3b8', marginTop: '100px' }}>Đang tải thông tin gói tập...</p>
        </div>
      </MainLayout>
    );
  }

  // Màn hình thành công
  if (successData) {
    return (
      <MainLayout>
        <div className="buy-package-page">
          <div className="buy-package-container success-container">
            <CheckCircle size={80} color="#22c55e" style={{ margin: '0 auto 20px' }} />
            <h2>Đã tạo yêu cầu đăng ký!</h2>
            <p>
              Yêu cầu mua gói <strong>{successData.packageName}</strong> thời hạn <strong>{successData.durationDays} ngày</strong> đã được ghi nhận.<br/>
              Mã giao dịch của bạn là: <strong>#{successData.transactionId}</strong><br/>
              Tổng tiền: <strong>{formatCurrency(successData.finalAmount)}</strong>
            </p>
            <div style={{ background: 'rgba(255,255,255,0.05)', padding: '20px', borderRadius: '8px', marginBottom: '30px', textAlign: 'left' }}>
              {paymentMethod === 'BANK' ? (
                paymentInfo?.bankAccountNumber ? (
                  <p style={{ margin: 0, color: '#f1f5f9' }}>
                    Vui lòng chuyển khoản theo thông tin sau:<br/><br/>
                    Ngân hàng: <strong>{paymentInfo.bankName}</strong><br/>
                    STK: <strong>{paymentInfo.bankAccountNumber}</strong><br/>
                    Chủ TK: <strong>{paymentInfo.bankAccountHolder}</strong><br/>
                    Nội dung: <strong>{paymentInfo.transferPrefix || 'GYMPRO'} {successData.transactionId}</strong>
                  </p>
                ) : (
                  <p style={{ margin: 0, color: '#f1f5f9' }}>
                    Thông tin chuyển khoản chưa được cấu hình. Vui lòng liên hệ quầy lễ tân.
                  </p>
                )
              ) : (
                <p style={{ margin: 0, color: '#f1f5f9' }}>
                  Vui lòng thanh toán tiền mặt tại quầy lễ tân và cung cấp mã giao dịch <strong>#{successData.transactionId}</strong>.
                </p>
              )}
            </div>
            <p style={{ color: '#f97316', fontSize: '0.9rem' }}>
              Gói tập sẽ được kích hoạt sau khi Admin xác nhận. Giao dịch chờ quá {paymentInfo?.pendingExpirationHours || 24} giờ sẽ tự động hủy.
            </p>
            <Link to="/member/dashboard" className="btn-dashboard">Về Dashboard</Link>
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
            <h1>Thanh Toán Gói Tập</h1>
            <p>Hoàn tất đăng ký để bắt đầu hành trình thay đổi bản thân</p>
          </div>

          {error && <div className="buy-alert error">{error}</div>}

          {gymPackage && (
            <div className="package-summary">
              <div className="summary-info">
                <h3>Gói {gymPackage.name}</h3>
                <p>{gymPackage.description}</p>
              </div>
              <div className="summary-price">
                <span className="price">{formatCurrency(gymPackage.dailyPrice)}</span>
                <span className="duration">/ ngày</span>
              </div>
            </div>
          )}

          {gymPackage && (
            <form className="buy-form" onSubmit={reviewPurchase}>
              {/* Chọn mốc thời gian */}
              <div className="form-group">
                <label><Clock size={16} style={{ verticalAlign: 'middle', marginRight: '6px' }} />Chọn thời hạn đăng ký</label>
                <div className="duration-milestones">
                  {DURATION_MILESTONES.map((ms) => {
                    const msDiscount = getDiscountForDays(ms.days);
                    const isSelected = selectedMilestone === ms.key;
                    const msPrice = gymPackage.dailyPrice * ms.days * (1 - msDiscount / 100);
                    
                    return (
                      <button
                        type="button"
                        key={ms.key}
                        className={`milestone-btn ${isSelected ? 'selected' : ''}`}
                        onClick={() => setSelectedMilestone(ms.key)}
                      >
                        <span className="ms-label">{ms.label}</span>
                        <span className="ms-price">{formatCurrency(msPrice)}</span>
                        {msDiscount > 0 && (
                          <span className="ms-discount">
                            <Tag size={11} /> -{msDiscount}%
                          </span>
                        )}
                      </button>
                    );
                  })}
                </div>
              </div>

              {/* Gợi ý chiết khấu */}
              {discountPercent > 0 && (
                <div className="discount-banner">
                  <Tag size={18} />
                  <div>
                    <strong>Bạn được giảm {discountPercent}%!</strong><br/>
                    Tiết kiệm <span className="saved-amount">{formatCurrency(gross - afterDiscount)}</span> khi đăng ký {DURATION_MILESTONES.find(m => m.key === selectedMilestone)?.label}
                  </div>
                </div>
              )}

              {gymPackage.canChoosePt && (
                <div className="form-group">
                  <label>Chọn Huấn luyện viên cá nhân (PT)</label>
                  <select 
                    value={selectedPtId} 
                    onChange={(e) => setSelectedPtId(e.target.value)}
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
                <label>Mã giảm giá (Nếu có)</label>
                <input
                  type="text"
                  placeholder="Nhập mã khuyến mãi hoặc mã được nhân viên giới thiệu..."
                  value={discountCode}
                  onChange={(e) => {
                    setDiscountCode(e.target.value.toUpperCase());
                    setResolvedCodeType(null);
                    setPurchasePreview(null);
                  }}
                />
                <small className="discount-code-hint">Hệ thống sẽ tự nhận diện loại mã và kiểm tra mức giảm trước khi tạo giao dịch.</small>
              </div>

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

              {/* Bảng phân rã giá */}
              <div className="price-breakdown">
                <div className="breakdown-row">
                  <span>Đơn giá/ngày:</span>
                  <span>{formatCurrency(gymPackage.dailyPrice)}/ngày</span>
                </div>
                <div className="breakdown-row">
                  <span>Thời hạn:</span>
                  <span>{DURATION_MILESTONES.find(m => m.key === selectedMilestone)?.label} ({durationDays} ngày)</span>
                </div>
                <div className="breakdown-row">
                  <span>Giá gốc:</span>
                  <span>{formatCurrency(gross)}</span>
                </div>
                {discountPercent > 0 && (
                  <div className="breakdown-row discount-row">
                    <span>Chiết khấu dài hạn ({discountPercent}%):</span>
                    <span>-{formatCurrency(gross - afterDiscount)}</span>
                  </div>
                )}
              </div>

              <div className="total-section">
                <span className="total-label">Tổng thanh toán:</span>
                <span className="total-amount">{formatCurrency(afterDiscount)}</span>
              </div>

              <label className="terms-acceptance">
                <input type="checkbox" checked={acceptedTerms} onChange={e => setAcceptedTerms(e.target.checked)} />
                <span>Tôi đã đọc và đồng ý <button type="button" onClick={() => setShowTerms(true)}>Điều khoản thành viên hiện hành</button>.</span>
              </label>

              <button type="submit" className="btn-submit-buy" disabled={submitting}>
                {submitting ? 'Đang xử lý...' : 'XEM LẠI ĐĂNG KÝ'}
              </button>
            </form>
          )}

          {showConfirmation && <div className="purchase-confirm-overlay"><div className="purchase-confirm">
            <h2>Xác nhận đăng ký và thanh toán</h2>
            <p>Gói <b>{gymPackage.name}</b> · {durationDays} ngày</p>
            <p>Phương thức: <b>{paymentMethod === 'BANK' ? 'Chuyển khoản' : 'Tiền mặt tại quầy'}</b></p>
            <p>Mã áp dụng: <b>{discountCode.trim() || 'Không có'}</b></p>
            {purchasePreview?.codeDiscount > 0 && <p>
              {purchasePreview.codeType === 'SALE_REFERRAL' ? 'Ưu đãi mã giới thiệu' : 'Ưu đãi mã khuyến mãi'}:
              <b> -{purchasePreview.codeDiscount}%</b>
            </p>}
            <p className="confirm-total">Tổng thanh toán: {formatCurrency(purchasePreview?.finalAmount ?? afterDiscount)}</p>
            <p className="confirm-note">Sau bước này hệ thống tạo giao dịch chờ duyệt. Gói chỉ kích hoạt khi Admin xác nhận đã thanh toán.</p>
            <div><button className="btn-dashboard" onClick={() => setShowConfirmation(false)}>Quay lại</button>
              <button className="btn-submit-buy" disabled={submitting} onClick={submitPurchase}>XÁC NHẬN TẠO GIAO DỊCH</button></div>
          </div></div>}

          {showTerms && <PolicyDialog
            policy={terms}
            onClose={() => setShowTerms(false)}
            onAgree={() => {
              setAcceptedTerms(true);
              setShowTerms(false);
            }}
          />}

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
