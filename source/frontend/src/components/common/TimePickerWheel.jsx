import React, { useState, useRef, useEffect } from 'react';
import './TimePickerWheel.css';

const HOURS = Array.from({ length: 24 }, (_, i) => i);
const MINUTES = [0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55];

const pad2 = (n) => String(n).padStart(2, '0');

const TimePickerWheel = ({ value = '08:00', onChange, label }) => {
  const [open, setOpen] = useState(false);
  const containerRef = useRef(null);
  const hourListRef = useRef(null);
  const minuteListRef = useRef(null);

  const [h, m] = (value || '08:00').split(':').map(Number);
  const selectedHour = h;
  const selectedMinute = MINUTES.reduce((prev, curr) =>
    Math.abs(curr - m) < Math.abs(prev - m) ? curr : prev
  , 0);

  // Close on outside click
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (containerRef.current && !containerRef.current.contains(e.target)) {
        setOpen(false);
      }
    };
    if (open) {
      document.addEventListener('mousedown', handleClickOutside);
    }
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [open]);

  // Scroll to selected on open
  useEffect(() => {
    if (open) {
      setTimeout(() => {
        const hEl = hourListRef.current?.querySelector('.tw-item.active');
        if (hEl) hEl.scrollIntoView({ block: 'center', behavior: 'instant' });
        const mEl = minuteListRef.current?.querySelector('.tw-item.active');
        if (mEl) mEl.scrollIntoView({ block: 'center', behavior: 'instant' });
      }, 30);
    }
  }, [open]);

  const handleSelect = (newH, newM) => {
    const timeStr = `${pad2(newH)}:${pad2(newM)}`;
    onChange(timeStr);
  };

  const getTimeLabel = () => {
    return `${pad2(selectedHour)}:${pad2(selectedMinute)}`;
  };

  return (
    <div className="tw-container" ref={containerRef}>
      {label && <label className="tw-label">{label}</label>}
      <button
        type="button"
        className={`tw-trigger ${open ? 'open' : ''}`}
        onClick={() => setOpen(!open)}
      >
        <span className="tw-clock-icon">🕐</span>
        <span className="tw-display-time">{getTimeLabel()}</span>
        <span className="tw-chevron">{open ? '▲' : '▼'}</span>
      </button>

      {open && (
        <div className="tw-dropdown">
          <div className="tw-columns">
            {/* Hours */}
            <div className="tw-column">
              <div className="tw-col-header">Giờ</div>
              <div className="tw-col-list" ref={hourListRef}>
                {HOURS.map((hr) => (
                  <button
                    key={hr}
                    type="button"
                    className={`tw-item ${hr === selectedHour ? 'active' : ''}`}
                    onClick={() => handleSelect(hr, selectedMinute)}
                  >
                    {pad2(hr)}
                  </button>
                ))}
              </div>
            </div>

            {/* Minutes */}
            <div className="tw-column">
              <div className="tw-col-header">Phút</div>
              <div className="tw-col-list" ref={minuteListRef}>
                {MINUTES.map((min) => (
                  <button
                    key={min}
                    type="button"
                    className={`tw-item ${min === selectedMinute ? 'active' : ''}`}
                    onClick={() => handleSelect(selectedHour, min)}
                  >
                    {pad2(min)}
                  </button>
                ))}
              </div>
            </div>
          </div>

          {/* Quick Picks */}
          <div className="tw-quick-picks">
            <span className="tw-quick-label">Nhanh:</span>
            {[
              { label: '6:00', h: 6, m: 0 },
              { label: '8:00', h: 8, m: 0 },
              { label: '10:00', h: 10, m: 0 },
              { label: '14:00', h: 14, m: 0 },
              { label: '17:00', h: 17, m: 0 },
              { label: '19:00', h: 19, m: 0 },
              { label: '21:00', h: 21, m: 0 },
            ].map((q) => (
              <button
                key={q.label}
                type="button"
                className={`tw-quick-btn ${selectedHour === q.h && selectedMinute === q.m ? 'active' : ''}`}
                onClick={() => {
                  handleSelect(q.h, q.m);
                  setOpen(false);
                }}
              >
                {q.label}
              </button>
            ))}
          </div>

          <button
            type="button"
            className="tw-done-btn"
            onClick={() => setOpen(false)}
          >
            ✓ Xong
          </button>
        </div>
      )}
    </div>
  );
};

export default TimePickerWheel;
