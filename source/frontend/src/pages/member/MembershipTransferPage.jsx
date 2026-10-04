import { useCallback, useEffect, useRef, useState } from 'react';
import MainLayout from '../../components/layout/MainLayout';
import transferService from '../../services/membershipTransferService';
import { getPolicy } from '../../services/policyService';
import authService from '../../services/authService';
import { confirmDialog, promptDialog } from '../../utils/dialog';
import { useAuth } from '../../context/AuthContext';
import PolicyDialog from '../../components/common/PolicyDialog';
import './MembershipTransferPage.css';

const MembershipTransferPage = () => {
  const { user } = useAuth();
  const [incoming, setIncoming] = useState([]);
  const [outgoing, setOutgoing] = useState([]);
  const [policy, setPolicy] = useState(null);
  const [form, setForm] = useState({ recipientEmail: '', password: '', otp: '', acceptedPolicy: false });
  const [verified, setVerified] = useState(false);
  const [error, setError] = useState('');
  const [googleClientId, setGoogleClientId] = useState('');
  const [policyDialogMode, setPolicyDialogMode] = useState(null);
  const googleButtonRef = useRef(null);

  const load = async () => {
    const [ins, outs, p] = await Promise.all([
      transferService.incoming(), transferService.outgoing(), getPolicy('TRANSFER_POLICY'),
    ]);
    setIncoming(ins); setOutgoing(outs); setPolicy(p);
  };
  useEffect(() => {
    load().catch(e => setError(e.response?.data?.message || 'Không thể tải dữ liệu'));
    authService.getGoogleClientId().then(setGoogleClientId).catch(() => {});
  }, []);

  const verifyGoogle = useCallback(async response => {
    if (!form.recipientEmail) {
      setError('Vui lòng nhập email người nhận trước khi xác thực Google.');
      return;
    }
    setError('');
    try {
      await transferService.verifySender({
        recipientEmail: form.recipientEmail,
        googleIdToken: response.credential,
      });
      setVerified(true);
    } catch (err) {
      setError(err.response?.data?.message || 'Xác thực Google thất bại');
    }
  }, [form.recipientEmail]);

  useEffect(() => {
    if (!verified && window.google && googleClientId && googleButtonRef.current) {
      googleButtonRef.current.replaceChildren();
      window.google.accounts.id.initialize({ client_id: googleClientId, callback: verifyGoogle });
      window.google.accounts.id.renderButton(googleButtonRef.current, {
        theme: 'outline', size: 'large', text: 'continue_with', width: 280,
      });
    }
  }, [googleClientId, verified, verifyGoogle]);

  const verify = async (e) => {
    e.preventDefault(); setError('');
    try {
      await transferService.verifySender({ recipientEmail: form.recipientEmail, password: form.password });
      setVerified(true);
    } catch (e2) { setError(e2.response?.data?.message || 'Xác thực thất bại'); }
  };
  const create = async (e) => {
    e.preventDefault(); setError('');
    try {
      await transferService.create({ recipientEmail: form.recipientEmail, otp: form.otp,
        transferPolicyVersionId: policy.id, acceptedPolicy: form.acceptedPolicy });
      setVerified(false); setForm({ recipientEmail: '', password: '', otp: '', acceptedPolicy: false });
      await load();
    } catch (e2) { setError(e2.response?.data?.message || 'Không thể tạo yêu cầu'); }
  };
  const accept = async (item) => {
    try {
      if (!await confirmDialog(
        'Việc nhận gói sẽ áp dụng khấu trừ thời hạn theo chính sách. Nếu gói được nhận khác loại, gói hiện tại của bạn có thể bị thay thế hoàn toàn.',
        {
          title: 'Xác nhận nhận gói tập',
          confirmText: 'Đồng ý và tiếp tục',
          requireAcknowledgement: true,
          acknowledgementText: 'Tôi đã đọc kỹ Chính sách chuyển nhượng hiện hành, hiểu rõ các điều kiện và chấp nhận mọi rủi ro phát sinh khi nhận gói.',
        },
      )) return;
      await transferService.sendAcceptOtp(item.id);
      const otp = await promptDialog('Mã xác thực đã được gửi đến email của bạn.', {
        title: 'Xác nhận mã OTP',
        placeholder: 'Nhập 6 chữ số',
        inputMode: 'numeric',
        maxLength: 6,
        confirmText: 'Xác nhận OTP',
        required: true,
      });
      if (!otp) return;
      const replace = !item.replacesCurrentPackage || await confirmDialog(
        `Gói ${item.recipientCurrentPackage} hiện tại sẽ bị mất hoàn toàn và được thay bằng ${item.packageName}. Hành động này không thể hoàn tác.`,
        { title: 'Cảnh báo thay thế gói', confirmText: 'Thay thế gói', danger: true },
      );
      if (!replace) return;
      await transferService.accept(item.id, { otp, transferPolicyVersionId: policy.id,
        acceptedPolicy: true, confirmedReplacement: item.replacesCurrentPackage });
      await load();
    } catch (e) { setError(e.response?.data?.message || 'Không thể nhận gói'); }
  };

  return <MainLayout><div className="transfer-page">
    <h1>Chuyển Nhượng Gói Tập</h1>
    <p className="transfer-lead">Chuyển toàn bộ thời gian còn lại. Hệ thống khấu trừ 10%, tối thiểu 3 và tối đa 30 ngày.</p>
    {error && <div className="transfer-error">{error}</div>}
    <section className="transfer-panel transfer-create-panel"><h2>Tạo yêu cầu</h2>
      <p className="transfer-section-description">Xác minh danh tính của bạn trước khi gửi yêu cầu cho một tài khoản Member khác.</p>
      <div className="transfer-create-layout">
        <aside className="transfer-sender-summary">
          <span>Tài khoản đang chuyển gói</span>
          <div className="transfer-sender-avatar" aria-hidden="true">
            {(user?.fullName || 'M').trim().charAt(0).toUpperCase()}
          </div>
          <strong>{user?.fullName || 'Member hiện tại'}</strong>
          <small>{user?.email}</small>
          <div className="transfer-step-status">
            <b>{verified ? 'Bước 2/2' : 'Bước 1/2'}</b>
            <p>{verified ? 'Nhập OTP và đồng ý chính sách để gửi yêu cầu.' : 'Xác thực danh tính bằng mật khẩu hoặc Google.'}</p>
          </div>
        </aside>
        <form onSubmit={verified ? create : verify} className="transfer-form" autoComplete="off">
        <div className="transfer-field">
          <label htmlFor="transfer-recipient-email">Email của Member nhận gói</label>
          <input id="transfer-recipient-email" name="transferRecipientEmail" type="email" required
            placeholder="Ví dụ: member@example.com" value={form.recipientEmail} disabled={verified}
            autoComplete="off" spellCheck="false" data-lpignore="true" data-1p-ignore="true"
            aria-describedby="transfer-recipient-hint"
            onChange={e => setForm({ ...form, recipientEmail: e.target.value })} />
          <small id="transfer-recipient-hint">Chỉ chấp nhận email của tài khoản có vai trò Member đang hoạt động; không nhập email PT, Sale hoặc Admin.</small>
        </div>
        {!verified ? <>
          <div className="transfer-field">
            <label htmlFor="transfer-sender-password">Mật khẩu của tài khoản đang chuyển gói</label>
            <input id="transfer-sender-password" name="transferSenderPassword" type="password" required
              placeholder="Nhập mật khẩu của bạn" value={form.password} autoComplete="current-password"
              aria-describedby="transfer-password-hint"
              onChange={e => setForm({ ...form, password: e.target.value })} />
            <small id="transfer-password-hint">Đây là mật khẩu của {user?.email || 'tài khoản đang đăng nhập'}, không phải mật khẩu người nhận.</small>
          </div>
          <div className="transfer-auth-divider">hoặc xác thực tài khoản Google</div>
          <div className="transfer-google-button" ref={googleButtonRef} />
        </> : <>
          <div className="transfer-verified-note">Đã xác thực danh tính. Mã OTP đang được gửi đến email của bạn.</div>
          <div className="transfer-field">
            <label htmlFor="transfer-sender-otp">Mã OTP của người chuyển gói</label>
            <input id="transfer-sender-otp" name="transferSenderOtp" required pattern="\d{6}"
              inputMode="numeric" maxLength="6" autoComplete="one-time-code"
              placeholder="Nhập mã OTP gồm 6 số" value={form.otp}
              aria-describedby="transfer-otp-hint"
              onChange={e => setForm({ ...form, otp: e.target.value.replace(/\D/g, '').slice(0, 6) })} />
            <small id="transfer-otp-hint">Mã được gửi đến {user?.email || 'email của tài khoản đang đăng nhập'}.</small>
          </div>
          <label className="policy-check"><input type="checkbox" checked={form.acceptedPolicy}
            onChange={e => setForm({ ...form, acceptedPolicy: e.target.checked })} />
            Tôi đồng ý <button type="button" onClick={() => setPolicyDialogMode('agree')}>chính sách chuyển nhượng hiện hành</button>
          </label></>}
          <button className="transfer-submit-button" disabled={verified && !form.acceptedPolicy}>{verified ? 'Xác nhận chuyển nhượng' : 'Xác thực và gửi OTP'}</button>
        </form>
      </div>
    </section>
    <div className="transfer-columns">
      <section className="transfer-panel"><h2>Gói đang chờ bạn nhận</h2>
        {incoming.length === 0 ? <p>Không có yêu cầu.</p> : incoming.map(item => <article key={item.id} className="transfer-item">
          <strong>{item.packageName} — {item.remainingDays} ngày còn lại</strong>
          <span>Từ: {item.senderName} · Khấu trừ dự kiến: {item.estimatedDeductionDays} ngày</span>
          <span>Hạn xác nhận: {new Date(item.expiresAt).toLocaleString('vi-VN')}</span>
          <button type="button" className="secondary" onClick={() => setPolicyDialogMode('view')}>Xem chính sách chuyển nhượng</button>
          {item.replacesCurrentPackage && <b className="transfer-warning">Nhận gói này sẽ ghi đè gói {item.recipientCurrentPackage} hiện tại.</b>}
          {item.status === 'PENDING_RECIPIENT' && <div><button onClick={() => accept(item)}>Nhận gói</button>
            <button className="secondary" onClick={() => transferService.reject(item.id).then(load)}>Từ chối</button></div>}
          <em>{item.status}</em>
        </article>)}
      </section>
      <section className="transfer-panel"><h2>Yêu cầu đã gửi</h2>
        {outgoing.length === 0 ? <p>Chưa có yêu cầu.</p> : outgoing.map(item => <article key={item.id} className="transfer-item">
          <strong>{item.packageName} → {item.recipientName}</strong><span>{item.remainingDays} ngày còn lại</span><em>{item.status}</em>
          {item.status === 'PENDING_RECIPIENT' && <button className="secondary" onClick={() => transferService.cancel(item.id).then(load)}>Hủy yêu cầu</button>}
        </article>)}
      </section>
    </div>
    {policyDialogMode && <PolicyDialog
      policy={policy}
      onClose={() => setPolicyDialogMode(null)}
      onAgree={policyDialogMode === 'agree' ? () => {
        setForm(current => ({ ...current, acceptedPolicy: true }));
        setPolicyDialogMode(null);
      } : undefined}
    />}
  </div></MainLayout>;
};
export default MembershipTransferPage;
