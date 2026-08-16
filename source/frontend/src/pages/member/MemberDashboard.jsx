import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import MainLayout from '../../components/layout/MainLayout';
import membershipService from '../../services/membershipService';
import membershipTransferService from '../../services/membershipTransferService';
import ptScheduleService from '../../services/ptScheduleService';
import notificationService from '../../services/notificationService';
import { Package, CreditCard, Bell, User, Calendar, Star, Utensils, X, CheckCheck, Trash2, HeartPulse, ArrowRightLeft } from 'lucide-react';
import './DashboardPage.css';

const MemberDashboard = () => {
  const { user } = useAuth();
  const [membership, setMembership] = useState(null);
  const [latestTransaction, setLatestTransaction] = useState(null);
  const [schedule, setSchedule] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [pendingTransfers, setPendingTransfers] = useState([]);
  const [loading, setLoading] = useState(true);

  // Notification modal state
  const [showNotifModal, setShowNotifModal] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [notifLoading, setNotifLoading] = useState(false);

  const fetchDashboardData = async () => {
    try {
      const currentData = await membershipService.getCurrentMembership();
      setMembership(currentData);
    } catch (error) {
      console.log('User has no active membership or error occurred');
    }
    try {
      const history = await membershipService.getMembershipHistory();
      setLatestTransaction(history.find((item) => item.transactionId) || null);
    } catch (error) {
      console.log('User has no transaction history');
    }
    try {
      const now = new Date();
      now.setHours(0, 0, 0, 0);
      const day = now.getDay();
      const diff = day === 0 ? -6 : 1 - day;
      const monday = new Date(now);
      monday.setDate(now.getDate() + diff);
      const weekStart = `${monday.getFullYear()}-${String(monday.getMonth() + 1).padStart(2, '0')}-${String(monday.getDate()).padStart(2, '0')}`;

      const scheduleData = await ptScheduleService.getMemberSchedule(weekStart);
      setSchedule(scheduleData);
    } catch (error) {
      console.log('User has no active schedule');
    }
    try {
      const countRes = await notificationService.getUnreadCount();
      setUnreadCount(countRes.unreadCount || 0);
    } catch (error) {
      console.log('Could not fetch unread notification count');
    }
    try {
      const transferData = await membershipTransferService.incoming();
      setPendingTransfers(transferData.filter(item => item.status === 'PENDING_RECIPIENT'));
    } catch (error) {
      console.log('Could not fetch incoming membership transfers');
    }
    setLoading(false);
  };

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const openNotifModal = async () => {
    setShowNotifModal(true);
    setNotifLoading(true);
    try {
      const data = await notificationService.getMyNotifications(0, 20);
      setNotifications(data.content || []);
    } catch (err) {
      console.error('Lỗi tải danh sách thông báo:', err);
    } finally {
      setNotifLoading(false);
    }
  };

  const handleMarkAsRead = async (id) => {
    try {
      await notificationService.markAsRead(id);
      setNotifications(prev => prev.map(n => n.id === id ? { ...n, isRead: true } : n));
      const countRes = await notificationService.getUnreadCount();
      setUnreadCount(countRes.unreadCount || 0);
    } catch (err) {
      console.error('Lỗi đánh dấu đã đọc:', err);
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await notificationService.markAllAsRead();
      setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
      setUnreadCount(0);
    } catch (err) {
      console.error('Lỗi đánh dấu tất cả đã đọc:', err);
    }
  };

  const handleDeleteNotif = async (id) => {
    try {
      await notificationService.deleteNotification(id);
      setNotifications(prev => prev.filter(n => n.id !== id));
      const countRes = await notificationService.getUnreadCount();
      setUnreadCount(countRes.unreadCount || 0);
    } catch (err) {
      console.error('Lỗi xóa thông báo:', err);
    }
  };

  return (
    <MainLayout>
      <div className="dashboard-page">
        <div className="dashboard-header">
          <h1>Dashboard Hội Viên</h1>
          <p>Xin chào, <strong>{user?.fullName || 'Hội viên'}</strong>! Chúc bạn có buổi tập hiệu quả.</p>
        </div>

        <div className="dashboard-cards">
          <div className="dash-card">
            <Package size={32} className="dash-icon" />
            <h3>Gói tập hiện tại</h3>
            {loading ? (
              <p className="dash-value">Đang tải...</p>
            ) : membership ? (
              <p className="dash-value" style={{ color: '#22c55e' }}>{membership.packageName}</p>
            ) : pendingTransfers.length > 0 ? (
              <p className="dash-value dash-value-transfer">Có gói đang chờ nhận</p>
            ) : (
              <p className="dash-value">Chưa đăng ký</p>
            )}
            {membership ? (
              <Link to="/member/membership" className="dash-link">Quản lý gói tập →</Link>
            ) : pendingTransfers.length > 0 ? (
              <Link to="/member/membership-transfer" className="dash-link">Xem và nhận gói →</Link>
            ) : (
              <Link to="/packages" className="dash-link">Xem gói tập →</Link>
            )}
          </div>

          <div className="dash-card">
            <CreditCard size={32} className="dash-icon" />
            <h3>Giao dịch</h3>
            <p className="dash-value">
              {loading
                ? 'Đang tải...'
                : !latestTransaction
                  ? 'Chưa có giao dịch'
                  : latestTransaction.transactionStatus === 'PENDING'
                    ? 'Đang chờ duyệt'
                    : latestTransaction.transactionStatus === 'CONFIRMED'
                      ? 'Đã xác nhận'
                      : latestTransaction.transactionStatus === 'CANCELLED'
                        ? 'Đã hủy'
                        : 'Chưa xác định'}
            </p>
            <Link to="/member/transactions" className="dash-link">Xem lịch sử →</Link>
          </div>

          <div className="dash-card">
            <Calendar size={32} className="dash-icon" />
            <h3>Lịch kèm PT</h3>
            {loading ? (
              <p className="dash-value">Đang tải...</p>
            ) : schedule.length > 0 ? (
              <p className="dash-value" style={{ color: '#3b82f6' }}>{schedule.length} buổi / tuần</p>
            ) : (
              <p className="dash-value">Chưa xếp lịch</p>
            )}
            <Link to="/member/schedule" className="dash-link">Xem thời khóa biểu →</Link>
          </div>

          <div className="dash-card">
            <Star size={32} className="dash-icon" />
            <h3>PT của bạn</h3>
            {loading ? (
              <p className="dash-value">Đang tải...</p>
            ) : membership?.ptName ? (
              <p className="dash-value" style={{ color: '#f97316' }}>{membership.ptName}</p>
            ) : (
              <p className="dash-value">Chưa được gán</p>
            )}
            {membership?.ptId ? (
              <Link to={`/pts/${membership.ptId}`} className="dash-link">Xem hồ sơ PT →</Link>
            ) : (
              <span className="dash-link" style={{ opacity: 0.55, cursor: 'default' }}>Chưa có hồ sơ PT</span>
            )}
          </div>

          <div className="dash-card">
            <Utensils size={32} className="dash-icon" />
            <h3>Khẩu phần ăn</h3>
            <p className="dash-value" style={{ color: '#10b981' }}>Thực đơn từ PT</p>
            <Link to="/member/diet" className="dash-link">Xem thực đơn →</Link>
          </div>

          <div className="dash-card">
            <Bell size={32} className="dash-icon" />
            <h3>Thông báo</h3>
            {loading ? (
              <p className="dash-value">Đang tải...</p>
            ) : (
              <p className="dash-value" style={{ color: unreadCount > 0 ? '#ef4444' : '#f1f5f9' }}>
                {unreadCount} thông báo mới
              </p>
            )}
            <span className="dash-link" onClick={openNotifModal}>Xem tất cả →</span>
          </div>

          <div className="dash-card">
            <HeartPulse size={32} className="dash-icon" />
            <h3>Hồ sơ thể chất</h3>
            <p className="dash-value" style={{ color: '#38bdf8' }}>Chỉ số và mục tiêu</p>
            <Link to="/member/physical-profile" className="dash-link">Xem và cập nhật →</Link>
          </div>

          <div className="dash-card">
            <User size={32} className="dash-icon" />
            <h3>Hồ sơ cá nhân</h3>
            <p className="dash-value">{user?.email || ''}</p>
            <Link to="/profile" className="dash-link">Chỉnh sửa →</Link>
          </div>

          <div className={`dash-card transfer-dash-card ${pendingTransfers.length > 0 ? 'has-pending-transfer' : ''}`}>
            <ArrowRightLeft size={32} className="dash-icon" />
            <h3>Chuyển nhượng gói tập</h3>
            {loading ? (
              <p className="dash-value">Đang tải...</p>
            ) : pendingTransfers.length > 0 ? (
              <p className="dash-value dash-value-transfer">
                {pendingTransfers.length} gói đang chờ nhận
              </p>
            ) : (
              <p className="dash-value">Không có yêu cầu mới</p>
            )}
            <Link to="/member/membership-transfer" className="dash-link">
              {pendingTransfers.length > 0 ? 'Nhận gói ngay →' : 'Xem chuyển nhượng →'}
            </Link>
          </div>
        </div>

        {/* MODAL THÔNG BÁO */}
        {showNotifModal && (
          <div className="notif-modal-backdrop" onClick={(e) => { if (e.target === e.currentTarget) setShowNotifModal(false); }}>
            <div className="notif-modal-card">
              <div className="notif-modal-header">
                <h3>
                  <Bell size={20} color="#f97316" />
                  Thông Báo Của Tôi
                </h3>
                <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                  {notifications.some(n => !n.isRead) && (
                    <button className="notif-btn-readall" onClick={handleMarkAllAsRead}>
                      <CheckCheck size={14} /> Đọc tất cả
                    </button>
                  )}
                  <button className="notif-btn-close" onClick={() => setShowNotifModal(false)}>
                    <X size={18} />
                  </button>
                </div>
              </div>

              <div className="notif-modal-body">
                {notifLoading ? (
                  <p style={{ textAlign: 'center', padding: '30px', color: '#64748b' }}>Đang tải thông báo...</p>
                ) : notifications.length === 0 ? (
                  <p style={{ textAlign: 'center', padding: '30px', color: '#64748b' }}>Bạn không có thông báo nào.</p>
                ) : (
                  notifications.map(n => (
                    <div key={n.id} className={`notif-item ${!n.isRead ? 'unread' : ''}`}>
                      <div className="notif-item-content" onClick={() => !n.isRead && handleMarkAsRead(n.id)}>
                        <div className="notif-item-title">
                          {!n.isRead && <span className="notif-unread-dot" />}
                          {n.title}
                        </div>
                        <div className="notif-item-msg">{n.message}</div>
                        <div className="notif-item-time">{n.createdAt ? new Date(n.createdAt).toLocaleString('vi-VN') : ''}</div>
                      </div>
                      <button className="notif-btn-del" title="Xóa" onClick={() => handleDeleteNotif(n.id)}>
                        <Trash2 size={14} />
                      </button>
                    </div>
                  ))
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </MainLayout>
  );
};

export default MemberDashboard;
