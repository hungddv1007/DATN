import React, { useState, useEffect, useCallback } from 'react';
import MainLayout from '../../components/layout/MainLayout';
import api from '../../services/api';
import { ChevronLeft, ChevronRight, Dumbbell, Coffee, Utensils } from 'lucide-react';
import './MemberDietPage.css';

const DAY_LABELS = ['Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7', 'CN'];
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

const MemberDietPage = () => {
  const [weekOffset, setWeekOffset] = useState(0);
  const [weekDiets, setWeekDiets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedDay, setSelectedDay] = useState(null); // index 0-6
  const [error, setError] = useState('');

  const getDisplayedMonday = useCallback(() => {
    return addDays(getMonday(new Date()), weekOffset * 7);
  }, [weekOffset]);

  useEffect(() => {
    fetchWeekDiets();
  }, [weekOffset]);

  const fetchWeekDiets = async () => {
    setLoading(true);
    setError('');
    try {
      const monday = getDisplayedMonday();
      const sunday = addDays(monday, 6);
      const from = toISODate(monday);
      const to = toISODate(sunday);
      const res = await api.get(`/member/diets/week?from=${from}&to=${to}`);
      setWeekDiets(res.data);
      
      // Auto-select today or Monday
      const today = new Date();
      today.setHours(0,0,0,0);
      const dayIdx = Math.round((today - monday) / (1000 * 60 * 60 * 24));
      if (dayIdx >= 0 && dayIdx <= 6) {
        setSelectedDay(dayIdx);
      } else {
        setSelectedDay(0);
      }
    } catch (err) {
      setError('Không thể tải thực đơn. Có thể PT chưa lên khẩu phần cho bạn.');
      setWeekDiets([]);
    } finally {
      setLoading(false);
    }
  };

  const monday = getDisplayedMonday();
  const weekDays = Array.from({ length: 7 }, (_, i) => addDays(monday, i));
  const todayStr = toISODate(new Date());
  const selectedDiet = weekDiets[selectedDay] || null;

  const getMealsList = (diet) => {
    if (!diet) return [];
    return [
      { label: 'Bữa sáng', emoji: '🌅', value: diet.breakfast },
      { label: 'Pre-workout', emoji: '⚡', value: diet.snackMorning },
      { label: 'Bữa trưa', emoji: '☀️', value: diet.lunch },
      { label: 'Post-workout', emoji: '💪', value: diet.snackAfternoon },
      { label: 'Bữa tối', emoji: '🌙', value: diet.dinner },
    ].filter(m => m.value);
  };

  return (
    <MainLayout>
      <div className="diet-page">
        {/* Header */}
        <div className="diet-header">
          <div className="diet-header-left">
            <Utensils size={28} className="diet-header-icon" />
            <div>
              <h1>Khẩu Phần Ăn</h1>
              <p>Thực đơn được PT thiết kế riêng cho bạn</p>
            </div>
          </div>
        </div>

        {/* Week Navigator */}
        <div className="diet-week-nav">
          <button className="diet-nav-btn" onClick={() => setWeekOffset(w => w - 1)}>
            <ChevronLeft size={20} />
          </button>
          <span className="diet-week-label">
            {formatDDMM(weekDays[0])} — {formatDDMM(weekDays[6])}
          </span>
          <button className="diet-nav-btn" onClick={() => setWeekOffset(w => w + 1)}>
            <ChevronRight size={20} />
          </button>
          {weekOffset !== 0 && (
            <button className="diet-today-btn" onClick={() => setWeekOffset(0)}>
              Hôm nay
            </button>
          )}
        </div>

        {loading ? (
          <div className="diet-loading">Đang tải thực đơn...</div>
        ) : error ? (
          <div className="diet-empty">
            <Utensils size={48} />
            <p>{error}</p>
          </div>
        ) : (
          <>
            {/* Day Tabs */}
            <div className="diet-day-tabs">
              {weekDays.map((day, idx) => {
                const isToday = toISODate(day) === todayStr;
                const isSelected = selectedDay === idx;
                const diet = weekDiets[idx];
                const isTraining = diet?.isTrainingDay;
                return (
                  <button
                    key={idx}
                    className={`diet-day-tab ${isSelected ? 'active' : ''} ${isToday ? 'today' : ''} ${isTraining ? 'training' : 'rest'}`}
                    onClick={() => setSelectedDay(idx)}
                  >
                    <span className="diet-day-name">{DAY_LABELS[idx]}</span>
                    <span className="diet-day-date">{formatDDMM(day)}</span>
                    <span className={`diet-day-badge ${isTraining ? 'training' : 'rest'}`}>
                      {isTraining ? <Dumbbell size={12} /> : <Coffee size={12} />}
                      {isTraining ? 'Tập' : 'Nghỉ'}
                    </span>
                  </button>
                );
              })}
            </div>

            {/* Selected Day Detail */}
            {selectedDiet ? (
              <div className="diet-detail">
                {/* Day Type Banner */}
                <div className={`diet-type-banner ${selectedDiet.isTrainingDay ? 'training' : 'rest'}`}>
                  <div className="diet-type-icon">
                    {selectedDiet.isTrainingDay ? <Dumbbell size={22} /> : <Coffee size={22} />}
                  </div>
                  <div>
                    <div className="diet-type-label">
                      {selectedDiet.isTrainingDay ? '🏋️ NGÀY TẬP — Nạp đủ năng lượng' : '☕ NGÀY NGHỈ — Phục hồi & tái tạo'}
                    </div>
                    {selectedDiet.title && (
                      <div className="diet-type-title">{selectedDiet.title}</div>
                    )}
                  </div>
                </div>

                {/* Macro Summary */}
                <div className="diet-macros">
                  {[
                    { label: 'Calo', val: selectedDiet.calories, unit: 'kcal', color: '#f97316' },
                    { label: 'Protein', val: selectedDiet.proteinG, unit: 'g', color: '#3b82f6' },
                    { label: 'Carbs', val: selectedDiet.carbsG, unit: 'g', color: '#eab308' },
                    { label: 'Fat', val: selectedDiet.fatG, unit: 'g', color: '#ef4444' },
                  ].map(({ label, val, unit, color }) => (
                    <div key={label} className="diet-macro-card" style={{ '--macro-color': color }}>
                      <div className="diet-macro-value">{val || 0}</div>
                      <div className="diet-macro-label">{label} ({unit})</div>
                    </div>
                  ))}
                </div>

                {/* Meals */}
                <div className="diet-meals">
                  {getMealsList(selectedDiet).map((meal, i) => (
                    <div key={i} className="diet-meal-card">
                      <div className="diet-meal-emoji">{meal.emoji}</div>
                      <div className="diet-meal-content">
                        <div className="diet-meal-label">{meal.label}</div>
                        <div className="diet-meal-value">{meal.value}</div>
                      </div>
                    </div>
                  ))}
                </div>

                {/* Note */}
                {selectedDiet.note && (
                  <div className="diet-note">
                    <span className="diet-note-icon">📝</span>
                    <div>
                      <div className="diet-note-label">Lời dặn từ PT</div>
                      <div className="diet-note-text">{selectedDiet.note}</div>
                    </div>
                  </div>
                )}

                {/* PT Info */}
                {selectedDiet.ptName && (
                  <div className="diet-pt-info">
                    Thực đơn bởi PT: <strong>{selectedDiet.ptName}</strong>
                  </div>
                )}
              </div>
            ) : (
              <div className="diet-empty">
                <Utensils size={48} />
                <p>Chưa có thực đơn cho ngày này.</p>
                <span>PT chưa tạo mẫu thực đơn cho bạn.</span>
              </div>
            )}
          </>
        )}
      </div>
    </MainLayout>
  );
};

export default MemberDietPage;
