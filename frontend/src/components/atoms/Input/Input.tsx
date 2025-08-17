/**
 * Input atom component
 * 
 * A reusable input component with various styles and states.
 */

import React, { forwardRef } from 'react';
import './Input.scss';

export interface InputProps extends Omit<React.InputHTMLAttributes<HTMLInputElement>, 'size'> {
  variant?: 'default' | 'search' | 'outline';
  size?: 'small' | 'medium' | 'large';
  error?: boolean;
  errorMessage?: string;
  label?: string;
  helperText?: string;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
  fullWidth?: boolean;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(({
  variant = 'default',
  size = 'medium',
  error = false,
  errorMessage,
  label,
  helperText,
  leftIcon,
  rightIcon,
  fullWidth = false,
  className = '',
  disabled,
  ...props
}, ref) => {
  const inputClasses = [
    'input',
    `input--${variant}`,
    `input--${size}`,
    error && 'input--error',
    disabled && 'input--disabled',
    leftIcon && 'input--with-left-icon',
    rightIcon && 'input--with-right-icon',
    fullWidth && 'input--full-width',
    className,
  ].filter(Boolean).join(' ');

  const wrapperClasses = [
    'input-wrapper',
    fullWidth && 'input-wrapper--full-width',
  ].filter(Boolean).join(' ');

  return (
    <div className={wrapperClasses}>
      {label && (
        <label className="input__label" htmlFor={props.id}>
          {label}
        </label>
      )}
      
      <div className="input__container">
        {leftIcon && (
          <div className="input__icon input__icon--left">
            {leftIcon}
          </div>
        )}
        
        <input
          ref={ref}
          className={inputClasses}
          disabled={disabled}
          {...props}
        />
        
        {rightIcon && (
          <div className="input__icon input__icon--right">
            {rightIcon}
          </div>
        )}
      </div>
      
      {(errorMessage || helperText) && (
        <div className={`input__helper ${error ? 'input__helper--error' : ''}`}>
          {error ? errorMessage : helperText}
        </div>
      )}
    </div>
  );
});

Input.displayName = 'Input';

export default Input;