import React, { useState, useEffect } from 'react';
import { useLocation, Link } from 'react-router-dom';
import MainLayout from '../../components/layout/MainLayout';
import packageService from '../../services/packageService';
import ptService from '../../services/ptService';
import membershipService from '../../services/membershipService';
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
  const [promoCode, setPromoCode] = useState('');
  const [paymentMethod, setPaymentMethod] = useState('BANK');
  const [selectedPtId, setSelectedPtId] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [successData, setSuccessData] = useState(null);

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

        const discountData = await packageService.getPublicDiscounts();
        setDiscounts(discountData);
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

  const handleSubmit = async (e) => {
    e.preventDefault();
    
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
        promotionCode: promoCode || null,
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
            <h2>Đăng ký thành công!</h2>
            <p>
              Bạn đã đăng ký gói <strong>{successData.packageName}</strong> với thời hạn <strong>{successData.durationDays} ngày</strong>.<br/>
              Mã giao dịch của bạn là: <strong>#{successData.transactionId}</strong><br/>
              Tổng tiền: <strong>{formatCurrency(successData.finalAmount)}</strong>
            </p>
            <div style={{ background: 'rgba(255,255,255,0.05)', padding: '20px', borderRadius: '8px', marginBottom: '30px', textAlign: 'left' }}>
              <p style={{ margin: 0, color: '#f1f5f9' }}>
                Vui lòng thanh toán tại quầy lễ tân hoặc chuyển khoản theo thông tin sau:<br/><br/>
                Ngân hàng: <strong>Vietcombank</strong><br/>
                STK: <strong>1234567890</strong><br/>
                Chủ TK: <strong>GYMPRO VN</strong><br/>
                Nội dung: <strong>GYMPRO {successData.transactionId}</strong>
              </p>
            </div>
            <p style={{ color: '#f97316', fontSize: '0.9rem' }}>
              Gói tập của bạn sẽ được kích hoạt ngay sau khi Admin xác nhận thanh toán.
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
            <form className="buy-form" onSubmit={handleSubmit}>
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
                <label>Mã khuyến mãi (Nếu có)</label>
                <input 
                  type="text" 
                  placeholder="Nhập mã giảm giá..." 
                  value={promoCode}
                  onChange={(e) => setPromoCode(e.target.value)}
                />
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

              <button type="submit" className="btn-submit-buy" disabled={submitting}>
                {submitting ? 'Đang xử lý...' : 'XÁC NHẬN ĐĂNG KÝ'}
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
