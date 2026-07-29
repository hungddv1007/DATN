import React, { useState, useEffect } from 'react';
import { Star, Award, ShieldCheck } from 'lucide-react';
import MainLayout from '../../components/layout/MainLayout';
import ptService from '../../services/ptService';
import { resolveFileUrl } from '../../utils/fileUrl';
import './PtListPage.css';

const PtListPage = () => {
  const [pts, setPts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchPts = async () => {
      try {
        const data = await ptService.getAllPtProfiles();
        setPts(data);
      } catch (error) {
        console.error('Lỗi khi tải danh sách PT:', error);
      } finally {
        setLoading(false);
      }
    };
    fetchPts();
  }, []);

  return (
    <MainLayout>
      <div className="pt-list-page">
        <div className="pt-hero">
          <h1>Đội Ngũ Huấn Luyện Viên</h1>
          <p>Đồng hành cùng bạn trên con đường thay đổi vóc dáng</p>
        </div>

        <div className="pt-container">
          {loading ? (
            <div style={{ textAlign: 'center', padding: '50px', color: '#94a3b8' }}>Đang tải danh sách...</div>
          ) : pts.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '50px', color: '#94a3b8' }}>Hiện chưa có Huấn luyện viên nào.</div>
          ) : (
            <div className="pt-grid">
              {pts.map((pt) => (
                <div key={pt.id} className="pt-card">
                  <div className="pt-avatar">
                    {pt.avatar ? (
                      <img src={resolveFileUrl(pt.avatar)} alt={pt.fullName} />
                    ) : (
                      <div className="avatar-placeholder avatar-initial">
                        {pt.fullName?.charAt(0)?.toUpperCase() || '?'}
                      </div>
                    )}
                  </div>
                  <div className="pt-info">
                    <h3>{pt.fullName}</h3>
                    <div className="pt-specialty">{pt.specialization || 'Huấn luyện viên'}</div>
                    
                    <div className="pt-meta">
                      <div className="pt-stat">
                        <Star size={16} className="icon-star" />
                        <span>{pt.ratingScore ? `${pt.ratingScore}/5` : 'Chưa có'}</span>
                      </div>
                      <div className="pt-stat">
                        <Award size={16} className="icon-exp" />
                        <span>{pt.totalMembers || 0} học viên</span>
                      </div>
                    </div>
                    
                    <div className="pt-desc">
                      {pt.bio || 'Đang cập nhật thông tin giới thiệu.'}
                    </div>

                    <div className="pt-cert">
                      <ShieldCheck size={16} /> 
                      <span>{pt.certificates || 'Chứng nhận chuyên nghiệp'}</span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </MainLayout>
  );
};

export default PtListPage;
