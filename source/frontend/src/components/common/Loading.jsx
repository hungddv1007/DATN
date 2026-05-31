import React from 'react';
import './Loading.css';

const Loading = ({ fullScreen = false }) => {
  if (fullScreen) {
    return (
      <div className="loading-overlay">
        <div className="spinner"></div>
      </div>
    );
  }
  return <div className="spinner"></div>;
};

export default Loading;
