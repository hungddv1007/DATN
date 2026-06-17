import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import PtLayout from '../../components/layout/PtLayout';
import api from '../../services/api';
import { ArrowLeft, Plus, Trash2, Edit2, Dumbbell, Calendar, ChevronDown, ChevronRight, Save, X } from 'lucide-react';
import '../admin/AdminManagement.css';

const DAY_LABELS = ['', 'Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7', 'Chủ nhật'];

const PtRouteDetail = () => {
  const { routeId } = useParams();
  const navigate = useNavigate();

  const [route, setRoute] = useState(null);
  const [exercises, setExercises] = useState([]);
  const [loading, setLoading] = useState(true);
  const [expandedWeeks, setExpandedWeeks] = useState({});

  // Session modal
  const [showSessionModal, setShowSessionModal] = useState(false);
  const [editingSession, setEditingSession] = useState(null);
  const [sessionForm, setSessionForm] = useState({ weekNum: 1, dayNum: 1, name: '', isRestDay: false });

  // Exercise modal
  const [showExerciseModal, setShowExerciseModal] = useState(false);
  const [editingExercise, setEditingExercise] = useState(null);
  const [targetSessionId, setTargetSessionId] = useState(null);
  const [exerciseForm, setExerciseForm] = useState({ exerciseId: '', sets: 3, reps: 10, weightKg: '', notes: '' });
  const [exerciseSearch, setExerciseSearch] = useState('');

  useEffect(() => { fetchData(); }, [routeId]);

  const fetchData = async () => {
    try {
      const [routeRes, exRes] = await Promise.all([
        api.get(`/pt/training-routes/${routeId}`),
        api.get('/exercises')
      ]);
      setRoute(routeRes.data);
      setExercises(exRes.data);
      // Auto-expand all weeks
      if (routeRes.data.weeks) {
        const expanded = {};
        Object.keys(routeRes.data.weeks).forEach(w => expanded[w] = true);
        setExpandedWeeks(expanded);
      }
    } catch (err) {
      console.error('Lỗi tải lộ trình:', err);
    } finally {
      setLoading(false);
    }
  };

  const toggleWeek = (weekNum) => {
    setExpandedWeeks(prev => ({ ...prev, [weekNum]: !prev[weekNum] }));
  };

  // ===== SESSION CRUD =====
  const openAddSession = (weekNum = 1) => {
    setEditingSession(null);
    setSessionForm({ weekNum, dayNum: 1, name: '', isRestDay: false });
    setShowSessionModal(true);
  };

  const openEditSession = (session) => {
    setEditingSession(session);
    setSessionForm({ weekNum: session.weekNum, dayNum: session.dayNum, name: session.name || '', isRestDay: session.isRestDay || false });
    setShowSessionModal(true);
  };

  const handleSessionSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingSession) {
        await api.put(`/pt/training-routes/${routeId}/sessions/${editingSession.id}`, sessionForm);
      } else {
        await api.post(`/pt/training-routes/${routeId}/sessions`, sessionForm);
      }
      setShowSessionModal(false);
      fetchData();
    } catch (err) {
      alert(err.response?.data?.message || 'Có lỗi xảy ra');
    }
  };

  const handleDeleteSession = async (sessionId) => {
    if (!window.confirm('Xóa buổi tập này và tất cả bài tập bên trong?')) return;
    try {
      await api.delete(`/pt/training-routes/${routeId}/sessions/${sessionId}`);
      fetchData();
    } catch (err) {
      alert(err.response?.data?.message || 'Có lỗi xảy ra');
    }
  };

  // ===== EXERCISE CRUD =====
  const openAddExercise = (sessionId) => {
    setEditingExercise(null);
    setTargetSessionId(sessionId);
    setExerciseForm({ exerciseId: '', sets: 3, reps: 10, weightKg: '', notes: '' });
    setExerciseSearch('');
    setShowExerciseModal(true);
  };

  const openEditExercise = (sessionId, se) => {
    setEditingExercise(se);
    setTargetSessionId(sessionId);
    setExerciseForm({
      exerciseId: se.exerciseId,
      sets: se.sets || 3,
      reps: se.reps || 10,
      weightKg: se.weightKg || '',
      notes: se.notes || ''
    });
    setExerciseSearch('');
    setShowExerciseModal(true);
  };

  const handleExerciseSubmit = async (e) => {
    e.preventDefault();
    if (!exerciseForm.exerciseId) { alert('Vui lòng chọn bài tập'); return; }
    const payload = {
      ...exerciseForm,
      weightKg: exerciseForm.weightKg === '' ? null : parseFloat(exerciseForm.weightKg)
    };
    try {
      if (editingExercise) {
        await api.put(`/pt/training-routes/${routeId}/sessions/${targetSessionId}/exercises/${editingExercise.id}`, payload);
      } else {
        await api.post(`/pt/training-routes/${routeId}/sessions/${targetSessionId}/exercises`, payload);
      }
      setShowExerciseModal(false);
      fetchData();
    } catch (err) {
      alert(err.response?.data?.message || 'Có lỗi xảy ra');
    }
  };

  const handleDeleteExercise = async (sessionId, seId) => {
    if (!window.confirm('Xóa bài tập này khỏi buổi tập?')) return;
    try {
      await api.delete(`/pt/training-routes/${routeId}/sessions/${sessionId}/exercises/${seId}`);
      fetchData();
    } catch (err) {
      alert(err.response?.data?.message || 'Có lỗi xảy ra');
    }
  };

  const filteredExercises = exercises.filter(ex =>
    ex.name?.toLowerCase().includes(exerciseSearch.toLowerCase()) ||
    ex.muscleGroup?.toLowerCase().includes(exerciseSearch.toLowerCase())
  );

  if (loading) {
    return <PtLayout><div style={{ textAlign: 'center', padding: '80px', color: '#94a3b8' }}>Đang tải...</div></PtLayout>;
  }

  if (!route) {
    return <PtLayout><div style={{ textAlign: 'center', padding: '80px', color: '#64748b' }}>Không tìm thấy lộ trình.</div></PtLayout>;
  }

  const weeks = route.weeks || {};
  const weekNums = Object.keys(weeks).map(Number).sort((a, b) => a - b);

  return (
    <PtLayout>
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '8px' }}>
        <button className="btn-icon" onClick={() => navigate('/pt/templates')} style={{ color: '#94a3b8' }}>
          <ArrowLeft size={20} />
        </button>
        <h1 style={{ margin: 0 }}>{route.name}</h1>
        <span className={`status-badge ${route.status === 'TEMPLATE' ? 'status-pending' : route.status === 'ASSIGNED' ? 'status-confirmed' : 'status-cancelled'}`}
          style={{ marginLeft: '8px' }}>
          {route.status === 'TEMPLATE' ? 'Mẫu' : route.status === 'ASSIGNED' ? 'Đang tập' : route.status === 'COMPLETED' ? 'Hoàn thành' : route.status}
        </span>
      </div>
      <p>
        {route.memberName ? <>Gán cho: <strong style={{ color: '#f97316' }}>{route.memberName}</strong></> : 'Lộ trình mẫu — chưa gán cho học viên nào.'}
        {' • '}{route.totalSessions || 0} buổi tập • {route.totalWeeks || 0} tuần
      </p>

      {/* Add session button */}
      <div style={{ display: 'flex', gap: '12px', marginBottom: '20px' }}>
        <button className="btn-primary-action green" onClick={() => openAddSession(weekNums.length > 0 ? Math.max(...weekNums) : 1)}>
          <Plus size={20} /> Thêm buổi tập
        </button>
      </div>

      {/* Weeks Accordion */}
      {weekNums.length === 0 ? (
        <div className="admin-table-container" style={{ marginTop: 0 }}>
          <div style={{ padding: '50px 20px', textAlign: 'center', color: '#64748b' }}>
            <Calendar size={48} style={{ marginBottom: '16px', opacity: 0.5 }} />
            <p style={{ margin: 0, fontSize: '1.05rem' }}>Chưa có buổi tập nào.</p>
            <p style={{ margin: '8px 0 0', fontSize: '0.9rem' }}>Nhấn "Thêm buổi tập" để bắt đầu xây dựng lộ trình.</p>
          </div>
        </div>
      ) : (
        weekNums.map(weekNum => (
          <div key={weekNum} className="admin-table-container" style={{ marginTop: 0, marginBottom: '16px' }}>
            {/* Week Header */}
            <div onClick={() => toggleWeek(weekNum)} style={{
              padding: '14px 20px', borderBottom: expandedWeeks[weekNum] ? '1px solid rgba(255,255,255,0.06)' : 'none',
              display: 'flex', justifyContent: 'space-between', alignItems: 'center', cursor: 'pointer',
              transition: 'background 0.2s'
            }}
            onMouseEnter={e => e.currentTarget.style.background = 'rgba(255,255,255,0.02)'}
            onMouseLeave={e => e.currentTarget.style.background = 'transparent'}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                {expandedWeeks[weekNum] ? <ChevronDown size={18} color="#f97316" /> : <ChevronRight size={18} color="#94a3b8" />}
                <h3 style={{ color: '#f1f5f9', margin: 0 }}>Tuần {weekNum}</h3>
                <span style={{ color: '#64748b', fontSize: '0.85rem' }}>({weeks[weekNum]?.length || 0} buổi)</span>
              </div>
              <button className="btn-icon" onClick={(e) => { e.stopPropagation(); openAddSession(weekNum); }}
                title="Thêm buổi" style={{ color: '#10b981', background: 'rgba(16,185,129,0.1)' }}>
                <Plus size={16} />
              </button>
            </div>

            {/* Sessions */}
            {expandedWeeks[weekNum] && weeks[weekNum]?.sort((a, b) => a.dayNum - b.dayNum).map(session => (
              <div key={session.id} style={{ borderBottom: '1px solid rgba(255,255,255,0.04)' }}>
                {/* Session header */}
                <div style={{
                  padding: '12px 20px 12px 44px',
                  display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                  background: session.isRestDay ? 'rgba(234,179,8,0.04)' : 'transparent'
                }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <span style={{
                      display: 'inline-block', width: '28px', height: '28px', borderRadius: '6px',
                      background: session.isRestDay ? 'rgba(234,179,8,0.15)' : 'rgba(59,130,246,0.15)',
                      color: session.isRestDay ? '#fde047' : '#93c5fd',
                      textAlign: 'center', lineHeight: '28px', fontSize: '0.85rem', fontWeight: '700'
                    }}>
                      {DAY_LABELS[session.dayNum]?.substring(0, 2) || `N${session.dayNum}`}
                    </span>
                    <div>
                      <span style={{ color: '#f1f5f9', fontWeight: '500' }}>
                        {session.name || `${DAY_LABELS[session.dayNum] || `Ngày ${session.dayNum}`}`}
                      </span>
                      {session.isRestDay && <span style={{ color: '#eab308', fontSize: '0.8rem', marginLeft: '8px' }}>🔄 Nghỉ ngơi</span>}
                      {!session.isRestDay && session.exercises?.length > 0 && (
                        <span style={{ color: '#64748b', fontSize: '0.8rem', marginLeft: '8px' }}>
                          {session.exercises.length} bài tập
                        </span>
                      )}
                    </div>
                  </div>
                  <div className="action-btns">
                    {!session.isRestDay && (
                      <button className="btn-icon" title="Thêm bài tập" onClick={() => openAddExercise(session.id)}
                        style={{ color: '#10b981', background: 'rgba(16,185,129,0.1)' }}>
                        <Plus size={14} />
                      </button>
                    )}
                    <button className="btn-icon" title="Sửa buổi" onClick={() => openEditSession(session)}
                      style={{ color: '#3b82f6', background: 'rgba(59,130,246,0.1)' }}>
                      <Edit2 size={14} />
                    </button>
                    <button className="btn-icon cancel" title="Xóa buổi" onClick={() => handleDeleteSession(session.id)}>
                      <Trash2 size={14} />
                    </button>
                  </div>
                </div>

                {/* Exercises list */}
                {!session.isRestDay && session.exercises?.length > 0 && (
                  <div style={{ padding: '0 20px 12px 72px' }}>
                    {session.exercises.map((se, idx) => (
                      <div key={se.id} style={{
                        display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                        padding: '8px 12px', borderRadius: '6px', marginBottom: '4px',
                        background: idx % 2 === 0 ? 'rgba(15,23,42,0.3)' : 'transparent',
                        transition: 'background 0.2s'
                      }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', flex: 1 }}>
                          <Dumbbell size={14} style={{ color: '#f97316', flexShrink: 0 }} />
                          <span style={{ color: '#e2e8f0', fontWeight: '500', minWidth: '140px' }}>{se.exerciseName}</span>
                          <span style={{ color: '#94a3b8', fontSize: '0.85rem' }}>{se.muscleGroup}</span>
                          <span style={{ color: '#f97316', fontSize: '0.85rem', fontWeight: '600', marginLeft: 'auto', whiteSpace: 'nowrap' }}>
                            {se.sets}×{se.reps}{se.weightKg ? ` • ${se.weightKg}kg` : ''}
                          </span>
                        </div>
                        <div className="action-btns" style={{ marginLeft: '12px' }}>
                          <button className="btn-icon" title="Sửa" onClick={() => openEditExercise(session.id, se)}
                            style={{ color: '#3b82f6', background: 'rgba(59,130,246,0.1)' }}>
                            <Edit2 size={12} />
                          </button>
                          <button className="btn-icon cancel" title="Xóa" onClick={() => handleDeleteExercise(session.id, se.id)}>
                            <Trash2 size={12} />
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            ))}
          </div>
        ))
      )}

      {/* ===== MODAL: Thêm/Sửa buổi tập ===== */}
      {showSessionModal && (
        <div className="modal-overlay" onClick={() => setShowSessionModal(false)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <h2>{editingSession ? 'Sửa Buổi Tập' : 'Thêm Buổi Tập'}</h2>
            <form onSubmit={handleSessionSubmit}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <div>
                  <label>Tuần thứ</label>
                  <input type="number" min="1" max="52" value={sessionForm.weekNum}
                    onChange={e => setSessionForm({ ...sessionForm, weekNum: parseInt(e.target.value) || 1 })} required />
                </div>
                <div>
                  <label>Ngày trong tuần</label>
                  <select value={sessionForm.dayNum} onChange={e => setSessionForm({ ...sessionForm, dayNum: parseInt(e.target.value) })}>
                    {[1,2,3,4,5,6,7].map(d => <option key={d} value={d}>{DAY_LABELS[d]}</option>)}
                  </select>
                </div>
              </div>
              <div>
                <label>Tên buổi tập (tuỳ chọn)</label>
                <input type="text" value={sessionForm.name} onChange={e => setSessionForm({ ...sessionForm, name: e.target.value })}
                  placeholder="VD: Push Day, Leg Day..." />
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <input type="checkbox" id="isRestDay" checked={sessionForm.isRestDay}
                  onChange={e => setSessionForm({ ...sessionForm, isRestDay: e.target.checked })}
                  style={{ width: '18px', height: '18px', accentColor: '#f97316' }} />
                <label htmlFor="isRestDay" style={{ margin: 0, cursor: 'pointer' }}>Ngày nghỉ ngơi (Rest Day)</label>
              </div>
              <div style={{ display: 'flex', gap: '12px', marginTop: '10px' }}>
                <button type="submit" className="btn-submit">
                  <Save size={16} style={{ verticalAlign: 'middle', marginRight: '6px' }} />{editingSession ? 'Cập nhật' : 'Thêm'}
                </button>
                <button type="button" className="btn-cancel" onClick={() => setShowSessionModal(false)}>Hủy</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* ===== MODAL: Thêm/Sửa bài tập ===== */}
      {showExerciseModal && (
        <div className="modal-overlay" onClick={() => setShowExerciseModal(false)}>
          <div className="modal-content" onClick={e => e.stopPropagation()} style={{ maxWidth: '650px' }}>
            <h2>{editingExercise ? 'Sửa Bài Tập' : 'Thêm Bài Tập Vào Buổi'}</h2>
            <form onSubmit={handleExerciseSubmit}>
              {/* Exercise Picker */}
              <div>
                <label>Chọn bài tập</label>
                <input type="text" value={exerciseSearch} onChange={e => setExerciseSearch(e.target.value)}
                  placeholder="Gõ để tìm bài tập..." />
                <div style={{
                  maxHeight: '180px', overflowY: 'auto', marginTop: '8px',
                  border: '1px solid rgba(255,255,255,0.08)', borderRadius: '8px'
                }}>
                  {filteredExercises.length === 0 ? (
                    <div style={{ padding: '12px', color: '#64748b', textAlign: 'center' }}>Không tìm thấy bài tập.</div>
                  ) : filteredExercises.map(ex => (
                    <div key={ex.id} onClick={() => { setExerciseForm({ ...exerciseForm, exerciseId: ex.id }); setExerciseSearch(ex.name); }}
                      style={{
                        padding: '10px 14px', cursor: 'pointer',
                        background: exerciseForm.exerciseId === ex.id ? 'rgba(249,115,22,0.12)' : 'transparent',
                        borderBottom: '1px solid rgba(255,255,255,0.04)',
                        transition: 'background 0.15s'
                      }}
                      onMouseEnter={e => { if (exerciseForm.exerciseId !== ex.id) e.currentTarget.style.background = 'rgba(255,255,255,0.03)'; }}
                      onMouseLeave={e => { if (exerciseForm.exerciseId !== ex.id) e.currentTarget.style.background = 'transparent'; }}
                    >
                      <div style={{ color: exerciseForm.exerciseId === ex.id ? '#f97316' : '#f1f5f9', fontWeight: '500' }}>{ex.name}</div>
                      <div style={{ color: '#64748b', fontSize: '0.8rem' }}>{ex.muscleGroup}</div>
                    </div>
                  ))}
                </div>
              </div>

              {/* Parameters */}
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '12px' }}>
                <div>
                  <label>Số set</label>
                  <input type="number" min="1" max="100" value={exerciseForm.sets}
                    onChange={e => setExerciseForm({ ...exerciseForm, sets: parseInt(e.target.value) || 1 })} required />
                </div>
                <div>
                  <label>Số rep</label>
                  <input type="number" min="1" max="1000" value={exerciseForm.reps}
                    onChange={e => setExerciseForm({ ...exerciseForm, reps: parseInt(e.target.value) || 1 })} required />
                </div>
                <div>
                  <label>Tạ (kg)</label>
                  <input type="number" step="0.5" min="0" value={exerciseForm.weightKg}
                    onChange={e => setExerciseForm({ ...exerciseForm, weightKg: e.target.value })}
                    placeholder="Tùy chọn" />
                </div>
              </div>

              <div>
                <label>Ghi chú</label>
                <input type="text" value={exerciseForm.notes} onChange={e => setExerciseForm({ ...exerciseForm, notes: e.target.value })}
                  placeholder="VD: Tăng 2.5kg mỗi tuần, Tempo 3-1-2..." />
              </div>

              <div style={{ display: 'flex', gap: '12px', marginTop: '10px' }}>
                <button type="submit" className="btn-submit">
                  <Save size={16} style={{ verticalAlign: 'middle', marginRight: '6px' }} />{editingExercise ? 'Cập nhật' : 'Thêm'}
                </button>
                <button type="button" className="btn-cancel" onClick={() => setShowExerciseModal(false)}>Hủy</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </PtLayout>
  );
};

export default PtRouteDetail;
