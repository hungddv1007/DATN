import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import PtLayout from '../../components/layout/PtLayout';
import { SummaryCard, SummaryGrid } from '../../components/common/SummaryCards';
import api from '../../services/api';
import memberProfileService from '../../services/memberProfileService';
import { confirmDialog } from '../../utils/dialog';
import ptScheduleService from '../../services/ptScheduleService';
import { analyzeNutrition, generateDietFromPhysicalProfile } from '../../services/nutritionAIService';
import { ArrowLeft, Send, Trash2, User, Package, Calendar, StickyNote, Edit2, Utensils, Dumbbell, Coffee, Save, X, Plus, Activity, Sparkles, ChartNoAxesColumnIncreasing } from 'lucide-react';
import PhysicalProfileView from '../../components/member/PhysicalProfileView';
import '../admin/AdminManagement.css';
import './PtMemberDetail.css';

const TABS = [
  { key: 'physical', label: 'Hồ sơ thể chất', icon: Activity },
  { key: 'notes', label: 'Ghi chú', icon: StickyNote },
  { key: 'training', label: 'Thống kê tập luyện', icon: ChartNoAxesColumnIncreasing },
  { key: 'diet', label: 'Khẩu phần ăn', icon: Utensils },
];

const toISODate = date => {
  const local = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
  return local.toISOString().slice(0, 10);
};

const getTrainingRange = period => {
  const today = new Date();
  const from = new Date(today);
  const to = new Date(today);
  const day = today.getDay() || 7;
  if (period === 'CURRENT_WEEK' || period === 'PREVIOUS_WEEK') {
    from.setDate(today.getDate() - day + 1 - (period === 'PREVIOUS_WEEK' ? 7 : 0));
    to.setDate(from.getDate() + 6);
  } else {
    const monthOffset = period === 'PREVIOUS_MONTH' ? -1 : 0;
    from.setFullYear(today.getFullYear(), today.getMonth() + monthOffset, 1);
    to.setFullYear(today.getFullYear(), today.getMonth() + monthOffset + 1, 0);
  }
  return { from: toISODate(from), to: toISODate(to) };
};

const emptyDiet = {
  title: '', breakfast: '', snackMorning: '', lunch: '',
  snackAfternoon: '', dinner: '', calories: 0, proteinG: 0,
  carbsG: 0, fatG: 0, note: ''
};

const PtMemberDetail = () => {
  const { memberId } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const queryTab = new URLSearchParams(location.search).get('tab');
  const initialTab = location.state?.tab || queryTab || 'physical';

  const [member, setMember] = useState(null);
  const [notes, setNotes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [noteText, setNoteText] = useState('');
  const [editingNote, setEditingNote] = useState(null);
  const [activeTab, setActiveTab] = useState(initialTab);
  const [physicalProfile, setPhysicalProfile] = useState(null);
  const [physicalProfileLoading, setPhysicalProfileLoading] = useState(false);
  const [physicalProfileError, setPhysicalProfileError] = useState('');
  const [trainingPeriod, setTrainingPeriod] = useState('CURRENT_WEEK');
  const [trainingStats, setTrainingStats] = useState(null);
  const [trainingStatsLoading, setTrainingStatsLoading] = useState(false);
  const [trainingStatsError, setTrainingStatsError] = useState('');

  // Diet state
  const [trainingDiet, setTrainingDiet] = useState(null);
  const [restDiet, setRestDiet] = useState(null);
  const [editingDiet, setEditingDiet] = useState(null); // 'TRAINING_DAY' | 'REST_DAY' | null
  const [dietForm, setDietForm] = useState({ ...emptyDiet });
  const [dietLoading, setDietLoading] = useState(false);
  const [dietSaving, setDietSaving] = useState(false);

  // AI Analysis state
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [analysisResult, setAnalysisResult] = useState(null);
  const [analysisError, setAnalysisError] = useState(null);
  const [isGeneratingDiet, setIsGeneratingDiet] = useState(false);
  const [dietGenerationError, setDietGenerationError] = useState('');
  const [dietGenerationSuccess, setDietGenerationSuccess] = useState('');

  const fetchData = useCallback(async () => {
    try {
      const [membersRes, notesRes] = await Promise.all([
        api.get('/pt/members'),
        api.get(`/pt/notes/member/${memberId}`)
      ]);
      const found = membersRes.data.find(m => m.memberId === parseInt(memberId));
      setMember(found || null);
      setNotes(notesRes.data);
    } catch (err) {
      console.error('Lỗi tải dữ liệu:', err);
    } finally {
      setLoading(false);
    }
  }, [memberId]);

  // ===== DIETS =====
  const fetchDiets = useCallback(async () => {
    setDietLoading(true);
    try {
      const res = await api.get(`/pt/diets/member/${memberId}`);
      const diets = res.data;
      setTrainingDiet(diets.find(d => d.dayType === 'TRAINING_DAY') || null);
      setRestDiet(diets.find(d => d.dayType === 'REST_DAY') || null);
    } catch (err) {
      console.error('Lỗi tải khẩu phần:', err);
    } finally {
      setDietLoading(false);
    }
  }, [memberId]);

  const fetchPhysicalProfile = useCallback(async () => {
    setPhysicalProfileLoading(true);
    setPhysicalProfileError('');
    try {
      const profile = await memberProfileService
        .getAssignedMemberPhysicalProfile(memberId);
      setPhysicalProfile(profile);
    } catch (err) {
      setPhysicalProfileError(
        err.response?.data?.message || 'Không thể tải hồ sơ thể chất.',
      );
    } finally {
      setPhysicalProfileLoading(false);
    }
  }, [memberId]);

  const fetchTrainingStats = useCallback(async () => {
    setTrainingStatsLoading(true);
    setTrainingStatsError('');
    try {
      const range = getTrainingRange(trainingPeriod);
      setTrainingStats(await ptScheduleService.getTrainingStats(memberId, range.from, range.to));
    } catch (err) {
      setTrainingStatsError(err.response?.data?.message || 'Không thể tải thống kê tập luyện.');
    } finally {
      setTrainingStatsLoading(false);
    }
  }, [memberId, trainingPeriod]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  useEffect(() => {
    if (activeTab === 'diet') fetchDiets();
    if (activeTab === 'physical') fetchPhysicalProfile();
    if (activeTab === 'training') fetchTrainingStats();
  }, [activeTab, fetchDiets, fetchPhysicalProfile, fetchTrainingStats]);

  const startEditDiet = (type) => {
    setAnalysisResult(null);
    setAnalysisError(null);
    setDietGenerationError('');
    setDietGenerationSuccess('');
    const existing = type === 'TRAINING_DAY' ? trainingDiet : restDiet;
    if (existing) {
      setDietForm({
        title: existing.title || '',
        breakfast: existing.breakfast || '',
        snackMorning: existing.snackMorning || '',
        lunch: existing.lunch || '',
        snackAfternoon: existing.snackAfternoon || '',
        dinner: existing.dinner || '',
        calories: existing.calories || 0,
        proteinG: existing.proteinG || 0,
        carbsG: existing.carbsG || 0,
        fatG: existing.fatG || 0,
        note: existing.note || ''
      });
    } else {
      setDietForm({
        ...emptyDiet,
        title: type === 'TRAINING_DAY' ? 'Thực đơn ngày tập' : 'Thực đơn ngày nghỉ'
      });
    }
    setEditingDiet(type);
  };

  const cancelEditDiet = () => {
    setEditingDiet(null);
    setDietForm({ ...emptyDiet });
    setAnalysisResult(null);
    setAnalysisError(null);
    setDietGenerationError('');
    setDietGenerationSuccess('');
  };

  const handleAIGenerateDiet = async () => {
    if (!editingDiet) return;

    setIsGeneratingDiet(true);
    setDietGenerationError('');
    setDietGenerationSuccess('');
    setAnalysisResult(null);
    setAnalysisError(null);

    try {
      const generatedDiet = await generateDietFromPhysicalProfile(memberId, editingDiet);
      setDietForm(prev => ({
        ...prev,
        title: generatedDiet.title || prev.title,
        breakfast: generatedDiet.breakfast || '',
        snackMorning: generatedDiet.snackMorning || '',
        lunch: generatedDiet.lunch || '',
        snackAfternoon: generatedDiet.snackAfternoon || '',
        dinner: generatedDiet.dinner || '',
        note: generatedDiet.note || '',
        // Thực đơn mới chưa được phân tích dinh dưỡng.
        calories: 0,
        proteinG: 0,
        carbsG: 0,
        fatG: 0,
      }));
      setDietGenerationSuccess(
        'AI đã tạo thực đơn từ hồ sơ thể chất. Vui lòng kiểm tra và điều chỉnh trước khi lưu.',
      );
    } catch (err) {
      setDietGenerationError(
        err.response?.data?.message
          || 'Không thể tạo thực đơn bằng AI. Vui lòng thử lại.',
      );
    } finally {
      setIsGeneratingDiet(false);
    }
  };

  const handleAIAnalyze = async () => {
    const isRestDay = editingDiet === 'REST_DAY';
    const mealsData = {
      breakfastMeal: dietForm.breakfast || '',
      preworkoutMeal: isRestDay ? '' : (dietForm.snackMorning || ''),
      lunchMeal: dietForm.lunch || '',
      postworkoutMeal: isRestDay ? '' : (dietForm.snackAfternoon || ''),
      dinnerMeal: dietForm.dinner || '',
      dayType: editingDiet || 'TRAINING_DAY',
    };

    const hasAnyMeal = [
      mealsData.breakfastMeal,
      mealsData.preworkoutMeal,
      mealsData.lunchMeal,
      mealsData.postworkoutMeal,
      mealsData.dinnerMeal
    ].some(v => v && v.trim());

    if (!hasAnyMeal) {
      setAnalysisError('Vui lòng nhập nội dung ít nhất một bữa ăn trước khi phân tích AI');
      return;
    }

    setIsAnalyzing(true);
    setAnalysisError(null);
    setAnalysisResult(null);

    try {
      const result = await analyzeNutrition(mealsData);
      setAnalysisResult(result);
      setDietForm(prev => ({
        ...prev,
        calories: result.totalCalories || 0,
        proteinG: result.totalProtein || 0,
        carbsG: result.totalCarbs || 0,
        fatG: result.totalFat || 0,
        note: result.aiNote ? result.aiNote : prev.note,
      }));
    } catch (err) {
      setAnalysisError(err.response?.data?.message || err.message || 'Có lỗi xảy ra khi phân tích. Vui lòng thử lại.');
    } finally {
      setIsAnalyzing(false);
    }
  };

  const handleSaveDiet = async (type) => {
    setDietSaving(true);
    try {
      const existing = type === 'TRAINING_DAY' ? trainingDiet : restDiet;
      if (existing) {
        await api.put(`/pt/diets/${existing.id}`, dietForm);
      } else {
        await api.post('/pt/diets', {
          ...dietForm,
          memberId: parseInt(memberId),
          dayType: type
        });
      }
      await fetchDiets();
      setEditingDiet(null);
      setDietForm({ ...emptyDiet });
    } catch (err) {
      alert(err.response?.data?.message || 'Có lỗi xảy ra khi lưu thực đơn');
    } finally {
      setDietSaving(false);
    }
  };

  const handleDeleteDiet = async (dietId) => {
    if (!await confirmDialog('Bạn có chắc muốn xóa mẫu thực đơn này?', { confirmText: 'Xóa thực đơn', danger: true })) return;
    try {
      await api.delete(`/pt/diets/${dietId}`);
      await fetchDiets();
    } catch (err) {
      alert(err.response?.data?.message || 'Lỗi xóa thực đơn');
    }
  };

  // ===== GHI CHÚ =====
  const handleAddNote = async (e) => {
    e.preventDefault();
    if (!noteText.trim()) return;
    try {
      if (editingNote) {
        await api.put(`/pt/notes/${editingNote.id}`, { memberId: parseInt(memberId), content: noteText });
        setEditingNote(null);
      } else {
        await api.post('/pt/notes', { memberId: parseInt(memberId), content: noteText });
      }
      setNoteText('');
      const res = await api.get(`/pt/notes/member/${memberId}`);
      setNotes(res.data);
    } catch (err) {
      alert(err.response?.data?.message || 'Có lỗi xảy ra');
    }
  };

  const handleDeleteNote = async (noteId) => {
    if (!await confirmDialog('Bạn có chắc muốn xóa ghi chú này?', { confirmText: 'Xóa ghi chú', danger: true })) return;
    try {
      await api.delete(`/pt/notes/${noteId}`);
      const res = await api.get(`/pt/notes/member/${memberId}`);
      setNotes(res.data);
    } catch (err) {
      alert(err.response?.data?.message || 'Có lỗi xảy ra');
    }
  };

  const startEdit = (note) => {
    setEditingNote(note);
    setNoteText(note.content);
  };

  const cancelEdit = () => {
    setEditingNote(null);
    setNoteText('');
  };

  // ===== RENDER HELPERS =====

  const renderMealInput = (label, icon, field) => (
    <div style={{ marginBottom: '12px' }}>
      <label style={{ color: '#94a3b8', fontSize: '0.85rem', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '6px', marginBottom: '6px' }}>
        {icon} {label}
      </label>
      <textarea
        value={dietForm[field]}
        onChange={e => setDietForm(prev => ({ ...prev, [field]: e.target.value }))}
        placeholder={`Nhập ${label.toLowerCase()}...`}
        rows={2}
        style={{
          width: '100%', padding: '10px 12px', background: 'rgba(15,23,42,0.5)',
          border: '1px solid rgba(255,255,255,0.1)', borderRadius: '8px',
          color: '#f8fafc', fontSize: '0.9rem', outline: 'none', resize: 'vertical',
          fontFamily: 'inherit'
        }}
      />
    </div>
  );

  const renderMacroInputs = () => (
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr 1fr', gap: '10px', marginTop: '12px' }}>
      {[
        { label: 'Calo (kcal)', field: 'calories', color: '#f97316' },
        { label: 'Protein (g)', field: 'proteinG', color: '#3b82f6' },
        { label: 'Carbs (g)', field: 'carbsG', color: '#eab308' },
        { label: 'Fat (g)', field: 'fatG', color: '#ef4444' },
      ].map(({ label, field, color }) => (
        <div key={field}>
          <label style={{ color: '#94a3b8', fontSize: '0.78rem', fontWeight: 600, marginBottom: '4px', display: 'block' }}>
            <span style={{ color, fontWeight: 700 }}>●</span> {label}
          </label>
          <input
            type="number" min="0"
            value={dietForm[field]}
            onChange={e => setDietForm(prev => ({ ...prev, [field]: parseInt(e.target.value) || 0 }))}
            style={{
              width: '100%', padding: '8px 10px', background: 'rgba(15,23,42,0.5)',
              border: '1px solid rgba(255,255,255,0.1)', borderRadius: '8px',
              color: '#f8fafc', fontSize: '0.9rem', outline: 'none'
            }}
          />
        </div>
      ))}
    </div>
  );

  const renderDietPanel = (type, diet, bgGrad, icon, titleLabel) => {
    const isEditing = editingDiet === type;

    return (
      <div className="admin-table-container" style={{ marginTop: 0, overflow: 'visible' }}>
        {/* Header */}
        <div style={{
          padding: '16px 20px', borderBottom: '1px solid rgba(255,255,255,0.06)',
          background: bgGrad, borderRadius: '12px 12px 0 0',
          display: 'flex', alignItems: 'center', justifyContent: 'space-between'
        }}>
          <h3 style={{ color: '#f1f5f9', margin: 0, display: 'flex', alignItems: 'center', gap: '8px' }}>
            {icon} {titleLabel}
          </h3>
          <div className="action-btns">
            {!isEditing && (
              <button className="btn-submit" onClick={() => startEditDiet(type)}
                style={{ display: 'flex', alignItems: 'center', gap: '6px', padding: '6px 14px', fontSize: '0.85rem' }}>
                {diet ? <><Edit2 size={14} /> Sửa</> : <><Plus size={14} /> Tạo mẫu</>}
              </button>
            )}
            {diet && !isEditing && (
              <button className="btn-icon cancel" title="Xóa mẫu" onClick={() => handleDeleteDiet(diet.id)}>
                <Trash2 size={14} />
              </button>
            )}
          </div>
        </div>

        {/* Content */}
        <div style={{ padding: '16px 20px' }}>
          {isEditing ? (
            // EDIT MODE
            <>
              <div className="ai-diet-generator">
                <div className="ai-diet-generator-copy">
                  <span className="ai-diet-generator-icon"><Sparkles size={20} /></span>
                  <div>
                    <strong>Tạo thực đơn từ hồ sơ thể chất</strong>
                    <span>Gemini sẽ điền các bữa ăn để PT kiểm tra trước khi lưu.</span>
                  </div>
                </div>
                <button
                  type="button"
                  className="ai-diet-generate-button"
                  onClick={handleAIGenerateDiet}
                  disabled={isGeneratingDiet || isAnalyzing}
                >
                  <Sparkles size={16} />
                  {isGeneratingDiet ? 'Đang tạo...' : 'Tạo bằng AI'}
                </button>
              </div>

              {dietGenerationError && (
                <div className="ai-diet-message ai-diet-message--error">
                  ⚠️ {dietGenerationError}
                </div>
              )}
              {dietGenerationSuccess && (
                <div className="ai-diet-message ai-diet-message--success">
                  ✓ {dietGenerationSuccess}
                </div>
              )}

              <div style={{ marginBottom: '12px' }}>
                <label style={{ color: '#94a3b8', fontSize: '0.85rem', fontWeight: 600, marginBottom: '6px', display: 'block' }}>
                  Tiêu đề thực đơn
                </label>
                <input
                  type="text" value={dietForm.title}
                  onChange={e => setDietForm(prev => ({ ...prev, title: e.target.value }))}
                  placeholder="VD: Thực đơn tăng cơ ngày tập"
                  style={{
                    width: '100%', padding: '10px 12px', background: 'rgba(15,23,42,0.5)',
                    border: '1px solid rgba(255,255,255,0.1)', borderRadius: '8px',
                    color: '#f8fafc', fontSize: '0.9rem', outline: 'none'
                  }}
                />
              </div>

              {renderMealInput('Bữa sáng', '🌅', 'breakfast')}
              {type === 'TRAINING_DAY' && renderMealInput('Bữa phụ sáng / Pre-workout', '⚡', 'snackMorning')}
              {renderMealInput('Bữa trưa', '☀️', 'lunch')}
              {type === 'TRAINING_DAY' && renderMealInput('Bữa phụ chiều / Post-workout', '💪', 'snackAfternoon')}
              {renderMealInput('Bữa tối', '🌙', 'dinner')}

              {/* Button AI */}
              <button
                type="button"
                onClick={handleAIAnalyze}
                disabled={isAnalyzing || isGeneratingDiet}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                  padding: '10px 20px',
                  background: isAnalyzing ? '#64748b' : 'linear-gradient(135deg, #8b5cf6, #3b82f6)',
                  color: '#fff',
                  border: 'none',
                  borderRadius: '8px',
                  fontSize: '14px',
                  fontWeight: '600',
                  cursor: (isAnalyzing || isGeneratingDiet) ? 'not-allowed' : 'pointer',
                  margin: '14px 0 10px 0',
                  boxShadow: '0 4px 12px rgba(139, 92, 246, 0.3)',
                  transition: 'all 0.2s ease'
                }}
              >
                {isAnalyzing ? (
                  <>⏳ Đang phân tích...</>
                ) : (
                  <>✨ Phân tích dinh dưỡng bằng AI <span style={{fontSize:'11px', background:'rgba(255,255,255,0.2)', padding:'2px 8px', borderRadius:'99px'}}>Gemini</span></>
                )}
              </button>

              {/* Error AI */}
              {analysisError && (
                <div style={{
                  background: 'rgba(239,68,68,0.15)', border: '1px solid rgba(239,68,68,0.3)',
                  borderRadius: '8px', padding: '10px 14px',
                  fontSize: '13px', color: '#fca5a5', marginBottom: '12px'
                }}>
                  ⚠️ {analysisError}
                </div>
              )}

              {/* Panel kết quả chi tiết từ AI */}
              {analysisResult && (
                <div style={{
                  background: 'rgba(139,92,246,0.1)', border: '1px solid rgba(139,92,246,0.25)',
                  borderRadius: '10px', padding: '14px', marginBottom: '14px'
                }}>
                  <div style={{fontSize:'13px', fontWeight:'700', color:'#c4b5fd', marginBottom:'10px', display:'flex', alignItems:'center', gap:'6px'}}>
                    ✨ Kết quả phân tích từ Gemini AI
                  </div>

                  {/* Breakdown từng bữa */}
                  {analysisResult.meals && analysisResult.meals.map((meal, idx) => (
                    <div key={idx} style={{
                      display:'flex', justifyContent:'space-between',
                      padding:'6px 0', borderBottom:'1px solid rgba(255,255,255,0.06)',
                      fontSize:'13px'
                    }}>
                      <span style={{color:'#ddd6fe', fontWeight:'500'}}>{meal.mealName}</span>
                      <span style={{color:'#a78bfa', fontSize:'12px', fontWeight:'600'}}>
                        {meal.calories} kcal · P {meal.protein}g · C {meal.carbs}g · F {meal.fat}g
                      </span>
                    </div>
                  ))}

                  {/* Lời khuyên từ AI */}
                  {analysisResult.aiNote && (
                    <div style={{
                      marginTop:'10px', fontSize:'13px',
                      color:'#e9d5ff', lineHeight:'1.6', background:'rgba(0,0,0,0.2)',
                      padding:'8px 12px', borderRadius:'6px'
                    }}>
                      💡 {analysisResult.aiNote}
                    </div>
                  )}
                </div>
              )}

              {renderMacroInputs()}

              <div style={{ marginTop: '12px' }}>
                <label style={{ color: '#94a3b8', fontSize: '0.85rem', fontWeight: 600, marginBottom: '6px', display: 'block' }}>
                  📝 Ghi chú cho học viên
                </label>
                <textarea
                  value={dietForm.note}
                  onChange={e => setDietForm(prev => ({ ...prev, note: e.target.value }))}
                  placeholder="VD: Uống 3-4 lít nước/ngày..."
                  rows={2}
                  style={{
                    width: '100%', padding: '10px 12px', background: 'rgba(15,23,42,0.5)',
                    border: '1px solid rgba(255,255,255,0.1)', borderRadius: '8px',
                    color: '#f8fafc', fontSize: '0.9rem', outline: 'none', resize: 'vertical',
                    fontFamily: 'inherit'
                  }}
                />
              </div>

              <div style={{ display: 'flex', gap: '10px', marginTop: '16px', justifyContent: 'flex-end' }}>
                <button className="btn-cancel" onClick={cancelEditDiet} style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                  <X size={14} /> Hủy
                </button>
                <button className="btn-submit" onClick={() => handleSaveDiet(type)} disabled={dietSaving || isGeneratingDiet}
                  style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                  <Save size={14} /> {dietSaving ? 'Đang lưu...' : 'Lưu thực đơn'}
                </button>
              </div>
            </>
          ) : diet ? (
            // VIEW MODE
            <>
              {diet.title && (
                <div style={{ color: '#e2e8f0', fontWeight: 600, fontSize: '1.05rem', marginBottom: '14px' }}>
                  {diet.title}
                </div>
              )}

              {[
                { label: '🌅 Bữa sáng', val: diet.breakfast },
                { label: '⚡ Pre-workout', val: diet.snackMorning },
                { label: '☀️ Bữa trưa', val: diet.lunch },
                { label: '💪 Post-workout', val: diet.snackAfternoon },
                { label: '🌙 Bữa tối', val: diet.dinner },
              ].filter(m => m.val).map((m, i) => (
                <div key={i} style={{
                  padding: '10px 14px', marginBottom: '8px', borderRadius: '8px',
                  background: 'rgba(255,255,255,0.03)', border: '1px solid rgba(255,255,255,0.06)'
                }}>
                  <div style={{ color: '#94a3b8', fontSize: '0.8rem', fontWeight: 600, marginBottom: '4px' }}>{m.label}</div>
                  <div style={{ color: '#e2e8f0', fontSize: '0.95rem', lineHeight: 1.5 }}>{m.val}</div>
                </div>
              ))}

              {/* Macro badges */}
              <div style={{ display: 'flex', gap: '10px', marginTop: '14px', flexWrap: 'wrap' }}>
                {[
                  { label: 'Calo', val: diet.calories, unit: 'kcal', color: '#f97316', bg: 'rgba(249,115,22,0.1)' },
                  { label: 'Protein', val: diet.proteinG, unit: 'g', color: '#3b82f6', bg: 'rgba(59,130,246,0.1)' },
                  { label: 'Carbs', val: diet.carbsG, unit: 'g', color: '#eab308', bg: 'rgba(234,179,8,0.1)' },
                  { label: 'Fat', val: diet.fatG, unit: 'g', color: '#ef4444', bg: 'rgba(239,68,68,0.1)' },
                ].map(({ label, val, unit, color, bg }) => (
                  <div key={label} style={{
                    padding: '8px 14px', borderRadius: '10px', background: bg,
                    border: `1px solid ${color}30`, textAlign: 'center', minWidth: '80px'
                  }}>
                    <div style={{ color, fontWeight: 700, fontSize: '1.1rem' }}>{val || 0}</div>
                    <div style={{ color: '#94a3b8', fontSize: '0.75rem', fontWeight: 600 }}>{label} ({unit})</div>
                  </div>
                ))}
              </div>

              {diet.note && (
                <div style={{
                  marginTop: '14px', padding: '12px 14px', borderRadius: '8px',
                  background: 'rgba(234,179,8,0.05)', border: '1px solid rgba(234,179,8,0.15)',
                  color: '#fde68a', fontSize: '0.9rem', lineHeight: 1.5
                }}>
                  📝 {diet.note}
                </div>
              )}
            </>
          ) : (
            // EMPTY STATE
            <div style={{ textAlign: 'center', padding: '40px 20px', color: '#64748b' }}>
              <div style={{ fontSize: '2rem', marginBottom: '10px' }}>{type === 'TRAINING_DAY' ? '🏋️' : '☕'}</div>
              <p>Chưa có mẫu thực đơn {type === 'TRAINING_DAY' ? 'ngày tập' : 'ngày nghỉ'}.</p>
              <button className="btn-submit" onClick={() => startEditDiet(type)}
                style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', marginTop: '10px' }}>
                <Plus size={16} /> Tạo mẫu
              </button>
            </div>
          )}
        </div>
      </div>
    );
  };


  if (loading) {
    return <PtLayout><div style={{ textAlign: 'center', padding: '80px', color: '#94a3b8' }}>Đang tải...</div></PtLayout>;
  }

  if (!member) {
    return (
      <PtLayout>
        <div style={{ textAlign: 'center', padding: '80px', color: '#64748b' }}>
          <p>Không tìm thấy học viên.</p>
          <button className="btn-cancel" onClick={() => navigate('/pt/members')}>← Quay lại</button>
        </div>
      </PtLayout>
    );
  }

  return (
    <PtLayout>
      {/* Back button + Header */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '8px' }}>
        <button className="btn-icon" onClick={() => navigate('/pt/members')} style={{ color: '#94a3b8' }}>
          <ArrowLeft size={20} />
        </button>
        <h1 style={{ margin: 0 }}>Chi Tiết Học Viên</h1>
      </div>
      <p>Thông tin và quản lý cho học viên <strong style={{ color: '#f97316' }}>{member.memberName}</strong>.</p>

      {/* Member Info Cards */}
      <SummaryGrid columns={4} ariaLabel="Thông tin tổng quan học viên">
        <SummaryCard icon={User} label="Họ tên" value={member.memberName} tone="blue" compact />
        <SummaryCard icon={Package} label="Gói tập" value={member.packageName} tone="green" compact />
        <SummaryCard
          icon={Calendar}
          label="Hết hạn"
          value={new Date(member.endDate).toLocaleDateString('vi-VN')}
          tone="yellow"
          compact
        />
        <SummaryCard
          icon={Utensils}
          label="Khẩu phần ăn"
          value={`${(trainingDiet ? 1 : 0) + (restDiet ? 1 : 0)} / 2 mẫu`}
          tone="teal"
          compact
        />
      </SummaryGrid>

      {/* Contact info */}
      <div className="admin-table-container" style={{ marginTop: 0, marginBottom: '20px' }}>
        <div style={{ padding: '16px 20px', borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
          <h3 style={{ color: '#f1f5f9', margin: 0 }}>Thông tin liên hệ</h3>
        </div>
        <div style={{ padding: '16px 20px', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
          <div>
            <span style={{ color: '#94a3b8', fontSize: '0.85rem', textTransform: 'uppercase', fontWeight: '600', letterSpacing: '0.04em' }}>Email</span>
            <div style={{ color: '#e2e8f0', marginTop: '4px' }}>{member.memberEmail}</div>
          </div>
          <div>
            <span style={{ color: '#94a3b8', fontSize: '0.85rem', textTransform: 'uppercase', fontWeight: '600', letterSpacing: '0.04em' }}>Số điện thoại</span>
            <div style={{ color: '#e2e8f0', marginTop: '4px' }}>{member.memberPhone || '—'}</div>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div style={{
        display: 'flex', gap: '4px', marginBottom: '20px', background: 'rgba(15,23,42,0.4)',
        borderRadius: '12px', padding: '4px', width: 'fit-content'
      }}>
        {TABS.map(tab => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.key;
          return (
            <button key={tab.key} onClick={() => setActiveTab(tab.key)}
              style={{
                display: 'flex', alignItems: 'center', gap: '6px', padding: '10px 20px',
                borderRadius: '10px', border: 'none', cursor: 'pointer', fontWeight: 600,
                fontSize: '0.9rem', transition: 'all 0.2s',
                background: isActive ? 'linear-gradient(135deg, #f97316, #ea580c)' : 'transparent',
                color: isActive ? '#fff' : '#94a3b8',
              }}>
              <Icon size={16} /> {tab.label}
            </button>
          );
        })}
      </div>

      {/* Tab Content */}
      {activeTab === 'physical' && (
        <PhysicalProfileView
          profile={physicalProfile}
          loading={physicalProfileLoading}
          error={physicalProfileError}
        />
      )}

      {activeTab === 'notes' && (
        <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '20px' }}>
          <div className="admin-table-container" style={{ marginTop: 0, display: 'flex', flexDirection: 'column' }}>
            <div style={{ padding: '16px 20px', borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
              <h3 style={{ color: '#f1f5f9', margin: 0 }}>Ghi chú của PT</h3>
            </div>

            <form onSubmit={handleAddNote} style={{ padding: '16px 20px', display: 'flex', gap: '10px', borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
              <input type="text" value={noteText} onChange={e => setNoteText(e.target.value)}
                placeholder={editingNote ? 'Sửa ghi chú...' : 'Viết ghi chú mới...'}
                style={{
                  flex: 1, padding: '10px 14px', background: 'rgba(15,23,42,0.5)',
                  border: '1px solid rgba(255,255,255,0.1)', borderRadius: '8px',
                  color: '#f8fafc', fontSize: '0.95rem', outline: 'none'
                }} />
              <button type="submit" className="btn-submit" style={{ display: 'flex', alignItems: 'center', gap: '6px', padding: '8px 14px' }}>
                <Send size={16} /> {editingNote ? 'Lưu' : 'Gửi'}
              </button>
              {editingNote && (
                <button type="button" className="btn-cancel" onClick={cancelEdit} style={{ padding: '8px 14px' }}>Hủy</button>
              )}
            </form>

            <div style={{ flex: 1, overflowY: 'auto', maxHeight: '500px' }}>
              {notes.length === 0 ? (
                <div style={{ padding: '30px 20px', textAlign: 'center', color: '#64748b' }}>Chưa có ghi chú nào.</div>
              ) : (
                notes.map(note => (
                  <div key={note.id} style={{
                    padding: '14px 20px', borderBottom: '1px solid rgba(255,255,255,0.04)',
                    transition: 'background 0.2s'
                  }}
                  onMouseEnter={e => e.currentTarget.style.background = 'rgba(255,255,255,0.02)'}
                  onMouseLeave={e => e.currentTarget.style.background = 'transparent'}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                      <p style={{ color: '#e2e8f0', margin: '0 0 6px 0', lineHeight: '1.5', flex: 1 }}>{note.content}</p>
                      <div className="action-btns" style={{ marginLeft: '10px', flexShrink: 0 }}>
                        <button className="btn-icon" title="Sửa" onClick={() => startEdit(note)} style={{ color: '#3b82f6', background: 'rgba(59,130,246,0.1)' }}>
                          <Edit2 size={14} />
                        </button>
                        <button className="btn-icon cancel" title="Xóa" onClick={() => handleDeleteNote(note.id)}><Trash2 size={14} /></button>
                      </div>
                    </div>
                    <span style={{ color: '#64748b', fontSize: '0.8rem' }}>
                      {note.createdAt ? new Date(note.createdAt).toLocaleString('vi-VN') : ''}
                    </span>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>
      )}

      {activeTab === 'training' && (
        <section className="pt-training-stats">
          <div className="pt-training-toolbar">
            <div>
              <h3>Hiệu quả tập luyện</h3>
              <p>Theo dõi lịch sử để điều chỉnh giáo án cho buổi tiếp theo.</p>
            </div>
            <select value={trainingPeriod} onChange={event => setTrainingPeriod(event.target.value)}>
              <option value="CURRENT_WEEK">Tuần này</option>
              <option value="PREVIOUS_WEEK">Tuần trước</option>
              <option value="CURRENT_MONTH">Tháng này</option>
              <option value="PREVIOUS_MONTH">Tháng trước</option>
            </select>
          </div>

          {trainingStatsLoading && <div className="pt-training-empty">Đang tải thống kê...</div>}
          {!trainingStatsLoading && trainingStatsError && <div className="pt-training-error">{trainingStatsError}</div>}
          {!trainingStatsLoading && trainingStats && <>
            <div className="pt-training-summary">
              <div><span>Hoàn thành</span><strong>{trainingStats.completedSessions}</strong></div>
              <div><span>Đang lên lịch</span><strong>{trainingStats.scheduledSessions}</strong></div>
              <div><span>Vắng mặt</span><strong>{trainingStats.noShowSessions}</strong></div>
              <div><span>Thời gian đã tập</span><strong>{trainingStats.completedMinutes} phút</strong></div>
            </div>

            <div className="pt-training-breakdown">
              <div>
                <h4>Nhóm cơ đã tập</h4>
                {Object.keys(trainingStats.muscleGroupFrequency || {}).length === 0
                  ? <p>Chưa có dữ liệu hoàn thành trong kỳ.</p>
                  : Object.entries(trainingStats.muscleGroupFrequency).map(([name, count]) => (
                    <div className="pt-training-frequency" key={name}><span>{name}</span><strong>{count} buổi</strong></div>
                  ))}
              </div>
              <div>
                <h4>Bài tập đã thực hiện</h4>
                {Object.keys(trainingStats.exerciseFrequency || {}).length === 0
                  ? <p>Chưa có dữ liệu hoàn thành trong kỳ.</p>
                  : Object.entries(trainingStats.exerciseFrequency).map(([name, count]) => (
                    <div className="pt-training-frequency" key={name}><span>{name}</span><strong>{count} lần</strong></div>
                  ))}
              </div>
            </div>

            <div className="pt-training-history">
              <h4>Chi tiết các buổi tập</h4>
              {(trainingStats.sessions || []).length === 0 ? (
                <div className="pt-training-empty">Không có lịch tập trong khoảng thời gian này.</div>
              ) : (
                <div className="pt-training-table-wrap"><table>
                  <thead><tr><th>Ngày</th><th>Giờ</th><th>Nội dung</th><th>Trạng thái</th><th>Kết quả</th></tr></thead>
                  <tbody>{trainingStats.sessions.map(session => (
                    <tr key={session.id}>
                      <td>{new Date(`${session.scheduleDate}T00:00:00`).toLocaleDateString('vi-VN')}</td>
                      <td>{session.startTime}–{session.endTime}</td>
                      <td>{session.exerciseNote || '—'}</td>
                      <td><span className={`pt-training-status status-${(session.status || '').toLowerCase()}`}>
                        {{ SCHEDULED: 'Đã lên lịch', COMPLETED: 'Hoàn thành', CANCELLED: 'Đã hủy', NO_SHOW: 'Vắng mặt' }[session.status] || session.status}
                      </span></td>
                      <td>{(session.exercises || []).map(item => item.exerciseName).join(', ') || session.actualNote || '—'}</td>
                    </tr>
                  ))}</tbody>
                </table></div>
              )}
            </div>
          </>}
        </section>
      )}

      {activeTab === 'diet' && (
        dietLoading ? (
          <div style={{ textAlign: 'center', padding: '60px', color: '#94a3b8' }}>Đang tải thực đơn...</div>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
            {renderDietPanel(
              'TRAINING_DAY', trainingDiet,
              'linear-gradient(135deg, rgba(249,115,22,0.15), rgba(234,88,12,0.08))',
              <Dumbbell size={18} style={{ color: '#f97316' }} />,
              'Ngày Tập (Training Day)'
            )}
            {renderDietPanel(
              'REST_DAY', restDiet,
              'linear-gradient(135deg, rgba(59,130,246,0.15), rgba(37,99,235,0.08))',
              <Coffee size={18} style={{ color: '#3b82f6' }} />,
              'Ngày Nghỉ (Rest Day)'
            )}
          </div>
        )
      )}

    </PtLayout>
  );
};

export default PtMemberDetail;
