import React from 'react';
import { Users, UserCircle, ClipboardList } from 'lucide-react';
import './AboutSection.css';

const AboutSection = () => {
  return (
    <section className="about-section">
      <div className="about-container">
        
        {/* Khối thống kê bên trái */}
        <div className="stats-cards">
          <div className="stat-card">
            <Users size={48} className="stat-icon" strokeWidth={1.5} />
            <h3 className="stat-number">1000+</h3>
            <p className="stat-text">Hội viên</p>
          </div>
          <div className="stat-card">
            <UserCircle size={48} className="stat-icon" strokeWidth={1.5} />
            <h3 className="stat-number">50+</h3>
            <p className="stat-text">Huấn luyện viên</p>
          </div>
          <div className="stat-card">
            <ClipboardList size={48} className="stat-icon" strokeWidth={1.5} />
            <h3 className="stat-number">100+</h3>
            <p className="stat-text">Lộ trình</p>
          </div>
        </div>
        
        {/* Khối logo công nghệ bên phải */}
        <div className="tech-stack-container">
            <div className="tech-grid">
               <div className="tech-item">
                  <img src="https://cdn.worldvectorlogo.com/logos/java-14.svg" alt="Java" className="tech-logo" />
               </div>
               <div className="tech-item">
                  <img src="https://cdn.worldvectorlogo.com/logos/spring-3.svg" alt="Spring Boot" className="tech-logo" />
               </div>
               <div className="tech-item">
                  <img src="https://upload.wikimedia.org/wikipedia/commons/a/a7/React-icon.svg" alt="React" className="tech-logo react-logo" />
               </div>
            </div>
           <div className="tech-text">Spring Boot React.js</div>
        </div>
        
      </div>
    </section>
  );
};

export default AboutSection;
