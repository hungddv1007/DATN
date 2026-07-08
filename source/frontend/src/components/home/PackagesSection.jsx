import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Check, X } from 'lucide-react';
import packageService from '../../services/packageService';
import './PackagesSection.css';

const PackagesSection = () => {
  const navigate = useNavigate();
  const [packages, setPackages] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchPackages = async () => {
      try {
        const data = await packageService.getAllPackages();
        const sorted = data.sort((a, b) => a.dailyPrice - b.dailyPrice);
        setPackages(sorted);
      } catch (err) {
        console.error('Lỗi tải gói tập:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchPackages();
  }, []);

  const formatPrice = (price) => {
    return new Intl.NumberFormat('vi-VN').format(price);
  };

  // Map card style theo thứ tự giá
  const getCardClass = (index) => {
    if (index === 0) return 'basic-card';
    if (index === 1) return 'premium-card';
    return 'vip-card';
  };

  if (loading) return null; // Không hiển thị gì khi đang tải

  return (
    <section className="packages-section">
      <div className="container">
        <h2 className="section-title">HỆ THỐNG GÓI TẬP</h2>
        
        <div className="packages-grid">
          {packages.map((pkg, index) => (
            <div className={`package-card ${getCardClass(index)}`} key={pkg.id}>
              {index === packages.length - 1 && <div className="special-badge">SPECIAL</div>}
              <h3 className="package-name">{pkg.name}</h3>
              <div className="package-price" style={{display: 'flex', flexDirection: 'column', gap: '5px'}}>
                <span>Giá: {formatPrice(pkg.dailyPrice)}đ / ngày</span>
                <span style={{fontSize: '0.8em', opacity: 0.8}}>Đăng ký tối thiểu {pkg.minDays} ngày</span>
              </div>
              <ul className="package-features">
                <li><Check className={index >= 2 ? 'check-icon-vip' : 'check-icon'} /> Sử dụng thiết bị phòng tập</li>
                <li className={pkg.hasPt ? '' : 'disabled'}>
                  {pkg.hasPt ? <Check className={index >= 2 ? 'check-icon-vip' : 'check-icon'} /> : <X className="x-icon" />} PT hướng dẫn
                </li>
                <li className={pkg.canChoosePt ? '' : 'disabled'}>
                  {pkg.canChoosePt ? <Check className={index >= 2 ? 'check-icon-vip' : 'check-icon'} /> : <X className="x-icon" />} Được chọn PT
                </li>
                <li className={pkg.hasMealPlan ? '' : 'disabled'}>
                  {pkg.hasMealPlan ? <Check className={index >= 2 ? 'check-icon-vip' : 'check-icon'} /> : <X className="x-icon" />} Khẩu phần ăn riêng
                </li>
              </ul>
              <button 
                className={`btn-package ${index === 0 ? 'btn-basic' : index === 1 ? 'btn-premium' : 'btn-vip'}`} 
                onClick={() => navigate('/services')}
              >
                ĐĂNG KÝ GÓI
              </button>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};

export default PackagesSection;
