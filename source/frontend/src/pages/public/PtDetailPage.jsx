import { useCallback, useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Award, ShieldCheck, Star, Trash2 } from 'lucide-react';
import MainLayout from '../../components/layout/MainLayout';
import { useAuth } from '../../context/AuthContext';
import ptService from '../../services/ptService';
import reviewService from '../../services/reviewService';
import { resolveFileUrl } from '../../utils/fileUrl';
import './PtDetailPage.css';

const PtDetailPage = () => {
  const { ptId } = useParams();
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [myReview, setMyReview] = useState(null);
  const [ratingStar, setRatingStar] = useState(5);
  const [comment, setComment] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const isMember = user?.role === 'MEMBER';

  const loadData = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const [profileData, reviewData] = await Promise.all([
        ptService.getPtProfileByUserId(ptId),
        reviewService.getReviewsByPt(ptId),
      ]);
      setProfile(profileData);
      setReviews(reviewData);

      if (isMember) {
        const mine = await reviewService.getMyReviews();
        const existing = mine.find((review) => review.ptId === Number(ptId)) || null;
        setMyReview(existing);
        setRatingStar(existing?.ratingStar || 5);
        setComment(existing?.comment || '');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể tải hồ sơ PT.');
    } finally {
      setLoading(false);
    }
  }, [isMember, ptId]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSaving(true);
    setError('');
    setMessage('');
    try {
      if (myReview) {
        await reviewService.updateReview(myReview.id, ratingStar, comment.trim());
        setMessage('Đã cập nhật đánh giá.');
      } else {
        await reviewService.createReview(Number(ptId), ratingStar, comment.trim());
        setMessage('Đã gửi đánh giá.');
      }
      await loadData();
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể lưu đánh giá.');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!myReview || !window.confirm('Bạn có chắc muốn xóa đánh giá này?')) return;
    setSaving(true);
    setError('');
    try {
      await reviewService.deleteReview(myReview.id);
      setMyReview(null);
      setRatingStar(5);
      setComment('');
      setMessage('Đã xóa đánh giá.');
      await loadData();
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể xóa đánh giá.');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <MainLayout><div className="pt-detail-state">Đang tải hồ sơ PT...</div></MainLayout>;
  }

  if (!profile) {
    return <MainLayout><div className="pt-detail-state">{error || 'Không tìm thấy PT.'}</div></MainLayout>;
  }

  return (
    <MainLayout>
      <div className="pt-detail-page">
        <Link className="pt-detail-back" to="/pts">← Danh sách PT</Link>
        <section className="pt-detail-profile">
          <div className="pt-detail-avatar">
            {profile.avatar
              ? <img src={resolveFileUrl(profile.avatar)} alt={profile.fullName} />
              : <span>{profile.fullName?.charAt(0)?.toUpperCase() || '?'}</span>}
          </div>
          <div>
            <h1>{profile.fullName}</h1>
            <p className="pt-detail-specialization">{profile.specialization || 'Huấn luyện viên'}</p>
            <p>{profile.bio || 'PT chưa cập nhật phần giới thiệu.'}</p>
            <div className="pt-detail-meta">
              <span><Star size={17} /> {profile.ratingScore || 0}/5 ({profile.totalReviews || 0} đánh giá)</span>
              <span><Award size={17} /> {profile.totalMembers || 0} hội viên</span>
              <span><ShieldCheck size={17} /> {profile.certificates || 'Chưa cập nhật chứng chỉ'}</span>
            </div>
          </div>
        </section>

        {isMember && (
          <section className="pt-review-form-card">
            <h2>{myReview ? 'Chỉnh sửa đánh giá của bạn' : 'Đánh giá PT này'}</h2>
            {error && <div className="pt-review-error">{error}</div>}
            {message && <div className="pt-review-success">{message}</div>}
            <form onSubmit={handleSubmit}>
              <div className="pt-rating-picker">
                {[1, 2, 3, 4, 5].map((star) => (
                  <button type="button" key={star} aria-label={`${star} sao`}
                    className={star <= ratingStar ? 'active' : ''}
                    onClick={() => setRatingStar(star)}>★</button>
                ))}
              </div>
              <textarea value={comment} onChange={(event) => setComment(event.target.value)}
                maxLength={2000} rows={4} placeholder="Chia sẻ trải nghiệm tập luyện với PT..." />
              <div className="pt-review-actions">
                <button type="submit" disabled={saving}>{saving ? 'Đang lưu...' : myReview ? 'Cập nhật' : 'Gửi đánh giá'}</button>
                {myReview && (
                  <button type="button" className="danger" onClick={handleDelete} disabled={saving}>
                    <Trash2 size={16} /> Xóa
                  </button>
                )}
              </div>
            </form>
          </section>
        )}

        <section className="pt-reviews-card">
          <h2>Đánh giá từ hội viên</h2>
          {reviews.length === 0 ? <p>Chưa có đánh giá nào.</p> : reviews.map((review) => (
            <article key={review.id} className="pt-review-item">
              <div><strong>{review.memberName}</strong><span>{'★'.repeat(review.ratingStar)}{'☆'.repeat(5 - review.ratingStar)}</span></div>
              <p>{review.comment || 'Không có nhận xét.'}</p>
              <small>{review.createdAt ? new Date(review.createdAt).toLocaleString('vi-VN') : ''}</small>
            </article>
          ))}
        </section>
      </div>
    </MainLayout>
  );
};

export default PtDetailPage;
