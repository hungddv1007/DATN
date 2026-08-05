import React, { useState, useEffect } from 'react';
import AdminLayout from '../../components/layout/AdminLayout';
import blogService from '../../services/blogService';
import { resolveFileUrl } from '../../utils/fileUrl';
import fileService from '../../services/fileService';
import { Edit, Trash2, Plus, Eye, EyeOff, Image as ImageIcon, Upload } from 'lucide-react';
import AdminPagination from '../../components/admin/AdminPagination';
import useClientPagination from '../../hooks/useClientPagination';
import './AdminManagement.css';

const BlogsManagement = () => {
  const [blogs, setBlogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  const { page, setPage, totalPages, pageItems: visibleBlogs } = useClientPagination(blogs);
  
  // Modal state
  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState({
    title: '',
    content: '',
    thumbnail: '',
    status: 'PUBLISHED'
  });

  const fetchBlogs = async () => {
    setLoading(true);
    try {
      const data = await blogService.getAllBlogs();
      setBlogs(data);
    } catch (error) {
      console.error('Lỗi tải bài viết:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBlogs();
  }, []);

  const handleAddNew = () => {
    setEditingId(null);
    setFormData({
      title: '',
      content: '',
      thumbnail: '',
      status: 'PUBLISHED'
    });
    setShowModal(true);
  };

  const handleEdit = (blog) => {
    setEditingId(blog.id);
    setFormData({
      title: blog.title,
      content: blog.content,
      thumbnail: blog.thumbnail || '',
      status: blog.status
    });
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Bạn có chắc chắn muốn xóa vĩnh viễn bài viết này?')) return;
    try {
      await blogService.deleteBlog(id);
      alert('Xóa thành công!');
      fetchBlogs();
    } catch (error) {
      alert(error.response?.data?.message || 'Lỗi khi xóa bài viết');
    }
  };

  const handleToggleStatus = async (id, currentStatus) => {
    const action = currentStatus === 'PUBLISHED' ? 'Chuyển về Bản Nháp' : 'Xuất bản';
    if (!window.confirm(`Bạn có chắc chắn muốn ${action}?`)) return;
    try {
      await blogService.toggleBlogStatus(id);
      alert(`${action} thành công!`);
      fetchBlogs();
    } catch (error) {
      alert(error.response?.data?.message || `Lỗi khi ${action}`);
    }
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingId) {
        await blogService.updateBlog(editingId, formData);
        alert('Cập nhật thành công!');
      } else {
        await blogService.createBlog(formData);
        alert('Thêm mới thành công!');
      }
      setShowModal(false);
      fetchBlogs();
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
      setFormData({...formData, thumbnail: data.fileUrl});
    } catch (error) {
      alert('Lỗi khi tải ảnh lên');
    } finally {
      setUploading(false);
    }
  };

  return (
    <AdminLayout>
      <div className="admin-page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h1>Quản lý Bài Viết</h1>
        <button 
          onClick={handleAddNew}
          style={{
            display: 'flex', alignItems: 'center', gap: '8px',
            background: '#f97316', color: 'white', border: 'none',
            padding: '10px 20px', borderRadius: '8px', cursor: 'pointer', fontWeight: 'bold'
          }}
        >
          <Plus size={20} /> Viết bài mới
        </button>
      </div>

      <div className="admin-table-container">
        <table className="admin-table">
          <thead>
            <tr>
              <th>Hình ảnh</th>
              <th style={{ width: '40%' }}>Tiêu đề</th>
              <th>Tác giả</th>
              <th>Trạng thái</th>
              <th>Ngày tạo</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan="6" style={{ textAlign: 'center' }}>Đang tải...</td></tr>
            ) : blogs.length === 0 ? (
              <tr><td colSpan="6" style={{ textAlign: 'center' }}>Chưa có bài viết nào</td></tr>
            ) : (
              visibleBlogs.map(blog => (
                <tr key={blog.id} style={{ opacity: blog.status === 'PUBLISHED' ? 1 : 0.6 }}>
                  <td>
                    <div style={{ 
                      width: '60px', height: '40px', borderRadius: '4px', background: '#334155',
                      display: 'flex', alignItems: 'center', justifyContent: 'center', overflow: 'hidden'
                    }}>
                      {blog.thumbnail ? (
                        <img src={resolveFileUrl(blog.thumbnail)} alt="thumbnail" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                      ) : (
                        <ImageIcon size={20} color="#94a3b8" />
                      )}
                    </div>
                  </td>
                  <td style={{ fontWeight: 'bold', color: '#f1f5f9' }}>{blog.title}</td>
                  <td>{blog.authorName}</td>
                  <td>
                    {blog.status === 'PUBLISHED' 
                      ? <span className="status-badge status-confirmed">Đã xuất bản</span> 
                      : <span className="status-badge" style={{ background: 'rgba(148,163,184,0.2)', color: '#94a3b8' }}>Bản nháp</span>
                    }
                  </td>
                  <td>{new Date(blog.createdAt).toLocaleDateString('vi-VN')}</td>
                  <td>
                    <div className="action-btns">
                      <button className="btn-icon" style={{ color: '#60a5fa', background: 'rgba(59,130,246,0.1)' }} onClick={() => handleEdit(blog)} title="Sửa">
                        <Edit size={18} />
                      </button>
                      <button className="btn-icon" style={{ color: '#eab308', background: 'rgba(234,179,8,0.1)' }} onClick={() => handleToggleStatus(blog.id, blog.status)} title={blog.status === 'PUBLISHED' ? "Chuyển về Bản Nháp" : "Xuất bản ngay"}>
                        {blog.status === 'PUBLISHED' ? <EyeOff size={18} /> : <Eye size={18} />}
                      </button>
                      <button className="btn-icon cancel" onClick={() => handleDelete(blog.id)} title="Xóa vĩnh viễn">
                        <Trash2 size={18} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
        <AdminPagination page={page} totalPages={totalPages} onPageChange={setPage} />
      </div>

      {/* Modal Thêm/Sửa */}
      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)} style={{ backdropFilter: 'blur(8px)', zIndex: 10000 }}>
          <div className="modal-content" style={{ maxWidth: '800px', background: '#1e293b', border: '2px solid #3b82f6', boxShadow: '0 25px 50px rgba(0,0,0,0.5)' }} onClick={e => e.stopPropagation()}>
            <h2 style={{ marginBottom: '20px', color: '#ffffff', borderBottom: '1px solid rgba(255,255,255,0.2)', paddingBottom: '10px' }}>
              {editingId ? 'Sửa Bài Viết' : 'Viết Bài Mới'}
            </h2>
            
            <form onSubmit={handleSave} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
              <div>
                <label style={{ color: '#ffffff', fontWeight: 'bold', display: 'block', marginBottom: '8px' }}>Tiêu đề</label>
                <input 
                  type="text" 
                  value={formData.title} 
                  onChange={e => setFormData({...formData, title: e.target.value})}
                  required 
                  placeholder="Nhập tiêu đề bài viết..."
                  style={{ width: '100%', padding: '12px', background: 'rgba(15,23,42,0.6)', color: 'white', border: '1px solid rgba(255,255,255,0.2)', borderRadius: '8px' }}
                />
              </div>

              <div>
                <label style={{ color: '#ffffff', fontWeight: 'bold', display: 'block', marginBottom: '8px' }}>Hình Thu nhỏ</label>
                <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
                  <input 
                    type="text" 
                    value={formData.thumbnail} 
                    onChange={e => setFormData({...formData, thumbnail: e.target.value})}
                    placeholder="https://example.com/image.jpg"
                    style={{ flex: 1, padding: '12px', background: 'rgba(15,23,42,0.6)', color: 'white', border: '1px solid rgba(255,255,255,0.2)', borderRadius: '8px' }}
                  />
                  <label style={{ 
                    cursor: 'pointer', background: 'linear-gradient(to right, #3b82f6, #2563eb)', color: 'white', 
                    padding: '12px 20px', borderRadius: '8px', display: 'flex', alignItems: 'center', gap: '5px', fontWeight: 'bold', margin: 0 
                  }}>
                    <Upload size={16} /> {uploading ? 'Đang tải...' : 'Tải lên'}
                    <input type="file" accept="image/*" style={{ display: 'none' }} onChange={handleFileUpload} disabled={uploading} />
                  </label>
                </div>
                {formData.thumbnail && (
                  <div style={{ marginTop: '10px' }}>
                    <img src={resolveFileUrl(formData.thumbnail)} alt="preview" style={{ maxHeight: '100px', borderRadius: '4px', border: '1px solid #3b82f6' }} />
                  </div>
                )}
              </div>

              <div>
                <label style={{ color: '#ffffff', fontWeight: 'bold', display: 'block', marginBottom: '8px' }}>Nội dung</label>
                <textarea 
                  value={formData.content} 
                  onChange={e => setFormData({...formData, content: e.target.value})}
                  required 
                  rows="10"
                  style={{ width: '100%', padding: '12px', background: 'rgba(15,23,42,0.6)', color: 'white', border: '1px solid rgba(255,255,255,0.2)', borderRadius: '8px' }}
                  placeholder="Nhập nội dung bài viết..."
                ></textarea>
              </div>
              
              <div>
                <label style={{ color: '#ffffff', fontWeight: 'bold', display: 'block', marginBottom: '8px' }}>Trạng thái</label>
                <select 
                  value={formData.status} 
                  onChange={e => setFormData({...formData, status: e.target.value})}
                  style={{ width: '100%', padding: '12px', background: 'rgba(15,23,42,0.6)', color: 'white', border: '1px solid rgba(255,255,255,0.2)', borderRadius: '8px' }}
                >
                  <option value="PUBLISHED">Đã xuất bản (Hiện ngay)</option>
                  <option value="DRAFT">Bản nháp (Ẩn đi)</option>
                </select>
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '15px', marginTop: '20px' }}>
                <button type="button" onClick={() => setShowModal(false)} className="btn-cancel" style={{ padding: '12px 24px', fontWeight: 'bold', borderRadius: '8px' }}>Hủy</button>
                <button type="submit" className="btn-submit" style={{ padding: '12px 24px', fontWeight: 'bold', borderRadius: '8px', background: 'linear-gradient(to right, #f97316, #ea580c)', boxShadow: '0 4px 6px rgba(249, 115, 22, 0.3)' }}>Lưu thay đổi</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </AdminLayout>
  );
};

export default BlogsManagement;
