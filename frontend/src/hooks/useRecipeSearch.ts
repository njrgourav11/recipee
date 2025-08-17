import { useState, useCallback, useEffect } from 'react';
import { RecipeApiService, ApiUtils } from '../services/api';
import { useDebounce } from './useDebounce';
import type {
  SearchState,
  SearchFilters,
  UseRecipeSearchReturn,
  Recipe,
} from '../types/recipe';

const initialFilters: SearchFilters = {
  sortBy: 'name',
  sortOrder: 'asc',
  tags: [],
};

const initialState: SearchState = {
  query: '',
  recipes: [],
  loading: false,
  error: null,
  totalPages: 0,
  currentPage: 0,
  totalElements: 0,
  filters: initialFilters,
};

export function useRecipeSearch(
  initialQuery = '',
  autoSearch = true
): UseRecipeSearchReturn {
  const [searchState, setSearchState] = useState<SearchState>({
    ...initialState,
    query: initialQuery,
  });

  const { debouncedValue: debouncedQuery } = useDebounce(searchState.query, 300);

  const searchRecipes = useCallback(async (query: string, page = 0) => {
    if (query.length > 0 && query.length < 3) {
      setSearchState(prev => ({
        ...prev,
        recipes: [],
        totalPages: 0,
        totalElements: 0,
        error: 'Please enter at least 3 characters to search',
      }));
      return;
    }

    setSearchState(prev => ({
      ...prev,
      loading: true,
      error: null,
      currentPage: page,
    }));

    try {
      const searchParams = ApiUtils.buildSearchParams(
        query || undefined,
        page,
        20,
        searchState.filters.sortBy,
        searchState.filters.sortOrder
      );

      const response = await RecipeApiService.searchRecipes(searchParams);

      setSearchState(prev => ({
        ...prev,
        recipes: page === 0 ? response.content : [...prev.recipes, ...response.content],
        totalPages: response.totalPages,
        totalElements: response.totalElements,
        loading: false,
        error: null,
      }));
    } catch (error) {
      const errorMessage = ApiUtils.getErrorMessage(error);
      setSearchState(prev => ({
        ...prev,
        loading: false,
        error: errorMessage,
        recipes: page === 0 ? [] : prev.recipes,
      }));
    }
  }, [searchState.filters.sortBy, searchState.filters.sortOrder]);

  const loadMore = useCallback(async () => {
    if (searchState.loading || searchState.currentPage >= searchState.totalPages - 1) {
      return;
    }

    await searchRecipes(searchState.query, searchState.currentPage + 1);
  }, [searchRecipes, searchState.loading, searchState.currentPage, searchState.totalPages, searchState.query]);

  const setFilters = useCallback((newFilters: Partial<SearchFilters>) => {
    setSearchState(prev => {
      const updatedFilters = { ...prev.filters, ...newFilters };
      return {
        ...prev,
        filters: updatedFilters,
      };
    });
  }, []);

  const clearSearch = useCallback(() => {
    setSearchState({
      ...initialState,
      filters: searchState.filters,
    });
  }, [searchState.filters]);

  const setQuery = useCallback((query: string) => {
    setSearchState(prev => ({
      ...prev,
      query,
    }));
  }, []);

  const getFilteredRecipes = useCallback((): Recipe[] => {
    let filtered = [...searchState.recipes];

    if (searchState.filters.tags.length > 0) {
      filtered = filtered.filter(recipe =>
        recipe.tags.some(tag =>
          searchState.filters.tags.some(filterTag =>
            tag.toLowerCase().includes(filterTag.toLowerCase())
          )
        )
      );
    }

    if (searchState.filters.cuisine) {
      filtered = filtered.filter(recipe =>
        recipe.cuisine.toLowerCase().includes(searchState.filters.cuisine!.toLowerCase())
      );
    }

    if (searchState.filters.difficulty) {
      filtered = filtered.filter(recipe =>
        recipe.difficulty === searchState.filters.difficulty
      );
    }

    if (searchState.filters.maxCookTime) {
      filtered = filtered.filter(recipe =>
        recipe.cook_time_minutes <= searchState.filters.maxCookTime!
      );
    }

    if (searchState.filters.minRating) {
      filtered = filtered.filter(recipe =>
        recipe.rating >= searchState.filters.minRating!
      );
    }

    filtered.sort((a, b) => {
      const { sortBy, sortOrder } = searchState.filters;
      let aValue: string | number;
      let bValue: string | number;

      switch (sortBy) {
        case 'name':
          aValue = a.name.toLowerCase();
          bValue = b.name.toLowerCase();
          break;
        case 'rating':
          aValue = a.rating || 0;
          bValue = b.rating || 0;
          break;
        case 'cook_time_minutes':
          aValue = a.cook_time_minutes || 0;
          bValue = b.cook_time_minutes || 0;
          break;
        case 'prep_time_minutes':
          aValue = a.prep_time_minutes || 0;
          bValue = b.prep_time_minutes || 0;
          break;
        default:
          aValue = a.name.toLowerCase();
          bValue = b.name.toLowerCase();
      }

      if (typeof aValue === 'string' && typeof bValue === 'string') {
        return sortOrder === 'asc' 
          ? aValue.localeCompare(bValue)
          : bValue.localeCompare(aValue);
      }

      return sortOrder === 'asc' 
        ? (aValue as number) - (bValue as number)
        : (bValue as number) - (aValue as number);
    });

    return filtered;
  }, [searchState.recipes, searchState.filters]);

  useEffect(() => {
    if (autoSearch && debouncedQuery !== searchState.query) {
      setSearchState(prev => ({ ...prev, query: debouncedQuery }));
    }
  }, [debouncedQuery, autoSearch, searchState.query]);

  useEffect(() => {
    if (autoSearch) {
      searchRecipes(debouncedQuery, 0);
    }
  }, [debouncedQuery, autoSearch, searchRecipes]);

  useEffect(() => {
    if (searchState.query && (
      searchState.filters.sortBy !== initialFilters.sortBy ||
      searchState.filters.sortOrder !== initialFilters.sortOrder
    )) {
      searchRecipes(searchState.query, 0);
    }
  }, [searchState.filters.sortBy, searchState.filters.sortOrder, searchState.query, searchRecipes]);

  return {
    searchState: {
      ...searchState,
      recipes: getFilteredRecipes(),
    },
    searchRecipes,
    setFilters,
    clearSearch,
    loadMore,
    setQuery,
  };
}

export default useRecipeSearch;