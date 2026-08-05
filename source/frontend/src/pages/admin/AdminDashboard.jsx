import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { Users, Package, CreditCard, BarChart3 } from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip as RechartsTooltip, ResponsiveContainer, PieChart, Pie, Cell, Legend } from 'recharts';
import AdminLayout from '../../components/layout/AdminLayout';
import statisticsService from '../../services/statisticsService';

const AdminDashboard = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState({
    totalUsers: 0,
    newRegistrationsThisMonth: 0,
    monthlyRevenue: 0,
    activePTs: 0,
    revenueData: [],
    packageData: []
  });

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const data = await statisticsService.getOverview();
        // Ensure data arrays exist even if empty
        setStats({
          ...data,
          revenueData: data.revenueData || [],
          packageData: data.packageData || []
        });
      } catch (error) {
        console.error('Lỗi tải thống kê:', error);
      }
    };
    fetchStats();
  }, []);

  const COLORS = ['#3b82f6', '#f97316', '#10b981', '#8b5cf6', '#ef4444'];

  return (
    <AdminLayout>
      <h1 style={{ marginBottom: '10px' }}>Tổng Quan</h1>
      <p style={{ color: '#94a3b8', marginBottom: '30px' }}>Chào mừng trở lại, <strong style={{ color: '#f1f5f9' }}>{user?.fullName || 'Admin'}</strong>!</p>

      <div className="admin-stats" style={{ marginBottom: '40px' }}>
        <div className="stat-card" style={{ background: 'linear-gradient(135deg, rgba(59,130,246,0.1), rgba(30,58,138,0.2))', border: '1px solid rgba(59,130,246,0.2)' }}>
          <Users size={28} className="stat-icon" style={{ color: '#60a5fa' }} />
          <div className="stat-label">Tổng người dùng</div>
          <div className="stat-value">{stats.totalUsers}</div>
        </div>
        <div className="stat-card" style={{ background: 'linear-gradient(135deg, rgba(16,185,129,0.1), rgba(6,78,59,0.2))', border: '1px solid rgba(16,185,129,0.2)' }}>
          <Package size={28} className="stat-icon" style={{ color: '#34d399' }} />
          <div className="stat-label">Đăng ký tháng này</div>
          <div className="stat-value">{stats.newRegistrationsThisMonth}</div>
        </div>
        <div className="stat-card" style={{ background: 'linear-gradient(135deg, rgba(249,115,22,0.1), rgba(124,45,18,0.2))', border: '1px solid rgba(249,115,22,0.2)' }}>
          <CreditCard size={28} className="stat-icon" style={{ color: '#fb923c' }} />
          <div className="stat-label">Doanh thu tháng (VNĐ)</div>
          <div className="stat-value">{stats.monthlyRevenue.toLocaleString('vi-VN')} đ</div>
        </div>
        <div className="stat-card" style={{ background: 'linear-gradient(135deg, rgba(139,92,246,0.1), rgba(76,29,149,0.2))', border: '1px solid rgba(139,92,246,0.2)' }}>
          <BarChart3 size={28} className="stat-icon" style={{ color: '#a78bfa' }} />
          <div className="stat-label">PT đang hoạt động</div>
          <div className="stat-value">{stats.activePTs}</div>
        </div>
      </div>

      <div style={{ display: 'flex', gap: '20px', flexWrap: 'wrap' }}>
        {/* Biểu đồ Doanh thu */}
        <div style={{ flex: '2', minWidth: 'min(500px, 100%)', background: '#1e293b', padding: '20px', borderRadius: '12px', border: '1px solid rgba(255,255,255,0.05)' }}>
          <h3 style={{ color: '#f1f5f9', marginBottom: '20px' }}>Doanh Thu 6 Tháng Gần Nhất (VNĐ)</h3>
          <div style={{ width: '100%', height: '300px' }}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={stats.revenueData}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.1)" />
                <XAxis dataKey="name" stroke="#94a3b8" />
                <YAxis stroke="#94a3b8" tickFormatter={(value) => new Intl.NumberFormat('vi-VN', { notation: "compact", compactDisplay: "short" }).format(value)} />
                <RechartsTooltip 
                  cursor={{fill: 'rgba(255,255,255,0.05)'}}
                  contentStyle={{ backgroundColor: '#0f172a', border: 'none', borderRadius: '8px', color: '#fff' }}
                  formatter={(value) => new Intl.NumberFormat('vi-VN').format(value) + ' đ'}
                />
                <Bar dataKey="value" fill="#3b82f6" radius={[4, 4, 0, 0]} barSize={40} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Biểu đồ Phân bổ Gói Tập */}
        <div style={{ flex: '1', minWidth: 'min(300px, 100%)', background: '#1e293b', padding: '20px', borderRadius: '12px', border: '1px solid rgba(255,255,255,0.05)' }}>
          <h3 style={{ color: '#f1f5f9', marginBottom: '20px', textAlign: 'center' }}>Phân Bổ Gói Tập Hiện Tại</h3>
          <div style={{ width: '100%', height: '300px' }}>
            {stats.packageData.length > 0 ? (
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={stats.packageData}
                    cx="50%"
                    cy="50%"
                    innerRadius={70}
                    outerRadius={100}
                    paddingAngle={5}
                    dataKey="value"
                  >
                    {stats.packageData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <RechartsTooltip 
                    contentStyle={{ backgroundColor: '#0f172a', border: 'none', borderRadius: '8px', color: '#fff' }}
                  />
                  <Legend verticalAlign="bottom" height={36} wrapperStyle={{ color: '#cbd5e1' }}/>
                </PieChart>
              </ResponsiveContainer>
            ) : (
              <div style={{ display: 'flex', height: '100%', alignItems: 'center', justifyContent: 'center', color: '#64748b' }}>
                Chưa có dữ liệu gói tập
              </div>
            )}
          </div>
        </div>
      </div>
    </AdminLayout>
  );
};

export default AdminDashboard;
