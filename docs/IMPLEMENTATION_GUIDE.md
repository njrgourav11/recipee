# Implementation Guide - Recipe Management System

## Development Phases

### Phase 1: Project Setup and Infrastructure

#### 1.1 Project Structure Setup
```
publicis/
├── backend/
├── frontend/
├── docs/
├── scripts/
└── README.md
```

#### 1.2 Backend Project Initialization
- Create Spring Boot project using Spring Initializr
- Configure Maven with required dependencies
- Set up basic project structure
- Configure application properties

#### 1.3 Frontend Project Initialization
- Create React TypeScript project using Vite
- Configure TypeScript and ESLint
- Set up SCSS and CSS Modules
- Configure testing environment

### Phase 2: Backend Development

#### 2.1 Core Configuration
```java
// HibernateSearchConfig.java
@Configuration
@EnableJpaRepositories
public class HibernateSearchConfig {
    
    @Bean
    public SearchMapping searchMapping(EntityManagerFactory entityManagerFactory) {
        SearchMapping mapping = Search.mapping(entityManagerFactory);
        return mapping;
    }
}

// WebConfig.java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}
```

#### 2.2 Entity Model Implementation
```java
@Entity
@Table(name = "recipes")
@Indexed
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @FullTextField(analyzer = "standard")
    @Column(nullable = false)
    private String name;
    
    @FullTextField(analyzer = "standard")
    @Column(nullable = false)
    private String cuisine;
    
    // Additional fields as per specification
    // Constructors, getters, setters, equals, hashCode
}
```

#### 2.3 Repository Layer
```java
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    
    @Query("SELECT r FROM Recipe r WHERE " +
           "LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.cuisine) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Recipe> findByNameOrCuisineContainingIgnoreCase(
        @Param("query") String query, Pageable pageable);
}

@Component
public class RecipeSearchRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public Page<Recipe> searchRecipes(String query, Pageable pageable) {
        SearchSession searchSession = Search.session(entityManager);
        
        SearchResult<Recipe> result = searchSession.search(Recipe.class)
            .where(f -> f.match()
                .fields("name", "cuisine")
                .matching(query))
            .fetch((int) pageable.getOffset(), pageable.getPageSize());
            
        List<Recipe> recipes = result.hits();
        long totalHits = result.total().hitCount();
        
        return new PageImpl<>(recipes, pageable, totalHits);
    }
}
```

#### 2.4 Service Layer Implementation
```java
@Service
@Transactional
public class RecipeService {
    
    private final RecipeRepository recipeRepository;
    private final RecipeSearchRepository searchRepository;
    private final ExternalRecipeService externalService;
    
    public RecipeService(RecipeRepository recipeRepository,
                        RecipeSearchRepository searchRepository,
                        ExternalRecipeService externalService) {
        this.recipeRepository = recipeRepository;
        this.searchRepository = searchRepository;
        this.externalService = externalService;
    }
    
    public Page<Recipe> searchRecipes(String query, Pageable pageable) {
        if (StringUtils.hasText(query)) {
            return searchRepository.searchRecipes(query, pageable);
        }
        return recipeRepository.findAll(pageable);
    }
    
    public Optional<Recipe> findById(Long id) {
        return recipeRepository.findById(id);
    }
    
    @Async
    public CompletableFuture<Integer> loadRecipesFromExternalApi() {
        List<Recipe> recipes = externalService.fetchAllRecipes();
        List<Recipe> savedRecipes = recipeRepository.saveAll(recipes);
        return CompletableFuture.completedFuture(savedRecipes.size());
    }
}
```

#### 2.5 External API Service
```java
@Service
public class ExternalRecipeService {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${external.api.recipes.url}")
    private String recipesApiUrl;
    
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public List<Recipe> fetchAllRecipes() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(recipesApiUrl, String.class);
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode recipesNode = rootNode.get("recipes");
            
            List<Recipe> recipes = new ArrayList<>();
            for (JsonNode recipeNode : recipesNode) {
                Recipe recipe = mapToRecipe(recipeNode);
                recipes.add(recipe);
            }
            
            return recipes;
        } catch (Exception e) {
            log.error("Failed to fetch recipes from external API", e);
            throw new ExternalApiException("Failed to fetch recipes", e);
        }
    }
    
    private Recipe mapToRecipe(JsonNode node) {
        // Map JSON node to Recipe entity
        Recipe recipe = new Recipe();
        recipe.setName(node.get("name").asText());
        recipe.setCuisine(node.get("cuisine").asText());
        // Map other fields
        return recipe;
    }
}
```

#### 2.6 REST Controllers
```java
@RestController
@RequestMapping("/api/recipes")
@Validated
public class RecipeController {
    
    private final RecipeService recipeService;
    
    @GetMapping("/search")
    public ResponseEntity<Page<Recipe>> searchRecipes(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipes = recipeService.searchRecipes(q, pageable);
        return ResponseEntity.ok(recipes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        return recipeService.findById(id)
            .map(recipe -> ResponseEntity.ok(recipe))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/load")
    public ResponseEntity<Map<String, Object>> loadRecipes() {
        CompletableFuture<Integer> future = recipeService.loadRecipesFromExternalApi();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Recipe loading initiated");
        response.put("timestamp", Instant.now());
        
        return ResponseEntity.accepted().body(response);
    }
}
```

### Phase 3: Frontend Development

#### 3.1 Project Structure Setup
```
src/
├── components/
│   ├── atoms/
│   │   ├── Button/
│   │   ├── Input/
│   │   └── Loading/
│   ├── molecules/
│   │   ├── SearchBar/
│   │   ├── RecipeCard/
│   │   └── FilterPanel/
│   ├── organisms/
│   │   ├── RecipeGrid/
│   │   └── Header/
│   └── templates/
│       └── SearchPage/
├── services/
├── hooks/
├── types/
├── utils/
└── styles/
```

#### 3.2 API Service Layer
```typescript
// services/api.ts
import axios, { AxiosResponse } from 'axios';
import { Recipe, SearchResponse } from '../types/recipe';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for logging
apiClient.interceptors.request.use(
  (config) => {
    console.log(`API Request: ${config.method?.toUpperCase()} ${config.url}`);
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export const recipeApi = {
  searchRecipes: async (query: string, page = 0, size = 20): Promise<SearchResponse> => {
    const response: AxiosResponse<SearchResponse> = await apiClient.get('/recipes/search', {
      params: { q: query, page, size },
    });
    return response.data;
  },

  getRecipeById: async (id: number): Promise<Recipe> => {
    const response: AxiosResponse<Recipe> = await apiClient.get(`/recipes/${id}`);
    return response.data;
  },

  loadRecipes: async (): Promise<void> => {
    await apiClient.post('/recipes/load');
  },
};
```

#### 3.3 Custom Hooks
```typescript
// hooks/useDebounce.ts
import { useState, useEffect } from 'react';

export function useDebounce<T>(value: T, delay: number): T {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  return debouncedValue;
}

// hooks/useRecipeSearch.ts
import { useState, useEffect, useCallback } from 'react';
import { Recipe, SearchResponse } from '../types/recipe';
import { recipeApi } from '../services/api';
import { useDebounce } from './useDebounce';

export function useRecipeSearch() {
  const [query, setQuery] = useState('');
  const [recipes, setRecipes] = useState<Recipe[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [totalPages, setTotalPages] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);

  const debouncedQuery = useDebounce(query, 300);

  const searchRecipes = useCallback(async (searchQuery: string, page = 0) => {
    if (searchQuery.length < 3) {
      setRecipes([]);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response: SearchResponse = await recipeApi.searchRecipes(searchQuery, page);
      setRecipes(page === 0 ? response.content : [...recipes, ...response.content]);
      setTotalPages(response.totalPages);
      setCurrentPage(page);
    } catch (err) {
      setError('Failed to search recipes. Please try again.');
      console.error('Search error:', err);
    } finally {
      setLoading(false);
    }
  }, [recipes]);

  useEffect(() => {
    if (debouncedQuery) {
      searchRecipes(debouncedQuery, 0);
    }
  }, [debouncedQuery, searchRecipes]);

  return {
    query,
    setQuery,
    recipes,
    loading,
    error,
    totalPages,
    currentPage,
    searchRecipes,
  };
}
```

#### 3.4 Component Implementation
```typescript
// components/molecules/SearchBar/SearchBar.tsx
import React from 'react';
import { Input } from '../../atoms/Input/Input';
import { Button } from '../../atoms/Button/Button';
import styles from './SearchBar.module.scss';

interface SearchBarProps {
  value: string;
  onChange: (value: string) => void;
  onSearch: () => void;
  placeholder?: string;
  loading?: boolean;
}

export const SearchBar: React.FC<SearchBarProps> = ({
  value,
  onChange,
  onSearch,
  placeholder = "Search recipes by name or cuisine...",
  loading = false,
}) => {
  const handleKeyPress = (event: React.KeyboardEvent) => {
    if (event.key === 'Enter') {
      onSearch();
    }
  };

  return (
    <div className={styles.searchBar}>
      <Input
        value={value}
        onChange={onChange}
        onKeyPress={handleKeyPress}
        placeholder={placeholder}
        disabled={loading}
        className={styles.searchInput}
      />
      <Button
        onClick={onSearch}
        disabled={loading || value.length < 3}
        className={styles.searchButton}
      >
        {loading ? 'Searching...' : 'Search'}
      </Button>
    </div>
  );
};
```

### Phase 4: Integration and Testing

#### 4.1 Backend Testing Strategy
```java
// RecipeServiceTest.java
@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {
    
    @Mock
    private RecipeRepository recipeRepository;
    
    @Mock
    private RecipeSearchRepository searchRepository;
    
    @InjectMocks
    private RecipeService recipeService;
    
    @Test
    void searchRecipes_WithQuery_ShouldReturnSearchResults() {
        // Given
        String query = "pizza";
        Pageable pageable = PageRequest.of(0, 20);
        List<Recipe> recipes = Arrays.asList(createTestRecipe());
        Page<Recipe> expectedPage = new PageImpl<>(recipes, pageable, 1);
        
        when(searchRepository.searchRecipes(query, pageable)).thenReturn(expectedPage);
        
        // When
        Page<Recipe> result = recipeService.searchRecipes(query, pageable);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(searchRepository).searchRecipes(query, pageable);
    }
}
```

#### 4.2 Frontend Testing Strategy
```typescript
// components/molecules/SearchBar/SearchBar.test.tsx
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { SearchBar } from './SearchBar';

describe('SearchBar', () => {
  const mockOnChange = jest.fn();
  const mockOnSearch = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render search input and button', () => {
    render(
      <SearchBar
        value=""
        onChange={mockOnChange}
        onSearch={mockOnSearch}
      />
    );

    expect(screen.getByPlaceholderText(/search recipes/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /search/i })).toBeInTheDocument();
  });

  it('should call onSearch when Enter key is pressed', async () => {
    render(
      <SearchBar
        value="pizza"
        onChange={mockOnChange}
        onSearch={mockOnSearch}
      />
    );

    const input = screen.getByPlaceholderText(/search recipes/i);
    fireEvent.keyPress(input, { key: 'Enter', code: 'Enter' });

    await waitFor(() => {
      expect(mockOnSearch).toHaveBeenCalledTimes(1);
    });
  });
});
```

### Phase 5: Performance Optimization

#### 5.1 Backend Optimizations
- Database connection pooling configuration
- Hibernate Search index optimization
- Caching strategy for external API calls
- Async processing for data loading

#### 5.2 Frontend Optimizations
- Code splitting with React.lazy()
- Image lazy loading with Intersection Observer
- Virtual scrolling for large datasets
- Memoization of expensive computations

### Phase 6: Documentation and Deployment

#### 6.1 API Documentation
- Swagger/OpenAPI configuration
- Interactive API documentation
- Example requests and responses

#### 6.2 Deployment Configuration
- Docker containerization
- Environment-specific configurations
- CI/CD pipeline setup
- Production deployment scripts

This implementation guide provides a comprehensive roadmap for developing the recipe management system with all required features and quality standards.