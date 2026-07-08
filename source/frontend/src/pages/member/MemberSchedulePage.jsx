import React, { useState, useEffect } from 'react';
import MainLayout from '../../components/layout/MainLayout';
import ptScheduleService from '../../services/ptScheduleService';
import { Calendar, User, Clock } from 'lucide-react';
import './MemberSchedulePage.css';

const DAYS = ['Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7'];
const SLOTS = [
  { index: 0, label: 'Sáng', time: '07:00 - 08:00' },
  { index: 1, label: 'Sáng', time: '08:00 - 09:00' },
  { index: 2, label: 'Sáng', time: '09:00 - 10:00' },
  { index: 3, label: 'Chiều', time: '13:00 - 14:00' },
  { index: 4, label: 'Chiều', time: '14:00 - 15:00' },
  { index: 5, label: 'Chiều', time: '15:00 - 16:00' },
  { index: 6, label: 'Tối', time: '18:00 - 19:00' },
  { index: 7, label: 'Tối', time: '19:00 - 20:00' }
];

const MemberSchedulePage = () => {
  const [schedule, setSchedule] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchSchedule = async () => {
      try {
        const data = await ptScheduleService.getMemberSchedule();
        setSchedule(data);
      } catch (err) {
        setError('Không thể tải lịch tập của bạn. Bạn đã đăng ký gói tập có PT chưa?');
      } finally {
        setLoading(false);
      }
    };
    fetchSchedule();
  }, []);

  const getCellStatus = (dayIndex, slotIndex) => {
    const slot = schedule.find(s => s.dayOfWeek === dayIndex && s.slotIndex === slotIndex);
    if (slot) {
      return { booked: true, note: slot.exerciseNote, ptName: slot.ptName };
    }
    return { booked: false };
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
  };

  if (loading) {
    return (
      <MainLayout>
        <div className="member-schedule-page">
          <p style={{ color: '#94a3b8', marginTop: '100px', textAlign: 'center' }}>Đang tải lịch huấn luyện của bạn...</p>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout>
      <div className="member-schedule-page">
        <div className="schedule-header">
          <h1>Lịch Huấn Luyện Của Tôi</h1>
          <p>Theo dõi lịch tập cố định hàng tuần cùng Huấn luyện viên cá nhân</p>
        </div>

        {error && <div className="schedule-error-box">{error}</div>}

        {!error && (
          <>
            {schedule.length > 0 ? (
              <div className="pt-info-summary">
                <User size={18} />
                <span>Huấn luyện viên: <strong>{schedule[0].ptName}</strong></span>
                <Clock size={18} style={{ marginLeft: '20px' }} />
                <span>Số buổi trong tuần: <strong>{schedule.length} buổi</strong></span>
              </div>
            ) : (
              <div className="schedule-error-box" style={{ background: 'rgba(234,179,8,0.1)', border: '1px solid rgba(234,179,8,0.2)', color: '#facc15' }}>
                HLV của bạn chưa xếp lịch trình cho tuần này. Vui lòng liên hệ HLV để xếp lịch.
              </div>
            )}

            {/* Grid thời khóa biểu */}
            <div className="timetable-container">
              <table className="timetable">
                <thead>
                  <tr>
                    <th>Buổi</th>
                    <th>Khung giờ</th>
                    {DAYS.map((day, i) => <th key={i}>{day}</th>)}
                  </tr>
                </thead>
                <tbody>
                  {SLOTS.map((slot, sIdx) => {
                    const showPeriodCell = sIdx === 0 || sIdx === 3 || sIdx === 6;
                    const periodRowSpan = sIdx === 0 || sIdx === 3 ? 3 : 2;

                    return (
                      <tr key={slot.index}>
                        {showPeriodCell && (
                          <td className="period-cell" rowSpan={periodRowSpan}>
                            {slot.label}
                          </td>
                        )}
                        <td className="time-cell">{slot.time}</td>
                        {DAYS.map((_, dIdx) => {
                          const status = getCellStatus(dIdx, slot.index);
                          
                          return (
                            <td 
                              key={dIdx} 
                              className={`schedule-cell ${status.booked ? 'cell-booked' : 'cell-empty'}`}
                            >
                              {status.booked && (
                                <div className="cell-details">
                                  <span className="exercise-badge">{status.note}</span>
                                </div>
                              )}
                            </td>
                          );
                        })}
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </>
        )}
      </div>
    </MainLayout>
  );
};

export default MemberSchedulePage;
