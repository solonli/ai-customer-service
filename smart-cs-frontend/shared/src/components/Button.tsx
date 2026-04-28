import React from 'react';
import clsx from 'clsx';
import './Button.css';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost';
  size?: 'sm' | 'md' | 'lg';
  loading?: boolean;
  children: React.ReactNode;
}

export const Button: React.FC<ButtonProps> = ({
  variant = 'primary',
  size = 'md',
  loading = false,
  disabled,
  className,
  children,
  ...props
}) => {
  return (
    <button
      className={clsx('btn', `btn-${variant}`, `btn-${size}`, className)}
      disabled={disabled || loading}
      {...props}
    >
      {loading ? <span className="btn-loading" /> : children}
    </button>
  );
};
