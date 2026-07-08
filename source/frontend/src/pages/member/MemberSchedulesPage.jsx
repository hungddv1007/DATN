import React, { useState, useEffect } from 'react';
import MainLayout from '../../components/layout/MainLayout';
import ptScheduleService from '../../services/ptScheduleService';
import { Calendar, Clock, User, Info } from 'lucide-react';
import './DashboardPage.css';

const MemberSchedulesPage = () => {
  const [schedules, setSchedules] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const DAYS = {
    'MONDAY': 'Thứ 2',
    'TUESDAY': 'Thứ 3',
    'WEDNESDAY': 'Thứ 4',
    'THURSDAY': 'Thứ 5',
    'FRIDAY': 'Thứ 6',
    'SATURDAY': 'Thứ 7',
    'SUNDAY': 'Chủ nhật'
  };

  const SLOTS = {
    'MORNING': 'Sáng (06:00 - 10:00)',
    'AFTERNOON': 'Chiều (14:00 - 17:00)',
    'EVENING': 'Tối (18:00 - 21:00)'
  };

  useEffect(() => {
    const fetchSchedules = async () => {
      try {
        const data = await ptScheduleService.getMemberSchedules();
        setSchedules(data);
      } catch (err) {
        setError(err.response?.data?.message || 'Lỗi tải lịch tập PT');
      } finally {
        setLoading(false);
      }
    };
    fetchSchedules();
  }, []);

  return (
    <MainLayout>
      <div className="container" style={{ padding: '40px 0' }}>
        <h1 style={{ color: 'white', marginBottom: '10px' }}>Lịch Tập Cùng PT</h1>
        <p style={{ color: '#94a3b8', marginBottom: '30px' }}>
          Xem các lịch hẹn cố định hàng tuần với Huấn luyện viên của bạn.
        </p>

        {error && <div style={{ background: 'rgba(239, 68, 68, 0.1)', color: '#ef4444', padding: '15px', borderRadius: '8px', marginBottom: '20px' }}>{error}</div>}

        {loading ? (
          <p style={{ color: '#94a3b8', textAlign: 'center', padding: '50px' }}>Đang tải lịch...</p>
        ) : schedules.length === 0 ? (
          <div style={{ background: 'rgba(15, 23, 42, 0.5)', padding: '50px', borderRadius: '12px', textAlign: 'center', color: '#94a3b8' }}>
            <Calendar size={48} style={{ margin: '0 auto 20px', opacity: 0.5 }} />
            <p>Bạn chưa có lịch tập nào được thiết lập.</p>
            <p style={{ fontSize: '0.9rem', marginTop: '10px' }}>
              Hãy liên hệ PT của bạn để xếp lịch tập cố định.
            </p>
          </div>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '20px' }}>
            {schedules.map(sched => (
              <div key={sched.id} style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '12px', padding: '25px', display: 'flex', flexDirection: 'column', gap: '15px', position: 'relative', overflow: 'hidden' }}>
                <div style={{ position: 'absolute', top: 0, left: 0, width: '4px', height: '100%', background: '#3b82f6' }}></div>
                
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px', color: '#f8fafc', fontSize: '1.4rem', fontWeight: 'bold' }}>
                  <Calendar size={24} color="#f97316" /> {DAYS[sched.dayOfWeek]}
                </div>
                
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px', color: '#cbd5e1', fontSize: '1.1rem' }}>
                  <Clock size={20} color="#3b82f6" /> {SLOTS[sched.slot]}
                </div>
                
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px', color: '#cbd5e1', fontSize: '1.1rem' }}>
                  <User size={20} color="#4ade80" /> PT: <strong>{sched.ptFullName}</strong>
                </div>

                <div style={{ background: 'rgba(255,255,255,0.05)', padding: '10px', borderRadius: '8px', display: 'flex', gap: '10px', alignItems: 'flex-start', marginTop: '10px' }}>
                  <Info size={16} color="#94a3b8" style={{ flexShrink: 0, marginTop: '2px' }} />
                  <span style={{ fontSize: '0.85rem', color: '#94a3b8', lineHeight: 1.4 }}>
                    Vui lòng có mặt tại phòng tập trước 10 phút để khởi động trước khi vào buổi kèm chính thức.
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </MainLayout>
  );
};

export default MemberSchedulesPage;
