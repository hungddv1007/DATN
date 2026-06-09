import React from 'react';
import { Link } from 'react-router-dom';
import { Check, X, Star, Dumbbell, Users, Utensils } from 'lucide-react';
import MainLayout from '../../components/layout/MainLayout';
import './PackagesPage.css';

const packages = [
  {
    name: 'BASIC',
    price: '500.000đ',
    duration: '1 tháng',
    description: 'Phù hợp cho người mới bắt đầu muốn tự tập luyện.',
    icon: <Dumbbell size={32} />,
    features: [
      { text: 'Tập tự do tại phòng gym', included: true },
      { text: 'Xem blog & video tập luyện', included: true },
      { text: 'Sử dụng thiết bị cơ bản', included: true },
      { text: 'PT kèm riêng', included: false },
      { text: 'Lộ trình tập luyện', included: false },
      { text: 'Chế độ ăn do PT lên', included: false },
    ],
    color: '#3b82f6',
    popular: false,
  },
  {
    name: 'PREMIUM',
    price: '2.000.000đ',
    duration: '1 tháng',
    description: 'Có PT hướng dẫn, lộ trình bài bản, điểm danh theo dõi.',
    icon: <Users size={32} />,
    features: [
      { text: 'Tất cả quyền lợi Basic', included: true },
      { text: 'PT gán ngẫu nhiên kèm riêng', included: true },
      { text: 'Xem lộ trình tập luyện', included: true },
      { text: 'Điểm danh buổi tập', included: true },
      { text: 'Đánh giá PT', included: true },
      { text: 'Chế độ ăn do PT lên', included: false },
    ],
    color: '#f97316',
    popular: true,
  },
  {
    name: 'VIP',
    price: '5.000.000đ',
    duration: '1 tháng',
    description: 'Trải nghiệm cao cấp nhất: chọn PT, chế độ ăn riêng.',
    icon: <Utensils size={32} />,
    features: [
      { text: 'Tất cả quyền lợi Premium', included: true },
      { text: 'Được CHỌN PT yêu thích', included: true },
      { text: 'Chế độ ăn do PT thiết kế', included: true },
      { text: 'Ưu tiên hỗ trợ', included: true },
      { text: 'Free: Khăn tập, Nước uống', included: true },
      { text: 'Tư vấn dinh dưỡng 1-1', included: true },
    ],
    color: '#a855f7',
    popular: false,
  },
];

const PackagesPage = () => {
  return (
    <MainLayout>
      <div className="packages-page">
        <div className="packages-hero">
          <h1>Chọn Gói Tập Phù Hợp</h1>
          <p>Ba gói tập với mức giá hợp lý, đáp ứng mọi nhu cầu tập luyện của bạn.</p>
        </div>

        <div className="packages-container">
          {packages.map((pkg) => (
            <div key={pkg.name} className={`pkg-card ${pkg.popular ? 'pkg-popular' : ''}`}>
              {pkg.popular && <div className="pkg-badge">PHỔ BIẾN NHẤT</div>}
              <div className="pkg-icon" style={{ color: pkg.color }}>{pkg.icon}</div>
              <h2 className="pkg-name" style={{ color: pkg.color }}>{pkg.name}</h2>
              <div className="pkg-price">{pkg.price}<span>/{pkg.duration}</span></div>
              <p className="pkg-desc">{pkg.description}</p>
              <ul className="pkg-features">
                {pkg.features.map((f, i) => (
                  <li key={i} className={f.included ? '' : 'disabled'}>
                    {f.included
                      ? <Check size={16} className="icon-check" />
                      : <X size={16} className="icon-x" />}
                    {f.text}
                  </li>
                ))}
              </ul>
              <Link to="/register" className="pkg-btn" style={{ background: pkg.color }}>
                ĐĂNG KÝ NGAY
              </Link>
            </div>
          ))}
        </div>
      </div>
    </MainLayout>
  );
};

export default PackagesPage;
