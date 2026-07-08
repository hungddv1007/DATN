import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import MainLayout from '../../components/layout/MainLayout';
import membershipService from '../../services/membershipService';
import packageService from '../../services/packageService';
import { Activity, Clock, ArrowUpCircle, CheckCircle, RefreshCw, PauseCircle, PlayCircle, XCircle } from 'lucide-react';

const MembershipManagePage = () => {
  const navigate = useNavigate();
  const [currentMembership, setCurrentMembership] = useState(null);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [packages, setPackages] = useState([]);

  const [showUpgradeModal, setShowUpgradeModal] = useState(false);
  const [showRenewModal, setShowRenewModal] = useState(false);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [current, hist, pkgs] = await Promise.all([
        membershipService.getMyCurrentMembership().catch(() => null),
        membershipService.getMyMembershipHistory().catch(() => []),
        packageService.getAllPackages()
      ]);
      setCurrentMembership(current);
      setHistory(hist);
      setPackages(pkgs);
    } catch (error) {
      console.error('Lỗi tải dữ liệu gói tập', error);
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('vi-VN');
  };

  const handlePause = async () => {
    if (!window.confirm('Bạn có chắc chắn muốn bảo lưu gói tập hiện tại? Thời gian sử dụng sẽ bị tạm dừng.')) return;
    try {
      await membershipService.pauseMembership();
      alert('Đã bảo lưu thành công!');
      fetchData();
    } catch (err) {
      alert(err.response?.data?.message || 'Lỗi khi bảo lưu');
    }
  };

  const handleResume = async () => {
    if (!window.confirm('Bạn có muốn tiếp tục sử dụng gói tập?')) return;
    try {
      await membershipService.resumeMembership();
      alert('Đã hủy bảo lưu thành công!');
      fetchData();
    } catch (err) {
      alert(err.response?.data?.message || 'Lỗi khi hủy bảo lưu');
    }
  };

  const handleCancel = async () => {
    if (!window.confirm('BẠN SẼ MẤT TOÀN BỘ SỐ NGÀY CÒN LẠI! Cân nhắc kỹ trước khi HỦY VĨNH VIỄN gói tập!')) return;
    try {
      await membershipService.cancelMembership();
      alert('Đã hủy gói tập.');
      fetchData();
    } catch (err) {
      alert(err.response?.data?.message || 'Lỗi khi hủy gói tập');
    }
  };

  if (loading) {
    return <MainLayout><div style={{ padding: '50px', textAlign: 'center', color: '#94a3b8' }}>Đang tải...</div></MainLayout>;
  }

  return (
    <MainLayout>
      <div className="container" style={{ padding: '40px 0' }}>
        <h1 style={{ color: 'white', marginBottom: '30px' }}>Quản Lý Gói Tập Của Tôi</h1>
        
        {/* CURRENT MEMBERSHIP */}
        <div style={{ background: '#1e293b', padding: '30px', borderRadius: '12px', border: '1px solid #334155', marginBottom: '30px' }}>
          <h2 style={{ color: '#f97316', display: 'flex', alignItems: 'center', gap: '10px' }}>
            <Activity size={24} /> Gói tập hiện tại
          </h2>
          
          {currentMembership ? (
            <div>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '20px', marginTop: '20px' }}>
                <div style={{ flex: '1 1 200px', background: 'rgba(15,23,42,0.5)', padding: '20px', borderRadius: '8px' }}>
                  <div style={{ color: '#94a3b8', fontSize: '0.9rem' }}>Tên gói</div>
                  <div style={{ color: 'white', fontSize: '1.2rem', fontWeight: 'bold' }}>{currentMembership.packageName}</div>
                </div>
                <div style={{ flex: '1 1 200px', background: 'rgba(15,23,42,0.5)', padding: '20px', borderRadius: '8px' }}>
                  <div style={{ color: '#94a3b8', fontSize: '0.9rem' }}>Trạng thái</div>
                  <div style={{ 
                    color: currentMembership.status === 'ACTIVE' ? '#4ade80' : '#facc15', 
                    fontSize: '1.2rem', fontWeight: 'bold' 
                  }}>
                    {currentMembership.status === 'ACTIVE' ? 'Đang hoạt động' : 'Đang bảo lưu'}
                  </div>
                </div>
                <div style={{ flex: '1 1 200px', background: 'rgba(15,23,42,0.5)', padding: '20px', borderRadius: '8px' }}>
                  <div style={{ color: '#94a3b8', fontSize: '0.9rem' }}>Ngày hết hạn</div>
                  <div style={{ color: 'white', fontSize: '1.2rem', fontWeight: 'bold' }}>{formatDate(currentMembership.endDate)}</div>
                </div>
                <div style={{ flex: '1 1 200px', background: 'rgba(15,23,42,0.5)', padding: '20px', borderRadius: '8px' }}>
                  <div style={{ color: '#94a3b8', fontSize: '0.9rem' }}>Số lần bảo lưu còn lại</div>
                  <div style={{ color: 'white', fontSize: '1.2rem', fontWeight: 'bold' }}>{currentMembership.holdTimesRemaining} lần</div>
                </div>
              </div>

              {/* ACTION BUTTONS */}
              <div style={{ marginTop: '30px', display: 'flex', gap: '15px', flexWrap: 'wrap' }}>
                <button 
                  onClick={() => navigate('/packages')}
                  style={{ padding: '10px 20px', background: '#3b82f6', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '8px', fontWeight: 'bold' }}>
                  <ArrowUpCircle size={20}/> Nâng cấp / Gia hạn
                </button>
                
                {currentMembership.status === 'ACTIVE' ? (
                  <button 
                    onClick={handlePause}
                    style={{ padding: '10px 20px', background: '#eab308', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '8px', fontWeight: 'bold' }}>
                    <PauseCircle size={20}/> Bảo lưu
                  </button>
                ) : (
                  <button 
                    onClick={handleResume}
                    style={{ padding: '10px 20px', background: '#4ade80', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '8px', fontWeight: 'bold' }}>
                    <PlayCircle size={20}/> Hủy bảo lưu
                  </button>
                )}

                <button 
                  onClick={handleCancel}
                  style={{ padding: '10px 20px', background: '#ef4444', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '8px', fontWeight: 'bold', marginLeft: 'auto' }}>
                  <XCircle size={20}/> Hủy gói
                </button>
              </div>
            </div>
          ) : (
            <div style={{ marginTop: '20px', color: '#94a3b8' }}>
              Bạn chưa đăng ký gói tập nào hoặc gói tập đã hết hạn. <br/><br/>
              <button 
                onClick={() => navigate('/packages')}
                style={{ padding: '10px 20px', background: '#f97316', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: 'bold' }}>
                Mua Gói Ngay
              </button>
            </div>
          )}
        </div>

        {/* HISTORY */}
        <div style={{ background: '#1e293b', padding: '30px', borderRadius: '12px', border: '1px solid #334155' }}>
          <h2 style={{ color: '#f8fafc', display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '20px' }}>
            <Clock size={24} /> Lịch sử gói tập
          </h2>
          
          {history.length > 0 ? (
            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse', color: 'white' }}>
                <thead>
                  <tr style={{ borderBottom: '1px solid #334155', textAlign: 'left' }}>
                    <th style={{ padding: '12px' }}>Tên gói</th>
                    <th style={{ padding: '12px' }}>Ngày đăng ký</th>
                    <th style={{ padding: '12px' }}>Ngày hết hạn</th>
                    <th style={{ padding: '12px' }}>Trạng thái</th>
                  </tr>
                </thead>
                <tbody>
                  {history.map(hist => (
                    <tr key={hist.id} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                      <td style={{ padding: '12px' }}>{hist.packageName}</td>
                      <td style={{ padding: '12px' }}>{formatDate(hist.startDate)}</td>
                      <td style={{ padding: '12px' }}>{formatDate(hist.endDate)}</td>
                      <td style={{ padding: '12px' }}>
                        <span style={{ 
                          padding: '4px 8px', borderRadius: '4px', fontSize: '0.8rem',
                          background: hist.status === 'EXPIRED' ? 'rgba(239,68,68,0.2)' : 'rgba(148,163,184,0.2)',
                          color: hist.status === 'EXPIRED' ? '#f87171' : '#94a3b8'
                        }}>
                          {hist.status}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <div style={{ color: '#94a3b8' }}>Chưa có lịch sử đăng ký nào.</div>
          )}
        </div>
      </div>
    </MainLayout>
  );
};

export default MembershipManagePage;
