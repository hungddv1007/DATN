import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import AdminLayout from '../../components/layout/AdminLayout';
import PtLayout from '../../components/layout/PtLayout';
import { useAuth } from '../../context/AuthContext';
import notificationService from '../../services/notificationService';
import ptDashboardService from '../../services/ptDashboardService';
import userService from '../../services/userService';
import {
  Bell,
  Check,
  CheckCircle2,
  ChevronDown,
  Search,
  Send,
  UserRound,
  X,
} from 'lucide-react';
import './NotificationComposePage.css';

const NotificationComposePage = () => {
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';
  const Layout = isAdmin ? AdminLayout : PtLayout;
  const [recipients, setRecipients] = useState([]);
  const [selectedUserIds, setSelectedUserIds] = useState([]);
  const [recipientSearch, setRecipientSearch] = useState('');
  const [recipientOpen, setRecipientOpen] = useState(false);
  const [title, setTitle] = useState('');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const recipientPickerRef = useRef(null);

  const normalizeRole = (role) => String(role || '').toUpperCase();

  const filteredRecipients = useMemo(() => {
    const keyword = recipientSearch.trim().toLocaleLowerCase('vi-VN');
    if (!keyword) return recipients;
    return recipients.filter((recipient) =>
      `${recipient.name} ${recipient.email} ${recipient.role}`
        .toLocaleLowerCase('vi-VN')
        .includes(keyword));
  }, [recipientSearch, recipients]);

  const selectedRecipients = useMemo(() => {
    const selectedIds = new Set(selectedUserIds);
    return recipients.filter((recipient) => selectedIds.has(recipient.id));
  }, [recipients, selectedUserIds]);

  const loadRecipients = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      if (isAdmin) {
        const users = await userService.getAllUsers();
        setRecipients(users
          .filter((item) => item.id !== user?.id && item.status !== false)
          .map((item) => ({ id: item.id, name: item.fullName, email: item.email, role: item.role })));
      } else {
        const members = await ptDashboardService.getAssignedMembers();
        setRecipients(members.map((item) => ({
          id: item.memberId,
          name: item.memberName,
          email: item.memberEmail,
          role: 'MEMBER',
        })));
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể tải danh sách người nhận.');
    } finally {
      setLoading(false);
    }
  }, [isAdmin, user?.id]);

  useEffect(() => {
    loadRecipients();
  }, [loadRecipients]);

  useEffect(() => {
    const handlePointerDown = (event) => {
      if (recipientPickerRef.current
          && !recipientPickerRef.current.contains(event.target)) {
        setRecipientOpen(false);
      }
    };
    const handleKeyDown = (event) => {
      if (event.key === 'Escape') setRecipientOpen(false);
    };

    document.addEventListener('mousedown', handlePointerDown);
    document.addEventListener('keydown', handleKeyDown);
    return () => {
      document.removeEventListener('mousedown', handlePointerDown);
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, []);

  const toggleRecipient = (recipientId) => {
    setSelectedUserIds((current) => current.includes(recipientId)
      ? current.filter((id) => id !== recipientId)
      : [...current, recipientId]);
    setError('');
    setSuccess('');
  };

  const selectRecipientsByRole = (role) => {
    const matchingIds = recipients
      .filter((recipient) => !role || normalizeRole(recipient.role) === role)
      .map((recipient) => recipient.id);
    setSelectedUserIds(matchingIds);
    setError('');
    setSuccess('');
  };

  const removeRecipient = (recipientId) => {
    setSelectedUserIds((current) => current.filter((id) => id !== recipientId));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    setSuccess('');

    const cleanTitle = title.trim();
    const cleanMessage = message.trim();
    if (selectedUserIds.length === 0) {
      setError('Vui lòng chọn ít nhất một người nhận.');
      return;
    }
    if (!cleanTitle || !cleanMessage) {
      setError('Vui lòng nhập đầy đủ tiêu đề và nội dung thông báo.');
      return;
    }

    setSending(true);
    try {
      const result = await notificationService.sendNotifications(
        selectedUserIds,
        cleanTitle,
        cleanMessage,
      );
      setSuccess(result.message || `Đã gửi thông báo tới ${selectedUserIds.length} người nhận.`);
      setSelectedUserIds([]);
      setTitle('');
      setMessage('');
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể gửi thông báo.');
    } finally {
      setSending(false);
    }
  };

  return (
    <Layout>
      <div className="notification-compose-page">
        <header className="notification-compose-header">
          <div className="notification-compose-icon" aria-hidden="true">
            <Bell size={26} />
          </div>
          <div>
            <h1>Gửi thông báo</h1>
            <p>{isAdmin ? 'Gửi thông báo tới người dùng trong hệ thống' : 'Gửi thông báo tới hội viên được phân công'}</p>
          </div>
        </header>

        <section className="notification-compose-card">
          <div className="notification-compose-card-heading">
            <div>
              <h2>Soạn thông báo mới</h2>
              <p>Điền đầy đủ thông tin bên dưới để gửi thông báo.</p>
            </div>
            <span className="notification-recipient-count">
              <UserRound size={16} />
              {loading ? 'Đang tải người nhận' : `${recipients.length} người nhận khả dụng`}
            </span>
          </div>

          {error && <div className="alert-error">{error}</div>}
          {success && (
            <div className="notification-alert notification-alert-success" role="status">
              <CheckCircle2 size={19} />
              {success}
            </div>
          )}

          <form className="notification-compose-form" onSubmit={handleSubmit}>
            <div className="notification-form-group">
              <div className="notification-label-row">
                <label id="notification-recipient-label">Người nhận <span>*</span></label>
                <small>{selectedUserIds.length} người đã chọn</small>
              </div>

              <div className="notification-recipient-picker" ref={recipientPickerRef}>
                <button
                  className={`notification-recipient-trigger${recipientOpen ? ' is-open' : ''}`}
                  type="button"
                  aria-labelledby="notification-recipient-label"
                  aria-expanded={recipientOpen}
                  disabled={loading}
                  onClick={() => setRecipientOpen((current) => !current)}
                >
                  <span>
                    {loading
                      ? 'Đang tải danh sách người nhận...'
                      : selectedUserIds.length > 0
                        ? `Đã chọn ${selectedUserIds.length} người nhận`
                        : '-- Chọn người nhận --'}
                  </span>
                  <ChevronDown size={19} aria-hidden="true" />
                </button>

                {recipientOpen && (
                  <div className="notification-recipient-dropdown">
                    <div className="notification-recipient-search">
                      <Search size={17} aria-hidden="true" />
                      <input
                        value={recipientSearch}
                        onChange={(event) => setRecipientSearch(event.target.value)}
                        placeholder="Tìm theo tên, email hoặc vai trò..."
                        aria-label="Tìm người nhận"
                        autoFocus
                      />
                    </div>

                    <div className="notification-quick-select">
                      <button type="button" onClick={() => selectRecipientsByRole(null)}>
                        {isAdmin ? 'Chọn tất cả' : 'Chọn tất cả hội viên'}
                      </button>
                      {isAdmin && (
                        <>
                          <button type="button" onClick={() => selectRecipientsByRole('PT')}>
                            Tất cả PT
                          </button>
                          <button type="button" onClick={() => selectRecipientsByRole('MEMBER')}>
                            Tất cả Member
                          </button>
                        </>
                      )}
                      <button
                        className="notification-clear-selection"
                        type="button"
                        disabled={selectedUserIds.length === 0}
                        onClick={() => setSelectedUserIds([])}
                      >
                        Bỏ chọn
                      </button>
                    </div>

                    <div
                      className="notification-recipient-options"
                      role="listbox"
                      aria-multiselectable="true"
                      aria-labelledby="notification-recipient-label"
                    >
                      {filteredRecipients.length === 0 ? (
                        <p className="notification-recipient-empty">
                          Không tìm thấy người nhận phù hợp.
                        </p>
                      ) : filteredRecipients.map((recipient) => {
                        const checked = selectedUserIds.includes(recipient.id);
                        const role = normalizeRole(recipient.role);
                        return (
                          <label
                            className={`notification-recipient-option${checked ? ' is-selected' : ''}`}
                            key={recipient.id}
                            role="option"
                            aria-selected={checked}
                          >
                            <input
                              type="checkbox"
                              checked={checked}
                              onChange={() => toggleRecipient(recipient.id)}
                            />
                            <span className="notification-recipient-checkbox" aria-hidden="true">
                              {checked && <Check size={14} />}
                            </span>
                            <span className="notification-recipient-identity">
                              <strong>{recipient.name}</strong>
                              <small>{recipient.email}</small>
                            </span>
                            <span className={`notification-role-badge role-${role.toLowerCase()}`}>
                              {role === 'MEMBER' ? 'Member' : role}
                            </span>
                          </label>
                        );
                      })}
                    </div>
                  </div>
                )}
              </div>

              {selectedRecipients.length > 0 && (
                <div className="notification-selected-recipients" aria-label="Người nhận đã chọn">
                  {selectedRecipients.slice(0, 4).map((recipient) => (
                    <span key={recipient.id}>
                      {recipient.name}
                      <button
                        type="button"
                        aria-label={`Bỏ chọn ${recipient.name}`}
                        onClick={() => removeRecipient(recipient.id)}
                      >
                        <X size={13} />
                      </button>
                    </span>
                  ))}
                  {selectedRecipients.length > 4 && (
                    <span className="notification-selected-more">
                      +{selectedRecipients.length - 4} người khác
                    </span>
                  )}
                </div>
              )}
            </div>

            <div className="notification-form-group">
              <div className="notification-label-row">
                <label htmlFor="notification-title">Tiêu đề <span>*</span></label>
                <small>{title.length}/200</small>
              </div>
              <input
                id="notification-title"
                value={title}
                onChange={(event) => setTitle(event.target.value)}
                maxLength={200}
                placeholder="Nhập tiêu đề ngắn gọn cho thông báo"
                required
              />
            </div>

            <div className="notification-form-group">
              <div className="notification-label-row">
                <label htmlFor="notification-message">Nội dung <span>*</span></label>
                <small>{message.length}/3000</small>
              </div>
              <textarea
                id="notification-message"
                value={message}
                onChange={(event) => setMessage(event.target.value)}
                maxLength={3000}
                rows={7}
                placeholder="Nhập nội dung muốn gửi tới người nhận..."
                required
              />
            </div>

            <div className="notification-form-actions">
              <p>Thông báo sẽ xuất hiện ngay trong tài khoản của người nhận.</p>
              <button className="notification-submit-button" type="submit" disabled={sending || loading}>
                <Send size={18} />
                {sending ? 'Đang gửi...' : 'Gửi thông báo'}
              </button>
            </div>
          </form>
        </section>
      </div>
    </Layout>
  );
};

export default NotificationComposePage;
