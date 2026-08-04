import './PtSummaryCards.css';

export const PtSummaryGrid = ({ children, columns = 3, ariaLabel }) => (
  <section
    className="pt-summary-grid"
    style={{ '--pt-summary-columns': columns }}
    aria-label={ariaLabel}
  >
    {children}
  </section>
);

export const PtSummaryCard = ({ icon: Icon, label, value, tone = 'orange', compact = false }) => (
  <article className={`pt-summary-card pt-summary-card--${tone}${compact ? ' pt-summary-card--compact' : ''}`}>
    <span className="pt-summary-card-icon" aria-hidden="true">
      <Icon size={25} />
    </span>
    <div className="pt-summary-card-content">
      <span className="pt-summary-card-label">{label}</span>
      <strong className="pt-summary-card-value">{value}</strong>
    </div>
  </article>
);
