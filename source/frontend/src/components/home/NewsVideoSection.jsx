import React from 'react';
import { PlayCircle } from 'lucide-react';
import './NewsVideoSection.css';

const NewsVideoSection = () => {
  return (
    <section className="news-video-section">
      <div className="container">
        <h2 className="section-title">TIN TỨC & VIDEO CÔNG KHAI</h2>
        
        <div className="content-grid">
          {/* Cột Trái - Tin tức */}
          <div className="news-column">
            <h3 className="column-title">TIN TỨC MỚI</h3>
            <div className="news-list">
              {[1, 2, 3].map((item) => (
                <div key={item} className="news-item">
                  <div className="news-thumb-wrapper">
                    <img 
                      src={`https://images.unsplash.com/photo-1517836357463-d25dfeac3438?q=80&w=150&auto=format&fit=crop&sig=${item}`} 
                      alt="News thumbnail" 
                      className="news-thumb" 
                    />
                  </div>
                  <div className="news-content">
                    <h4 className="news-title">Tập tự do - Làm sao để không theo đám đông nhưng vẫn hiệu quả</h4>
                    <p className="news-desc">Cùng chuyên gia đánh giá và nhận định các bài tập nâng cao sức bền trong quá trình tập luyện của bạn...</p>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Cột Phải - Video */}
          <div className="video-column">
            <h3 className="column-title">VIDEO HƯỚNG DẪN</h3>
            <div className="main-video-wrapper">
               <img 
                 src="https://images.unsplash.com/photo-1540497077202-7c8a3999166f?q=80&w=800&auto=format&fit=crop" 
                 alt="Main video" 
                 className="main-video-thumb" 
               />
               <div className="play-overlay">
                 <PlayCircle size={64} className="play-icon" fill="#ea580c" color="white" />
               </div>
            </div>
            
            <div className="video-list">
               <div className="video-item">
                 <img src="https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?q=80&w=150&auto=format&fit=crop" alt="vid1" className="video-thumb-small" />
                 <h5 className="video-title-small">Xem ngay các bài tập cơ bụng số 1</h5>
               </div>
               <div className="video-item">
                 <img src="https://images.unsplash.com/photo-1599058917212-d750089bc07e?q=80&w=150&auto=format&fit=crop" alt="vid2" className="video-thumb-small" />
                 <h5 className="video-title-small">Tập tạ đơn - Các lỗi sai cơ bản thường gặp</h5>
               </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default NewsVideoSection;
