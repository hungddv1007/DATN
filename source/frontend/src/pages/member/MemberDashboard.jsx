import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import MainLayout from '../../components/layout/MainLayout';
import membershipService from '../../services/membershipService';
import memberPlanService from '../../services/memberPlanService';
import { Package, CreditCard, Bell, User, Dumbbell, Star } from 'lucide-react';
import './DashboardPage.css';

const MemberDashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [membership, setMembership] = useState(null);
  const [activePlan, setActivePlan] = useState(null);
  const [loading, setLoading] = useState(true);
  const [processing, setProcessing] = useState(false);

  const fetchDashboardData = async () => {
    try {
      const currentData = await membershipService.getCurrentMembership();
      setMembership(currentData);
    } catch (error) {
      setMembership(null);
    }
    try {
      const planData = await memberPlanService.getActivePlan();
      if (planData) setActivePlan(planData);
    } catch (error) {
      console.log('User has no active plan');
    }
    setLoading(false);
  };

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const handleCancel = async () => {
    if (!membership) return;
    if (!window.confirm('Bạn có chắc muốn hủy đăng ký gói tập này?')) return;
    setProcessing(true);
    try {
      await membershipService.cancelMembership(membership.id);
      alert('Đã hủy đăng ký thành công!');
      setMembership(null);
      fetchDashboardData();
    } catch (error) {
      alert(error.response?.data?.message || 'Lỗi khi hủy đăng ký');
    } finally {
      setProcessing(false);
    }
  };

  const handleSwitch = async () => {
    if (!membership) return;
    if (!window.confirm('Hủy gói hiện tại và chọn gói khác?')) return;
    setProcessing(true);
    try {
      await membershipService.cancelMembership(membership.id);
      navigate('/packages');
    } catch (error) {
      alert(error.response?.data?.message || 'Lỗi khi chuyển gói');
      setProcessing(false);
    }
  };

  const isPending = membership?.status === 'PENDING';
  const isActive = membership?.status === 'ACTIVE';

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
              <>
                <p className="dash-value" style={{ color: isActive ? '#22c55e' : '#f59e0b' }}>
                  {membership.packageName}
                </p>
                {isPending && (
                  <p style={{ fontSize: '0.8rem', color: '#f59e0b', marginTop: '-8px', marginBottom: '10px' }}>
                    Đã đăng ký — đang chờ Admin xác nhận
                  </p>
                )}
              </>
            ) : (
              <p className="dash-value">Chưa đăng ký</p>
            )}

            {isPending && (
              <div style={{ display: 'flex', gap: '8px', marginBottom: '10px' }}>
                <button
                  onClick={handleCancel}
                  disabled={processing}
                  style={{ flex: 1, padding: '8px', borderRadius: '6px', border: '1px solid #ef4444', background: 'transparent', color: '#ef4444', cursor: 'pointer', fontSize: '0.8rem', fontWeight: 600 }}
                >
                  Hủy
                </button>
                <button
                  onClick={handleSwitch}
                  disabled={processing}
                  style={{ flex: 1, padding: '8px', borderRadius: '6px', border: '1px solid #3b82f6', background: 'transparent', color: '#3b82f6', cursor: 'pointer', fontSize: '0.8rem', fontWeight: 600 }}
                >
                  Chuyển gói
                </button>
              </div>
            )}

            <Link to="/packages" className="dash-link">Xem gói tập →</Link>
          </div>

          <div className="dash-card">
            <CreditCard size={32} className="dash-icon" />
            <h3>Giao dịch</h3>
            <p className="dash-value">
              {isPending ? 'Đang chờ duyệt' : isActive ? 'Đã thanh toán' : 'Chưa có'}
            </p>
            <Link to="/member/transactions" className="dash-link">Xem lịch sử →</Link>
          </div>

          <div className="dash-card">
            <Dumbbell size={32} className="dash-icon" />
            <h3>Lộ trình tập</h3>
            {loading ? (
              <p className="dash-value">Đang tải...</p>
            ) : activePlan ? (
              <p className="dash-value" style={{ color: '#3b82f6' }}>{activePlan.title}</p>
            ) : (
              <p className="dash-value">Chưa có lộ trình</p>
            )}
            <Link to="/member/plan" className="dash-link">Xem chi tiết →</Link>
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
            <span className="dash-link">Xem hồ sơ PT →</span>
          </div>

          <div className="dash-card">
            <Bell size={32} className="dash-icon" />
            <h3>Thông báo</h3>
            <p className="dash-value">0 thông báo mới</p>
            <span className="dash-link">Xem tất cả →</span>
          </div>

          <div className="dash-card">
            <User size={32} className="dash-icon" />
            <h3>Hồ sơ cá nhân</h3>
            <p className="dash-value">{user?.email || ''}</p>
            <Link to="/profile" className="dash-link">Chỉnh sửa →</Link>
          </div>
        </div>
      </div>
    </MainLayout>
  );
};

export default MemberDashboard;