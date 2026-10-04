import { useEffect, useState } from 'react';
import SliderRaw from 'react-slick';
import { Quote, Star } from 'lucide-react';
import api from '../../services/api';
import { resolveFileUrl } from '../../utils/fileUrl';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';
import './CustomerReviewsSection.css';

const Slider = SliderRaw.default || SliderRaw;

const CustomerReviewsSection = () => {
  const [reviews, setReviews] = useState([]);

  useEffect(() => {
    api.get('/public/service-reviews/featured')
      .then(response => setReviews(response.data || []))
      .catch(() => setReviews([]));
  }, []);

  if (reviews.length === 0) return null;

  const visibleSlides = maximum => (
    reviews.length === 1 ? 1 : Math.min(reviews.length - 1, maximum)
  );

  const settings = {
    dots: true,
    arrows: reviews.length > 1,
    infinite: reviews.length > 1,
    speed: 650,
    slidesToShow: visibleSlides(3),
    slidesToScroll: 1,
    autoplay: reviews.length > 1,
    autoplaySpeed: 4500,
    pauseOnHover: true,
    responsive: [
      { breakpoint: 1024, settings: { slidesToShow: visibleSlides(2) } },
      { breakpoint: 680, settings: { slidesToShow: 1, arrows: false } },
    ],
  };

  return (
    <section className="customer-reviews">
      <div className="reviews-inner">
        <p className="eyebrow">TRẢI NGHIỆM THỰC TẾ</p>
        <h2>Khách hàng nói gì về GymPro?</h2>
        <p className="reviews-description">Những đánh giá tiêu biểu từ hội viên đã trực tiếp sử dụng dịch vụ tại GymPro.</p>

        <div className="reviews-carousel">
          <Slider {...settings}>
            {reviews.map(review => (
              <div className="review-slide" key={review.id}>
                <article className="customer-review-card">
                  <Quote className="review-quote-icon" size={30} />
                  <div className="review-stars" aria-label={`${review.ratingStar} trên 5 sao`}>
                    {Array.from({ length: 5 }, (_, index) => (
                      <Star key={index} size={18} fill={index < review.ratingStar ? 'currentColor' : 'none'} />
                    ))}
                  </div>
                  <blockquote>“{review.comment}”</blockquote>
                  <footer>
                    {review.memberAvatar
                      ? <img src={resolveFileUrl(review.memberAvatar)} alt={`Ảnh đại diện ${review.memberName}`} />
                      : <span>{review.memberName?.charAt(0)?.toUpperCase() || 'G'}</span>}
                    <div><b>{review.memberName}</b><small>Hội viên gói {review.packageName}</small></div>
                  </footer>
                </article>
              </div>
            ))}
          </Slider>
        </div>
      </div>
    </section>
  );
};

export default CustomerReviewsSection;
