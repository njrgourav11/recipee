/**
 * API service for communicating with the Recipe Management System backend.
 * 
 * This service provides a centralized way to make HTTP requests to the backend
 * with proper error handling, request/response interceptors, and TypeScript support.
 */

import axios from 'axios';
import type { AxiosInstance, AxiosResponse, AxiosError } from 'axios';
import type {
  Recipe,
  SearchResponse,
  SearchParams,
  LoadRecipesResponse,
  DataLoadStatus,
  RecipeStatistics,
  ApiError,
} from '../types/recipe';

// API Configuration
const API_CONFIG = {
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
};

// Create axios instance
const apiClient: AxiosInstance = axios.create(API_CONFIG);

// Request interceptor for logging and adding correlation IDs
apiClient.interceptors.request.use(
  (config) => {
    // Add correlation ID for request tracking
    const correlationId = `req-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
    config.headers['X-Correlation-ID'] = correlationId;
    
    console.log(`[API Request] ${config.method?.toUpperCase()} ${config.url}`, {
      correlationId,
      params: config.params,
      data: config.data,
    });
    
    return config;
  },
  (error) => {
    console.error('[API Request Error]', error);
    return Promise.reject(error);
  }
);

// Response interceptor for logging and error handling
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    const correlationId = response.config.headers['X-Correlation-ID'];
    console.log(`[API Response] ${response.status} ${response.config.url}`, {
      correlationId,
      data: response.data,
    });
    return response;
  },
  (error: AxiosError<ApiError>) => {
    const correlationId = error.config?.headers['X-Correlation-ID'];
    console.error(`[API Error] ${error.response?.status || 'Network Error'}`, {
      correlationId,
      url: error.config?.url,
      message: error.message,
      response: error.response?.data,
    });
    
    // Transform axios error to our ApiError format
    if (error.response?.data) {
      return Promise.reject(error.response.data);
    }
    
    // Network or other errors
    const apiError: ApiError = {
      timestamp: new Date().toISOString(),
      status: error.response?.status || 0,
      error: error.code || 'Network Error',
      message: error.message || 'An unexpected error occurred',
      path: error.config?.url || '',
    };
    
    return Promise.reject(apiError);
  }
);

/**
 * Recipe API service class
 */
export class RecipeApiService {
  /**
   * Search recipes with optional query and pagination
   */
  static async searchRecipes(params: SearchParams = {}): Promise<SearchResponse> {
    const response = await apiClient.get<SearchResponse>('/recipes/search', { params });
    return response.data;
  }

  /**
   * Get a specific recipe by ID
   */
  static async getRecipeById(id: number): Promise<Recipe> {
    const response = await apiClient.get<Recipe>(`/recipes/${id}`);
    return response.data;
  }

  /**
   * Get recipes by cuisine
   */
  static async getRecipesByCuisine(
    cuisine: string,
    page = 0,
    size = 20
  ): Promise<SearchResponse> {
    const response = await apiClient.get<SearchResponse>(`/recipes/cuisine/${cuisine}`, {
      params: { page, size },
    });
    return response.data;
  }

  /**
   * Get top-rated recipes
   */
  static async getTopRatedRecipes(page = 0, size = 20): Promise<SearchResponse> {
    const response = await apiClient.get<SearchResponse>('/recipes/top-rated', {
      params: { page, size },
    });
    return response.data;
  }

  /**
   * Get search suggestions based on partial input
   */
  static async getSearchSuggestions(query: string, limit = 10): Promise<string[]> {
    const response = await apiClient.get<string[]>('/recipes/suggestions', {
      params: { q: query, limit },
    });
    return response.data;
  }

  /**
   * Get recipe statistics
   */
  static async getRecipeStatistics(): Promise<RecipeStatistics> {
    const response = await apiClient.get<RecipeStatistics>('/recipes/statistics');
    return response.data;
  }

  /**
   * Load recipes from external API
   */
  static async loadRecipes(): Promise<LoadRecipesResponse> {
    const response = await apiClient.post<LoadRecipesResponse>('/recipes/load');
    return response.data;
  }

  /**
   * Get data loading status
   */
  static async getLoadingStatus(): Promise<DataLoadStatus> {
    const response = await apiClient.get<DataLoadStatus>('/recipes/load/status');
    return response.data;
  }

  /**
   * Check if the API is healthy
   */
  static async healthCheck(): Promise<boolean> {
    try {
      await apiClient.get('/recipes/statistics');
      return true;
    } catch (error) {
      console.warn('[Health Check] API is not responding:', error);
      return false;
    }
  }
}

/**
 * Utility functions for API operations
 */
export class ApiUtils {
  /**
   * Build search parameters object
   */
  static buildSearchParams(
    query?: string,
    page = 0,
    size = 20,
    sort = 'name',
    direction: 'asc' | 'desc' = 'asc'
  ): SearchParams {
    const params: SearchParams = { page, size, sort, direction };
    if (query && query.trim()) {
      params.q = query.trim();
    }
    return params;
  }

  /**
   * Extract error message from API error
   */
  static getErrorMessage(error: unknown): string {
    if (typeof error === 'string') {
      return error;
    }
    
    if (error && typeof error === 'object' && 'message' in error) {
      return (error as ApiError).message;
    }
    
    return 'An unexpected error occurred';
  }

  /**
   * Check if error is a network error
   */
  static isNetworkError(error: unknown): boolean {
    if (error && typeof error === 'object' && 'status' in error) {
      return (error as ApiError).status === 0;
    }
    return false;
  }

  /**
   * Check if error is a client error (4xx)
   */
  static isClientError(error: unknown): boolean {
    if (error && typeof error === 'object' && 'status' in error) {
      const status = (error as ApiError).status;
      return status >= 400 && status < 500;
    }
    return false;
  }

  /**
   * Check if error is a server error (5xx)
   */
  static isServerError(error: unknown): boolean {
    if (error && typeof error === 'object' && 'status' in error) {
      const status = (error as ApiError).status;
      return status >= 500 && status < 600;
    }
    return false;
  }

  /**
   * Retry function with exponential backoff
   */
  static async retry<T>(
    fn: () => Promise<T>,
    maxAttempts = 3,
    baseDelay = 1000
  ): Promise<T> {
    let lastError: unknown;
    
    for (let attempt = 1; attempt <= maxAttempts; attempt++) {
      try {
        return await fn();
      } catch (error) {
        lastError = error;
        
        if (attempt === maxAttempts) {
          break;
        }
        
        // Only retry on network errors or server errors
        if (!this.isNetworkError(error) && !this.isServerError(error)) {
          break;
        }
        
        // Exponential backoff
        const delay = baseDelay * Math.pow(2, attempt - 1);
        console.log(`[API Retry] Attempt ${attempt} failed, retrying in ${delay}ms...`);
        await new Promise(resolve => setTimeout(resolve, delay));
      }
    }
    
    throw lastError;
  }
}

/**
 * Default export for convenience
 */
export default RecipeApiService;

/**
 * Export the configured axios instance for advanced usage
 */
export { apiClient };