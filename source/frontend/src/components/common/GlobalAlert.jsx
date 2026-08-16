import { useEffect, useState } from 'react';
import { AlertCircle, CircleHelp, X } from 'lucide-react';
import { CONFIRM_DIALOG_EVENT, PROMPT_DIALOG_EVENT } from '../../utils/dialog';
import './GlobalAlert.css';

const GlobalAlert = () => {
  const [message, setMessage] = useState('');
  const [confirmation, setConfirmation] = useState(null);
  const [confirmationAcknowledged, setConfirmationAcknowledged] = useState(false);
  const [promptRequest, setPromptRequest] = useState(null);
  const [promptValue, setPromptValue] = useState('');

  useEffect(() => {
    const nativeAlert = window.alert;
    window.alert = value => setMessage(String(value ?? ''));
    const handleConfirmRequest = event => {
      setConfirmationAcknowledged(false);
      setConfirmation(current => {
        current?.resolve(false);
        return event.detail;
      });
    };
    const handlePromptRequest = event => {
      setPromptRequest(current => {
        current?.resolve(null);
        return event.detail;
      });
      setPromptValue(event.detail.defaultValue);
    };
    window.addEventListener(CONFIRM_DIALOG_EVENT, handleConfirmRequest);
    window.addEventListener(PROMPT_DIALOG_EVENT, handlePromptRequest);
    return () => {
      window.alert = nativeAlert;
      window.removeEventListener(CONFIRM_DIALOG_EVENT, handleConfirmRequest);
      window.removeEventListener(PROMPT_DIALOG_EVENT, handlePromptRequest);
    };
  }, []);

  const closeConfirmation = result => {
    setConfirmation(current => {
      current?.resolve(result);
      return null;
    });
    setConfirmationAcknowledged(false);
  };

  const closePrompt = result => {
    setPromptRequest(current => {
      current?.resolve(result);
      return null;
    });
  };

  useEffect(() => {
    if (!message) return undefined;
    const handleKeyDown = event => {
      if (event.key === 'Escape' || event.key === 'Enter') setMessage('');
    };
    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [message]);

  useEffect(() => {
    if (!confirmation) return undefined;
    const handleKeyDown = event => {
      if (event.key === 'Escape') closeConfirmation(false);
      if (event.key === 'Enter'
        && (!confirmation.requireAcknowledgement || confirmationAcknowledged)) {
        closeConfirmation(true);
      }
    };
    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [confirmation, confirmationAcknowledged]);

  useEffect(() => {
    if (!promptRequest) return undefined;
    const handleKeyDown = event => {
      if (event.key === 'Escape') closePrompt(null);
    };
    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [promptRequest]);

  if (confirmation) {
    return (
      <div className="global-alert-backdrop" role="presentation" onMouseDown={event => {
        if (event.target === event.currentTarget) closeConfirmation(false);
      }}>
        <section className="global-alert-dialog" role="alertdialog" aria-modal="true" aria-labelledby="global-confirm-title" aria-describedby="global-confirm-message">
          <button type="button" className="global-alert-close" aria-label="Đóng hộp xác nhận" onClick={() => closeConfirmation(false)}>
            <X size={18} />
          </button>
          <span className="global-alert-icon global-confirm-icon"><CircleHelp size={27} /></span>
          <h2 id="global-confirm-title">{confirmation.title}</h2>
          <p id="global-confirm-message">{confirmation.message}</p>
          {confirmation.requireAcknowledgement && (
            <label className="global-confirm-acknowledgement">
              <input
                type="checkbox"
                checked={confirmationAcknowledged}
                onChange={event => setConfirmationAcknowledged(event.target.checked)}
              />
              <span>{confirmation.acknowledgementText}</span>
            </label>
          )}
          <div className="global-confirm-actions">
            <button type="button" className="global-confirm-cancel" onClick={() => closeConfirmation(false)}>
              {confirmation.cancelText}
            </button>
            <button
              type="button"
              className={`global-alert-confirm${confirmation.danger ? ' danger' : ''}`}
              autoFocus={!confirmation.requireAcknowledgement}
              disabled={confirmation.requireAcknowledgement && !confirmationAcknowledged}
              onClick={() => closeConfirmation(true)}
            >
              {confirmation.confirmText}
            </button>
          </div>
        </section>
      </div>
    );
  }

  if (promptRequest) {
    const inputProps = {
      value: promptValue,
      placeholder: promptRequest.placeholder,
      onChange: event => setPromptValue(event.target.value),
      autoFocus: true,
      required: promptRequest.required,
      maxLength: promptRequest.maxLength,
      'aria-label': promptRequest.title,
    };

    return (
      <div className="global-alert-backdrop" role="presentation" onMouseDown={event => {
        if (event.target === event.currentTarget) closePrompt(null);
      }}>
        <form className="global-alert-dialog" role="dialog" aria-modal="true" aria-labelledby="global-prompt-title" onSubmit={event => {
          event.preventDefault();
          closePrompt(promptValue);
        }}>
          <button type="button" className="global-alert-close" aria-label="Đóng hộp nhập liệu" onClick={() => closePrompt(null)}>
            <X size={18} />
          </button>
          <span className="global-alert-icon global-confirm-icon"><CircleHelp size={27} /></span>
          <h2 id="global-prompt-title">{promptRequest.title}</h2>
          <p>{promptRequest.message}</p>
          {promptRequest.multiline ? (
            <textarea className="global-prompt-input" rows="4" {...inputProps} />
          ) : (
            <input
              className="global-prompt-input"
              type={promptRequest.inputType}
              inputMode={promptRequest.inputMode}
              min={promptRequest.min}
              max={promptRequest.max}
              {...inputProps}
            />
          )}
          <div className="global-confirm-actions">
            <button type="button" className="global-confirm-cancel" onClick={() => closePrompt(null)}>{promptRequest.cancelText}</button>
            <button type="submit" className="global-alert-confirm" disabled={promptRequest.required && !promptValue.trim()}>{promptRequest.confirmText}</button>
          </div>
        </form>
      </div>
    );
  }

  if (!message) return null;

  return (
    <div className="global-alert-backdrop" role="presentation" onMouseDown={event => {
      if (event.target === event.currentTarget) setMessage('');
    }}>
      <section className="global-alert-dialog" role="alertdialog" aria-modal="true" aria-labelledby="global-alert-title">
        <button type="button" className="global-alert-close" aria-label="Đóng thông báo" onClick={() => setMessage('')}>
          <X size={18} />
        </button>
        <span className="global-alert-icon"><AlertCircle size={26} /></span>
        <h2 id="global-alert-title">Thông báo</h2>
        <p>{message}</p>
        <button type="button" className="global-alert-confirm" autoFocus onClick={() => setMessage('')}>Đã hiểu</button>
      </section>
    </div>
  );
};

export default GlobalAlert;
