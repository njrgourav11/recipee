/**
 * Custom hook for debouncing values.
 * 
 * This hook delays updating the debounced value until after the specified
 * delay has passed since the last time the input value changed.
 */

import { useState, useEffect } from 'react';
import type { UseDebounceReturn } from '../types/recipe';

/**
 * Hook that debounces a value
 * 
 * @param value - The value to debounce
 * @param delay - The delay in milliseconds
 * @returns Object containing the debounced value and debouncing status
 */
export function useDebounce<T>(value: T, delay: number): UseDebounceReturn<T> {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);
  const [isDebouncing, setIsDebouncing] = useState(false);

  useEffect(() => {
    // Set debouncing to true when value changes
    setIsDebouncing(true);

    // Set up the timeout
    const handler = setTimeout(() => {
      setDebouncedValue(value);
      setIsDebouncing(false);
    }, delay);

    // Cleanup function to cancel the timeout if value changes again
    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  // Initialize debounced value on first render
  useEffect(() => {
    setDebouncedValue(value);
  }, []);

  return {
    debouncedValue,
    isDebouncing,
  };
}

export default useDebounce;