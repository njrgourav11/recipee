/**
 * SearchBar molecule component
 * 
 * A search input with integrated search functionality and debounced input.
 */

import React, { useState } from 'react';
import { Input } from '../../atoms/Input/Input';
import { Button } from '../../atoms/Button/Button';
import type { SearchBarProps } from '../../../types/recipe';
import './SearchBar.scss';

export const SearchBar: React.FC<SearchBarProps> = ({
  value,
  onChange,
  onSearch,
  placeholder = "Search recipes by name or cuisine...",
  loading = false,
  className = '',
}) => {
  const [localValue, setLocalValue] = useState(value);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = e.target.value;
    setLocalValue(newValue);
    onChange(newValue);
  };

  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      onSearch();
    }
  };

  const handleSearchClick = () => {
    onSearch();
  };

  const searchIcon = (
    <svg viewBox="0 0 20 20" fill="currentColor">
      <path fillRule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clipRule="evenodd" />
    </svg>
  );

  return (
    <div className={`search-bar ${className}`}>
      <div className="search-bar__input-container">
        <Input
          type="text"
          variant="search"
          size="large"
          value={localValue}
          onChange={handleInputChange}
          onKeyPress={handleKeyPress}
          placeholder={placeholder}
          disabled={loading}
          leftIcon={searchIcon}
          fullWidth
        />
        <Button
          variant="primary"
          size="large"
          onClick={handleSearchClick}
          disabled={loading || localValue.length < 3}
          loading={loading}
          className="search-bar__button"
        >
          Search
        </Button>
      </div>
    </div>
  );
};

export default SearchBar;
          