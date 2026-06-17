import React, { useState, useEffect } from 'react';
import PtLayout from '../../components/layout/PtLayout';
import api from '../../services/api';
import { Star, MessageSquare } from 'lucide-react';
import '../admin/AdminManagement.css';

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

  return (
    <PtLayout>
      <h1>Đánh Giá Từ Học Viên</h1>
      <p>Xem phản hồi và đánh giá từ học viên của bạn.</p>

      {/* Stats */}
      <div className="admin-stats" style={{ gridTemplateColumns: 'repeat(3, 1fr)' }}>
        <div className="stat-card">
          <Star size={28} className="stat-icon" style={{ color: '#eab308' }} />
          <div className="stat-label">Điểm trung bình</div>
          <div className="stat-value">{avgRating || '—'}</div>
        </div>
        <div className="stat-card">
          <MessageSquare size={28} className="stat-icon" />
          <div className="stat-label">Tổng lượt đánh giá</div>
          <div className="stat-value">{reviews.length}</div>
        </div>
        <div className="stat-card">
          <Star size={28} className="stat-icon" style={{ color: '#22c55e' }} />
          <div className="stat-label">Đánh giá 5 sao</div>
          <div className="stat-value">{reviews.filter(r => r.ratingStar === 5).length}</div>
        </div>
      </div>

      {/* Reviews List */}
      <div className="admin-table-container" style={{ marginTop: 0 }}>
        <div style={{ padding: '16px 20px', borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
          <h3 style={{ color: '#f1f5f9', margin: 0 }}>Tất cả đánh giá</h3>
        </div>
        {loading ? (
          <div style={{ textAlign: 'center', padding: '40px', color: '#94a3b8' }}>Đang tải...</div>
        ) : reviews.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '40px', color: '#64748b' }}>Chưa có đánh giá nào.</div>
        ) : (
          <div style={{ padding: '0' }}>
            {reviews.map(review => (
              <div key={review.id} style={{
                padding: '20px 24px', borderBottom: '1px solid rgba(255,255,255,0.06)',
                transition: 'background 0.2s'
              }}
              onMouseEnter={e => e.currentTarget.style.background = 'rgba(255,255,255,0.02)'}
              onMouseLeave={e => e.currentTarget.style.background = 'transparent'}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '8px' }}>
                  <div>
                    <span style={{ color: '#f1f5f9', fontWeight: '500', marginRight: '12px' }}>{review.memberName}</span>
                    <span style={{ display: 'inline-flex', gap: '2px', verticalAlign: 'middle' }}>{renderStars(review.ratingStar)}</span>
                  </div>
                  <span style={{ color: '#64748b', fontSize: '0.85rem' }}>
                    {review.createdAt ? new Date(review.createdAt).toLocaleDateString('vi-VN') : ''}
                  </span>
                </div>
                {review.comment && (
                  <p style={{ color: '#cbd5e1', margin: 0, lineHeight: '1.6', fontSize: '0.95rem' }}>
                    {review.comment}
                  </p>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </PtLayout>
  );
};

export default PtReviewsPage;
