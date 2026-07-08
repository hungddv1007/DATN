import React, { useState, useEffect } from 'react';
import PtLayout from '../../components/layout/PtLayout';
import ptScheduleService from '../../services/ptScheduleService';
import ptService from '../../services/ptService';
import { Calendar, Trash2, Plus, Clock, User } from 'lucide-react';
import '../../pages/member/DashboardPage.css';

const PtSchedulesPage = () => {
  const [schedules, setSchedules] = useState([]);
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(true);

  // Form State
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({
    memberId: '',
    dayOfWeek: 'MONDAY',
    slot: 'MORNING'
  });

  const DAYS = [
    { value: 'MONDAY', label: 'Thứ 2' },
    { value: 'TUESDAY', label: 'Thứ 3' },
    { value: 'WEDNESDAY', label: 'Thứ 4' },
    { value: 'THURSDAY', label: 'Thứ 5' },
    { value: 'FRIDAY', label: 'Thứ 6' },
    { value: 'SATURDAY', label: 'Thứ 7' },
    { value: 'SUNDAY', label: 'Chủ nhật' }
  ];

  const SLOTS = [
    { value: 'MORNING', label: 'Sáng (06:00 - 10:00)' },
    { value: 'AFTERNOON', label: 'Chiều (14:00 - 17:00)' },
    { value: 'EVENING', label: 'Tối (18:00 - 21:00)' }
  ];

  const fetchSchedules = async () => {
    setLoading(true);
    try {
      const [schedData, membersData] = await Promise.all([
        ptScheduleService.getPtSchedules(),
        ptService.getMyMembers()
      ]);
      setSchedules(schedData);
      setMembers(membersData);
    } catch (error) {
      console.error('Lỗi tải lịch:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSchedules();
  }, []);

  const handleAddNew = () => {
    setFormData({ memberId: '', dayOfWeek: 'MONDAY', slot: 'MORNING' });
    setShowModal(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    if (!formData.memberId) {
      alert("Vui lòng chọn học viên!");
      return;
    }
    try {
      await ptScheduleService.createSchedule({
        memberId: parseInt(formData.memberId),
        dayOfWeek: formData.dayOfWeek,
        slot: formData.slot
      });
      alert('Thêm lịch thành công!');
      setShowModal(false);
      fetchSchedules();
    } catch (error) {
      const resData = error.response?.data;
      if (resData && typeof resData === 'object') {
        const firstError = resData.message || Object.values(resData)[0];
        alert(firstError || 'Có lỗi xảy ra');
      } else {
        alert('Lỗi kết nối máy chủ!');
      }
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Bạn có chắc muốn xóa lịch hẹn này?')) return;
    try {
      await ptScheduleService.deleteSchedule(id);
      alert('Xóa thành công!');
      fetchSchedules();
    } catch (error) {
      alert(error.response?.data?.message || 'Lỗi khi xóa lịch');
    }
  };

  const getDayLabel = (val) => DAYS.find(d => d.value === val)?.label || val;
  const getSlotLabel = (val) => SLOTS.find(s => s.value === val)?.label || val;

  return (
    <PtLayout>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h1>Quản Lý Lịch Kèm (Schedules)</h1>
        <button 
          onClick={handleAddNew}
          style={{
            display: 'flex', alignItems: 'center', gap: '8px',
            background: '#3b82f6', color: 'white', border: 'none',
            padding: '10px 20px', borderRadius: '8px', cursor: 'pointer', fontWeight: 'bold'
          }}
        >
          <Plus size={20} /> Thêm lịch hẹn mới
        </button>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '20px' }}>
        {loading ? (
          <p style={{ color: '#94a3b8' }}>Đang tải...</p>
        ) : schedules.length === 0 ? (
          <p style={{ color: '#94a3b8' }}>Chưa có lịch hẹn nào. Hãy thêm mới.</p>
        ) : (
          schedules.map(sched => (
            <div key={sched.id} style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '12px', padding: '20px', display: 'flex', flexDirection: 'column', gap: '15px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: '#f97316', fontWeight: 'bold', fontSize: '1.2rem' }}>
                  <Calendar size={24} /> {getDayLabel(sched.dayOfWeek)}
                </div>
                <button onClick={() => handleDelete(sched.id)} style={{ background: 'transparent', border: 'none', color: '#ef4444', cursor: 'pointer' }} title="Xóa lịch">
                  <Trash2 size={20} />
                </button>
              </div>

              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: '#cbd5e1' }}>
                <Clock size={18} color="#60a5fa" /> Khung giờ: <strong>{getSlotLabel(sched.slot)}</strong>
              </div>

              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: '#cbd5e1' }}>
                <User size={18} color="#4ade80" /> Học viên: <strong>{sched.memberFullName}</strong>
              </div>
            </div>
          ))
        )}
      </div>

      {showModal && (
        <div style={{
          position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
          background: 'rgba(15, 23, 42, 0.8)', backdropFilter: 'blur(8px)', zIndex: 1000,
          display: 'flex', alignItems: 'center', justifyContent: 'center'
        }} onClick={() => setShowModal(false)}>
          <div onClick={e => e.stopPropagation()} style={{
            background: '#1e293b', padding: '30px', borderRadius: '12px', width: '90%', maxWidth: '500px',
            border: '2px solid #3b82f6', boxShadow: '0 25px 50px rgba(0,0,0,0.5)'
          }}>
            <h2 style={{ color: '#ffffff', marginBottom: '20px', borderBottom: '1px solid rgba(255,255,255,0.2)', paddingBottom: '10px' }}>
              Thêm Lịch Kèm Mới
            </h2>
            <form onSubmit={handleSave} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
              
              <div>
                <label style={{ display: 'block', marginBottom: '5px', color: '#ffffff', fontWeight: 'bold' }}>Chọn Học Viên</label>
                <select 
                  required
                  value={formData.memberId} 
                  onChange={e => setFormData({...formData, memberId: e.target.value})}
                  style={{ width: '100%', padding: '12px', background: 'rgba(15,23,42,0.6)', border: '1px solid rgba(255,255,255,0.2)', color: 'white', borderRadius: '8px' }}
                >
                  <option value="">-- Chọn một học viên --</option>
                  {members.map(m => (
                    <option key={m.memberId} value={m.memberId}>{m.memberFullName} ({m.memberEmail})</option>
                  ))}
                </select>
              </div>

              <div>
                <label style={{ display: 'block', marginBottom: '5px', color: '#ffffff', fontWeight: 'bold' }}>Ngày trong tuần</label>
                <select 
                  required
                  value={formData.dayOfWeek} 
                  onChange={e => setFormData({...formData, dayOfWeek: e.target.value})}
                  style={{ width: '100%', padding: '12px', background: 'rgba(15,23,42,0.6)', border: '1px solid rgba(255,255,255,0.2)', color: 'white', borderRadius: '8px' }}
                >
                  {DAYS.map(d => (
                    <option key={d.value} value={d.value}>{d.label}</option>
                  ))}
                </select>
              </div>

              <div>
                <label style={{ display: 'block', marginBottom: '5px', color: '#ffffff', fontWeight: 'bold' }}>Khung giờ</label>
                <select 
                  required
                  value={formData.slot} 
                  onChange={e => setFormData({...formData, slot: e.target.value})}
                  style={{ width: '100%', padding: '12px', background: 'rgba(15,23,42,0.6)', border: '1px solid rgba(255,255,255,0.2)', color: 'white', borderRadius: '8px' }}
                >
                  {SLOTS.map(s => (
                    <option key={s.value} value={s.value}>{s.label}</option>
                  ))}
                </select>
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '15px', marginTop: '20px' }}>
                <button type="button" onClick={() => setShowModal(false)} style={{ padding: '12px 24px', background: 'transparent', border: '1px solid #475569', color: 'white', borderRadius: '8px', cursor: 'pointer', fontWeight: 'bold' }}>Hủy</button>
                <button type="submit" style={{ padding: '12px 24px', background: 'linear-gradient(to right, #3b82f6, #2563eb)', boxShadow: '0 4px 6px rgba(59, 130, 246, 0.3)', border: 'none', color: 'white', borderRadius: '8px', cursor: 'pointer', fontWeight: 'bold' }}>Lưu thay đổi</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </PtLayout>
  );
};

export default PtSchedulesPage;
