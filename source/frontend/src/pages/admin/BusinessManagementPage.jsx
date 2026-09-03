import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  BadgeCheck,
  BriefcaseBusiness,
  UserPlus,
  Users,
  WalletCards,
} from 'lucide-react';
import AdminLayout from '../../components/layout/AdminLayout';
import AdminPagination from '../../components/admin/AdminPagination';
import useClientPagination from '../../hooks/useClientPagination';
import api from '../../services/api';
import { confirmDialog } from '../../utils/dialog';
import './AdminManagement.css';

const money = value => `${new Intl.NumberFormat('vi-VN').format(value || 0)} ₫`;

const waitingTooltip = (payableAt, nowMs) => {
  if (!payableAt) return 'Chưa xác định được thời điểm đủ điều kiện chi trả.';
  if (nowMs === 0) return 'Đang đồng bộ thời gian chờ...';
  const payableDate = new Date(payableAt);
  const remainingMs = payableDate.getTime() - nowMs;
  const eligibleAt = payableDate.toLocaleString('vi-VN', {
    hour: '2-digit', minute: '2-digit', second: '2-digit',
    day: '2-digit', month: '2-digit', year: 'numeric',
  });
  if (remainingMs <= 0) {
    return `Đủ điều kiện từ: ${eligibleAt}\nĐã hết thời gian chờ, hệ thống đang cập nhật trạng thái.`;
  }

  const totalSeconds = Math.floor(remainingMs / 1000);
  const days = Math.floor(totalSeconds / 86400);
  const hours = Math.floor((totalSeconds % 86400) / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const seconds = totalSeconds % 60;
  const countdown = `${days} ngày ${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
  return `Đủ điều kiện lúc: ${eligibleAt}\nCòn lại: ${countdown}`;
};

const commissionLabels = {
  PENDING: 'Đang chờ',
  PAYABLE: 'Có thể chi trả',
  PAID: 'Đã chi trả',
  REVERSED: 'Đã thu hồi',
};

const BusinessManagementPage = () => {
  const [sales, setSales] = useState([]);
  const [commissions, setCommissions] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [nowMs, setNowMs] = useState(0);
  const [form, setForm] = useState({ email: '', password: '123456', fullName: '', phone: '' });

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const [saleResponse, commissionResponse] = await Promise.all([
        api.get('/admin/sales'),
        api.get('/admin/sales/commissions'),
      ]);
      setSales(saleResponse.data || []);
      setCommissions(commissionResponse.data || []);
      setError('');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load().catch(err => setError(err.response?.data?.message || 'Không thể tải dữ liệu'));
  }, [load]);

  useEffect(() => {
    if (!commissions.some(item => item.status === 'PENDING')) return undefined;
    const timer = window.setInterval(() => setNowMs(Date.now()), 1000);
    return () => window.clearInterval(timer);
  }, [commissions]);

  const summary = useMemo(() => ({
    activeSales: sales.filter(sale => sale.status).length,
    pendingCommission: commissions
      .filter(item => item.status === 'PENDING' || item.status === 'PAYABLE')
      .reduce((total, item) => total + Number(item.commissionAmount || 0), 0),
    paidCommission: commissions
      .filter(item => item.status === 'PAID')
      .reduce((total, item) => total + Number(item.commissionAmount || 0), 0),
  }), [sales, commissions]);

  const {
    page: commissionPage,
    setPage: setCommissionPage,
    totalPages: commissionTotalPages,
    pageItems: visibleCommissions,
  } = useClientPagination(commissions);

  const create = async event => {
    event.preventDefault();
    setError('');
    const normalizedPhone = form.phone.trim();
    if (normalizedPhone && sales.some(sale => sale.phone?.trim() === normalizedPhone)) {
      setError('Số điện thoại đã tồn tại');
      return;
    }
    setSubmitting(true);
    try {
      await api.post('/admin/sales/accounts', { ...form, phone: normalizedPhone || null });
      setForm({ email: '', password: '123456', fullName: '', phone: '' });
      await load();
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể tạo tài khoản');
    } finally {
      setSubmitting(false);
    }
  };

  const markPaid = async commission => {
    const confirmed = await confirmDialog(
      `Xác nhận chi trả ${money(commission.commissionAmount)} cho nhân viên Sale ${commission.saleName} (${commission.saleEmail}), thuộc giao dịch #${commission.transactionId}?`,
      { confirmText: 'Thanh toán' },
    );
    if (!confirmed) return;

    setError('');
    try {
      await api.put(`/admin/sales/commissions/${commission.id}/paid`);
      await load();
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể xác nhận chi trả');
    }
  };

  return (
    <AdminLayout>
      <div className="business-page">
        <header className="business-header">
          <div>
            <span className="business-eyebrow"><BriefcaseBusiness size={16} /> Trung tâm vận hành</span>
            <h1>Quản lý kinh doanh</h1>
            <p>Quản lý nhân viên Sale và đối soát các khoản hoa hồng phát sinh từ giao dịch.</p>
          </div>
        </header>

        {error && <div className="business-alert" role="alert">{error}</div>}

        <section className="business-summary" aria-label="Tổng quan kinh doanh">
          <article className="business-summary-card summary-blue">
            <div className="business-summary-icon"><Users size={23} /></div>
            <div><span>Sale đang hoạt động</span><strong>{summary.activeSales}</strong></div>
          </article>
          <article className="business-summary-card summary-orange">
            <div className="business-summary-icon"><WalletCards size={23} /></div>
            <div><span>Hoa hồng chưa chi</span><strong>{money(summary.pendingCommission)}</strong></div>
          </article>
          <article className="business-summary-card summary-yellow">
            <div className="business-summary-icon"><BadgeCheck size={23} /></div>
            <div><span>Hoa hồng đã chi</span><strong>{money(summary.paidCommission)}</strong></div>
          </article>
        </section>

        <section className="business-panel">
          <div className="business-panel-heading">
            <div className="business-heading-icon"><UserPlus size={21} /></div>
            <div><h2>Cấp tài khoản nhân viên Sale</h2><p>Tạo tài khoản mới và theo dõi trạng thái đội ngũ kinh doanh.</p></div>
          </div>

          <form className="business-sale-form" onSubmit={create}>
            <label>
              <span>Họ và tên</span>
              <input required autoComplete="name" placeholder="Ví dụ: Nguyễn Minh Anh" value={form.fullName} onChange={event => setForm({ ...form, fullName: event.target.value })} />
            </label>
            <label>
              <span>Email đăng nhập</span>
              <input required type="email" autoComplete="off" placeholder="sale@gympro.com" value={form.email} onChange={event => setForm({ ...form, email: event.target.value })} />
            </label>
            <label>
              <span>Số điện thoại</span>
              <input autoComplete="tel" placeholder="09xxxxxxxx" value={form.phone} onChange={event => setForm({ ...form, phone: event.target.value })} />
            </label>
            <label>
              <span>Mật khẩu ban đầu</span>
              <input required type="password" minLength="6" autoComplete="new-password" placeholder="Tối thiểu 6 ký tự" value={form.password} onChange={event => setForm({ ...form, password: event.target.value })} />
            </label>
            <button className="business-primary-btn" type="submit" disabled={submitting}>
              <UserPlus size={18} /> {submitting ? 'Đang tạo...' : 'Tạo tài khoản Sale'}
            </button>
          </form>

          <div className="business-table-scroll">
            <table className="admin-table business-table">
              <thead><tr><th>Nhân viên</th><th>Email</th><th>Điện thoại</th><th>Trạng thái</th></tr></thead>
              <tbody>
                {loading ? <tr><td colSpan="4" className="business-empty">Đang tải dữ liệu...</td></tr>
                  : sales.length === 0 ? <tr><td colSpan="4" className="business-empty">Chưa có tài khoản Sale.</td></tr>
                    : sales.map(sale => (
                      <tr key={sale.id}>
                        <td><div className="business-person"><span>{sale.fullName?.charAt(0)?.toUpperCase() || 'S'}</span><strong>{sale.fullName}</strong></div></td>
                        <td>{sale.email}</td><td>{sale.phone || '—'}</td>
                        <td><span className={`business-status ${sale.status ? 'status-active' : 'status-locked'}`}>{sale.status ? 'Hoạt động' : 'Đã khóa'}</span></td>
                      </tr>
                    ))}
              </tbody>
            </table>
          </div>
        </section>

        <section className="business-panel">
          <div className="business-panel-heading">
            <div className="business-heading-icon icon-orange"><WalletCards size={21} /></div>
            <div><h2>Quản lý hoa hồng Sale</h2><p>Đối soát doanh thu và xác nhận các khoản hoa hồng đã chi trả.</p></div>
          </div>
          <div className="business-table-scroll">
            <table className="admin-table business-table">
              <thead><tr><th>Giao dịch</th><th>Khách hàng</th><th>Nhân viên Sale</th><th>Doanh thu</th><th>Tỷ lệ</th><th>Hoa hồng</th><th>Trạng thái</th><th>Thao tác</th></tr></thead>
              <tbody>
                {loading ? <tr><td colSpan="8" className="business-empty">Đang tải dữ liệu...</td></tr>
                  : visibleCommissions.length === 0 ? <tr><td colSpan="8" className="business-empty">Chưa phát sinh hoa hồng.</td></tr>
                    : visibleCommissions.map(item => (
                      <tr key={item.id}>
                        <td><strong className="business-code">#{item.transactionId}</strong></td>
                        <td>{item.memberName}</td>
                        <td><div className="business-sale-commission"><strong>{item.saleName}</strong><small>{item.saleEmail}</small></div></td>
                        <td>{money(item.baseAmount)}</td><td>{item.commissionRate}%</td>
                        <td><strong className="business-money">{money(item.commissionAmount)}</strong></td>
                        <td><span
                          className={`business-status status-${item.status?.toLowerCase()} ${item.status === 'PENDING' ? 'business-status-with-tooltip' : ''}`}
                          data-tooltip={item.status === 'PENDING' ? waitingTooltip(item.payableAt, nowMs) : undefined}
                          tabIndex={item.status === 'PENDING' ? 0 : undefined}
                        >{commissionLabels[item.status] || item.status}</span></td>
                        <td>{item.status === 'PAYABLE'
                          ? <button className="business-action-btn action-pay" type="button" onClick={() => markPaid(item)}><BadgeCheck size={17} /> Thanh toán</button>
                          : <span className="business-muted">—</span>}</td>
                      </tr>
                    ))}
              </tbody>
            </table>
          </div>
          <AdminPagination page={commissionPage} totalPages={commissionTotalPages} onPageChange={setCommissionPage} />
        </section>

      </div>
    </AdminLayout>
  );
};

export default BusinessManagementPage;
