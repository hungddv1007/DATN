import React, { useState, useEffect, useCallback } from 'react';
import { useLocation } from 'react-router-dom';
import PtLayout from '../../components/layout/PtLayout';
import ptScheduleService from '../../services/ptScheduleService';
import exerciseService from '../../services/exerciseService';
import ptDashboardService from '../../services/ptDashboardService';
import { confirmDialog } from '../../utils/dialog';
import { ChevronLeft, ChevronRight, Plus, X, Repeat, Bell, Trash2, CircleCheckBig, UserX } from 'lucide-react';
import TimePickerWheel from '../../components/common/TimePickerWheel';
import {
  getScheduleTransitionLabel,
  isInTimeBlock,
  SCHEDULE_TIME_BLOCKS,
  timeToMinutes,
} from '../../utils/scheduleTimeBlocks';
import './PtSchedulePage.css';

const DAY_LABELS = ['Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7', 'Chủ Nhật'];
const MUSCLE_GROUPS = ['Ngực', 'Lưng/Xô', 'Đùi/Mông/Chân', 'Vai', 'Tay Trước/Sau', 'Bụng/Core', 'Cardio/Thể lực', 'Toàn thân', 'Giãn cơ/Phục hồi'];

// ============ Helpers ============
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

const memberPalette = (memberId) => {
  const hue = (Number(memberId || 0) * 67 + 18) % 360;
  return { '--member-color': `hsl(${hue} 78% 62%)`, '--member-bg': `hsla(${hue},55%,22%,.55)` };
};

const STATUS_LABELS = {
  SCHEDULED: 'Đã lên lịch',
  COMPLETED: 'Đã hoàn thành',
  CANCELLED: 'Đã hủy',
  NO_SHOW: 'Vắng mặt',
};

const PtSchedulePage = () => {
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const initialMemberId = queryParams.get('memberId');

  // State
  const [weekOffset, setWeekOffset] = useState(0);
  const [schedules, setSchedules] = useState([]);
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [toast, setToast] = useState(null);
  const [selectedDayIndex, setSelectedDayIndex] = useState(() => {
    const day = new Date().getDay();
    return day === 0 ? 6 : day - 1;
  });

  // Modal state
  const [showModal, setShowModal] = useState(false);
  const [editingSchedule, setEditingSchedule] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState('');

  // Form fields
  const [formMemberId, setFormMemberId] = useState('');
  const [formDate, setFormDate] = useState('');
  const [formStartTime, setFormStartTime] = useState('08:00');
  const [formEndTime, setFormEndTime] = useState('09:00');
  const [formMuscleGroup, setFormMuscleGroup] = useState('Ngực');
  const [formCustomNote, setFormCustomNote] = useState('');
  const [formRecurring, setFormRecurring] = useState(false);
  const [formRecurringWeeks, setFormRecurringWeeks] = useState(8);
  const [formSendNotification, setFormSendNotification] = useState(false);

  // Delete modal state
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [deleteAll, setDeleteAll] = useState(false);
  const [deleteNotify, setDeleteNotify] = useState(false);

  // Completion state
  const [showCompletion, setShowCompletion] = useState(false);
  const [availableExercises, setAvailableExercises] = useState([]);
  const [completionRows, setCompletionRows] = useState([
    { exerciseId: '', setCount: '', repCount: '', weightKg: '', durationMinutes: '', note: '' }
  ]);
  const [actualNote, setActualNote] = useState('');

  // ============ Computed ============
  const getDisplayedMonday = useCallback(() => {
    return addDays(getMonday(new Date()), weekOffset * 7);
  }, [weekOffset]);

  const getDisplayedDates = useCallback(() => {
    const monday = getDisplayedMonday();
    return Array.from({ length: 7 }, (_, i) => addDays(monday, i));
  }, [getDisplayedMonday]);

  const weekStartISO = toISODate(getDisplayedMonday());

  // ============ Data Loading ============
  const loadSchedules = useCallback(async () => {
    try {
      const data = await ptScheduleService.getPtSchedules(weekStartISO);
      setSchedules(data);
    } catch (err) {
      console.error('Lỗi tải lịch:', err);
    }
  }, [weekStartISO]);

  const loadMembers = useCallback(async () => {
    try {
      const data = await ptDashboardService.getAssignedMembers();
      setMembers(data);
    } catch (err) {
      console.error('Lỗi tải danh sách học viên:', err);
    }
  }, []);

  useEffect(() => {
    const init = async () => {
      setLoading(true);
      await Promise.all([loadSchedules(), loadMembers()]);
      setLoading(false);
    };
    init();
  }, [loadMembers, loadSchedules]);

  useEffect(() => {
    if (initialMemberId) setFormMemberId(initialMemberId);
  }, [initialMemberId]);

  useEffect(() => {
    if (weekOffset === 0) {
      const day = new Date().getDay();
      setSelectedDayIndex(day === 0 ? 6 : day - 1);
    } else {
      setSelectedDayIndex(0);
    }
  }, [weekOffset]);

  // ============ Toast ============
  const showToast = (message, type = 'success') => {
    setToast({ message, type });
    setTimeout(() => setToast(null), 3000);
  };

  // ============ Week Navigation ============
  const navigateWeek = (delta) => setWeekOffset(prev => prev + delta);
  const goToday = () => setWeekOffset(0);

  // ============ Get schedules for a cell ============
  const getSchedulesForCell = (dateISO, block) => {
    return schedules
      .filter(s => s.scheduleDate === dateISO && isInTimeBlock(s.startTime, block))
      .sort((a, b) => timeToMinutes(a.startTime) - timeToMinutes(b.startTime));
  };

  // ============ Modal Open/Close ============
  const openCreateModal = (dateISO, defaultStart) => {
    setEditingSchedule(null);
    setFormError('');
    setFormMemberId(initialMemberId || '');
    setFormDate(dateISO || toISODate(new Date()));
    setFormStartTime(defaultStart || '08:00');
    const [h, m] = (defaultStart || '08:00').split(':');
    setFormEndTime(`${pad2((parseInt(h, 10) + 1) % 24)}:${m}`);
    setFormMuscleGroup('Ngực');
    setFormCustomNote('');
    setFormRecurring(false);
    setFormRecurringWeeks(8);
    setFormSendNotification(false);
    setShowModal(true);
  };

  const openEditModal = (schedule) => {
    setEditingSchedule(schedule);
    setFormError('');
    setFormMemberId(String(schedule.memberId));
    setFormDate(schedule.scheduleDate);
    setFormStartTime(schedule.startTime);
    setFormEndTime(schedule.endTime);
    setFormMuscleGroup('');
    setFormCustomNote(schedule.exerciseNote || '');
    setFormRecurring(false);
    setFormRecurringWeeks(8);
    setFormSendNotification(false);
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setEditingSchedule(null);
    setShowDeleteConfirm(false);
    setShowCompletion(false);
  };

  const openCompletion = async () => {
    setFormError('');
    setActualNote(editingSchedule?.actualNote || '');
    setCompletionRows([{ exerciseId: '', setCount: '', repCount: '', weightKg: '', durationMinutes: '', note: '' }]);
    try {
      if (availableExercises.length === 0) {
        setAvailableExercises(await exerciseService.getAllExercises());
      }
      setShowCompletion(true);
    } catch (err) {
      setFormError(err.response?.data?.message || 'Không thể tải từ điển bài tập.');
    }
  };

  const updateCompletionRow = (index, field, value) => {
    setCompletionRows(rows => rows.map((row, rowIndex) =>
      rowIndex === index ? { ...row, [field]: value } : row
    ));
  };

  const addCompletionRow = () => setCompletionRows(rows => [
    ...rows,
    { exerciseId: '', setCount: '', repCount: '', weightKg: '', durationMinutes: '', note: '' }
  ]);

  const removeCompletionRow = (index) => setCompletionRows(rows =>
    rows.length === 1 ? rows : rows.filter((_, rowIndex) => rowIndex !== index)
  );

  const confirmCompletion = async () => {
    if (!editingSchedule) return;
    if (completionRows.some(row => !row.exerciseId)) {
      setFormError('Vui lòng chọn bài tập cho tất cả các dòng.');
      return;
    }
    setSubmitting(true);
    setFormError('');
    try {
      const exercises = completionRows.map(row => ({
        exerciseId: Number(row.exerciseId),
        setCount: row.setCount ? Number(row.setCount) : null,
        repCount: row.repCount ? Number(row.repCount) : null,
        weightKg: row.weightKg !== '' ? Number(row.weightKg) : null,
        durationMinutes: row.durationMinutes ? Number(row.durationMinutes) : null,
        note: row.note.trim() || null,
      }));
      await ptScheduleService.completeSchedule(editingSchedule.id, {
        actualNote: actualNote.trim() || null,
        exercises,
      });
      showToast('Đã ghi nhận kết quả buổi tập.');
      closeModal();
      await loadSchedules();
    } catch (err) {
      setFormError(err.response?.data?.message || err.response?.data || 'Không thể hoàn thành buổi tập.');
    } finally {
      setSubmitting(false);
    }
  };

  const confirmNoShow = async () => {
    if (!editingSchedule || !await confirmDialog(`Xác nhận ${editingSchedule.memberName} vắng buổi tập này?`, { confirmText: 'Xác nhận vắng', danger: true })) return;
    setSubmitting(true);
    setFormError('');
    try {
      await ptScheduleService.markNoShow(editingSchedule.id);
      showToast('Đã ghi nhận học viên vắng mặt.');
      closeModal();
      await loadSchedules();
    } catch (err) {
      setFormError(err.response?.data?.message || 'Không thể ghi nhận vắng mặt.');
    } finally {
      setSubmitting(false);
    }
  };

  // ============ Form Submit ============
  const handleSubmit = async (e) => {
    e.preventDefault();
    setFormError('');

    if (!formDate || !formStartTime || !formEndTime) {
      setFormError('Vui lòng điền đầy đủ ngày và giờ.');
      return;
    }
    if (timeToMinutes(formEndTime) <= timeToMinutes(formStartTime)) {
      setFormError('Giờ kết thúc phải sau giờ bắt đầu.');
      return;
    }
    const duration = timeToMinutes(formEndTime) - timeToMinutes(formStartTime);
    if (duration < 30 || duration > 120) {
      setFormError('Mỗi buổi tập phải kéo dài tối thiểu 30 phút và tối đa 2 giờ.');
      return;
    }

    const exerciseNote = formCustomNote.trim() || formMuscleGroup || '';

    setSubmitting(true);
    try {
      if (editingSchedule) {
        // Update
        await ptScheduleService.updateSchedule(editingSchedule.id, {
          scheduleDate: formDate,
          startTime: formStartTime,
          endTime: formEndTime,
          exerciseNote,
          sendNotification: formSendNotification
        });
        showToast('Đã cập nhật buổi tập thành công!');
      } else {
        // Create
        if (!formMemberId) {
          setFormError('Vui lòng chọn học viên.');
          setSubmitting(false);
          return;
        }
        await ptScheduleService.createSchedule({
          memberId: parseInt(formMemberId),
          scheduleDate: formDate,
          startTime: formStartTime,
          endTime: formEndTime,
          exerciseNote,
          recurring: formRecurring,
          recurringWeeks: formRecurring ? formRecurringWeeks : null,
          sendNotification: formSendNotification
        });
        showToast(formRecurring
          ? `Đã tạo ${formRecurringWeeks} buổi tập lặp lại hàng tuần!`
          : 'Đã tạo buổi tập thành công!');
      }
      closeModal();
      await loadSchedules();
      await loadMembers();
    } catch (err) {
      setFormError(err.response?.data?.message || err.response?.data || 'Có lỗi xảy ra.');
    } finally {
      setSubmitting(false);
    }
  };

  // ============ Delete ============
  const handleDeleteClick = () => {
    setDeleteAll(false);
    setDeleteNotify(false);
    setShowDeleteConfirm(true);
  };

  const confirmDelete = async () => {
    if (!editingSchedule) return;
    setSubmitting(true);
    try {
      await ptScheduleService.deleteSchedule(editingSchedule.id, deleteAll, deleteNotify);
      showToast(deleteAll ? 'Đã hủy tất cả buổi trong nhóm lặp lại!' : 'Đã hủy buổi tập!');
      closeModal();
      await loadSchedules();
      await loadMembers();
    } catch (err) {
      setFormError(err.response?.data?.message || 'Lỗi khi hủy buổi tập.');
    } finally {
      setSubmitting(false);
    }
  };

  // ============ Render ============
  const todayISO = toISODate(new Date());
  const dates = getDisplayedDates();
  const mondayDisplay = getDisplayedMonday();
  const sundayDisplay = addDays(mondayDisplay, 6);
  const weekLabel = `${formatDDMM(mondayDisplay)} – ${formatDDMM(sundayDisplay)}`;
  const selectedDate = dates[selectedDayIndex];
  const selectedDateISO = toISODate(selectedDate);

  if (loading) {
    return (
      <PtLayout>
        <div className="pts-page">
          <div className="pts-loading">Đang tải lịch dạy tuần...</div>
        </div>
      </PtLayout>
    );
  }

  return (
    <PtLayout>
      <div className="pts-page">
        {/* HEADER */}
        <div className="pts-header">
          <div className="pts-title-group">
            <h1 className="pts-title">
              <span className="pts-title-bar" />
              Lịch Dạy Tuần Của PT
            </h1>
            <p className="pts-subtitle">Nhấp vào ô trống để thêm buổi tập, nhấp vào buổi tập đã có để sửa hoặc hủy</p>
          </div>
          <div className="pts-actions">
            <button className="pts-btn-today" onClick={goToday} disabled={weekOffset === 0}>
              Hôm nay
            </button>
            <div className="pts-week-nav">
              <button className="pts-nav-btn" onClick={() => navigateWeek(-1)}>
                <ChevronLeft size={16} />
              </button>
              <span className="pts-week-text">{weekLabel}</span>
              <button className="pts-nav-btn" onClick={() => navigateWeek(1)}>
                <ChevronRight size={16} />
              </button>
            </div>
            <button className="pts-btn-primary" onClick={() => openCreateModal()}>
              <Plus size={16} /> Đặt lịch mới
            </button>
          </div>
        </div>

        {/* LEGEND */}
        <div className="pts-legend">
          <div className="pts-legend-item"><span className="pts-dot pts-dot-booked" /> Đã xác nhận</div>
          <div className="pts-legend-item"><span className="pts-dot pts-dot-recurring" /> Lịch lặp lại</div>
        </div>

        <div className="pts-mobile-day-tabs" role="tablist" aria-label="Chọn ngày trong tuần">
          {dates.map((date, index) => {
            const iso = toISODate(date);
            return (
              <button
                type="button"
                role="tab"
                aria-selected={selectedDayIndex === index}
                key={iso}
                className={`${selectedDayIndex === index ? 'active' : ''} ${iso === todayISO ? 'today' : ''}`}
                onClick={() => setSelectedDayIndex(index)}
              >
                <span>{DAY_LABELS[index]}</span>
                <strong>{formatDDMM(date)}</strong>
              </button>
            );
          })}
        </div>

        <div className="pts-mobile-day-view" role="tabpanel">
          <div className="pts-mobile-day-heading">
            <div>
              <span>{DAY_LABELS[selectedDayIndex]}</span>
              <strong>{formatDDMM(selectedDate)}</strong>
            </div>
            <button type="button" onClick={() => openCreateModal(selectedDateISO)}>
              <Plus size={16} /> Thêm lịch
            </button>
          </div>
          {SCHEDULE_TIME_BLOCKS.map((block) => {
            const items = getSchedulesForCell(selectedDateISO, block);
            return (
              <section key={block.id} className="pts-mobile-block">
                <div className="pts-mobile-block-title">
                  <span>{block.label}</span>
                  <small>{block.range}</small>
                </div>
                <div className="pts-mobile-block-list">
                  {items.map(item => (
                    <button
                      type="button"
                      key={item.id}
                      className={`pts-card pts-status-${(item.status || 'SCHEDULED').toLowerCase()} ${item.recurringGroupId ? 'pts-card-recurring' : 'pts-card-confirmed'}`}
                      style={memberPalette(item.memberId)}
                      onClick={() => openEditModal(item)}
                    >
                      <div className="pts-card-top">
                        <span className="pts-time-badge">{item.startTime} - {item.endTime}</span>
                        {item.recurringGroupId && <Repeat size={13} title="Lịch lặp lại" />}
                        {item.status && item.status !== 'SCHEDULED' && <span className="pts-status-badge">{STATUS_LABELS[item.status] || item.status}</span>}
                      </div>
                      {getScheduleTransitionLabel(item.startTime, item.endTime) && (
                        <div className="pts-cross-block">
                          {getScheduleTransitionLabel(item.startTime, item.endTime)}
                        </div>
                      )}
                      <div className="pts-client-name">{item.memberName}</div>
                      {item.exerciseNote && <div className="pts-workout-pill">{item.exerciseNote}</div>}
                    </button>
                  ))}
                  <button
                    type="button"
                    className="pts-mobile-add-block"
                    onClick={() => openCreateModal(selectedDateISO, block.defaultStart)}
                  >
                    <Plus size={15} /> {items.length ? 'Thêm buổi' : 'Thêm lịch ở khung giờ này'}
                  </button>
                </div>
              </section>
            );
          })}
        </div>

        {/* DESKTOP SCHEDULE GRID */}
        <div className="pts-grid-container">
          <div className="pts-grid-table">
            {/* Header row */}
            <div className="pts-grid-row pts-grid-header">
              <div className="pts-col-header pts-time-header">Khung Giờ</div>
              {dates.map((d, i) => {
                const iso = toISODate(d);
                const isToday = iso === todayISO;
                return (
                  <div key={i} className={`pts-col-header ${isToday ? 'pts-day-today' : ''}`}>
                    <div className="pts-day-name">{DAY_LABELS[i]}</div>
                    <div className="pts-day-num">{formatDDMM(d)}</div>
                  </div>
                );
              })}
            </div>

            {/* Body rows - RẠNG SÁNG, BAN NGÀY và BUỔI TỐI */}
            {SCHEDULE_TIME_BLOCKS.map(block => (
              <div key={block.id} className="pts-grid-row pts-grid-row-body">
                <div className="pts-time-cell">
                  {block.label}
                  <span className="pts-time-range">{block.range}</span>
                </div>
                {dates.map((d, dayIdx) => {
                  const iso = toISODate(d);
                  const isToday = iso === todayISO;
                  const items = getSchedulesForCell(iso, block);

                  return (
                    <div key={dayIdx} className={`pts-slot-cell ${isToday ? 'pts-day-today' : ''}`}>
                      <div className="pts-cell-cards">
                        {items.map(item => (
                          <button
                            key={item.id}
                            className={`pts-card pts-status-${(item.status || 'SCHEDULED').toLowerCase()} ${item.recurringGroupId ? 'pts-card-recurring' : 'pts-card-confirmed'}`}
                            style={memberPalette(item.memberId)}
                            onClick={() => openEditModal(item)}
                          >
                            <div className="pts-card-top">
                              <span className="pts-time-badge">{item.startTime} - {item.endTime}</span>
                              {item.recurringGroupId && <Repeat size={13} title="Lịch lặp lại" />}
                              {item.status && item.status !== 'SCHEDULED' && <span className="pts-status-badge">{STATUS_LABELS[item.status] || item.status}</span>}
                            </div>
                            {getScheduleTransitionLabel(item.startTime, item.endTime) && (
                              <div className="pts-cross-block">
                                {getScheduleTransitionLabel(item.startTime, item.endTime)}
                              </div>
                            )}
                            <div className="pts-client-name">{item.memberName}</div>
                            {item.exerciseNote && (
                              <div className="pts-workout-pill">{item.exerciseNote}</div>
                            )}
                          </button>
                        ))}
                      </div>
                      <button
                        className={`pts-add-mini ${items.length ? 'pts-add-compact' : 'pts-add-empty'}`}
                        onClick={() => openCreateModal(iso, block.defaultStart)}
                      >
                        + {items.length ? 'Thêm' : 'Thêm lịch'}
                      </button>
                    </div>
                  );
                })}
              </div>
            ))}
          </div>
        </div>

        {/* BOOKING MODAL */}
        {showModal && (
          <div className="pts-modal-backdrop" onClick={(e) => { if (e.target === e.currentTarget) closeModal(); }}>
            <div className="pts-modal-card">
              <div className="pts-modal-header">
                <h3 className="pts-modal-title">
                  <span className="pts-modal-dot" />
                  {editingSchedule ? 'Sửa Buổi Tập' : 'Xếp Lịch Tập Mới'}
                </h3>
                <button className="pts-btn-close" onClick={closeModal}><X size={18} /></button>
              </div>

              <form onSubmit={handleSubmit} noValidate>
                {/* Học viên */}
                {!editingSchedule && (
                  <div className="pts-form-group">
                    <label className="pts-form-label">Học viên</label>
                    <select
                      className="pts-form-control"
                      value={formMemberId}
                      onChange={(e) => setFormMemberId(e.target.value)}
                    >
                      <option value="" disabled>-- Chọn học viên --</option>
                      {members.map(m => (
                        <option key={m.memberId} value={m.memberId}>
                          {m.memberName} ({m.packageName})
                        </option>
                      ))}
                    </select>
                  </div>
                )}

                {editingSchedule && (
                  <div className="pts-form-group">
                    <label className="pts-form-label">Học viên</label>
                    <div className="pts-form-static">{editingSchedule.memberName}</div>
                    <div className={`pts-modal-status pts-modal-status-${(editingSchedule.status || 'SCHEDULED').toLowerCase()}`}>
                      {STATUS_LABELS[editingSchedule.status || 'SCHEDULED']}
                    </div>
                    {editingSchedule.status === 'COMPLETED' && (
                      <div className="pts-completed-summary">
                        {(editingSchedule.exercises || []).map(exercise => (
                          <div key={exercise.id || `${exercise.exerciseId}-${exercise.exerciseName}`}>
                            <strong>{exercise.exerciseName}</strong>
                            <span>{[
                              exercise.setCount && `${exercise.setCount} hiệp`,
                              exercise.repCount && `${exercise.repCount} lần`,
                              exercise.weightKg != null && `${exercise.weightKg} kg`,
                              exercise.durationMinutes && `${exercise.durationMinutes} phút`,
                            ].filter(Boolean).join(' · ') || 'Đã thực hiện'}</span>
                          </div>
                        ))}
                        {editingSchedule.actualNote && <p>{editingSchedule.actualNote}</p>}
                      </div>
                    )}
                  </div>
                )}

                {/* Ngày */}
                <div className="pts-form-group">
                  <label className="pts-form-label">Ngày</label>
                  <input
                    type="date"
                    className="pts-form-control"
                    value={formDate}
                    onChange={(e) => setFormDate(e.target.value)}
                    required
                  />
                </div>

                {/* Giờ */}
                <div className="pts-form-grid2">
                  <div className="pts-form-group">
                    <TimePickerWheel
                      label="Giờ bắt đầu"
                      value={formStartTime}
                      onChange={(val) => setFormStartTime(val)}
                    />
                  </div>
                  <div className="pts-form-group">
                    <TimePickerWheel
                      label="Giờ kết thúc"
                      value={formEndTime}
                      onChange={(val) => setFormEndTime(val)}
                    />
                  </div>
                </div>

                {/* Nhóm cơ */}
                <div className="pts-form-group">
                  <label className="pts-form-label">Nội dung buổi tập</label>
                  <div className="pts-workout-tags">
                    {MUSCLE_GROUPS.map((mg, i) => (
                      <button
                        key={i}
                        type="button"
                        className={`pts-tag-btn ${formMuscleGroup === mg ? 'active' : ''}`}
                        onClick={() => { setFormMuscleGroup(mg); setFormCustomNote(''); }}
                      >
                        {mg}
                      </button>
                    ))}
                  </div>
                </div>

                {/* Ghi chú tùy chỉnh */}
                <div className="pts-form-group">
                  <label className="pts-form-label">Ghi chú chi tiết (Tùy chọn)</label>
                  <input
                    type="text"
                    className="pts-form-control"
                    placeholder="VD: Deadlift nặng, Cardio chạy bộ..."
                    value={formCustomNote}
                    onChange={(e) => setFormCustomNote(e.target.value)}
                  />
                </div>

                {/* Lặp lại - chỉ khi tạo mới */}
                {!editingSchedule && (
                  <div className="pts-form-group">
                    <label className="pts-checkbox-group">
                      <input
                        type="checkbox"
                        checked={formRecurring}
                        onChange={(e) => setFormRecurring(e.target.checked)}
                      />
                      <Repeat size={14} />
                      <span>Lặp lại cố định hàng tuần</span>
                    </label>
                    {formRecurring && (
                      <div className="pts-recurring-weeks">
                        <label className="pts-form-label">Số tuần lặp lại (tính cả tuần này)</label>
                        <input
                          type="number"
                          className="pts-form-control pts-input-narrow"
                          min="2"
                          max="52"
                          value={formRecurringWeeks}
                          onChange={(e) => setFormRecurringWeeks(parseInt(e.target.value) || 8)}
                        />
                      </div>
                    )}
                  </div>
                )}

                {/* Thông báo */}
                <div className="pts-form-group">
                  <label className="pts-checkbox-group">
                    <input
                      type="checkbox"
                      checked={formSendNotification}
                      onChange={(e) => setFormSendNotification(e.target.checked)}
                    />
                    <Bell size={14} />
                    <span>Gửi thông báo cho học viên</span>
                  </label>
                </div>

                {/* Error */}
                {formError && <div className="pts-form-error">{formError}</div>}

                {/* Actions */}
                <div className="pts-modal-footer">
                  {editingSchedule && (editingSchedule.status || 'SCHEDULED') === 'SCHEDULED' && <>
                    <button type="button" className="pts-btn-complete" onClick={openCompletion}>
                      <CircleCheckBig size={14} /> Hoàn thành
                    </button>
                    <button type="button" className="pts-btn-no-show" onClick={confirmNoShow}>
                      <UserX size={14} /> Vắng mặt
                    </button>
                    <button type="button" className="pts-btn-danger" onClick={handleDeleteClick}>
                      <Trash2 size={14} /> Hủy buổi
                    </button>
                  </>}
                  <button type="button" className="pts-btn-secondary" onClick={closeModal}>Đóng</button>
                  {(!editingSchedule || (editingSchedule.status || 'SCHEDULED') === 'SCHEDULED') && <button type="submit" className="pts-btn-primary" disabled={submitting}>
                    {submitting ? 'Đang xử lý...' : (editingSchedule ? 'Cập Nhật' : 'Xác Nhận Đặt Lịch')}
                  </button>}
                </div>
              </form>

              {/* Delete confirmation overlay */}
              {showDeleteConfirm && (
                <div className="pts-delete-overlay">
                  <div className="pts-delete-card">
                    <h4>Xác nhận hủy buổi tập</h4>
                    <p>Bạn có chắc muốn hủy buổi tập của <strong>{editingSchedule?.memberName}</strong>?</p>

                    {editingSchedule?.recurringGroupId && (
                      <label className="pts-checkbox-group" style={{ marginTop: '12px' }}>
                        <input
                          type="checkbox"
                          checked={deleteAll}
                          onChange={(e) => setDeleteAll(e.target.checked)}
                        />
                        <Repeat size={14} />
                        <span>Hủy tất cả buổi trong nhóm lặp lại</span>
                      </label>
                    )}

                    <label className="pts-checkbox-group" style={{ marginTop: '8px' }}>
                      <input
                        type="checkbox"
                        checked={deleteNotify}
                        onChange={(e) => setDeleteNotify(e.target.checked)}
                      />
                      <Bell size={14} />
                      <span>Gửi thông báo cho học viên</span>
                    </label>

                    <div className="pts-delete-actions">
                      <button className="pts-btn-secondary" onClick={() => setShowDeleteConfirm(false)}>Hủy bỏ</button>
                      <button className="pts-btn-danger" onClick={confirmDelete} disabled={submitting}>
                        {submitting ? 'Đang xóa...' : 'Xác nhận hủy'}
                      </button>
                    </div>
                  </div>
                </div>
              )}

              {showCompletion && (
                <div className="pts-delete-overlay">
                  <div className="pts-delete-card pts-completion-card">
                    <h4>Ghi nhận kết quả buổi tập</h4>
                    <p>Chọn các bài tập <strong>{editingSchedule?.memberName}</strong> đã thực hiện.</p>
                    <div className="pts-completion-rows">
                      {completionRows.map((row, index) => (
                        <div className="pts-completion-row" key={index}>
                          <select className="pts-form-control" value={row.exerciseId}
                            onChange={event => updateCompletionRow(index, 'exerciseId', event.target.value)}>
                            <option value="">-- Chọn bài tập --</option>
                            {availableExercises.map(exercise => (
                              <option value={exercise.id} key={exercise.id}>
                                {exercise.name}{exercise.muscleGroup ? ` · ${exercise.muscleGroup}` : ''}
                              </option>
                            ))}
                          </select>
                          <div className="pts-completion-metrics">
                            <input type="number" min="1" placeholder="Hiệp" value={row.setCount}
                              onChange={event => updateCompletionRow(index, 'setCount', event.target.value)} />
                            <input type="number" min="1" placeholder="Lần" value={row.repCount}
                              onChange={event => updateCompletionRow(index, 'repCount', event.target.value)} />
                            <input type="number" min="0" step="0.5" placeholder="Kg" value={row.weightKg}
                              onChange={event => updateCompletionRow(index, 'weightKg', event.target.value)} />
                            <input type="number" min="1" placeholder="Phút" value={row.durationMinutes}
                              onChange={event => updateCompletionRow(index, 'durationMinutes', event.target.value)} />
                            <button type="button" title="Xóa dòng" onClick={() => removeCompletionRow(index)}><Trash2 size={15} /></button>
                          </div>
                          <input className="pts-form-control" placeholder="Ghi chú riêng cho bài tập (tùy chọn)"
                            value={row.note} onChange={event => updateCompletionRow(index, 'note', event.target.value)} />
                        </div>
                      ))}
                    </div>
                    <button type="button" className="pts-btn-add-exercise" onClick={addCompletionRow}>
                      <Plus size={15} /> Thêm bài tập
                    </button>
                    <textarea className="pts-form-control pts-completion-note" rows="3"
                      placeholder="Nhận xét chung sau buổi tập (tùy chọn)" value={actualNote}
                      onChange={event => setActualNote(event.target.value)} />
                    {formError && <div className="pts-form-error">{formError}</div>}
                    <div className="pts-delete-actions">
                      <button type="button" className="pts-btn-secondary" onClick={() => setShowCompletion(false)}>Quay lại</button>
                      <button type="button" className="pts-btn-complete" onClick={confirmCompletion} disabled={submitting}>
                        <CircleCheckBig size={15} /> {submitting ? 'Đang lưu...' : 'Xác nhận hoàn thành'}
                      </button>
                    </div>
                  </div>
                </div>
              )}
            </div>
          </div>
        )}

        {/* Toast */}
        {toast && (
          <div className={`pts-toast pts-toast-${toast.type}`}>
            {toast.message}
          </div>
        )}
      </div>
    </PtLayout>
  );
};

export default PtSchedulePage;
