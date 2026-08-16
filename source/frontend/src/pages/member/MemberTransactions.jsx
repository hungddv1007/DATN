import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import MainLayout from '../../components/layout/MainLayout';
import membershipService from '../../services/membershipService';
import serviceReviewService from '../../services/serviceReviewService';
import { promptDialog } from '../../utils/dialog';
import { CreditCard, CheckCircle, Clock, XCircle } from 'lucide-react';
import './MemberTransactions.css';

const MemberTransactions = () => {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [reviewedIds, setReviewedIds] = useState(new Set());

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const [data, reviews] = await Promise.all([membershipService.getMembershipHistory(), serviceReviewService.mine()]);
        setHistory(data);
        setReviewedIds(new Set(reviews.map(r => r.transactionId)));
      } catch (error) {
        console.error('Failed to fetch membership history', error);
      } finally {
        setLoading(false);
      }
    };
    fetchHistory();
  }, []);

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
  };

  const getStatusIcon = (status) => {
    switch(status) {
      case 'CONFIRMED': return <CheckCircle size={20} color="#22c55e" />;
      case 'PENDING': return <Clock size={20} color="#f59e0b" />;
      case 'CANCELLED': return <XCircle size={20} color="#64748b" />;
      default: return null;
    }
  };

  const getStatusText = (status) => {
    switch(status) {
      case 'CONFIRMED': return <span style={{ color: '#22c55e', fontWeight: 600 }}>Đã xác nhận</span>;
      case 'PENDING': return <span style={{ color: '#f59e0b', fontWeight: 600 }}>Chờ duyệt</span>;
      case 'CANCELLED': return <span style={{ color: '#64748b', fontWeight: 600 }}>Đã hủy</span>;
      default: return <span>{status}</span>;
    }
  };

  const reviewTransaction = async (item) => {
    const ratingStar = Number(await promptDialog('Hãy chọn số điểm từ 1 đến 5.', {
      title: 'Đánh giá dịch vụ GymPro',
      inputType: 'number',
      inputMode: 'numeric',
      min: 1,
      max: 5,
      defaultValue: 5,
      confirmText: 'Tiếp tục',
      required: true,
    }));
    if (!Number.isInteger(ratingStar) || ratingStar < 1 || ratingStar > 5) return;
    const comment = await promptDialog('Ý kiến của bạn có thể được Admin duyệt để hiển thị tại mục Trải nghiệm thực tế.', {
      title: 'Chia sẻ trải nghiệm',
      placeholder: 'Nhập cảm nhận của bạn...',
      multiline: true,
      confirmText: 'Gửi đánh giá',
      required: true,
    });
    if (!comment?.trim()) return;
    try {
      await serviceReviewService.create({ transactionId: item.transactionId, ratingStar, comment, displayName: true });
      setReviewedIds(current => new Set([...current, item.transactionId]));
      alert('Cảm ơn bạn! Đánh giá đang chờ Admin kiểm duyệt.');
    } catch (e) { alert(e.response?.data?.message || 'Không thể gửi đánh giá'); }
  };

  return (
    <MainLayout>
      <div className="transactions-page">
        <div className="transactions-container">
          <div className="transactions-header">
            <h1>Lịch sử Giao dịch</h1>
            <p>Quản lý các gói tập và thanh toán của bạn</p>
          </div>

          <Link to="/member/dashboard" className="btn-back">← Quay lại Dashboard</Link>

          {loading ? (
            <div style={{ textAlign: 'center', padding: '50px', color: '#94a3b8' }}>Đang tải lịch sử...</div>
          ) : history.length === 0 ? (
            <div className="empty-state">
              <CreditCard size={48} color="#475569" />
              <p>Bạn chưa có giao dịch nào.</p>
              <Link to="/packages" className="btn-buy">Mua gói tập ngay</Link>
            </div>
          ) : (
            <div className="transactions-list">
              {history.map((item, index) => (
                <div key={index} className="transaction-card">
                  <div className="tx-header">
                    <div className="tx-id">Mã GD: #{item.transactionId || 'N/A'}</div>
                    <div className="tx-status">
                      {getStatusIcon(item.transactionStatus)}
                      {getStatusText(item.transactionStatus)}
                    </div>
                  </div>
                  
                  <div className="tx-body">
                    <div className="tx-info">
                      <h3>Gói {item.packageName}</h3>
                      <p>Ngày mua: {formatDate(item.createdAt)}</p>
                      <p>Hạn sử dụng: {formatDate(item.startDate)} - {formatDate(item.endDate)} ({item.durationDays} ngày)</p>
                      {item.ptName && <p>PT hướng dẫn: <strong>{item.ptName}</strong></p>}
                    </div>
                    <div className="tx-amount">
                      {item.discountPercent > 0 ? (
                        <>
                          <div className="tx-original">{formatCurrency(item.originalAmount)}</div>
                          <div className="tx-final">{formatCurrency(item.finalAmount)}</div>
                          <div className="tx-promo">Mã KM: {item.promotionCode} (-{item.discountPercent}%)</div>
                        </>
                      ) : (
                        <div className="tx-final">{formatCurrency(item.finalAmount)}</div>
                      )}
                      <div className="tx-method">
                        Phương thức: <strong>{item.paymentMethod === 'BANK' ? 'Chuyển khoản' : item.paymentMethod === 'CASH' ? 'Tiền mặt' : item.paymentMethod}</strong>
                      </div>
                      {item.transactionStatus === 'CONFIRMED' && <button className="btn-buy"
                        disabled={reviewedIds.has(item.transactionId)} onClick={() => reviewTransaction(item)}>
                        {reviewedIds.has(item.transactionId) ? 'Đã đánh giá' : 'Đánh giá dịch vụ'}
                      </button>}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </MainLayout>
  );
};

export default MemberTransactions;
