import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Check, X, Dumbbell, Users, Utensils } from 'lucide-react';
import MainLayout from '../../components/layout/MainLayout';
import packageService from '../../services/packageService';
import { useAuth } from '../../context/AuthContext';
import './PackagesPage.css';

const PackagesPage = () => {
  const [packages, setPackages] = useState([]);
  const [loading, setLoading] = useState(true);
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchPackages = async () => {
      try {
        const data = await packageService.getAllPackages();
        // Sắp xếp theo giá để render đẹp mắt
        const sorted = data.sort((a, b) => a.price - b.price);
        setPackages(sorted);
      } catch (error) {
        console.error('Failed to fetch packages:', error);
      } finally {
        setLoading(false);
      }
    };
    fetchPackages();
  }, []);

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
  };

  // Helper để lấy màu và icon theo level của gói tập (dựa vào index)
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

  return (
    <MainLayout>
      <div className="packages-page">
        <div className="packages-hero">
          <h1>Chọn Gói Tập Phù Hợp</h1>
          <p>Các gói tập với mức giá hợp lý, đáp ứng mọi nhu cầu tập luyện của bạn.</p>
        </div>

        {loading ? (
          <div style={{ textAlign: 'center', padding: '50px', color: '#94a3b8' }}>Đang tải danh sách gói tập...</div>
        ) : (
          <div className="packages-container">
            {packages.map((pkg, index) => {
              const style = getPackageStyle(index);
              
              return (
                <div key={pkg.id} className={`pkg-card ${style.popular ? 'pkg-popular' : ''}`}>
                  {style.popular && <div className="pkg-badge">PHỔ BIẾN NHẤT</div>}
                  <div className="pkg-icon" style={{ color: style.color }}>{style.icon}</div>
                  
                  <h2 className="pkg-name" style={{ color: style.color }}>{pkg.name}</h2>
                  <div className="pkg-price">
                    {formatCurrency(pkg.price)}<span>/{pkg.durationDays} ngày</span>
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
                    <li><Check size={16} className="icon-check" />Xem lộ trình tập luyện</li>
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
