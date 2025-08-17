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

const API_CONFIG = {
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
};

const apiClient: AxiosInstance = axios.create(API_CONFIG);

apiClient.interceptors.request.use(
  (config) => {
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
    
    if (error.response?.data) {
      return Promise.reject(error.response.data);
    }
    
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

export class RecipeApiService {
  static async searchRecipes(params: SearchParams = {}): Promise<SearchResponse> {
    const response = await apiClient.get<SearchResponse>('/recipes/search', { params });
    return response.data;
  }

  static async getRecipeById(id: number): Promise<Recipe> {
    const response = await apiClient.get<Recipe>(`/recipes/${id}`);
    return response.data;
  }

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

  static async getTopRatedRecipes(page = 0, size = 20): Promise<SearchResponse> {
    const response = await apiClient.get<SearchResponse>('/recipes/top-rated', {
      params: { page, size },
    });
    return response.data;
  }

  static async getSearchSuggestions(query: string, limit = 10): Promise<string[]> {
    const response = await apiClient.get<string[]>('/recipes/suggestions', {
      params: { q: query, limit },
    });
    return response.data;
  }

  static async getRecipeStatistics(): Promise<RecipeStatistics> {
    const response = await apiClient.get<RecipeStatistics>('/recipes/statistics');
    return response.data;
  }

  static async loadRecipes(): Promise<LoadRecipesResponse> {
    const response = await apiClient.post<LoadRecipesResponse>('/recipes/load');
    return response.data;
  }

  static async getLoadingStatus(): Promise<DataLoadStatus> {
    const response = await apiClient.get<DataLoadStatus>('/recipes/load/status');
    return response.data;
  }

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

export class ApiUtils {
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

  static getErrorMessage(error: unknown): string {
    if (typeof error === 'string') {
      return error;
    }
    
    if (error && typeof error === 'object' && 'message' in error) {
      return (error as ApiError).message;
    }
    
    return 'An unexpected error occurred';
  }

  static isNetworkError(error: unknown): boolean {
    if (error && typeof error === 'object' && 'status' in error) {
      return (error as ApiError).status === 0;
    }
    return false;
  }

  static isClientError(error: unknown): boolean {
    if (error && typeof error === 'object' && 'status' in error) {
      const status = (error as ApiError).status;
      return status >= 400 && status < 500;
    }
    return false;
  }

  static isServerError(error: unknown): boolean {
    if (error && typeof error === 'object' && 'status' in error) {
      const status = (error as ApiError).status;
      return status >= 500 && status < 600;
    }
    return false;
  }

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
        
        if (!this.isNetworkError(error) && !this.isServerError(error)) {
          break;
        }
        
        const delay = baseDelay * Math.pow(2, attempt - 1);
        console.log(`[API Retry] Attempt ${attempt} failed, retrying in ${delay}ms...`);
        await new Promise(resolve => setTimeout(resolve, delay));
      }
    }
    
    throw lastError;
  }
}

export default RecipeApiService;

export { apiClient };