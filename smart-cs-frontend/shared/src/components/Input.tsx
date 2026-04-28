import React from 'react';
import clsx from 'clsx';
import './Input.css';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  prefix?: React.ReactNode;
  suffix?: React.ReactNode;
}

export const Input: React.FC<InputProps> = ({
  label,
  error,
  prefix,
  suffix,
  className,
  ...props
}) => {
  return (
    <div className="input-wrapper">
      {label && <label className="input-label">{label}</label>}
      <div className={clsx('input-container', error && 'input-error')}>
        {prefix && <span className="input-prefix">{prefix}</span>}
        <input className={clsx('input', className)} {...props} />
        {suffix && <span className="input-suffix">{suffix}</span>}
      </div>
      {error && <span className="input-error-text">{error}</span>}
    </div>
  );
};
