import { useState, useEffect } from 'react';
import type { UseDebounceReturn } from '../types/recipe';

export function useDebounce<T>(value: T, delay: number): UseDebounceReturn<T> {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);
  const [isDebouncing, setIsDebouncing] = useState(false);

  useEffect(() => {
    setIsDebouncing(true);

    const handler = setTimeout(() => {
      setDebouncedValue(value);
      setIsDebouncing(false);
    }, delay);

    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  useEffect(() => {
    setDebouncedValue(value);
  }, []);

  return {
    debouncedValue,
    isDebouncing,
  };
}

export default useDebounce;