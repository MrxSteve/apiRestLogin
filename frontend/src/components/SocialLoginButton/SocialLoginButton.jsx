import React from 'react';
import './SocialLoginButton.css';

export const SocialLoginButton = ({ onClick, provider }) => {
  const providers = {
    google: {
      label: 'Continuar con Google',
      backgroundColor: '#ffffff',
      textColor: '#757575',
      borderColor: '#dadce0',
      icon: 'https://img.icons8.com/?size=100&id=V5cGWnc9R4xj&format=png&color=000000',
    },
  };

  const { label, backgroundColor, textColor, borderColor, icon } = providers[provider];

  return (
    <button
      className="social-login-button"
      style={{
        backgroundColor,
        color: textColor,
        border: `1px solid ${borderColor}`,
      }}
      onClick={onClick}
    >
      <img src={icon} alt={`${provider} icon`} className="social-icon" />
      {label}
    </button>
  );
};
