import './SummaryCards.css';

export const SummaryGrid = ({ children, columns = 3, ariaLabel, className = '' }) => (
  <section
    className={`summary-grid ${className}`.trim()}
    style={{ '--summary-columns': columns }}
    aria-label={ariaLabel}
  >
    {children}
  </section>
);

export const SummaryCard = ({ icon: Icon, label, value, tone = 'orange', compact = false }) => (
  <article className={`summary-card summary-card--${tone}${compact ? ' summary-card--compact' : ''}`}>
    <span className="summary-card-icon" aria-hidden="true">
      <Icon size={25} />
    </span>
    <div className="summary-card-content">
      <span className="summary-card-label">{label}</span>
      <strong className="summary-card-value">{value}</strong>
    </div>
  </article>
);
