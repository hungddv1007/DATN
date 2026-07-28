import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Check, X, Dumbbell, Users, Utensils, Tag, ChevronDown } from 'lucide-react';
import MainLayout from '../../components/layout/MainLayout';
import packageService from '../../services/packageService';
import { useAuth } from '../../context/AuthContext';
import './PackagesPage.css';

// Các mốc thời gian cố định
const DURATION_MILESTONES = [
  { key: '1D',  days: 1,    label: '1 ngày' },
  { key: '1W',  days: 7,    label: '1 tuần' },
  { key: '1M',  days: 30,   label: '1 tháng' },
  { key: '3M',  days: 90,   label: '3 tháng' },
  { key: '6M',  days: 180,  label: '6 tháng' },
  { key: '1Y',  days: 365,  label: '1 năm' },
  { key: '2Y',  days: 730,  label: '2 năm' },
];

const PackagesPage = () => {
  const [packages, setPackages] = useState([]);
  const [discounts, setDiscounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedDuration, setSelectedDuration] = useState('1M'); // Mặc định 1 tháng
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [pkgData, discountData] = await Promise.all([
          packageService.getAllPackages(),
          packageService.getPublicDiscounts()
        ]);
        const sorted = pkgData.sort((a, b) => a.dailyPrice - b.dailyPrice);
        setPackages(sorted);
        setDiscounts(discountData);
      } catch (error) {
        console.error('Failed to fetch packages:', error);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
  };

  const getCurrentMilestone = () => {
    return DURATION_MILESTONES.find(m => m.key === selectedDuration) || DURATION_MILESTONES[2];
  };

  const getDiscountForPackage = (pkgId, days) => {
    const pkgDiscounts = discounts.filter(d => d.packageId === null || d.packageId === pkgId);
    const applicable = pkgDiscounts.filter(d => d.minDays <= days);
    return applicable.length > 0 ? Math.max(...applicable.map(d => d.discountPercent)) : 0;
  };

  const getPackageStyle = (index) => {
    if (index === 0) return { color: '#3b82f6', icon: <Dumbbell size={32} />, popular: false };
    if (index === 1) return { color: '#f97316', icon: <Users size={32} />, popular: true };
    return { color: '#a855f7', icon: <Utensils size={32} />, popular: false };
  };

  const handleRegisterClick = (e, pkgId) => {
    e.preventDefault();
    if (!user) {
      alert("Vui lòng đăng nhập để đăng ký gói tập!");
      navigate('/login');
    } else if (user.role !== 'MEMBER') {
      alert("Chỉ Hội viên mới có thể mua gói tập!");
    } else {
      navigate(`/member/buy-package?pkgId=${pkgId}`);
    }
  };

  const ms = getCurrentMilestone();

  return (
    <MainLayout>
      <div className="packages-page">
        <div className="packages-hero">
          <h1>Chọn Gói Tập Phù Hợp</h1>
          <p>Các gói tập với mức giá hợp lý, đáp ứng mọi nhu cầu tập luyện của bạn.</p>
        </div>

        {/* Duration Selector */}
        <div className="duration-selector-wrapper">
          <div className="duration-selector">
            {DURATION_MILESTONES.map((m) => (
              <button
                key={m.key}
                className={`dur-btn ${selectedDuration === m.key ? 'active' : ''}`}
                onClick={() => setSelectedDuration(m.key)}
              >
                {m.label}
              </button>
            ))}
          </div>
        </div>

        {loading ? (
          <div style={{ textAlign: 'center', padding: '50px', color: '#94a3b8' }}>Đang tải danh sách gói tập...</div>
        ) : (
          <div className="packages-container">
            {packages.map((pkg, index) => {
              const style = getPackageStyle(index);
              const discountPct = getDiscountForPackage(pkg.id, ms.days);
              const grossPrice = pkg.dailyPrice * ms.days;
              const finalPrice = grossPrice * (1 - discountPct / 100);
              
              return (
                <div key={pkg.id} className={`pkg-card ${style.popular ? 'pkg-popular' : ''}`}>
                  {style.popular && <div className="pkg-badge">PHỔ BIẾN NHẤT</div>}
                  <div className="pkg-icon" style={{ color: style.color }}>{style.icon}</div>
                  
                  <h2 className="pkg-name" style={{ color: style.color }}>{pkg.name}</h2>
                  
                  <div className="pkg-price-block">
                    {discountPct > 0 && (
                      <div className="pkg-original-price">{formatCurrency(grossPrice)}</div>
                    )}
                    <div className="pkg-price">
                      {formatCurrency(finalPrice)}
                    </div>
                    <div className="pkg-duration-label">/ {ms.label}</div>
                    {discountPct > 0 && (
                      <span className="pkg-discount-tag">-{discountPct}%</span>
                    )}
                  </div>
                  
                  <div className="pkg-daily-note">
                    Tương đương {formatCurrency(pkg.dailyPrice)}/ngày
                  </div>
                  
                  <p className="pkg-desc">{pkg.description}</p>
                  
                  <ul className="pkg-features">
                    <li className={pkg.hasPt ? '' : 'disabled'}>
                      {pkg.hasPt ? <Check size={16} className="icon-check" /> : <X size={16} className="icon-x" />}
                      Có PT hướng dẫn
                    </li>
                    <li className={pkg.canChoosePt ? '' : 'disabled'}>
                      {pkg.canChoosePt ? <Check size={16} className="icon-check" /> : <X size={16} className="icon-x" />}
                      Được chọn PT yêu thích
                    </li>
                    <li className={pkg.hasMealPlan ? '' : 'disabled'}>
                      {pkg.hasMealPlan ? <Check size={16} className="icon-check" /> : <X size={16} className="icon-x" />}
                      Chế độ ăn riêng biệt
                    </li>
                    <li><Check size={16} className="icon-check" />Sử dụng thiết bị phòng tập</li>
                    <li><Check size={16} className="icon-check" />Lịch kèm riêng 1-1 với PT</li>
                  </ul>
                  
                  <a 
                    href="#" 
                    className="pkg-btn" 
                    style={{ background: style.color }}
                    onClick={(e) => handleRegisterClick(e, pkg.id)}
                  >
                    ĐĂNG KÝ NGAY
                  </a>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </MainLayout>
  );
};

export default PackagesPage;
