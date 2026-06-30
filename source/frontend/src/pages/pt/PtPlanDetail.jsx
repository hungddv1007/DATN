import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import PtLayout from '../../components/layout/PtLayout';
import trainingPlanService from '../../services/trainingPlanService';
import assignmentService from '../../services/assignmentService';
import api from '../../services/api';
import { ArrowLeft, Target, Calendar, CheckCircle, X } from 'lucide-react';
import './PtWorkoutManager.css';

const GOALS = { MUSCLE:'Tăng cơ', FAT_LOSS:'Giảm mỡ', ENDURANCE:'Sức bền', STRENGTH:'Sức mạnh', FLEXIBILITY:'Linh hoạt' };
const DIFFS = { EASY:'Dễ', MEDIUM:'Trung bình', HARD:'Nâng cao' };
const DAYS_SHORT = ['T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'CN'];

const PtPlanDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [plan, setPlan] = useState(null);
  const [assignments, setAssignments] = useState([]);
  const [loading, setLoading] = useState(true);
  
  const [currentWeek, setCurrentWeek] = useState(1);
  const [weeks, setWeeks] = useState([1]);

  const loadData = async () => {
    try {
      const planData = await trainingPlanService.getDetail(id);
      setPlan(planData);
      
      const allAssigments = await assignmentService.getAll();
      const planAssignments = allAssigments.filter(a => a.planId === Number(id));
      setAssignments(planAssignments);

      // Extract weeks
      const ws = [...new Set(planData.exercises.map(e => e.weekNumber))].sort((a,b) => a - b);
      if (ws.length > 0) {
        setWeeks(ws);
        if (!ws.includes(currentWeek)) setCurrentWeek(ws[0]);
      } else {
        setWeeks([1]);
      }
    } catch (err) {
      console.error(err);
      alert('Lỗi tải chi tiết lộ trình!');
      navigate('/pt/plans');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, [id]);

  if (loading) return <PtLayout><div style={{ textAlign: 'center', padding: '40px', color: 'var(--text3)' }}>Đang tải...</div></PtLayout>;
  if (!plan) return <PtLayout><div className="empty">Không tìm thấy lộ trình.</div></PtLayout>;

  const diffClass = plan.difficulty === 'EASY' ? 'badge-green' : plan.difficulty === 'MEDIUM' ? 'badge-amber' : 'badge-red';
  const activeAssignments = assignments.filter(a => a.status === 'ACTIVE');

  return (
    <PtLayout>
      <div className="pt-workout-manager">
        <button className="btn btn-ghost" onClick={() => navigate('/pt/plans')} style={{ marginBottom: '16px' }}>
          <ArrowLeft size={15} /> Quay lại
        </button>

        <div className="detail-hero">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '12px' }}>
            <div>
              <div className="detail-hero-title">{plan.title}</div>
              <div className="detail-hero-meta">
                <span className="badge badge-blue">{GOALS[plan.goal] || plan.goal}</span>
                <span className={`badge ${diffClass}`}>{DIFFS[plan.difficulty] || plan.difficulty}</span>
                {plan.isTemplate && <span className="badge badge-gray">⭐ Lộ trình mẫu</span>}
                <span className="badge badge-gray">{plan.durationWeeks} tuần</span>
              </div>
              <div style={{ marginTop: '10px', fontSize: '13px', color: 'rgba(255,255,255,.6)' }}>{plan.description}</div>
            </div>
          </div>
        </div>

        <div className="detail-stats">
          <div className="detail-stat"><div className="detail-stat-val">{plan.durationWeeks}</div><div className="detail-stat-lbl">Số tuần</div></div>
          <div className="detail-stat"><div className="detail-stat-val">{plan.exercises.length}</div><div className="detail-stat-lbl">Tổng bài tập</div></div>
          <div className="detail-stat"><div className="detail-stat-val" style={{ color: 'var(--green)' }}>{activeAssignments.length}</div><div className="detail-stat-lbl">Đang gán</div></div>
          <div className="detail-stat"><div className="detail-stat-val" style={{ color: 'var(--text2)' }}>{assignments.length}</div><div className="detail-stat-lbl">Tổng phân công</div></div>
        </div>

        <div style={{ background: 'var(--card)', border: '1px solid var(--border)', borderRadius: 'var(--radius)', padding: '20px', marginBottom: '20px' }}>
          <div className="section-title">Lịch tập theo tuần</div>
          <div className="week-tabs">
            {weeks.map(w => (
              <button key={w} className={`week-tab ${w === currentWeek ? 'active' : ''}`} onClick={() => setCurrentWeek(w)}>
                Tuần {w}
              </button>
            ))}
          </div>
          <div className="day-schedule">
            {DAYS_SHORT.map((day, idx) => {
              const exs = plan.exercises.filter(e => e.dayOfWeek === idx && e.weekNumber === currentWeek);
              return (
                <div className="day-col" key={idx}>
                  <div className="day-header">{day}</div>
                  <div className="day-exercises">
                    {exs.length > 0 ? exs.map(e => (
                      <div className="exercise-chip" key={e.id}>
                        {e.exerciseName}
                        <div className="exercise-chip-detail">{e.sets}x{e.reps} · {e.restSeconds}s</div>
                      </div>
                    )) : <div className="day-rest">Nghỉ</div>}
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {assignments.length > 0 && (
          <div style={{ background: 'var(--card)', border: '1px solid var(--border)', borderRadius: 'var(--radius)', padding: '20px' }}>
            <div className="section-title">Member đang theo lộ trình này</div>
            <table className="assignment-table">
              <thead>
                <tr><th>Member</th><th>Bắt đầu</th><th>Trạng thái</th><th>Ghi chú</th></tr>
              </thead>
              <tbody>
                {assignments.map(a => {
                  const sc = a.status === 'ACTIVE' ? '#10b981' : a.status === 'PAUSED' ? '#f59e0b' : '#94a3b8';
                  const sl = a.status === 'ACTIVE' ? 'Đang tập' : a.status === 'PAUSED' ? 'Tạm dừng' : 'Hoàn thành';
                  return (
                    <tr key={a.id}>
                      <td>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                          <div className="member-avatar" style={{ width: '28px', height: '28px', fontSize: '10px' }}>
                            {a.memberAvatar ? <img src={`http://localhost:8080/api/public/uploads/${a.memberAvatar}`} alt=""/> : a.memberName.charAt(0)}
                          </div>
                          <span style={{ fontWeight: '600', fontSize: '13px' }}>{a.memberName}</span>
                        </div>
                      </td>
                      <td style={{ fontSize: '13px', color: 'var(--text2)' }}>{a.startDate}</td>
                      <td>
                        <span style={{ display: 'inline-flex', alignItems: 'center', fontSize: '13px' }}>
                          <span className="status-dot" style={{ background: sc }}></span>{sl}
                        </span>
                      </td>
                      <td style={{ fontSize: '13px', color: 'var(--text2)' }}>{a.note || '—'}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}

      </div>
    </PtLayout>
  );
};

export default PtPlanDetail;
