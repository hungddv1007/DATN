import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import PtLayout from '../../components/layout/PtLayout';
import trainingPlanService from '../../services/trainingPlanService';
import assignmentService from '../../services/assignmentService';
import api from '../../services/api';
import { Plus, Search, Eye, Edit2, Copy, Trash2, CheckCircle, X, Users, Clock, Target, Calendar } from 'lucide-react';
import './PtWorkoutManager.css';

const GOALS = { MUSCLE:'Tăng cơ', FAT_LOSS:'Giảm mỡ', ENDURANCE:'Sức bền', STRENGTH:'Sức mạnh', FLEXIBILITY:'Linh hoạt' };
const DIFFS = { EASY:'Dễ', MEDIUM:'Trung bình', HARD:'Nâng cao' };
const DAYS = ['Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7', 'CN'];

const PtPlansPage = () => {
  const navigate = useNavigate();
  const [plans, setPlans] = useState([]);
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Filters
  const [filter, setFilter] = useState('all');
  const [search, setSearch] = useState('');

  // Modals state
  const [isPlanModalOpen, setIsPlanModalOpen] = useState(false);
  const [isAssignModalOpen, setIsAssignModalOpen] = useState(false);
  const [isConfirmModalOpen, setIsConfirmModalOpen] = useState(false);
  
  // Forms state
  const [editingPlanId, setEditingPlanId] = useState(null);
  const [deletingPlanId, setDeletingPlanId] = useState(null);
  const [assigningPlanId, setAssigningPlanId] = useState(null);
  
  const [planForm, setPlanForm] = useState({
    title: '', desc: '', weeks: 8, difficulty: 'MEDIUM', goal: 'MUSCLE', isTemplate: false, exercises: []
  });

  // Sắp xếp bài tập theo tuần → ngày
  const sortedExercises = [...planForm.exercises].sort((a, b) => (a.week || 1) - (b.week || 1) || (a.day || 0) - (b.day || 0));
  
  const [assignForm, setAssignForm] = useState({
    start: new Date().toISOString().slice(0, 10), note: '', selectedMembers: new Set()
  });

  const loadData = async () => {
    try {
      const data = await trainingPlanService.getAll();
      setPlans(data);
    } catch (err) {
      console.error(err);
      alert('Lỗi khi tải danh sách lộ trình!');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, []);

  // === RENDER PLANS ===
  const filteredPlans = plans.filter(p => {
    const matchFilter = filter === 'all' || 
                       (filter === 'template' && p.isTemplate) ||
                       (filter === 'assigned' && p.activeAssignments > 0);
    const matchSearch = !search || p.title.toLowerCase().includes(search.toLowerCase());
    return matchFilter && matchSearch;
  });

  // === PLAN MODAL (CREATE/EDIT) ===
  const openCreateModal = () => {
    setEditingPlanId(null);
    setPlanForm({ title: '', desc: '', weeks: 8, difficulty: 'MEDIUM', goal: 'MUSCLE', isTemplate: false, exercises: [{ id: Date.now(), name: '', sets: 3, reps: 10, rest: 60, day: 0, week: 1 }] });
    setIsPlanModalOpen(true);
  };

  const openEditModal = async (id) => {
    try {
      const data = await trainingPlanService.getDetail(id);
      setEditingPlanId(id);
      setPlanForm({
        title: data.title,
        desc: data.description || '',
        weeks: data.durationWeeks,
        difficulty: data.difficulty,
        goal: data.goal,
        isTemplate: data.isTemplate,
        exercises: data.exercises.map(e => ({
          id: e.id, name: e.exerciseName, sets: e.sets, reps: e.reps, rest: e.restSeconds, day: e.dayOfWeek, week: e.weekNumber || 1
        }))
      });
      setIsPlanModalOpen(true);
    } catch (err) { alert('Lỗi khi tải thông tin lộ trình!'); }
  };

  const addExerciseRow = () => {
    setPlanForm(prev => ({
      ...prev,
      exercises: [...prev.exercises, { id: Date.now(), name: '', sets: 3, reps: 10, rest: 60, day: 0, week: 1 }]
    }));
  };

  const updateExercise = (id, field, value) => {
    setPlanForm(prev => ({
      ...prev,
      exercises: prev.exercises.map(e => e.id === id ? { ...e, [field]: value } : e)
    }));
  };

  const removeExercise = (id) => {
    setPlanForm(prev => ({ ...prev, exercises: prev.exercises.filter(e => e.id !== id) }));
  };

  const handleSavePlan = async () => {
    if (!planForm.title.trim()) return alert('Vui lòng nhập tên lộ trình!');
    const exercises = planForm.exercises.filter(e => e.name.trim() !== '').map(e => ({
      exerciseName: e.name, sets: e.sets, reps: e.reps, restSeconds: e.rest, dayOfWeek: e.day, weekNumber: e.week || 1
    }));
    
    const data = {
      title: planForm.title, description: planForm.desc, durationWeeks: planForm.weeks,
      difficulty: planForm.difficulty, goal: planForm.goal, isTemplate: planForm.isTemplate,
      exercises
    };

    try {
      if (editingPlanId) await trainingPlanService.update(editingPlanId, data);
      else await trainingPlanService.create(data);
      setIsPlanModalOpen(false);
      loadData();
    } catch (err) { alert('Có lỗi xảy ra khi lưu lộ trình!'); }
  };

  const handleClonePlan = async (id) => {
    try {
      await trainingPlanService.clone(id);
      loadData();
    } catch (err) { alert('Lỗi nhân bản lộ trình!'); }
  };

  const handleDeletePlan = async () => {
    try {
      await trainingPlanService.delete(deletingPlanId);
      setIsConfirmModalOpen(false);
      loadData();
    } catch (err) { alert('Lỗi xoá lộ trình!'); }
  };

  // === ASSIGN MODAL ===
  const openAssignModal = async (id) => {
    try {
      const res = await api.get('/pt/members');
      setMembers(res.data);
      setAssigningPlanId(id);
      setAssignForm({ start: new Date().toISOString().slice(0, 10), note: '', selectedMembers: new Set() });
      setIsAssignModalOpen(true);
    } catch (err) { alert('Lỗi tải danh sách member!'); }
  };

  const toggleMember = (id) => {
    setAssignForm(prev => {
      const set = new Set(prev.selectedMembers);
      if (set.has(id)) set.delete(id); else set.add(id);
      return { ...prev, selectedMembers: set };
    });
  };

  const handleAssign = async () => {
    if (assignForm.selectedMembers.size === 0) return alert('Vui lòng chọn ít nhất 1 member!');
    try {
      await assignmentService.assign({
        planId: assigningPlanId,
        memberIds: Array.from(assignForm.selectedMembers),
        startDate: assignForm.start,
        note: assignForm.note
      });
      setIsAssignModalOpen(false);
      loadData(); // Cập nhật lại stats
      alert('Đã gán lộ trình thành công!');
    } catch (err) { alert('Lỗi gán lộ trình!'); }
  };

  return (
    <PtLayout>
      <div className="pt-workout-manager">
        <div className="topbar-actions">
          <div className="topbar-title">Lộ trình tập luyện</div>
          <button className="btn btn-primary" onClick={openCreateModal}>
            <Plus size={18} /> Tạo lộ trình mới
          </button>
        </div>

        <div className="tab-bar">
          <button className={`tab ${filter === 'all' ? 'active' : ''}`} onClick={() => setFilter('all')}>Tất cả</button>
          <button className={`tab ${filter === 'template' ? 'active' : ''}`} onClick={() => setFilter('template')}>Lộ trình mẫu</button>
          <button className={`tab ${filter === 'assigned' ? 'active' : ''}`} onClick={() => setFilter('assigned')}>Đang gán</button>
        </div>

        <div className="search-bar">
          <div className="search-wrap">
            <Search size={16} />
            <input 
              className="search-input" 
              placeholder="Tìm theo tên lộ trình..." 
              value={search}
              onChange={e => setSearch(e.target.value)}
            />
          </div>
        </div>

        {loading ? (
          <div style={{ textAlign: 'center', padding: '40px', color: 'var(--text3)' }}>Đang tải...</div>
        ) : filteredPlans.length === 0 ? (
          <div className="empty">
            <div className="empty-icon">📋</div>
            <div className="empty-title">Chưa có lộ trình nào</div>
            <div className="empty-desc">Nhấn "Tạo lộ trình mới" để bắt đầu.</div>
          </div>
        ) : (
          <div className="grid">
            {filteredPlans.map(plan => {
              const diffClass = plan.difficulty === 'EASY' ? 'badge-green' : plan.difficulty === 'MEDIUM' ? 'badge-amber' : 'badge-red';
              return (
                <div className="plan-card" key={plan.id}>
                  <div className="plan-card-header">
                    <div className="plan-card-title">{plan.title}</div>
                    <div className="plan-card-meta">
                      <span className="badge badge-purple">{GOALS[plan.goal] || plan.goal}</span>
                      <span className={`badge ${diffClass}`}>{DIFFS[plan.difficulty] || plan.difficulty}</span>
                      {plan.isTemplate && <span className="badge badge-gray">⭐ Mẫu</span>}
                    </div>
                  </div>
                  <div className="plan-card-body">
                    <div className="plan-card-desc">{plan.description || 'Chưa có mô tả.'}</div>
                    <div className="plan-stat">
                      <div className="stat-item"><div className="stat-val">{plan.durationWeeks}</div><div className="stat-lbl">tuần</div></div>
                      <div className="stat-item"><div className="stat-val">{plan.totalExercises}</div><div className="stat-lbl">bài tập</div></div>
                      <div className="stat-item"><div className="stat-val">{plan.activeAssignments}</div><div className="stat-lbl">đang gán</div></div>
                    </div>
                  </div>
                  <div className="plan-card-footer">
                    <span className="assign-count">{plan.activeAssignments > 0 ? `${plan.activeAssignments} member đang theo` : 'Chưa gán ai'}</span>
                    <button className="btn btn-sm btn-outline" onClick={() => navigate(`/pt/plans/${plan.id}`)} title="Xem chi tiết">
                      <Eye size={13} />
                    </button>
                    <button className="btn btn-sm btn-outline" onClick={() => openEditModal(plan.id)} title="Sửa">
                      <Edit2 size={13} />
                    </button>
                    <button className="btn btn-sm btn-outline" onClick={() => handleClonePlan(plan.id)} title="Nhân bản">
                      <Copy size={13} />
                    </button>
                    <button className="btn btn-sm btn-primary" onClick={() => openAssignModal(plan.id)}>
                      <Users size={13} /> Gán
                    </button>
                    <button className="btn btn-sm btn-danger" onClick={() => { setDeletingPlanId(plan.id); setIsConfirmModalOpen(true); }} title="Xoá">
                      <Trash2 size={13} />
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        )}

        {/* --- PLAN MODAL --- */}
        <div className={`overlay ${isPlanModalOpen ? 'open' : ''}`} onClick={(e) => { if (e.target === e.currentTarget) setIsPlanModalOpen(false); }}>
          <div className="modal">
            <div className="modal-header">
              <div className="modal-title">{editingPlanId ? 'Sửa lộ trình' : 'Tạo lộ trình mới'}</div>
              <button className="modal-close" onClick={() => setIsPlanModalOpen(false)}><X size={18} /></button>
            </div>
            <div className="modal-body">
              <div className="form-grid" style={{ marginBottom: '20px' }}>
                <div className="form-group full">
                  <label className="form-label">Tên lộ trình *</label>
                  <input className="form-input" value={planForm.title} onChange={e => setPlanForm({...planForm, title: e.target.value})} placeholder="VD: Tăng cơ toàn thân 8 tuần" />
                </div>
                <div className="form-group full">
                  <label className="form-label">Mô tả</label>
                  <textarea className="form-input" value={planForm.desc} onChange={e => setPlanForm({...planForm, desc: e.target.value})} placeholder="Mô tả ngắn..."></textarea>
                </div>
                <div className="form-group">
                  <label className="form-label">Số tuần</label>
                  <input className="form-input" type="number" min="1" max="52" value={planForm.weeks} onChange={e => setPlanForm({...planForm, weeks: +e.target.value})} />
                </div>
                <div className="form-group">
                  <label className="form-label">Độ khó</label>
                  <select className="form-input" value={planForm.difficulty} onChange={e => setPlanForm({...planForm, difficulty: e.target.value})}>
                    <option value="EASY">Dễ — Người mới</option>
                    <option value="MEDIUM">Trung bình</option>
                    <option value="HARD">Nâng cao</option>
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Mục tiêu</label>
                  <select className="form-input" value={planForm.goal} onChange={e => setPlanForm({...planForm, goal: e.target.value})}>
                    {Object.entries(GOALS).map(([k,v]) => <option key={k} value={k}>{v}</option>)}
                  </select>
                </div>
                <div className="form-group" style={{ justifyContent: 'flex-end', paddingTop: '20px' }}>
                  <label style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer', fontSize: '14px', fontWeight: '500', color: 'var(--text2)' }}>
                    <input type="checkbox" checked={planForm.isTemplate} onChange={e => setPlanForm({...planForm, isTemplate: e.target.checked})} style={{ width: '16px', height: '16px' }} />
                    Lưu làm lộ trình mẫu
                  </label>
                </div>
              </div>

              <hr className="divider" />
              <div className="section-title">Danh sách bài tập</div>
              
              <div className="exercise-list">
                {sortedExercises.map(ex => (
                  <div className="exercise-row" key={ex.id}>
                    <div><div className="ex-label">Tên bài tập</div><input className="ex-input" placeholder="VD: Bench Press" value={ex.name} onChange={e => updateExercise(ex.id, 'name', e.target.value)} /></div>
                    <div><div className="ex-label">Sets</div><input className="ex-input" type="number" min="1" value={ex.sets} onChange={e => updateExercise(ex.id, 'sets', +e.target.value)} /></div>
                    <div><div className="ex-label">Reps</div><input className="ex-input" type="number" min="1" value={ex.reps} onChange={e => updateExercise(ex.id, 'reps', +e.target.value)} /></div>
                    <div><div className="ex-label">Nghỉ(s)</div><input className="ex-input" type="number" min="0" value={ex.rest} onChange={e => updateExercise(ex.id, 'rest', +e.target.value)} /></div>
                    <div><div className="ex-label">Ngày</div>
                      <select className="ex-input" value={ex.day} onChange={e => updateExercise(ex.id, 'day', +e.target.value)}>
                        {DAYS.map((d,i) => <option key={i} value={i}>{d}</option>)}
                      </select>
                    </div>
                    <div><div className="ex-label">Tuần</div>
                      <select className="ex-input" value={ex.week || 1} onChange={e => updateExercise(ex.id, 'week', +e.target.value)}>
                        {Array.from({ length: planForm.weeks || 1 }, (_, i) => i + 1).map(w => <option key={w} value={w}>Tuần {w}</option>)}
                      </select>
                    </div>
                    <button onClick={() => removeExercise(ex.id)} style={{ background: 'var(--red-bg)', border: 'none', borderRadius: '6px', padding: '6px 10px', cursor: 'pointer', color: 'var(--red-text)', fontSize: '16px', alignSelf: 'flex-end', marginBottom: '1px' }}>×</button>
                  </div>
                ))}
              </div>
              <button className="add-exercise-btn" onClick={addExerciseRow}><Plus size={15} /> Thêm bài tập</button>
            </div>
            <div className="modal-footer">
              <button className="btn btn-outline" onClick={() => setIsPlanModalOpen(false)}>Huỷ</button>
              <button className="btn btn-primary" onClick={handleSavePlan}><CheckCircle size={14} /> Lưu lộ trình</button>
            </div>
          </div>
        </div>

        {/* --- ASSIGN MODAL --- */}
        <div className={`overlay ${isAssignModalOpen ? 'open' : ''}`} onClick={(e) => { if (e.target === e.currentTarget) setIsAssignModalOpen(false); }}>
          <div className="modal" style={{ width: '520px' }}>
            <div className="modal-header">
              <div>
                <div className="modal-title">Gán lộ trình</div>
                <div style={{ fontSize: '12px', color: 'var(--text3)' }}>Chọn một hoặc nhiều member</div>
              </div>
              <button className="modal-close" onClick={() => setIsAssignModalOpen(false)}><X size={18} /></button>
            </div>
            <div className="modal-body">
              <div className="form-grid" style={{ marginBottom: '16px' }}>
                <div className="form-group">
                  <label className="form-label">Ngày bắt đầu</label>
                  <input className="form-input" type="date" value={assignForm.start} onChange={e => setAssignForm({...assignForm, start: e.target.value})} />
                </div>
                <div className="form-group">
                  <label className="form-label">Ghi chú cho member</label>
                  <input className="form-input" placeholder="Lưu ý riêng..." value={assignForm.note} onChange={e => setAssignForm({...assignForm, note: e.target.value})} />
                </div>
              </div>
              <div className="section-title" style={{ marginBottom: '10px' }}>Chọn member</div>
              <div className="member-list">
                {members.map(m => {
                  const sel = assignForm.selectedMembers.has(m.id);
                  return (
                    <div key={m.id} className={`member-item ${sel ? 'selected' : ''}`} onClick={() => toggleMember(m.id)}>
                      <div className="member-avatar">
                        {m.avatar ? <img src={`http://localhost:8080/api/public/uploads/${m.avatar}`} alt="avt"/> : m.fullName.charAt(0)}
                      </div>
                      <div className="member-info">
                        <div className="member-name">{m.fullName}</div>
                        <div className="member-sub">{m.phone || m.email}</div>
                      </div>
                      <div className="member-check">{sel && <CheckCircle size={14} color="#fff" />}</div>
                    </div>
                  );
                })}
              </div>
              {assignForm.selectedMembers.size > 0 && (
                <div style={{ marginTop: '14px', padding: '10px 14px', background: 'var(--accent-bg)', borderRadius: '8px', fontSize: '13px', color: '#fb923c', border: '1px solid rgba(249,115,22,.3)' }}>
                  Đã chọn <strong>{assignForm.selectedMembers.size}</strong> member
                </div>
              )}
            </div>
            <div className="modal-footer">
              <button className="btn btn-outline" onClick={() => setIsAssignModalOpen(false)}>Huỷ</button>
              <button className="btn btn-primary" onClick={handleAssign}><Target size={14} /> Gán lộ trình</button>
            </div>
          </div>
        </div>

        {/* --- CONFIRM DELETE MODAL --- */}
        <div className={`overlay ${isConfirmModalOpen ? 'open' : ''}`}>
          <div className="modal confirm-modal">
            <div className="modal-body" style={{ textAlign: 'center', padding: '32px 24px' }}>
              <div className="confirm-icon"><Trash2 size={24} /></div>
              <div style={{ fontSize: '16px', fontWeight: '700', marginBottom: '8px' }}>Xoá lộ trình này?</div>
              <div style={{ fontSize: '13px', color: 'var(--text3)' }}>Hành động này không thể hoàn tác. Các phân công liên quan sẽ bị huỷ.</div>
            </div>
            <div className="modal-footer">
              <button className="btn btn-outline" onClick={() => setIsConfirmModalOpen(false)}>Huỷ</button>
              <button className="btn btn-danger" onClick={handleDeletePlan}>Xoá lộ trình</button>
            </div>
          </div>
        </div>

      </div>
    </PtLayout>
  );
};

export default PtPlansPage;
