import React from 'react';
import SliderRaw from 'react-slick';
import { Star } from 'lucide-react';
import "slick-carousel/slick/slick.css"; 
import "slick-carousel/slick/slick-theme.css";
import './PtSection.css';

const Slider = SliderRaw.default || SliderRaw;

const PtSection = () => {
  const pts = [
    { id: 1, name: 'Nguyễn Thành', spec: 'Giảm mỡ', rating: 4.9, img: 'https://images.unsplash.com/photo-1567598508481-65985588e295?q=80&w=200&auto=format&fit=crop' },
    { id: 2, name: 'Ngọc Trinh', spec: 'Giảm mỡ', rating: 4.9, img: 'https://images.unsplash.com/photo-1534438097544-77e891396a56?q=80&w=200&auto=format&fit=crop' },
    { id: 3, name: 'Ngọc Nam', spec: 'Tăng cơ', rating: 5.0, img: 'https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?q=80&w=200&auto=format&fit=crop' },
    { id: 4, name: 'Ngọc Thảo', spec: 'Tăng cơ', rating: 4.8, img: 'https://images.unsplash.com/photo-1518611012118-696072aa579a?q=80&w=200&auto=format&fit=crop' },
    { id: 5, name: 'Tuấn Anh', spec: 'Thể lực', rating: 4.9, img: 'https://images.unsplash.com/photo-1594381898411-846e7d193883?q=80&w=200&auto=format&fit=crop' },
  ];

  const settings = {
    dots: true,
    infinite: true,
    speed: 500,
    slidesToShow: 4,
    slidesToScroll: 1,
    responsive: [
      { breakpoint: 1024, settings: { slidesToShow: 3 } },
      { breakpoint: 768, settings: { slidesToShow: 2 } },
      { breakpoint: 480, settings: { slidesToShow: 1 } }
    ]
  };

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
                     <img src={pt.img} alt={pt.name} className="pt-img" />
                  </div>
                  <h3 className="pt-name">{pt.name}</h3>
                  <p className="pt-spec">Chuyên môn<br/><b>{pt.spec}</b></p>
                  <div className="pt-rating">
                    <Star size={18} fill="#eab308" color="#eab308" />
                    <span>{pt.rating}/5</span>
                  </div>
                  <button className="btn-view-profile">XEM HỒ SƠ</button>
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
