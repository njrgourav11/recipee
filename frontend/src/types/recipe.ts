export interface Recipe {
  id: number;
  name: string;
  cuisine: string;
  difficulty: 'Easy' | 'Medium' | 'Hard';
  prep_time_minutes: number;
  cook_time_minutes: number;
  servings: number;
  ingredients: string[];
  instructions: string[];
  tags: string[];
  image: string;
  rating: number;
  review_count: number;
  calories_per_serving: number;
  total_time_minutes: number;
  created_at: string;
  updated_at: string;
}

export interface SearchResponse {
  content: Recipe[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      sorted: boolean;
      unsorted: boolean;
    };
  };
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  validationErrors?: Record<string, string>;
}

export interface LoadingState {
  isLoading: boolean;
  error: string | null;
}

export interface SearchFilters {
  sortBy: 'name' | 'rating' | 'cook_time_minutes' | 'prep_time_minutes';
  sortOrder: 'asc' | 'desc';
  tags: string[];
  cuisine?: string;
  difficulty?: 'Easy' | 'Medium' | 'Hard';
  maxCookTime?: number;
  minRating?: number;
}

export interface SearchState {
  query: string;
  recipes: Recipe[];
  loading: boolean;
  error: string | null;
  totalPages: number;
  currentPage: number;
  totalElements: number;
  filters: SearchFilters;
}

export interface DataLoadStatus {
  isLoading: boolean;
  lastLoadTime: string | null;
  lastLoadCount: number;
  lastLoadStatus: string;
  totalRecipesInDatabase: number;
}

export interface RecipeStatistics {
  totalRecipes: number;
  cuisineDistribution: Record<string, number>;
  difficultyDistribution: Record<string, number>;
  searchIndexReady: boolean;
}

export interface RecipeCardProps {
  recipe: Recipe;
  onClick?: (recipe: Recipe) => void;
  className?: string;
}

export interface SearchBarProps {
  value: string;
  onChange: (value: string) => void;
  onSearch: () => void;
  onClear?: () => void;
  placeholder?: string;
  loading?: boolean;
  className?: string;
}

export interface FilterPanelProps {
  filters: SearchFilters;
  onFiltersChange: (filters: SearchFilters) => void;
  availableTags: string[];
  availableCuisines: string[];
  className?: string;
}

export interface RecipeGridProps {
  recipes: Recipe[];
  loading?: boolean;
  error?: string | null;
  onRecipeClick?: (recipe: Recipe) => void;
  onLoadMore?: () => void;
  hasMore?: boolean;
  className?: string;
}

export interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  className?: string;
}

export interface UseRecipeSearchReturn {
  searchState: SearchState;
  searchRecipes: (query: string, page?: number) => Promise<void>;
  setFilters: (filters: Partial<SearchFilters>) => void;
  clearSearch: () => void;
  loadMore: () => Promise<void>;
  setQuery: (query: string) => void;
}

export interface UseDebounceReturn<T> {
  debouncedValue: T;
  isDebouncing: boolean;
}

export interface UseLocalStorageReturn<T> {
  value: T;
  setValue: (value: T) => void;
  removeValue: () => void;
}

export interface ApiConfig {
  baseURL: string;
  timeout: number;
  headers?: Record<string, string>;
}

export interface SearchParams {
  q?: string;
  page?: number;
  size?: number;
  sort?: string;
  direction?: 'asc' | 'desc';
}

export interface LoadRecipesResponse {
  message: string;
  timestamp: string;
  status?: string;
  count?: number;
}

export type RecipeSortField = keyof Pick<Recipe, 'name' | 'rating' | 'cook_time_minutes' | 'prep_time_minutes'>;
export type SortDirection = 'asc' | 'desc';
export type DifficultyLevel = Recipe['difficulty'];

export interface SearchFormData {
  query: string;
  filters: SearchFilters;
}

export interface RecipeContextValue {
  searchState: SearchState;
  searchRecipes: (query: string, page?: number) => Promise<void>;
  setFilters: (filters: Partial<SearchFilters>) => void;
  clearSearch: () => void;
  loadRecipeData: () => Promise<void>;
  getRecipeById: (id: number) => Promise<Recipe | null>;
}

export interface ErrorBoundaryState {
  hasError: boolean;
  error: Error | null;
  errorInfo: string | null;
}

export interface Theme {
  colors: {
    primary: string;
    secondary: string;
    accent: string;
    background: string;
    surface: string;
    text: string;
    textSecondary: string;
    border: string;
    error: string;
    warning: string;
    success: string;
  };
  spacing: {
    xs: string;
    sm: string;
    md: string;
    lg: string;
    xl: string;
  };
  breakpoints: {
    mobile: string;
    tablet: string;
    desktop: string;
    wide: string;
  };
  typography: {
    fontFamily: string;
    fontSize: {
      xs: string;
      sm: string;
      md: string;
      lg: string;
      xl: string;
      xxl: string;
    };
    fontWeight: {
      normal: number;
      medium: number;
      bold: number;
    };
  };
}