import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Clock, User, ArrowLeft } from 'lucide-react';
import MainLayout from '../../components/layout/MainLayout';
import blogService from '../../services/blogService';
import { resolveFileUrl } from '../../utils/fileUrl';
import './BlogPage.css';

const BlogDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [blog, setBlog] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchBlog = async () => {
      try {
        const data = await blogService.getPublicBlogById(id);
        setBlog(data);
      } catch (err) {
        console.error('Lỗi tải bài viết:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchBlog();
  }, [id]);

  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    return new Date(dateStr).toLocaleDateString('vi-VN', {
      weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
    });
  };

  if (loading) {
    return (
      <MainLayout>
        <div style={{ textAlign: 'center', padding: '100px 20px', color: '#94a3b8' }}>Đang tải bài viết...</div>
      </MainLayout>
    );
  }

  if (!blog) {
    return (
      <MainLayout>
        <div style={{ textAlign: 'center', padding: '100px 20px', color: '#94a3b8' }}>
          <h2 style={{ color: '#f1f5f9', marginBottom: '12px' }}>Không tìm thấy bài viết</h2>
          <p>Bài viết đã bị xóa hoặc không tồn tại.</p>
          <button onClick={() => navigate('/blog')} style={{ marginTop: '20px', padding: '10px 24px', background: '#f97316', color: '#fff', border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: 600 }}>
            Quay lại Blog
          </button>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout>
      <div className="blog-detail-page">
        <div className="blog-detail-container">
          <button className="blog-back-btn" onClick={() => navigate('/blog')}>
            <ArrowLeft size={16} /> Quay lại Blog
          </button>

          {blog.thumbnail && (
            <div className="blog-detail-thumb">
              <img 
                src={resolveFileUrl(blog.thumbnail)}
                alt={blog.title} 
              />
            </div>
          )}

          <h1 className="blog-detail-title">{blog.title}</h1>

          <div className="blog-detail-meta">
            <span><User size={15} /> {blog.authorName}</span>
            <span><Clock size={15} /> {formatDate(blog.createdAt)}</span>
          </div>

          <div className="blog-detail-content">
            {blog.content}
          </div>
        </div>
      </div>
    </MainLayout>
  );
};

export default BlogDetailPage;
