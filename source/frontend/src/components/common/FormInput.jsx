import React from 'react';
import './FormInput.css';

const FormInput = ({ label, id, error, ...props }) => {
  return (
    <div className="form-input-group">
      {label && <label htmlFor={id} className="form-label">{label}</label>}
      <input
        id={id}
        className={`form-control ${error ? 'input-error' : ''}`}
        {...props}
      />
      {error && <span className="error-message">{error}</span>}
    </div>
  );
};

export default FormInput;
