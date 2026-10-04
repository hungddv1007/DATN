import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import SliderRaw from 'react-slick';
import { Star } from 'lucide-react';
import ptService from '../../services/ptService';
import { resolveFileUrl } from '../../utils/fileUrl';
import "slick-carousel/slick/slick.css"; 
import "slick-carousel/slick/slick-theme.css";
import './PtSection.css';

const Slider = SliderRaw.default || SliderRaw;

const PtSection = () => {
  const navigate = useNavigate();
  const [pts, setPts] = useState([]);

  useEffect(() => {
    const fetchPts = async () => {
      try {
        const data = await ptService.getAllPtProfiles();
        // Chỉ lấy PT đang hoạt động, tối đa 8 người cho slider
        setPts(data.filter(pt => pt.ratingScore > 0).slice(0, 8));
      } catch (err) {
        console.error('Lỗi tải danh sách PT:', err);
      }
    };
    fetchPts();
  }, []);

  const settings = {
    dots: true,
    infinite: pts.length > 4,
    speed: 500,
    slidesToShow: Math.min(pts.length, 4),
    slidesToScroll: 1,
    autoplay: true,
    autoplaySpeed: 4000,
    responsive: [
      { breakpoint: 1024, settings: { slidesToShow: Math.min(pts.length, 3) } },
      { breakpoint: 768, settings: { slidesToShow: Math.min(pts.length, 2) } },
      { breakpoint: 480, settings: { slidesToShow: 1 } }
    ]
  };

  if (pts.length === 0) return null;

  return (
    <section className="pt-section">
      <div className="container">
        <h2 className="section-title">ĐỘI NGŨ HUẤN LUYỆN VIÊN (PT)</h2>
        <div className="carousel-wrapper">
          <Slider {...settings}>
            {pts.map(pt => (
              <div key={pt.id} className="pt-slide">
                <div className="pt-card">
                  <div className="pt-img-wrapper">
                    {pt.avatar ? (
                      <img src={resolveFileUrl(pt.avatar)} alt={pt.fullName} className="pt-img" />
                    ) : (
                      <div className="pt-img pt-avatar-fallback">
                        {pt.fullName?.charAt(0)?.toUpperCase() || '?'}
                      </div>
                    )}
                  </div>
                  <h3 className="pt-name">{pt.fullName}</h3>
                  <p className="pt-spec">Chuyên môn<br/><b>{pt.specialization || 'Đa năng'}</b></p>
                  <div className="pt-rating">
                    <Star size={18} fill="#eab308" color="#eab308" />
                    <span>{pt.ratingScore ? `${pt.ratingScore}/5` : 'Mới'}</span>
                  </div>
                  <button className="btn-view-profile" onClick={() => navigate('/pts')}>XEM HỒ SƠ</button>
                </div>
              </div>
            ))}
          </Slider>
        </div>
      </div>
    </section>
  );
};

export default PtSection;
