import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate, Link } from 'react-router-dom';
import MainLayout from '../../components/layout/MainLayout';
import packageService from '../../services/packageService';
import ptService from '../../services/ptService';
import membershipService from '../../services/membershipService';
import { CreditCard, Banknote, CheckCircle } from 'lucide-react';
import './BuyPackagePage.css';

const BuyPackagePage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  
  // Lấy pkgId từ query param: /member/buy-package?pkgId=1
  const queryParams = new URLSearchParams(location.search);
  const pkgId = queryParams.get('pkgId');

  const [gymPackage, setGymPackage] = useState(null);
  const [pts, setPts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
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

    const fetchPackageAndPts = async () => {
      try {
        const data = await packageService.getPackageById(pkgId);
        
        if (data.isActive === false) {
          setError('Gói tập này hiện đã ngừng cung cấp.');
          setGymPackage(null);
          return;
        }

        setGymPackage(data);
        
        // Nếu gói tập cho phép chọn PT, tải danh sách PT
        if (data.canChoosePt) {
          const ptsData = await ptService.getAllPtProfiles();
          setPts(ptsData);
        }
      } catch (err) {
        setError('Lỗi tải thông tin gói tập. Có thể gói tập không tồn tại.');
      } finally {
        setLoading(false);
      }
    };

    fetchPackageAndPts();
  }, [pkgId]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Nếu bắt buộc chọn PT mà chưa chọn thì báo lỗi
    if (gymPackage?.canChoosePt && !selectedPtId) {
      setError('Vui lòng chọn một Huấn luyện viên cá nhân!');
      return;
    }
    
    setSubmitting(true);
    setError('');

    try {
      const data = await membershipService.registerPackage({
        packageId: parseInt(pkgId),
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
              Bạn đã đăng ký gói <strong>{successData.packageName}</strong>.<br/>
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
                <span className="price">{formatCurrency(gymPackage.price)}</span>
                <span className="duration">/ {gymPackage.durationDays} ngày</span>
              </div>
            </div>
          )}

          {gymPackage && (
            <form className="buy-form" onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Mã khuyến mãi (Nếu có)</label>
                <input 
                  type="text" 
                  placeholder="Nhập mã giảm giá..." 
                  value={promoCode}
                  onChange={(e) => setPromoCode(e.target.value)}
                />
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
                      <option key={pt.userId} value={pt.userId}>
                        {pt.fullName} - {pt.specialty} (Đánh giá: {pt.rating || 'Chưa có'}/5)
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
                <span className="total-label">Tổng thanh toán:</span>
                <span className="total-amount">{formatCurrency(gymPackage.price)}</span>
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
