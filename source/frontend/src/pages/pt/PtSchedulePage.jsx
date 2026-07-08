import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import PtLayout from '../../components/layout/PtLayout';
import ptScheduleService from '../../services/ptScheduleService';
import ptDashboardService from '../../services/ptDashboardService';
import { Calendar, Save, Trash2, Edit3, UserCheck, AlertCircle } from 'lucide-react';
import './PtSchedulePage.css';

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

const MUSCLE_GROUPS = ['Ngực', 'Lưng/Xô', 'Đùi/Mông/Chân', 'Vai', 'Tay Trước/Sau', 'Bụng/Core', 'Cardio/Thể lực', 'Nghỉ ngơi'];

const PtSchedulePage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const queryParams = new URLSearchParams(location.search);
  const initialMemberId = queryParams.get('memberId');

  const [members, setMembers] = useState([]);
  const [selectedMemberId, setSelectedMemberId] = useState('');
  const [ptSchedules, setPtSchedules] = useState([]); // Tất cả lịch của PT này
  const [memberSchedules, setMemberSchedules] = useState([]); // Lịch tạm thời của member đang chọn
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [tooltip, setTooltip] = useState({ show: false, text: '', type: '', x: 0, y: 0 });

  const handleMouseEnter = (e, text, type) => {
    if (!text) return;
    setTooltip({
      show: true,
      text: text,
      type: type,
      x: e.clientX,
      y: e.clientY
    });
  };

  const handleMouseMove = (e) => {
    setTooltip(prev => ({
      ...prev,
      x: e.clientX,
      y: e.clientY
    }));
  };

  const handleMouseLeave = () => {
    setTooltip(prev => ({ ...prev, show: false }));
  };

  // Dialog/Form state cho mô tả buổi tập
  const [showNoteModal, setShowNoteModal] = useState(false);
  const [currentSlotConfig, setCurrentSlotConfig] = useState(null); // { day, slotIndex }
  const [muscleGroup, setMuscleGroup] = useState('Ngực');
  const [customNote, setCustomNote] = useState('');

  const loadData = async () => {
    try {
      const membersData = await ptDashboardService.getAssignedMembers();
      setMembers(membersData);
      
      const schedulesData = await ptScheduleService.getPtSchedules();
      setPtSchedules(schedulesData);

      if (initialMemberId) {
        setSelectedMemberId(initialMemberId);
      }
    } catch (err) {
      setError('Lỗi tải dữ liệu thời khóa biểu');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [initialMemberId]);

  // Cập nhật memberSchedules khi đổi selectedMemberId
  useEffect(() => {
    if (!selectedMemberId) {
      setMemberSchedules([]);
      return;
    }
    // Lấy tất cả slot có sẵn trong ptSchedules của member này làm nháp
    const currentMemberSlots = ptSchedules
      .filter(s => s.memberId === parseInt(selectedMemberId))
      .map(s => ({
        dayOfWeek: s.dayOfWeek,
        slotIndex: s.slotIndex,
        exerciseNote: s.exerciseNote
      }));
    setMemberSchedules(currentMemberSlots);
  }, [selectedMemberId, ptSchedules]);

  const handleCellClick = (dayIndex, slotIndex) => {
    if (!selectedMemberId) {
      alert("Vui lòng chọn học viên ở thanh phía trên trước!");
      return;
    }

    // Tìm xem ô này có trong lịch của PT chưa
    const existingSlot = ptSchedules.find(s => s.dayOfWeek === dayIndex && s.slotIndex === slotIndex);
    
    if (existingSlot && existingSlot.memberId !== parseInt(selectedMemberId)) {
      // Slot đã bị học viên khác chiếm (Màu đỏ)
      return;
    }

    const isAlreadySelected = memberSchedules.some(s => s.dayOfWeek === dayIndex && s.slotIndex === slotIndex);

    if (isAlreadySelected) {
      // Hủy chọn (Vàng -> Xanh)
      setMemberSchedules(prev => prev.filter(s => !(s.dayOfWeek === dayIndex && s.slotIndex === slotIndex)));
    } else {
      // Mở modal cấu hình bài tập cho ô này
      setCurrentSlotConfig({ day: dayIndex, slotIndex });
      setMuscleGroup('Ngực');
      setCustomNote('');
      setShowNoteModal(true);
    }
  };

  const handleSaveSlotNote = (e) => {
    e.preventDefault();
    if (!currentSlotConfig) return;

    const note = customNote.trim() ? customNote.trim() : muscleGroup;
    const newSlot = {
      dayOfWeek: currentSlotConfig.day,
      slotIndex: currentSlotConfig.slotIndex,
      exerciseNote: note
    };

    setMemberSchedules(prev => [...prev, newSlot]);
    setShowNoteModal(false);
    setCurrentSlotConfig(null);
  };

  const handleSaveAll = async () => {
    if (!selectedMemberId) return;
    setSubmitting(true);
    setError('');
    setSuccess('');

    try {
      await ptScheduleService.saveMemberSchedule({
        memberId: parseInt(selectedMemberId),
        slots: memberSchedules
      });
      setSuccess('Lưu lịch trình huấn luyện thành công!');
      // Tải lại toàn bộ lịch và danh sách học viên
      const [schedulesData, membersData] = await Promise.all([
        ptScheduleService.getPtSchedules(),
        ptDashboardService.getAssignedMembers()
      ]);
      setPtSchedules(schedulesData);
      setMembers(membersData);
    } catch (err) {
      setError(err.response?.data?.message || 'Có lỗi xảy ra khi lưu lịch trình');
    } finally {
      setSubmitting(false);
    }
  };

  const getCellStatus = (dayIndex, slotIndex) => {
    // 1. Kiểm tra trong danh sách nháp trước
    const draftSlot = memberSchedules.find(s => s.dayOfWeek === dayIndex && s.slotIndex === slotIndex);
    if (draftSlot) {
      return { type: 'SELECTED', note: draftSlot.exerciseNote };
    }

    // 2. Kiểm tra trong danh sách tổng
    const bookedSlot = ptSchedules.find(s => s.dayOfWeek === dayIndex && s.slotIndex === slotIndex);
    if (bookedSlot) {
      return { type: 'BOOKED', note: bookedSlot.exerciseNote, memberName: bookedSlot.memberName };
    }

    return { type: 'EMPTY' };
  };

  if (loading) {
    return (
      <PtLayout>
        <div className="pt-schedule-page">
          <p style={{ color: '#94a3b8', marginTop: '100px', textAlign: 'center' }}>Đang tải cấu hình thời khóa biểu...</p>
        </div>
      </PtLayout>
    );
  }

  const selectedMember = members.find(m => m.memberId === parseInt(selectedMemberId));

  return (
    <PtLayout>
      <div className="pt-schedule-page">
        <div className="schedule-header">
          <h1>Lịch Trình Huấn Luyện</h1>
          <p>Xếp lịch tập cố định hàng tuần và chuẩn bị nội dung bài tập cho từng học viên</p>
        </div>

        {error && <div className="schedule-alert error">{error}</div>}
        {success && <div className="schedule-alert success">{success}</div>}

        {/* Bảng cấu hình */}
        <div className="schedule-controls">
          <div className="control-group">
            <label htmlFor="memberSelect">Chọn học viên cần lên lịch:</label>
            <select
              id="memberSelect"
              value={selectedMemberId}
              onChange={(e) => setSelectedMemberId(e.target.value)}
            >
              <option value="">-- Chọn học viên --</option>
              {members.map(m => (
                <option key={m.memberId} value={m.memberId}>
                  {m.memberName} ({m.packageName}) {m.isScheduled ? '✓ Đã có lịch' : '⚠ Chưa có lịch'}
                </option>
              ))}
            </select>
          </div>

          {selectedMemberId && (
            <button className="btn-save-schedule" onClick={handleSaveAll} disabled={submitting}>
              <Save size={18} /> {submitting ? 'Đang lưu...' : 'Lưu lịch trình'}
            </button>
          )}
        </div>

        {/* Legend */}
        <div className="schedule-legend">
          <div className="legend-item"><span className="box green"></span> Trống (Click để xếp)</div>
          <div className="legend-item"><span className="box yellow"></span> Đang chọn (Học viên này)</div>
          <div className="legend-item"><span className="box red"></span> Đã bận (Học viên khác)</div>
        </div>

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
                // Kiểm tra xem đây có phải là hàng đầu tiên của 1 buổi để gộp row không
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
                      
                      let cellClass = 'cell-empty';
                      let cellContent = null;
                      let hoverTitle = '';

                      if (status.type === 'SELECTED') {
                        cellClass = 'cell-selected';
                        const currentName = selectedMember?.memberName || 'Học viên đang chọn';
                        hoverTitle = currentName + (status.note ? ` - ${status.note}` : '');
                        cellContent = (
                          <div className="cell-details">
                            <span className="member-name">{currentName}</span>
                            <span className="exercise-badge">{status.note}</span>
                          </div>
                        );
                      } else if (status.type === 'BOOKED') {
                        cellClass = 'cell-booked';
                        hoverTitle = status.memberName + (status.note ? ` - ${status.note}` : '');
                        cellContent = (
                          <div className="cell-details">
                            <span className="member-name">{status.memberName}</span>
                            <span className="exercise-badge">{status.note}</span>
                          </div>
                        );
                      }

                      return (
                        <td 
                          key={dIdx} 
                          className={`schedule-cell ${cellClass}`}
                          onClick={() => handleCellClick(dIdx, slot.index)}
                          onMouseEnter={(e) => handleMouseEnter(e, hoverTitle, status.type)}
                          onMouseMove={handleMouseMove}
                          onMouseLeave={handleMouseLeave}
                        >
                          {cellContent}
                        </td>
                      );
                    })}
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>

        {/* Modal ghi chú bài tập */}
        {showNoteModal && (
          <div className="note-modal-overlay">
            <div className="note-modal">
              <div className="modal-header">
                <h2>Nội Dung Tập Luyện</h2>
                <button className="btn-close" onClick={() => setShowNoteModal(false)}>×</button>
              </div>
              <form onSubmit={handleSaveSlotNote}>
                <div className="modal-body">
                  <div className="form-group">
                    <label>Chọn nhóm cơ / mục tiêu chính:</label>
                    <div className="muscle-options">
                      {MUSCLE_GROUPS.map((mg, i) => (
                        <button
                          key={i}
                          type="button"
                          className={`muscle-btn ${muscleGroup === mg ? 'active' : ''}`}
                          onClick={() => setMuscleGroup(mg)}
                        >
                          {mg}
                        </button>
                      ))}
                    </div>
                  </div>

                  <div className="form-group" style={{ marginTop: '20px' }}>
                    <label htmlFor="customNoteInput">Ghi chú chi tiết (Tùy chọn):</label>
                    <input
                      id="customNoteInput"
                      type="text"
                      placeholder="Nhập ghi chú tùy ý (ví dụ: Deadlift nặng, Cardio chạy bộ...)"
                      value={customNote}
                      onChange={(e) => setCustomNote(e.target.value)}
                    />
                  </div>
                </div>
                <div className="modal-footer">
                  <button type="button" className="btn-cancel" onClick={() => setShowNoteModal(false)}>Hủy</button>
                  <button type="submit" className="btn-save">Gán vào thời khóa biểu</button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* Custom Premium Tooltip */}
        {tooltip.show && (() => {
          const isSelected = tooltip.type === 'SELECTED';
          const borderColor = isSelected ? 'rgba(234, 179, 8, 0.7)' : 'rgba(239, 68, 68, 0.7)';
          const nameColor = isSelected ? '#facc15' : '#f87171';
          const parts = tooltip.text.split(' - ');
          
          return (
            <div 
              className="custom-schedule-tooltip"
              style={{
                position: 'fixed',
                left: (tooltip.x + 15) + 'px',
                top: (tooltip.y + 15) + 'px',
                pointerEvents: 'none',
                zIndex: 9999,
                background: 'rgba(15, 23, 42, 0.96)',
                border: `1px solid ${borderColor}`,
                borderRadius: '8px',
                padding: '10px 14px',
                color: '#f8fafc',
                fontSize: '0.85rem',
                boxShadow: '0 10px 25px -5px rgba(0, 0, 0, 0.5), 0 8px 10px -6px rgba(0, 0, 0, 0.5)',
                backdropFilter: 'blur(8px)',
                animation: 'tooltipFadeIn 0.15s ease-out',
                display: 'flex',
                flexDirection: 'column',
                gap: '2px'
              }}
            >
              <div style={{ fontWeight: 'bold', color: nameColor, fontSize: '0.9rem' }}>{parts[0]}</div>
              {parts[1] && (
                <div style={{ marginTop: '4px', color: '#94a3b8', fontSize: '0.8rem', display: 'flex', alignItems: 'center', gap: '4px' }}>
                  <span>🎯</span> {parts[1]}
                </div>
              )}
            </div>
          );
        })()}
      </div>
    </PtLayout>
  );
};

export default PtSchedulePage;
