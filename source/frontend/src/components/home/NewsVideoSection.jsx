import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Clock, User, ArrowRight } from 'lucide-react';
import blogService from '../../services/blogService';
import { resolveFileUrl } from '../../utils/fileUrl';
import './NewsVideoSection.css';

const NewsVideoSection = () => {
  const [blogs, setBlogs] = useState([]);

  useEffect(() => {
    const fetchBlogs = async () => {
      try {
        const data = await blogService.getPublicBlogs();
        // Lấy 4 bài mới nhất
        setBlogs(data.slice(0, 4));
      } catch (err) {
        console.error('Lỗi tải blog:', err);
      }
    };
    fetchBlogs();
  }, []);

  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    return d.toLocaleDateString('vi-VN');
  };

  if (blogs.length === 0) return null;

  return (
    <section className="news-video-section">
      <div className="container">
        <h2 className="section-title">TIN TỨC & BÀI VIẾT MỚI NHẤT</h2>
        
        <div className="content-grid">
          {/* Bài viết nổi bật (bài đầu tiên) */}
          <div className="news-column">
            <h3 className="column-title">BÀI VIẾT NỔI BẬT</h3>
            <Link to={`/blog/${blogs[0].id}`} className="featured-news-item">
              {blogs[0].thumbnail && (
                <div className="featured-thumb-wrapper">
                  <img 
                    src={resolveFileUrl(blogs[0].thumbnail)}
                    alt={blogs[0].title} 
                    className="featured-thumb" 
                  />
                </div>
              )}
              <div className="featured-content">
                <h4 className="featured-title">{blogs[0].title}</h4>
                <p className="news-desc">{blogs[0].content?.substring(0, 120)}...</p>
                <div className="news-meta-row">
                  <span><User size={13} /> {blogs[0].authorName}</span>
                  <span><Clock size={13} /> {formatDate(blogs[0].createdAt)}</span>
                </div>
              </div>
            </Link>
          </div>

          {/* Danh sách bài viết còn lại */}
          <div className="video-column">
            <h3 className="column-title">BÀI VIẾT KHÁC</h3>
            <div className="news-list">
              {blogs.slice(1).map((blog) => (
                <Link to={`/blog/${blog.id}`} key={blog.id} className="news-item">
                  <div className="news-thumb-wrapper">
                    {blog.thumbnail ? (
                      <img 
                        src={resolveFileUrl(blog.thumbnail)}
                        alt={blog.title} 
                        className="news-thumb" 
                      />
                    ) : (
                      <div className="news-thumb news-thumb-placeholder">📝</div>
                    )}
                  </div>
                  <div className="news-content">
                    <h4 className="news-title">{blog.title}</h4>
                    <div className="news-meta-row">
                      <span><User size={12} /> {blog.authorName}</span>
                      <span><Clock size={12} /> {formatDate(blog.createdAt)}</span>
                    </div>
                  </div>
                </Link>
              ))}
            </div>
            <Link to="/blog" className="view-all-btn">
              Xem tất cả bài viết <ArrowRight size={16} />
            </Link>
          </div>
        </div>
      </div>
    </section>
  );
};

export default NewsVideoSection;
