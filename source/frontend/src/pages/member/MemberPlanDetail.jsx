import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import MainLayout from '../../components/layout/MainLayout';
import memberPlanService from '../../services/memberPlanService';
import { ArrowLeft } from 'lucide-react';
import '../pt/PtWorkoutManager.css'; // Tái sử dụng CSS từ PT

const GOALS = { MUSCLE:'Tăng cơ', FAT_LOSS:'Giảm mỡ', ENDURANCE:'Sức bền', STRENGTH:'Sức mạnh', FLEXIBILITY:'Linh hoạt' };
const DIFFS = { EASY:'Dễ', MEDIUM:'Trung bình', HARD:'Nâng cao' };
const DAYS_SHORT = ['T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'CN'];

const MemberPlanDetail = () => {
  const navigate = useNavigate();
  const [plan, setPlan] = useState(null);
  const [loading, setLoading] = useState(true);
  
  const [currentWeek, setCurrentWeek] = useState(1);
  const [weeks, setWeeks] = useState([1]);

  useEffect(() => {
    const loadData = async () => {
      try {
        const planData = await memberPlanService.getActivePlan();
        if (planData) {
          setPlan(planData);
          // Extract weeks
          const ws = [...new Set(planData.exercises.map(e => e.weekNumber))].sort((a,b) => a - b);
          if (ws.length > 0) {
            setWeeks(ws);
            if (!ws.includes(currentWeek)) setCurrentWeek(ws[0]);
          } else {
            setWeeks([1]);
          }
        }
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, []);

  if (loading) return <MainLayout><div style={{ textAlign: 'center', padding: '40px', color: 'var(--text3)' }}>Đang tải...</div></MainLayout>;
  
  if (!plan) return (
    <MainLayout>
      <div className="pt-workout-manager" style={{ maxWidth: '900px', margin: '0 auto', paddingTop: '40px' }}>
         <button className="btn btn-ghost" onClick={() => navigate('/member/dashboard')} style={{ marginBottom: '16px' }}>
          <ArrowLeft size={15} /> Quay lại Dashboard
        </button>
        <div className="empty">
          <div className="empty-icon">📋</div>
          <div className="empty-title">Bạn chưa có lộ trình nào</div>
          <div className="empty-desc">Hãy liên hệ PT của bạn để được thiết kế lộ trình tập luyện.</div>
        </div>
      </div>
    </MainLayout>
  );

  const diffClass = plan.difficulty === 'EASY' ? 'badge-green' : plan.difficulty === 'MEDIUM' ? 'badge-amber' : 'badge-red';

  return (
    <MainLayout>
      <div className="pt-workout-manager" style={{ maxWidth: '1000px', margin: '0 auto', paddingTop: '20px' }}>
        <button className="btn btn-ghost" onClick={() => navigate('/member/dashboard')} style={{ marginBottom: '16px' }}>
          <ArrowLeft size={15} /> Quay lại Dashboard
        </button>

        <div className="detail-hero">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '12px' }}>
            <div>
              <div className="detail-hero-title">{plan.title}</div>
              <div className="detail-hero-meta">
                <span className="badge badge-blue">{GOALS[plan.goal] || plan.goal}</span>
                <span className={`badge ${diffClass}`}>{DIFFS[plan.difficulty] || plan.difficulty}</span>
                <span className="badge badge-gray">{plan.durationWeeks} tuần</span>
              </div>
              <div style={{ marginTop: '10px', fontSize: '13px', color: 'rgba(255,255,255,.6)' }}>{plan.description}</div>
            </div>
          </div>
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
      </div>
    </MainLayout>
  );
};

export default MemberPlanDetail;
