import { useCallback, useEffect, useMemo, useState } from 'react';
import { MessageSquareText, Pin, PinOff, Star } from 'lucide-react';
import AdminLayout from '../../components/layout/AdminLayout';
import AdminPagination from '../../components/admin/AdminPagination';
import useClientPagination from '../../hooks/useClientPagination';
import serviceReviewService from '../../services/serviceReviewService';
import './AdminManagement.css';

const ServiceReviewsManagement = () => {
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [processingId, setProcessingId] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      setReviews(await serviceReviewService.adminAll() || []);
      setError('');
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể tải danh sách đánh giá');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const sortedReviews = useMemo(() => [...reviews].sort((a, b) => {
    if (a.featured !== b.featured) return a.featured ? -1 : 1;
    return new Date(b.createdAt) - new Date(a.createdAt);
  }), [reviews]);

  const { page, setPage, totalPages, pageItems } = useClientPagination(sortedReviews);
  const featuredCount = reviews.filter(review => review.featured).length;

  const toggleFeatured = async review => {
    setProcessingId(review.id);
    setError('');
    try {
      const updated = await serviceReviewService.setFeatured(review.id, !review.featured);
      setReviews(current => current.map(item => item.id === updated.id ? updated : item));
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể cập nhật trạng thái ghim');
    } finally {
      setProcessingId(null);
    }
  };

  return (
    <AdminLayout>
      <div className="business-page review-management-page">
        <header className="business-header review-admin-header">
          <div>
            <span className="business-eyebrow"><MessageSquareText size={16} /> Phản hồi hội viên</span>
            <h1>Quản lý đánh giá dịch vụ</h1>
            <p>Tất cả đánh giá của hội viên được quản lý tại đây. Đánh giá được ghim sẽ tự động xuất hiện trên trang chủ.</p>
          </div>
          <div className="review-admin-counts review-pinned-count">
            <span><strong>{featuredCount}</strong> đánh giá đang ghim</span>
          </div>
        </header>

        {error && <div className="business-alert" role="alert">{error}</div>}

        <section className="business-panel review-admin-panel">
          <div className="business-table-scroll">
            <table className="admin-table business-table business-review-table">
              <thead><tr><th>Hội viên</th><th>Điểm</th><th>Nội dung đánh giá</th><th>Trang chủ</th><th>Thao tác</th></tr></thead>
              <tbody>
                {loading ? <tr><td colSpan="5" className="business-empty">Đang tải dữ liệu...</td></tr>
                  : pageItems.length === 0 ? <tr><td colSpan="5" className="business-empty">Chưa có đánh giá dịch vụ nào.</td></tr>
                    : pageItems.map(review => (
                      <tr key={review.id} className={review.featured ? 'review-pinned-row' : ''}>
                        <td>
                          <div className="review-member-cell">
                            <strong>{review.memberName}</strong>
                            <small>Gói {review.packageName} · GD #{review.transactionId}</small>
                          </div>
                        </td>
                        <td><span className="business-rating"><Star size={15} fill="currentColor" /> {review.ratingStar}/5</span></td>
                        <td><p className="business-review-content">{review.comment}</p></td>
                        <td>
                          {review.featured
                            ? <span className="business-featured review-featured-only"><Pin size={12} fill="currentColor" /> Đang hiển thị</span>
                            : <span className="review-not-featured">Chưa ghim</span>}
                        </td>
                        <td>
                          <div className="business-actions review-admin-actions">
                            <button
                              disabled={processingId === review.id}
                              className={`business-action-btn ${review.featured ? 'action-unpin' : 'action-pin'}`}
                              type="button"
                              onClick={() => toggleFeatured(review)}
                            >
                              {review.featured ? <PinOff size={17} /> : <Pin size={17} />}
                              {processingId === review.id ? 'Đang cập nhật...' : review.featured ? 'Bỏ ghim' : 'Ghim trang chủ'}
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
              </tbody>
            </table>
          </div>
          <AdminPagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </section>
      </div>
    </AdminLayout>
  );
};

export default ServiceReviewsManagement;
