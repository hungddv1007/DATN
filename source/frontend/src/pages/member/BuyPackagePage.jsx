import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate, Link } from 'react-router-dom';
import MainLayout from '../../components/layout/MainLayout';
import packageService from '../../services/packageService';
import ptService from '../../services/ptService';
import membershipService from '../../services/membershipService';
import { CreditCard, Banknote, CheckCircle, Info } from 'lucide-react';
import './BuyPackagePage.css';

const BuyPackagePage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  
  // Lấy pkgId từ query param: /member/buy-package?pkgId=1
  const queryParams = new URLSearchParams(location.search);
  const pkgId = queryParams.get('pkgId');

  const [gymPackage, setGymPackage] = useState(null);
  const [pts, setPts] = useState([]);
  const [discounts, setDiscounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  const [durationDays, setDurationDays] = useState(30);
  const [promoCode, setPromoCode] = useState('');
  const [paymentMethod, setPaymentMethod] = useState('BANK'); // BANK or CASH
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
        setDurationDays(Math.max(30, pkgData.minDays));
        
        // Nếu gói tập cho phép chọn PT, tải danh sách PT
        if (pkgData.canChoosePt) {
          const ptsData = await ptService.getAllPtProfiles();
          setPts(ptsData);
        }

        // Tải danh sách chiết khấu để tính toán live
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

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Nếu bắt buộc chọn PT mà chưa chọn thì báo lỗi
    if (gymPackage?.canChoosePt && !selectedPtId) {
      setError('Vui lòng chọn một Huấn luyện viên cá nhân!');
      return;
    }

    if (gymPackage && durationDays < gymPackage.minDays) {
      setError(`Gói này yêu cầu số ngày đăng ký tối thiểu là ${gymPackage.minDays} ngày!`);
      return;
    }
    
    setSubmitting(true);
    setError('');

    try {
      const data = await membershipService.registerPackage({
        packageId: parseInt(pkgId),
        durationDays: parseInt(durationDays),
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

  // Tính toán số tiền trực tiếp trên giao diện
  const calculateLivePrice = () => {
    if (!gymPackage) return { gross: 0, discountPercent: 0, afterDiscount: 0 };
    
    const gross = gymPackage.dailyPrice * durationDays;
    
    // Lọc chiết khấu cho gói tập này
    const pkgDiscounts = discounts.filter(d => d.packageId === null || d.packageId === gymPackage.id);
    const applicable = pkgDiscounts.filter(d => d.minDays <= durationDays);
    
    const discountPercent = applicable.length > 0
      ? Math.max(...applicable.map(d => d.discountPercent))
      : 0;
      
    const afterDiscount = gross * (1 - discountPercent / 100);
    
    return { gross, discountPercent, afterDiscount };
  };

  const { gross, discountPercent, afterDiscount } = calculateLivePrice();

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
              <div className="form-group">
                <label>Số ngày đăng ký tập luyện</label>
                <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
                  <input 
                    type="number" 
                    min={gymPackage.minDays}
                    value={durationDays}
                    onChange={(e) => setDurationDays(Math.max(gymPackage.minDays, parseInt(e.target.value) || 0))}
                    style={{ flex: 1 }}
                    required
                  />
                  <span style={{ color: '#94a3b8', fontWeight: 'bold' }}>ngày</span>
                </div>
                <small style={{ color: '#94a3b8', marginTop: '5px', display: 'block' }}>
                  Gói {gymPackage.name} yêu cầu đăng ký tối thiểu {gymPackage.minDays} ngày.
                </small>
              </div>

              {/* Gợi ý chiết khấu */}
              <div style={{ background: 'rgba(249, 115, 22, 0.05)', padding: '12px', borderRadius: '8px', border: '1px dashed rgba(249, 115, 22, 0.2)', marginBottom: '20px', display: 'flex', gap: '10px', alignItems: 'flex-start' }}>
                <Info size={18} color="#f97316" style={{ flexShrink: 0, marginTop: '2px' }} />
                <div style={{ fontSize: '0.85rem', color: '#cbd5e1' }}>
                  <strong>Ưu đãi đăng ký dài hạn:</strong><br />
                  Đăng ký từ 90 ngày (giảm 5%), 180 ngày (giảm 10%), 365 ngày (giảm 15%). Đăng ký càng dài càng tiết kiệm!
                </div>
              </div>

              {gymPackage.canChoosePt && (
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
              <div style={{ background: 'rgba(15,23,42,0.4)', padding: '15px', borderRadius: '8px', marginBottom: '20px', border: '1px solid rgba(255,255,255,0.05)' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px', color: '#94a3b8', fontSize: '0.9rem' }}>
                  <span>Đơn giá/ngày:</span>
                  <span>{formatCurrency(gymPackage.dailyPrice)}/ngày</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px', color: '#94a3b8', fontSize: '0.9rem' }}>
                  <span>Số ngày tập:</span>
                  <span>{durationDays} ngày</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px', color: '#94a3b8', fontSize: '0.9rem' }}>
                  <span>Giá gốc:</span>
                  <span>{formatCurrency(gross)}</span>
                </div>
                {discountPercent > 0 && (
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px', color: '#22c55e', fontSize: '0.9rem' }}>
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
