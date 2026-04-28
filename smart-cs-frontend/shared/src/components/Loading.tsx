import React from 'react';
import clsx from 'clsx';
import './Loading.css';

interface LoadingProps {
  size?: 'sm' | 'md' | 'lg';
  fullScreen?: boolean;
  tip?: string;
}

export const Loading: React.FC<LoadingProps> = ({
  size = 'md',
  fullScreen = false,
  tip,
}) => {
  const content = (
    <div className={clsx('loading', `loading-${size}`)}>
      <div className="loading-spinner" />
      {tip && <span className="loading-tip">{tip}</span>}
    </div>
  );

  if (fullScreen) {
    return <div className="loading-fullscreen">{content}</div>;
  }

  return content;
};
