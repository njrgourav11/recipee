/**
 * Loading atom component
 * 
 * A reusable loading component with various styles and animations.
 */

import React from 'react';
import './Loading.scss';

export interface LoadingProps {
  variant?: 'spinner' | 'dots' | 'pulse' | 'skeleton';
  size?: 'small' | 'medium' | 'large';
  color?: 'primary' | 'secondary' | 'white';
  text?: string;
  className?: string;
}

export const Loading: React.FC<LoadingProps> = ({
  variant = 'spinner',
  size = 'medium',
  color = 'primary',
  text,
  className = '',
}) => {
  const classes = [
    'loading',
    `loading--${variant}`,
    `loading--${size}`,
    `loading--${color}`,
    className,
  ].filter(Boolean).join(' ');

  const renderSpinner = () => (
    <div className="loading__spinner">
      <div className="loading__spinner-circle"></div>
    </div>
  );

  const renderDots = () => (
    <div className="loading__dots">
      <div className="loading__dot"></div>
      <div className="loading__dot"></div>
      <div className="loading__dot"></div>
    </div>
  );

  const renderPulse = () => (
    <div className="loading__pulse">
      <div className="loading__pulse-circle"></div>
    </div>
  );

  const renderSkeleton = () => (
    <div className="loading__skeleton">
      <div className="loading__skeleton-line loading__skeleton-line--title"></div>
      <div className="loading__skeleton-line loading__skeleton-line--subtitle"></div>
      <div className="loading__skeleton-line loading__skeleton-line--content"></div>
    </div>
  );

  const renderVariant = () => {
    switch (variant) {
      case 'dots':
        return renderDots();
      case 'pulse':
        return renderPulse();
      case 'skeleton':
        return renderSkeleton();
      default:
        return renderSpinner();
    }
  };

  return (
    <div className={classes}>
      {renderVariant()}
      {text && <p className="loading__text">{text}</p>}
    </div>
  );
};

export default Loading;