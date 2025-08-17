import { renderHook, act } from '@testing-library/react'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { useRecipeSearch } from './useRecipeSearch'
import * as apiService from '../services/api'

// Mock the API service
vi.mock('../services/api', () => ({
  RecipeApiService: {
    searchRecipes: vi.fn(),
  },
  ApiUtils: {
    buildSearchParams: vi.fn(),
    getErrorMessage: vi.fn(),
  },
}))

// Mock the useDebounce hook
vi.mock('./useDebounce', () => ({
  useDebounce: vi.fn((value) => ({ debouncedValue: value })),
}))

const mockSearchResponse = {
  content: [
    {
      id: 1,
      name: 'Test Recipe',
      cuisine: 'Italian',
      difficulty: 'Medium' as const,
      rating: 4.5,
      cook_time_minutes: 30,
      prep_time_minutes: 15,
      total_time_minutes: 45,
      servings: 4,
      ingredients: ['ingredient1'],
      instructions: ['step1'],
      tags: ['tag1'],
      image: 'image.jpg',
      review_count: 10,
      calories_per_serving: 250,
      created_at: '2024-01-01T00:00:00Z',
      updated_at: '2024-01-01T00:00:00Z'
    }
  ],
  pageable: {
    pageNumber: 0,
    pageSize: 20,
    sort: {
      sorted: false,
      unsorted: true,
    },
  },
  totalPages: 1,
  totalElements: 1,
  first: true,
  last: true,
}

describe('useRecipeSearch', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    vi.mocked(apiService.ApiUtils.buildSearchParams).mockReturnValue({})
    vi.mocked(apiService.RecipeApiService.searchRecipes).mockResolvedValue(mockSearchResponse)
  })

  it('initializes with default state', () => {
    const { result } = renderHook(() => useRecipeSearch('', false))
    
    expect(result.current.searchState.query).toBe('')
    expect(result.current.searchState.recipes).toEqual([])
    expect(result.current.searchState.loading).toBe(false)
    expect(result.current.searchState.error).toBe(null)
    expect(result.current.searchState.totalPages).toBe(0)
    expect(result.current.searchState.currentPage).toBe(0)
    expect(result.current.searchState.totalElements).toBe(0)
  })

  it('initializes with provided query', () => {
    const { result } = renderHook(() => useRecipeSearch('pizza', false))
    
    expect(result.current.searchState.query).toBe('pizza')
  })

  it('searches recipes successfully', async () => {
    const { result } = renderHook(() => useRecipeSearch('', false))
    
    await act(async () => {
      await result.current.searchRecipes('pizza', 0)
    })
    
    expect(result.current.searchState.recipes).toEqual(mockSearchResponse.content)
    expect(result.current.searchState.totalPages).toBe(1)
    expect(result.current.searchState.totalElements).toBe(1)
    expect(result.current.searchState.loading).toBe(false)
    expect(result.current.searchState.error).toBe(null)
  })

  it('handles search error', async () => {
    const errorMessage = 'Network error'
    vi.mocked(apiService.RecipeApiService.searchRecipes).mockRejectedValue(new Error(errorMessage))
    vi.mocked(apiService.ApiUtils.getErrorMessage).mockReturnValue(errorMessage)
    
    const { result } = renderHook(() => useRecipeSearch('', false))
    
    await act(async () => {
      await result.current.searchRecipes('pizza', 0)
    })
    
    expect(result.current.searchState.error).toBe(errorMessage)
    expect(result.current.searchState.loading).toBe(false)
    expect(result.current.searchState.recipes).toEqual([])
  })

  it('validates minimum query length', async () => {
    const { result } = renderHook(() => useRecipeSearch('', false))
    
    await act(async () => {
      await result.current.searchRecipes('ab', 0)
    })
    
    expect(result.current.searchState.error).toBe('Please enter at least 3 characters to search')
    expect(result.current.searchState.recipes).toEqual([])
    expect(apiService.RecipeApiService.searchRecipes).not.toHaveBeenCalled()
  })

  it('allows empty query for search all', async () => {
    const { result } = renderHook(() => useRecipeSearch('', false))
    
    await act(async () => {
      await result.current.searchRecipes('', 0)
    })
    
    expect(apiService.RecipeApiService.searchRecipes).toHaveBeenCalled()
    expect(result.current.searchState.recipes).toEqual(mockSearchResponse.content)
  })

  it('sets query correctly', () => {
    const { result } = renderHook(() => useRecipeSearch('', false))
    
    act(() => {
      result.current.setQuery('new query')
    })
    
    expect(result.current.searchState.query).toBe('new query')
  })

  it('clears search correctly', () => {
    const { result } = renderHook(() => useRecipeSearch('pizza', false))
    
    // First set some state
    act(() => {
      result.current.setQuery('pizza')
    })
    
    // Then clear
    act(() => {
      result.current.clearSearch()
    })
    
    expect(result.current.searchState.query).toBe('')
    expect(result.current.searchState.recipes).toEqual([])
    expect(result.current.searchState.error).toBe(null)
    expect(result.current.searchState.loading).toBe(false)
  })

  it('updates filters correctly', () => {
    const { result } = renderHook(() => useRecipeSearch('', false))
    
    act(() => {
      result.current.setFilters({ sortBy: 'rating', sortOrder: 'desc' })
    })
    
    expect(result.current.searchState.filters.sortBy).toBe('rating')
    expect(result.current.searchState.filters.sortOrder).toBe('desc')
  })


  it('does not load more when already loading', async () => {
    const { result } = renderHook(() => useRecipeSearch('', false))
    
    // Set loading state
    await act(async () => {
      result.current.searchRecipes('pizza', 0)
      // Call loadMore while still loading
      await result.current.loadMore()
    })
    
    // Should only be called once for the initial search
    expect(apiService.RecipeApiService.searchRecipes).toHaveBeenCalledTimes(1)
  })

  it('does not load more when on last page', async () => {
    const { result } = renderHook(() => useRecipeSearch('', false))
    
    // Set state where we're on the last page
    await act(async () => {
      await result.current.searchRecipes('pizza', 0)
    })
    
    // Try to load more when already on last page
    await act(async () => {
      await result.current.loadMore()
    })
    
    // Should only be called once for the initial search
    expect(apiService.RecipeApiService.searchRecipes).toHaveBeenCalledTimes(1)
  })
})