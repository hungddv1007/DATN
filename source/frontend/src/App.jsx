import React, { useState } from 'react';
import MainLayout from './components/layout/MainLayout';
import Loading from './components/common/Loading';
import Modal from './components/common/Modal';
import FormInput from './components/common/FormInput';
import DataTable from './components/common/DataTable';

import './index.css';

function App() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  // Dữ liệu mẫu cho bảng
  const tableColumns = [
    { header: 'Họ Tên', accessor: 'name', width: '30%' },
    { header: 'Email', accessor: 'email', width: '40%' },
    { header: 'Vai Trò', accessor: 'role', width: '30%' }
  ];

  const tableData = [
    { name: 'Nguyễn Văn A', email: 'a@example.com', role: 'Admin' },
    { name: 'Trần Thị B', email: 'b@example.com', role: 'PT' },
    { name: 'Lê Văn C', email: 'c@example.com', role: 'Member' },
  ];

  const handleShowLoading = () => {
    setIsLoading(true);
    // Tự động tắt sau 2 giây
    setTimeout(() => setIsLoading(false), 2000);
  };

  return (
    <MainLayout>
      <div className="demo-container">
        <h1 className="demo-title">UI Components Showcase</h1>
        <p className="demo-desc">Dưới đây là các component dùng chung (common) đã được tạo cùng với Layout chuẩn.</p>

        {/* --- FormInput --- */}
        <section className="demo-section">
          <h2>1. Form Input</h2>
          <div className="demo-card">
            <FormInput 
              label="Địa chỉ Email" 
              id="email" 
              type="email" 
              placeholder="Nhập email của bạn..." 
            />
            <FormInput 
              label="Mật khẩu" 
              id="password" 
              type="password" 
              error="Mật khẩu phải có ít nhất 8 ký tự" 
              placeholder="Nhập mật khẩu..." 
            />
          </div>
        </section>

        {/* --- Buttons & Modal --- */}
        <section className="demo-section">
          <h2>2. Modal & Trạng thái tải (Loading)</h2>
          <div className="demo-card demo-flex">
            <button className="btn-primary" onClick={() => setIsModalOpen(true)}>
              Mở Modal Thông báo
            </button>
            <button className="btn-secondary" onClick={handleShowLoading}>
              Bật Loading Toàn màn hình (2s)
            </button>
          </div>
        </section>

        {/* --- DataTable --- */}
        <section className="demo-section">
          <h2>3. Data Table</h2>
          <div className="demo-card">
            <DataTable columns={tableColumns} data={tableData} />
          </div>
        </section>

      </div>

      {/* Render Modal (Bị ẩn, chỉ hiện khi isModalOpen = true) */}
      <Modal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)}
        title="Thông báo hệ thống"
        footer={
          <>
            <button className="btn-secondary" onClick={() => setIsModalOpen(false)}>Hủy</button>
            <button className="btn-primary" onClick={() => setIsModalOpen(false)}>Đồng ý</button>
          </>
        }
      >
        <p>Đây là nội dung của modal. Rất phù hợp để hiển thị cảnh báo, xác nhận thao tác hoặc hiển thị form nhập liệu nhanh.</p>
        <br/>
        <p>Thử bấm <b>Hủy</b>, <b>Đồng ý</b> hoặc bấm <b>ra ngoài khoảng đen</b> để đóng modal.</p>
      </Modal>

      {/* Render Fullscreen Loading */}
      {isLoading && <Loading fullScreen={true} />}
    </MainLayout>
  );
}

export default App;
