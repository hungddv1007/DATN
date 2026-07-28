import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Clock, User } from 'lucide-react';
import MainLayout from '../../components/layout/MainLayout';
import blogService from '../../services/blogService';
import { resolveFileUrl } from '../../utils/fileUrl';
import './BlogPage.css';

const BlogListPage = () => {
  const [blogs, setBlogs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchBlogs = async () => {
      try {
        const data = await blogService.getPublicBlogs();
        setBlogs(data);
      } catch (err) {
        console.error('Lỗi tải blog:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchBlogs();
  }, []);

  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    return new Date(dateStr).toLocaleDateString('vi-VN');
  };

  return (
    <MainLayout>
      <div className="blog-page">
        <div className="blog-hero">
          <h1>Blog & Kiến Thức</h1>
          <p>Chia sẻ kiến thức tập luyện, dinh dưỡng và sức khỏe từ đội ngũ chuyên gia GymPro.</p>
        </div>

        {loading ? (
          <div style={{ textAlign: 'center', padding: '50px', color: '#94a3b8' }}>Đang tải bài viết...</div>
        ) : blogs.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '50px', color: '#94a3b8' }}>Chưa có bài viết nào.</div>
        ) : (
          <div className="blog-grid">
            {blogs.map((blog) => (
              <Link to={`/blog/${blog.id}`} key={blog.id} className="blog-card">
                <div className="blog-thumb">
                  {blog.thumbnail ? (
                    <img 
                      src={resolveFileUrl(blog.thumbnail)}
                      alt={blog.title} 
                    />
                  ) : (
                    <div style={{ width: '100%', height: '100%', background: 'linear-gradient(135deg, #f97316, #ea580c)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '2rem' }}>📝</div>
                  )}
                </div>
                <div className="blog-info">
                  <h3 className="blog-title">{blog.title}</h3>
                  <p className="blog-excerpt">{blog.content?.substring(0, 100)}...</p>
                  <div className="blog-meta">
                    <span><User size={14} /> {blog.authorName}</span>
                    <span><Clock size={14} /> {formatDate(blog.createdAt)}</span>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        )}
      </div>
    </MainLayout>
  );
};

export default BlogListPage;
