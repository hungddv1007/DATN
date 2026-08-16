import { useEffect } from 'react';
import { ScrollText, X } from 'lucide-react';
import './PolicyDialog.css';

const getPolicySections = content => {
  if (!content) return [];
  const numbered = content
    .trim()
    .split(/(?:^|\s+)(?=\d+\.\s+)/)
    .map(item => item.replace(/^\d+\.\s*/, '').trim())
    .filter(Boolean);
  return numbered.length > 1 ? numbered : [];
};

const PolicyDialog = ({ policy, onClose, onAgree, agreeText = 'Tôi đã đọc và đồng ý' }) => {
  const sections = getPolicySections(policy?.content);

  useEffect(() => {
    const handleKeyDown = event => {
      if (event.key === 'Escape') onClose();
    };
    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [onClose]);

  return (
    <div className="policy-dialog-backdrop" role="presentation" onMouseDown={event => {
      if (event.target === event.currentTarget) onClose();
    }}>
      <section className="policy-dialog" role="dialog" aria-modal="true" aria-labelledby="policy-dialog-title">
        <header className="policy-dialog-header">
          <span className="policy-dialog-icon" aria-hidden="true"><ScrollText size={24} /></span>
          <div>
            <small>GYMPRO · CHÍNH SÁCH HIỆN HÀNH</small>
            <h2 id="policy-dialog-title">{policy?.title || 'Chính sách GymPro'}</h2>
            {policy?.effectiveAt && <p>Có hiệu lực từ {new Date(policy.effectiveAt).toLocaleDateString('vi-VN')}</p>}
          </div>
          <button type="button" className="policy-dialog-close" aria-label="Đóng chính sách" onClick={onClose}>
            <X size={20} />
          </button>
        </header>

        <div className="policy-dialog-body">
          {sections.length > 0 ? (
            <ol className="policy-dialog-sections">
              {sections.map((section, index) => <li key={`${index}-${section}`}>{section}</li>)}
            </ol>
          ) : (
            <p className="policy-dialog-paragraph">
              {policy?.content || 'Nội dung chính sách hiện chưa tải được. Vui lòng thử lại.'}
            </p>
          )}
        </div>

        <footer className="policy-dialog-footer">
          <button type="button" className="policy-dialog-secondary" onClick={onClose}>Đóng</button>
          {onAgree && <button type="button" className="policy-dialog-primary" onClick={onAgree}>{agreeText}</button>}
        </footer>
      </section>
    </div>
  );
};

export default PolicyDialog;
