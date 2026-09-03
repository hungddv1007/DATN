import { useCallback, useEffect, useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { QRCodeSVG } from 'qrcode.react';
import { CheckCircle2, Clock3, ExternalLink, RefreshCw, ShieldCheck, Smartphone } from 'lucide-react';
import MainLayout from '../../components/layout/MainLayout';
import paymentService from '../../services/paymentService';
import { confirmDialog } from '../../utils/dialog';
import './MomoPaymentPage.css';

const formatCurrency = (value) => new Intl.NumberFormat('vi-VN', {
  style: 'currency', currency: 'VND', maximumFractionDigits: 0,
}).format(value || 0);

const errorMessage = (error, fallback) => error.response?.data?.message
  || error.response?.data?.detail
  || fallback;

export default function MomoPaymentPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const transactionId = Number(searchParams.get('transactionId'));
  const [payment, setPayment] = useState(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState('');
  const [now, setNow] = useState(0);

  const loadPayment = useCallback(async () => {
    if (!Number.isInteger(transactionId) || transactionId <= 0) {
      setError('Đường dẫn thanh toán không hợp lệ.');
      setLoading(false);
      return;
    }
    try {
      let data = await paymentService.getMomoPayment(transactionId);
      setPayment(data);
      if (data.transactionStatus === 'PENDING' && !data.qrCode && !data.payUrl) {
        data = await paymentService.initiateMomoPayment(transactionId);
      }
      setPayment(data);
      setError('');
    } catch (requestError) {
      setError(errorMessage(requestError, 'Không thể tạo phiên thanh toán MoMo.'));
    } finally {
      setLoading(false);
    }
  }, [transactionId]);

  useEffect(() => { loadPayment(); }, [loadPayment]);

  useEffect(() => {
    if (payment?.transactionStatus !== 'PENDING') return undefined;
    const poller = window.setInterval(async () => {
      try {
        const data = await paymentService.getMomoPayment(transactionId);
        setPayment(data);
        if (data.transactionStatus !== 'PENDING') setError('');
      } catch {
        // Một lần poll lỗi không phá phiên thanh toán đang hiển thị.
      }
    }, 2000);
    return () => window.clearInterval(poller);
  }, [payment?.transactionStatus, transactionId]);

  useEffect(() => {
    setNow(Date.now());
    const ticker = window.setInterval(() => setNow(Date.now()), 1000);
    return () => window.clearInterval(ticker);
  }, []);

  const remainingSeconds = payment?.expiresAt
    ? Math.max(0, Math.floor((new Date(payment.expiresAt).getTime() - now) / 1000))
    : 0;

  const countdown = `${String(Math.floor(remainingSeconds / 60)).padStart(2, '0')}:${String(remainingSeconds % 60).padStart(2, '0')}`;
  const qrValue = payment?.qrCode || payment?.payUrl || payment?.deeplink;

  const refreshFromMomo = async () => {
    setRefreshing(true);
    setError('');
    try {
      setPayment(await paymentService.refreshMomoPayment(transactionId));
    } catch (requestError) {
      setError(errorMessage(requestError, 'Chưa thể kiểm tra trạng thái với MoMo.'));
    } finally {
      setRefreshing(false);
    }
  };

  const cancelPayment = async () => {
    if (!await confirmDialog(
      'Hủy phiên thanh toán MoMo này? Giao dịch sẽ kết thúc và mã giảm giá đã giữ chỗ (nếu có) sẽ được hoàn lại.',
      { confirmText: 'Hủy thanh toán', danger: true },
    )) return;
    setRefreshing(true);
    setError('');
    try {
      await paymentService.cancelMomoPayment(transactionId);
      navigate('/packages');
    } catch (requestError) {
      setError(errorMessage(requestError, 'Không thể hủy phiên thanh toán MoMo.'));
      setRefreshing(false);
    }
  };

  if (loading) return <MainLayout><div className="momo-page"><div className="momo-card">Đang tạo mã QR MoMo...</div></div></MainLayout>;

  if (payment?.transactionStatus === 'CONFIRMED') {
    return <MainLayout><div className="momo-page"><section className="momo-card momo-result success">
      <CheckCircle2 size={72} />
      <span className="momo-environment">MOMO SANDBOX</span>
      <h1>Thanh toán thành công</h1>
      <p>Giao dịch <strong>#{payment.transactionId}</strong> với số tiền <strong>{formatCurrency(payment.amount)}</strong> đã được xác nhận tự động.</p>
      <p>Gói tập đã được cập nhật. Email biên nhận đang được gửi ở chế độ nền.</p>
      <div className="momo-result-actions"><Link to="/member/membership">Xem gói tập</Link><Link to="/member/dashboard">Về Dashboard</Link></div>
    </section></div></MainLayout>;
  }

  if (payment?.transactionStatus === 'CANCELLED') {
    return <MainLayout><div className="momo-page"><section className="momo-card momo-result cancelled">
      <Clock3 size={64} />
      <h1>Giao dịch đã kết thúc</h1>
      <p>{payment.message || 'Thanh toán bị hủy, thất bại hoặc mã QR đã hết hạn.'}</p>
      <Link to="/packages">Chọn lại gói tập</Link>
    </section></div></MainLayout>;
  }

  return <MainLayout><div className="momo-page">
    <section className="momo-card">
      <header className="momo-header">
        <div><span className="momo-environment">MOMO SANDBOX · KHÔNG DÙNG TIỀN THẬT</span><h1>Quét mã để thanh toán</h1></div>
        <div className="momo-countdown"><Clock3 size={18} /><span>Còn lại</span><strong>{countdown}</strong></div>
      </header>

      {error && <div className="momo-error">{error}</div>}

      <div className="momo-checkout-grid">
        <div className="momo-qr-panel">
          {qrValue ? <div className="momo-qr"><QRCodeSVG value={qrValue} size={260} level="M" includeMargin /></div>
            : <div className="momo-qr-placeholder">Chưa nhận được dữ liệu QR từ MoMo.</div>}
          <p>Dùng camera điện thoại hoặc ứng dụng <strong>MoMo Test</strong> để quét mã.</p>
        </div>

        <div className="momo-details">
          <div><span>Mã giao dịch GymPro</span><strong>#{payment?.transactionId}</strong></div>
          <div><span>Mã đơn hàng MoMo</span><strong>{payment?.orderId || 'Đang tạo...'}</strong></div>
          <div className="momo-amount"><span>Số tiền thử nghiệm</span><strong>{formatCurrency(payment?.amount)}</strong></div>
          <div className="momo-waiting"><span className="momo-pulse" />Đang chờ MoMo gửi xác nhận IPN...</div>
          {payment?.payUrl && <a className="momo-open" href={payment.payUrl}><ExternalLink size={18} />Tiếp tục tới cổng thanh toán MoMo</a>}
          {payment?.deeplink && <a className="momo-deeplink" href={payment.deeplink}><Smartphone size={18} />Mở bằng MoMo Test</a>}
          <button type="button" className="momo-refresh" onClick={refreshFromMomo} disabled={refreshing}>
            <RefreshCw size={18} className={refreshing ? 'spin' : ''} />{refreshing ? 'Đang kiểm tra...' : 'Kiểm tra lại với MoMo'}
          </button>
          <button type="button" className="momo-cancel" onClick={cancelPayment} disabled={refreshing}>
            Hủy thanh toán
          </button>
        </div>
      </div>

      <footer className="momo-security"><ShieldCheck size={20} /><span>GymPro chỉ kích hoạt gói khi IPN hợp lệ, đúng mã đơn hàng và đúng số tiền.</span></footer>
    </section>
  </div></MainLayout>;
}
