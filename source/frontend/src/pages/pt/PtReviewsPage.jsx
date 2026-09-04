import React, { useState, useEffect } from 'react';
import PtLayout from '../../components/layout/PtLayout';
import { SummaryCard, SummaryGrid } from '../../components/common/SummaryCards';
import api from '../../services/api';
import { Star, MessageSquare } from 'lucide-react';
import '../admin/AdminManagement.css';
import './PtReviewsPage.css';

const PtReviewsPage = () => {
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [profile, setProfile] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const profileRes = await api.get('/pt/profile');
        setProfile(profileRes.data);
        const reviewsRes = await api.get(`/pt-profiles/${profileRes.data.id}/reviews`);
        setReviews(reviewsRes.data);
      } catch (err) {
        console.error('Lỗi tải đánh giá:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const renderStars = (count) => {
    return Array.from({ length: 5 }, (_, i) => (
      <Star key={i} size={16} fill={i < count ? '#eab308' : 'transparent'} color={i < count ? '#eab308' : '#475569'} />
    ));
  };

  const avgRating = profile?.ratingScore || 0;
  const formattedAvgRating = avgRating ? Number(avgRating).toFixed(1) : '—';
  const fiveStarReviews = reviews.filter(review => review.ratingStar === 5).length;

  return (
    <PtLayout>
      <div className="pt-reviews-page">
        <header className="pt-reviews-header">
          <h1>Đánh Giá Từ Học Viên</h1>
          <p>Xem phản hồi và đánh giá từ học viên của bạn.</p>
        </header>

        <SummaryGrid columns={3} ariaLabel="Thống kê đánh giá">
          <SummaryCard icon={Star} label="Điểm trung bình" value={formattedAvgRating} tone="yellow" />
          <SummaryCard icon={MessageSquare} label="Tổng lượt đánh giá" value={reviews.length} tone="orange" />
          <SummaryCard icon={Star} label="Đánh giá 5 sao" value={fiveStarReviews} tone="green" />
        </SummaryGrid>

        <section className="pt-review-list">
          <div className="pt-review-list-header">
            <div>
              <h2>Tất cả đánh giá</h2>
              <span>{reviews.length} phản hồi</span>
            </div>
          </div>

          {loading ? (
            <div className="pt-review-state">Đang tải đánh giá...</div>
          ) : reviews.length === 0 ? (
            <div className="pt-review-state pt-review-state--empty">
              <MessageSquare size={32} />
              <strong>Chưa có đánh giá nào</strong>
              <span>Đánh giá từ học viên sẽ xuất hiện tại đây.</span>
            </div>
          ) : (
            <div>
              {reviews.map(review => (
                <article className="pt-review-item" key={review.id}>
                  <div className="pt-review-item-header">
                    <div className="pt-review-member">
                      <span>{review.memberName}</span>
                      <span className="pt-review-stars" aria-label={`${review.ratingStar} trên 5 sao`}>
                        {renderStars(review.ratingStar)}
                      </span>
                    </div>
                    <time>
                      {review.createdAt ? new Date(review.createdAt).toLocaleDateString('vi-VN') : ''}
                    </time>
                  </div>
                  {review.comment && <p>{review.comment}</p>}
                </article>
              ))}
            </div>
          )}
        </section>
      </div>
    </PtLayout>
  );
};

export default PtReviewsPage;
