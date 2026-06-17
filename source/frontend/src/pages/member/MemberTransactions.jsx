import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import MainLayout from '../../components/layout/MainLayout';
import membershipService from '../../services/membershipService';
import { CreditCard, CheckCircle, Clock, XCircle } from 'lucide-react';
import './MemberTransactions.css';

const MemberTransactions = () => {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const data = await membershipService.getMembershipHistory();
        setHistory(data);
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
      case 'SUCCESS': return <CheckCircle size={20} color="#22c55e" />;
      case 'PENDING': return <Clock size={20} color="#f59e0b" />;
      case 'FAILED': return <XCircle size={20} color="#ef4444" />;
      case 'CANCELLED': return <XCircle size={20} color="#64748b" />;
      default: return null;
    }
  };

  const getStatusText = (status) => {
    switch(status) {
      case 'SUCCESS': return <span style={{ color: '#22c55e', fontWeight: 600 }}>Thành công</span>;
      case 'PENDING': return <span style={{ color: '#f59e0b', fontWeight: 600 }}>Chờ duyệt</span>;
      case 'FAILED': return <span style={{ color: '#ef4444', fontWeight: 600 }}>Thất bại</span>;
      case 'CANCELLED': return <span style={{ color: '#64748b', fontWeight: 600 }}>Đã hủy</span>;
      default: return <span>{status}</span>;
    }
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
                        Phương thức: <strong>{item.paymentMethod === 'BANK' ? 'Chuyển khoản' : 'Tiền mặt'}</strong>
                      </div>
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
