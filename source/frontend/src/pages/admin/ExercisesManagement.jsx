import React, { useState, useEffect } from 'react';
import AdminLayout from '../../components/layout/AdminLayout';
import exerciseService from '../../services/exerciseService';
import fileService from '../../services/fileService';
import { Edit, Trash2, Plus, PlayCircle, Upload } from 'lucide-react';
import './AdminManagement.css';

const ExercisesManagement = () => {
  const [exercises, setExercises] = useState([]);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  
  // Modal state
  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    muscleGroup: '',
    description: '',
    videoUrl: ''
  });

  const fetchExercises = async () => {
    setLoading(true);
    try {
      const data = await exerciseService.getAllExercises();
      setExercises(data);
    } catch (error) {
      console.error('Lỗi tải bài tập:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchExercises();
  }, []);

  const handleAddNew = () => {
    setEditingId(null);
    setFormData({
      name: '',
      muscleGroup: '',
      description: '',
      videoUrl: ''
    });
    setShowModal(true);
  };

  const handleEdit = (exercise) => {
    setEditingId(exercise.id);
    setFormData({
      name: exercise.name,
      muscleGroup: exercise.muscleGroup || '',
      description: exercise.description || '',
      videoUrl: exercise.videoUrl || ''
    });
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Bạn có chắc chắn muốn xóa bài tập này?')) return;
    try {
      await exerciseService.deleteExercise(id);
      alert('Xóa thành công!');
      fetchExercises();
    } catch (error) {
      alert(error.response?.data?.message || 'Lỗi khi xóa bài tập');
    }
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingId) {
        await exerciseService.updateExercise(editingId, formData);
        alert('Cập nhật thành công!');
      } else {
        await exerciseService.createExercise(formData);
        alert('Thêm mới thành công!');
      }
      setShowModal(false);
      fetchExercises();
    } catch (error) {
      alert(error.response?.data?.message || 'Có lỗi xảy ra');
    }
  };

  const handleFileUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    
    setUploading(true);
    try {
      const data = await fileService.uploadFile(file);
      setFormData({...formData, videoUrl: data.fileUrl});
    } catch (error) {
      alert('Lỗi khi tải file lên');
    } finally {
      setUploading(false);
    }
  };

  const muscleGroups = [
    'Ngực (Chest)', 'Lưng (Back)', 'Vai (Shoulders)', 
    'Tay trước (Biceps)', 'Tay sau (Triceps)', 
    'Bụng (Abs)', 'Chân (Legs)'
  ];

  return (
    <AdminLayout>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h1>Quản lý Từ Điển Bài Tập</h1>
        <button 
          onClick={handleAddNew}
          style={{
            display: 'flex', alignItems: 'center', gap: '8px',
            background: '#f97316', color: 'white', border: 'none',
            padding: '10px 20px', borderRadius: '8px', cursor: 'pointer', fontWeight: 'bold'
          }}
        >
          <Plus size={20} /> Thêm bài tập
        </button>
      </div>

      <div className="admin-table-container">
        <table className="admin-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Tên Bài Tập</th>
              <th>Nhóm Cơ</th>
              <th>Video/Ảnh minh họa</th>
              <th>Người tạo</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan="6" style={{ textAlign: 'center' }}>Đang tải...</td></tr>
            ) : exercises.length === 0 ? (
              <tr><td colSpan="6" style={{ textAlign: 'center' }}>Chưa có bài tập nào</td></tr>
            ) : (
              exercises.map(exercise => (
                <tr key={exercise.id}>
                  <td>{exercise.id}</td>
                  <td style={{ fontWeight: 'bold', color: '#f1f5f9' }}>{exercise.name}</td>
                  <td>
                    <span className="status-badge" style={{ background: 'rgba(59,130,246,0.2)', color: '#93c5fd' }}>
                      {exercise.muscleGroup || 'Chưa phân loại'}
                    </span>
                  </td>
                  <td>
                    {exercise.videoUrl ? (
                      <a href={exercise.videoUrl} target="_blank" rel="noopener noreferrer" style={{ display: 'flex', alignItems: 'center', gap: '5px', color: '#38bdf8', textDecoration: 'none' }}>
                        <PlayCircle size={18} /> Xem Media
                      </a>
                    ) : (
                      <span style={{ color: '#64748b' }}>Không có</span>
                    )}
                  </td>
                  <td>{exercise.createdBy}</td>
                  <td>
                    <div className="action-btns">
                      <button className="btn-icon" style={{ color: '#60a5fa', background: 'rgba(59,130,246,0.1)' }} onClick={() => handleEdit(exercise)} title="Sửa">
                        <Edit size={18} />
                      </button>
                      <button className="btn-icon cancel" onClick={() => handleDelete(exercise.id)} title="Xóa">
                        <Trash2 size={18} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Modal Thêm/Sửa */}
      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <h2 style={{ marginBottom: '20px', color: '#f1f5f9' }}>{editingId ? 'Sửa Bài Tập' : 'Thêm Bài Tập Mới'}</h2>
            
            <form onSubmit={handleSave} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
              <div>
                <label>Tên bài tập</label>
                <input 
                  type="text" 
                  value={formData.name} 
                  onChange={e => setFormData({...formData, name: e.target.value})}
                  required 
                  placeholder="VD: Barbell Bench Press"
                />
              </div>

              <div>
                <label>Nhóm cơ (Muscle Group)</label>
                <select 
                  value={formData.muscleGroup} 
                  onChange={e => setFormData({...formData, muscleGroup: e.target.value})}
                >
                  <option value="">-- Chọn nhóm cơ --</option>
                  {muscleGroups.map(group => (
                    <option key={group} value={group}>{group}</option>
                  ))}
                </select>
              </div>

              <div>
                <label>Link Video Hướng Dẫn / Hình minh họa</label>
                <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
                  <input 
                    type="text" 
                    value={formData.videoUrl} 
                    onChange={e => setFormData({...formData, videoUrl: e.target.value})}
                    placeholder="https://youtube.com/watch?v=... hoặc URL hình ảnh"
                    style={{ flex: 1 }}
                  />
                  <label style={{ 
                    cursor: 'pointer', background: '#3b82f6', color: 'white', 
                    padding: '8px 12px', borderRadius: '4px', display: 'flex', alignItems: 'center', gap: '5px' 
                  }}>
                    <Upload size={16} /> {uploading ? 'Đang tải...' : 'Tải lên'}
                    <input type="file" accept="video/*,image/*" style={{ display: 'none' }} onChange={handleFileUpload} disabled={uploading} />
                  </label>
                </div>
              </div>

              <div>
                <label>Mô tả / Cách tập</label>
                <textarea 
                  value={formData.description} 
                  onChange={e => setFormData({...formData, description: e.target.value})}
                  rows="4"
                  style={{ width: '100%', padding: '10px', background: 'rgba(255,255,255,0.05)', color: 'white', border: '1px solid rgba(255,255,255,0.2)', borderRadius: '8px' }}
                  placeholder="Hướng dẫn chi tiết..."
                ></textarea>
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '10px' }}>
                <button type="button" onClick={() => setShowModal(false)} className="btn-cancel">Hủy</button>
                <button type="submit" className="btn-submit">Lưu</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </AdminLayout>
  );
};

export default ExercisesManagement;
