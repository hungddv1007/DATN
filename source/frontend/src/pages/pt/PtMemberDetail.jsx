import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import PtLayout from '../../components/layout/PtLayout';
import api from '../../services/api';
import { ArrowLeft, Send, Trash2, User, Package, Calendar, StickyNote, Plus, Edit2, CheckCircle } from 'lucide-react';
import '../admin/AdminManagement.css';

const PtMemberDetail = () => {
  const { memberId } = useParams();
  const navigate = useNavigate();
  const [member, setMember] = useState(null);
  const [notes, setNotes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [noteText, setNoteText] = useState('');
  const [editingNote, setEditingNote] = useState(null);


  useEffect(() => {
    fetchData();
  }, [memberId]);

  const fetchData = async () => {
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
    if (!window.confirm('Bạn có chắc muốn xóa ghi chú này?')) return;
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
      <p>Thông tin và ghi chú cho học viên <strong style={{ color: '#f97316' }}>{member.memberName}</strong>.</p>

      {/* Member Info Cards */}
      <div className="admin-stats" style={{ gridTemplateColumns: 'repeat(4, 1fr)' }}>
        <div className="stat-card">
          <User size={24} className="stat-icon" style={{ color: '#3b82f6' }} />
          <div className="stat-label">Họ tên</div>
          <div className="stat-value" style={{ fontSize: '1.2rem' }}>{member.memberName}</div>
        </div>
        <div className="stat-card">
          <Package size={24} className="stat-icon" style={{ color: '#10b981' }} />
          <div className="stat-label">Gói tập</div>
          <div className="stat-value" style={{ fontSize: '1.2rem' }}>{member.packageName}</div>
        </div>
        <div className="stat-card">
          <Calendar size={24} className="stat-icon" style={{ color: '#eab308' }} />
          <div className="stat-label">Hết hạn</div>
          <div className="stat-value" style={{ fontSize: '1.2rem' }}>{new Date(member.endDate).toLocaleDateString('vi-VN')}</div>
        </div>
        <div className="stat-card">
          <StickyNote size={24} className="stat-icon" style={{ color: '#a855f7' }} />
          <div className="stat-label">Ghi chú</div>
          <div className="stat-value" style={{ fontSize: '1.2rem' }}>{notes.length}</div>
        </div>
      </div>

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

      {/* Two columns: Routes + Notes */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>



        {/* Notes */}
        <div className="admin-table-container" style={{ marginTop: 0, display: 'flex', flexDirection: 'column' }}>
          <div style={{ padding: '16px 20px', borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
            <h3 style={{ color: '#f1f5f9', margin: 0 }}>Ghi chú của PT</h3>
          </div>

          {/* Note input */}
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

          {/* Note list */}
          <div style={{ flex: 1, overflowY: 'auto', maxHeight: '400px' }}>
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


    </PtLayout>
  );
};

export default PtMemberDetail;
