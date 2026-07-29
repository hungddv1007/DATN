import React, { useState, useEffect, useCallback } from 'react';
import MainLayout from '../../components/layout/MainLayout';
import ptScheduleService from '../../services/ptScheduleService';
import { ChevronLeft, ChevronRight, User, Clock } from 'lucide-react';
import './MemberSchedulePage.css';

const DAY_LABELS = ['Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7', 'Chủ Nhật'];
const BLOCKS = [
  { label: 'NGÀY', range: '06:00 – 18:00', startHour: 6, endHour: 18 },
  { label: 'ĐÊM', range: '18:00 – 06:00', startHour: 18, endHour: 6 }
];

const pad2 = (n) => String(n).padStart(2, '0');
const toISODate = (d) => `${d.getFullYear()}-${pad2(d.getMonth() + 1)}-${pad2(d.getDate())}`;
const formatDDMM = (d) => `${pad2(d.getDate())}/${pad2(d.getMonth() + 1)}`;

function getMonday(d) {
  const x = new Date(d);
  x.setHours(0, 0, 0, 0);
  const day = x.getDay();
  const diff = day === 0 ? -6 : 1 - day;
  x.setDate(x.getDate() + diff);
  return x;
}

function addDays(d, n) {
  const x = new Date(d);
  x.setDate(x.getDate() + n);
  return x;
}

function timeToMinutes(t) {
  const [h, m] = t.split(':').map(Number);
  return h * 60 + m;
}

function isInBlock(startTime, blockIdx) {
  const mins = timeToMinutes(startTime);
  if (blockIdx === 0) return mins >= 360 && mins < 1080;
  return mins >= 1080 || mins < 360;
}

const MemberSchedulePage = () => {
  const [weekOffset, setWeekOffset] = useState(0);
  const [schedule, setSchedule] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const getDisplayedMonday = useCallback(() => {
    return addDays(getMonday(new Date()), weekOffset * 7);
  }, [weekOffset]);

  const getDisplayedDates = useCallback(() => {
    const monday = getDisplayedMonday();
    return Array.from({ length: 7 }, (_, i) => addDays(monday, i));
  }, [getDisplayedMonday]);

  const weekStartISO = toISODate(getDisplayedMonday());

  const loadSchedule = useCallback(async () => {
    try {
      const data = await ptScheduleService.getMemberSchedule(weekStartISO);
      setSchedule(data);
      setError('');
    } catch (err) {
      if (err.response?.status === 404) {
        setError('Bạn chưa đăng ký gói tập có PT.');
      } else {
        setError('Không thể tải lịch tập. Vui lòng thử lại.');
      }
      setSchedule([]);
    } finally {
      setLoading(false);
    }
  }, [weekStartISO]);

  useEffect(() => {
    loadSchedule();
  }, [loadSchedule]);

  const getSchedulesForCell = (dateISO, blockIdx) => {
    return schedule
      .filter(s => s.scheduleDate === dateISO && isInBlock(s.startTime, blockIdx))
      .sort((a, b) => timeToMinutes(a.startTime) - timeToMinutes(b.startTime));
  };

  const todayISO = toISODate(new Date());
  const dates = getDisplayedDates();
  const mondayDisplay = getDisplayedMonday();
  const sundayDisplay = addDays(mondayDisplay, 6);
  const weekLabel = `${formatDDMM(mondayDisplay)} – ${formatDDMM(sundayDisplay)}`;

  // Lấy thông tin PT từ schedule đầu tiên
  const ptName = schedule.length > 0 ? schedule[0].ptName : null;

  if (loading) {
    return (
      <MainLayout>
        <div className="msp-page">
          <div className="msp-loading">Đang tải lịch huấn luyện...</div>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout>
      <div className="msp-page">
        {/* Header */}
        <div className="msp-header">
          <div>
            <h1 className="msp-title">
              <span className="msp-title-bar" />
              Lịch Huấn Luyện Của Tôi
            </h1>
            <p className="msp-subtitle">Theo dõi lịch tập cùng Huấn luyện viên cá nhân</p>
          </div>
          <div className="msp-actions">
            <button className="msp-btn-today" onClick={() => setWeekOffset(0)} disabled={weekOffset === 0}>
              Hôm nay
            </button>
            <div className="msp-week-nav">
              <button className="msp-nav-btn" onClick={() => setWeekOffset(prev => prev - 1)}>
                <ChevronLeft size={16} />
              </button>
              <span className="msp-week-text">{weekLabel}</span>
              <button className="msp-nav-btn" onClick={() => setWeekOffset(prev => prev + 1)}>
                <ChevronRight size={16} />
              </button>
            </div>
          </div>
        </div>

        {error && <div className="msp-error-box">{error}</div>}

        {!error && (
          <>
            {/* PT Info */}
            {ptName && (
              <div className="msp-pt-info">
                <User size={16} />
                <span>Huấn luyện viên: <strong>{ptName}</strong></span>
                <Clock size={16} style={{ marginLeft: '16px' }} />
                <span>Tuần này: <strong>{schedule.length} buổi</strong></span>
              </div>
            )}

            {schedule.length === 0 && !error && (
              <div className="msp-empty-box">
                HLV của bạn chưa xếp lịch cho tuần này. Hãy điều hướng sang tuần khác hoặc liên hệ HLV.
              </div>
            )}

            {/* Grid */}
            <div className="msp-grid-container">
              <div className="msp-grid-table">
                <div className="msp-grid-row msp-grid-header">
                  <div className="msp-col-header msp-time-header">Khung Giờ</div>
                  {dates.map((d, i) => {
                    const iso = toISODate(d);
                    const isToday = iso === todayISO;
                    return (
                      <div key={i} className={`msp-col-header ${isToday ? 'msp-day-today' : ''}`}>
                        <div className="msp-day-name">{DAY_LABELS[i]}</div>
                        <div className="msp-day-num">{formatDDMM(d)}</div>
                      </div>
                    );
                  })}
                </div>

                {BLOCKS.map((block, blockIdx) => (
                  <div key={blockIdx} className="msp-grid-row msp-grid-row-body">
                    <div className="msp-time-cell">
                      {block.label}
                      <span className="msp-time-range">{block.range}</span>
                    </div>
                    {dates.map((d, dayIdx) => {
                      const iso = toISODate(d);
                      const isToday = iso === todayISO;
                      const items = getSchedulesForCell(iso, blockIdx);

                      return (
                        <div key={dayIdx} className={`msp-slot-cell ${isToday ? 'msp-day-today' : ''}`}>
                          {items.map(item => (
                            <div key={item.id} className="msp-card">
                              <div className="msp-card-time">{item.startTime} - {item.endTime}</div>
                              {item.exerciseNote && (
                                <div className="msp-card-note">{item.exerciseNote}</div>
                              )}
                            </div>
                          ))}
                        </div>
                      );
                    })}
                  </div>
                ))}
              </div>
            </div>
          </>
        )}
      </div>
    </MainLayout>
  );
};

export default MemberSchedulePage;
