import React, { useState, useEffect, useCallback } from 'react';
import AdminLayout from '../../components/layout/AdminLayout';
import transactionService from '../../services/transactionService';
import { Check, X } from 'lucide-react';
import AdminPagination from '../../components/admin/AdminPagination';
import { ADMIN_PAGE_SIZE } from '../../hooks/useClientPagination';
import './AdminManagement.css';

const TransactionsManagement = () => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('ALL'); // ALL or PENDING
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const fetchTransactions = useCallback(async () => {
    setLoading(true);
    try {
      let data;
      if (filter === 'PENDING') {
        data = await transactionService.getPendingTransactions(page, ADMIN_PAGE_SIZE);
      } else {
        data = await transactionService.getAllTransactions(page, ADMIN_PAGE_SIZE);
      }
      setTransactions(data.content || []);
      setTotalPages(data.totalPages || 0);
    } catch (error) {
      console.error('Lỗi tải giao dịch:', error);
    } finally {
      setLoading(false);
    }
  }, [filter, page]);

  useEffect(() => {
    fetchTransactions();
  }, [fetchTransactions]);

  const handleConfirm = async (id) => {
    if (!window.confirm('Bạn chắc chắn muốn DUYỆT giao dịch này?')) return;
    try {
      await transactionService.confirmTransaction(id);
      alert('Đã duyệt giao dịch thành công!');
      fetchTransactions();
    } catch (error) {
      alert(error.response?.data?.message || 'Lỗi khi duyệt giao dịch');
    }
  };

  const handleCancel = async (id) => {
    if (!window.confirm('Bạn chắc chắn muốn HỦY giao dịch này? Yêu cầu chưa duyệt sẽ không được áp dụng vào gói tập.')) return;
    try {
      await transactionService.cancelTransaction(id);
      alert('Đã hủy giao dịch thành công!');
      fetchTransactions();
    } catch (error) {
      alert(error.response?.data?.message || 'Lỗi khi hủy giao dịch');
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
  };

  const formatDate = (dateString) => {
    if (!dateString) return '';
    const d = new Date(dateString);
    return d.toLocaleString('vi-VN');
  };

  return (
    <AdminLayout>
      <div className="admin-page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h1>Quản lý Giao dịch</h1>
      </div>

      <div className="tab-buttons">
        <button 
          className={`tab-btn ${filter === 'ALL' ? 'active' : ''}`}
          onClick={() => { setFilter('ALL'); setPage(0); }}
        >
          Tất cả giao dịch
        </button>
        <button 
          className={`tab-btn ${filter === 'PENDING' ? 'active' : ''}`}
          onClick={() => { setFilter('PENDING'); setPage(0); }}
        >
          Đang chờ duyệt
        </button>
      </div>

      <div className="admin-table-container">
        <table className="admin-table">
          <thead>
            <tr>
              <th>Mã GD</th>
              <th>Hội viên</th>
              <th>Gói tập</th>
              <th>Loại</th>
              <th>Số tiền</th>
              <th>Phương thức</th>
              <th>Thời gian</th>
              <th>Trạng thái</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan="9" style={{ textAlign: 'center' }}>Đang tải...</td></tr>
            ) : transactions.length === 0 ? (
              <tr><td colSpan="9" style={{ textAlign: 'center' }}>Không có giao dịch nào</td></tr>
            ) : (
              transactions.map(tx => (
                <tr key={tx.id}>
                  <td>#{tx.id}</td>
                  <td>
                    <strong>{tx.memberName}</strong><br/>
                    <small style={{ color: '#94a3b8' }}>{tx.memberEmail}</small>
                  </td>
                  <td>
                    {tx.packageName}
                    {tx.promotionCode && (
                      <div style={{ fontSize: '0.8rem', color: '#f97316' }}>Mã: {tx.promotionCode} (-{tx.discountPercent}%)</div>
                    )}
                  </td>
                  <td>{tx.type === 'NEW' ? 'Đăng ký' : tx.type === 'RENEW' ? 'Gia hạn' : 'Nâng cấp'}</td>
                  <td style={{ fontWeight: 'bold', color: '#f1f5f9' }}>{formatCurrency(tx.amount)}</td>
                  <td>{tx.paymentMethod}</td>
                  <td>{formatDate(tx.createdAt)}</td>
                  <td>
                    <span className={`status-badge status-${tx.status.toLowerCase()}`}>
                      {tx.status}
                    </span>
                  </td>
                  <td>
                    {tx.status === 'PENDING' && (
                      <div className="action-btns">
                        <button className="btn-icon confirm" title="Duyệt" onClick={() => handleConfirm(tx.id)}>
                          <Check size={18} />
                        </button>
                        <button className="btn-icon cancel" title="Từ chối" onClick={() => handleCancel(tx.id)}>
                          <X size={18} />
                        </button>
                      </div>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
        
        <AdminPagination page={page} totalPages={totalPages} onPageChange={setPage} />
      </div>
    </AdminLayout>
  );
};

export default TransactionsManagement;
