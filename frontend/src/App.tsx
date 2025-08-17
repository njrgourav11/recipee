import { useState, useEffect, useRef } from 'react';
import { SearchBar } from './components/molecules/SearchBar/SearchBar';
import { RecipeCard } from './components/molecules/RecipeCard/RecipeCard';
import { RecipeListItem } from './components/molecules/RecipeListItem/RecipeListItem';
import { Loading } from './components/atoms/Loading/Loading';
import { useRecipeSearch } from './hooks/useRecipeSearch';
import './App.scss';

function App() {
  const [query, setQuery] = useState('');
  const [isVisible, setIsVisible] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);
  const [recentSearches, setRecentSearches] = useState<string[]>([]);
  const [sortBy, setSortBy] = useState<'name' | 'cook_time_minutes' | 'rating'>('name');
  const [sortOrder, setSortOrder] = useState<'asc' | 'desc'>('asc');
  const [selectedTags, setSelectedTags] = useState<string[]>([]);
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid');
  const { searchState, searchRecipes, setQuery: setSearchQuery, clearSearch } = useRecipeSearch('', false);
  const searchTimeoutRef = useRef<NodeJS.Timeout | null>(null);

  // Load recent searches from localStorage on mount
  useEffect(() => {
    const savedSearches = localStorage.getItem('recentSearches');
    if (savedSearches) {
      try {
        const parsed = JSON.parse(savedSearches);
        setRecentSearches(Array.isArray(parsed) ? parsed : []);
      } catch (error) {
        console.error('Error parsing recent searches:', error);
      }
    }
  }, []);

  // Add entrance animation
  useEffect(() => {
    setIsVisible(true);
  }, []);

  // Cleanup timeout on unmount
  useEffect(() => {
    return () => {
      if (searchTimeoutRef.current) {
        clearTimeout(searchTimeoutRef.current);
      }
    };
  }, []);

  // Function to add a search to recent searches
  const addToRecentSearches = (searchQuery: string) => {
    if (!searchQuery || searchQuery.length < 3) return;
    
    const trimmedQuery = searchQuery.trim().toLowerCase();
    setRecentSearches(prev => {
      // Remove if already exists and add to front
      const filtered = prev.filter(search => search.toLowerCase() !== trimmedQuery);
      const updated = [searchQuery.trim(), ...filtered].slice(0, 5); // Keep only 5 recent searches
      
      // Save to localStorage
      localStorage.setItem('recentSearches', JSON.stringify(updated));
      return updated;
    });
  };

  const handleSearch = () => {
    if (query.length >= 3) {
      setHasSearched(true); // Mark that user has searched
      addToRecentSearches(query); // Add to recent searches
      setSearchQuery(query);
      searchRecipes(query, 0);
    }
  };

  const handleQueryChange = (newQuery: string) => {
    setQuery(newQuery);
    setSearchQuery(newQuery);
    
    // Clear existing timeout
    if (searchTimeoutRef.current) {
      clearTimeout(searchTimeoutRef.current);
    }
    
    // Debounced auto-search
    if (newQuery.length >= 3) {
      setHasSearched(true); // Mark that user has searched
      searchTimeoutRef.current = setTimeout(() => {
        addToRecentSearches(newQuery); // Add to recent searches
        searchRecipes(newQuery, 0);
      }, 500); // 500ms delay
    } else if (newQuery.length === 0) {
      // Clear results immediately when search is empty
      clearSearch();
    }
  };

  const handleClearSearch = () => {
    setQuery('');
    setSearchQuery('');
    clearSearch();
  };

  const clearRecentSearches = () => {
    setRecentSearches([]);
    localStorage.removeItem('recentSearches');
  };

  const handleRecipeClick = (recipe: any) => {
    // Future: Open recipe detail modal or navigate to detail page
    console.log('Recipe clicked:', recipe);
  };

  // Get all unique tags from current recipes
  const getAllTags = () => {
    const allTags = searchState.recipes.flatMap(recipe => recipe.tags);
    return [...new Set(allTags)].sort();
  };

  // Filter and sort recipes client-side
  const getFilteredAndSortedRecipes = () => {
    let filtered = [...searchState.recipes];

    // Filter by selected tags
    if (selectedTags.length > 0) {
      filtered = filtered.filter(recipe =>
        selectedTags.some(tag => recipe.tags.includes(tag))
      );
    }

    // Sort recipes
    filtered.sort((a, b) => {
      let aValue: string | number;
      let bValue: string | number;

      switch (sortBy) {
        case 'name':
          aValue = a.name.toLowerCase();
          bValue = b.name.toLowerCase();
          break;
        case 'cook_time_minutes':
          aValue = a.cook_time_minutes || 0;
          bValue = b.cook_time_minutes || 0;
          break;
        case 'rating':
          aValue = a.rating || 0;
          bValue = b.rating || 0;
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
  };

  // Handle tag filter toggle
  const toggleTagFilter = (tag: string) => {
    setSelectedTags(prev => 
      prev.includes(tag) 
        ? prev.filter(t => t !== tag)
        : [...prev, tag]
    );
  };

  // Clear all filters
  const clearFilters = () => {
    setSelectedTags([]);
    setSortBy('name');
    setSortOrder('asc');
  };

  return (
    <div className={`app ${isVisible ? 'app--visible' : ''}`}>
      <header className="app__header">
        <div className="app__container">
          <div className="app__header-content">
            <h1 className="app__title">
              <span className="app__title-icon">🍳</span>
              Recipe Management System
            </h1>
            <p className="app__subtitle">
              Search and discover delicious recipes from around the world
            </p>
            <div className="app__stats">
              {searchState.totalElements > 0 && (
                <div className="app__stat">
                  <span className="app__stat-number">{searchState.totalElements}</span>
                  <span className="app__stat-label">Recipes Available</span>
                </div>
              )}
            </div>
          </div>
        </div>
      </header>

      <main className={`app__main ${searchState.recipes.length > 0 || searchState.loading ? 'app__main--with-results' : ''}`}>
        <div className={`app__container ${searchState.recipes.length > 0 || searchState.loading ? 'app__container--full-width' : ''}`}>
          <section className={`app__search-section ${searchState.recipes.length > 0 || searchState.loading ? 'app__search-section--with-results' : ''}`}>
            <SearchBar
              value={query}
              onChange={handleQueryChange}
              onSearch={handleSearch}
              onClear={handleClearSearch}
              loading={searchState.loading}
              placeholder="Search recipes by name or cuisine (min 3 characters)..."
              className={searchState.recipes.length > 0 || searchState.loading ? 'search-bar--full-width' : ''}
            />
          </section>

          {(searchState.recipes.length > 0 || searchState.loading || searchState.error || hasSearched) && (
            <section className={`app__results-section ${(searchState.recipes.length > 0 || searchState.loading || searchState.error) ? 'app__results-section--visible' : ''} ${searchState.recipes.length > 0 ? 'app__results-section--full-width' : ''}`}>
            {searchState.loading && (
              <div className="app__loading">
                <Loading 
                  variant="dots" 
                  size="large" 
                  color="primary" 
                  text="Searching for delicious recipes..." 
                />
                {/* Loading skeleton for better UX */}
                <div className="app__recipe-grid app__recipe-grid--loading">
                  {Array.from({ length: 6 }).map((_, index) => (
                    <div key={index} className="app__recipe-skeleton">
                      <Loading variant="skeleton" />
                    </div>
                  ))}
                </div>
              </div>
            )}

            {searchState.error && (
              <div className="app__error">
                <h3>Oops! Something went wrong</h3>
                <p>{searchState.error}</p>
                <p className="app__error-help">
                  Make sure the backend server is running on http://localhost:8080
                </p>
              </div>
            )}

            {!searchState.loading && !searchState.error && searchState.recipes.length > 0 && (
              <div className="app__results">
                <div className="app__results-header">
                  <h2>Found {searchState.totalElements} recipes</h2>
                  <div className="app__results-actions">
                    <button 
                      className={`app__view-toggle ${viewMode === 'grid' ? 'app__view-toggle--active' : ''}`}
                      onClick={() => setViewMode('grid')}
                      title="Grid view"
                    >
                      <svg viewBox="0 0 24 24" fill="currentColor">
                        <path d="M3 3h7v7H3V3zm0 11h7v7H3v-7zm11-11h7v7h-7V3zm0 11h7v7h-7v-7z"/>
                      </svg>
                    </button>
                    <button 
                      className={`app__view-toggle ${viewMode === 'list' ? 'app__view-toggle--active' : ''}`}
                      onClick={() => setViewMode('list')}
                      title="List view"
                    >
                      <svg viewBox="0 0 24 24" fill="currentColor">
                        <path d="M3 13h2v-2H3v2zm0 4h2v-2H3v2zm0-8h2V7H3v2zm4 4h14v-2H7v2zm0 4h14v-2H7v2zM7 7v2h14V7H7z"/>
                      </svg>
                    </button>
                  </div>
                </div>

                {/* Sorting and Filtering Controls */}
                <div className="app__controls">
                  <div className="app__controls-section">
                    <h4>Sort by:</h4>
                    <div className="app__sort-controls">
                      <select 
                        value={sortBy} 
                        onChange={(e) => setSortBy(e.target.value as 'name' | 'cook_time_minutes' | 'rating')}
                        className="app__sort-select"
                      >
                        <option value="name">Name</option>
                        <option value="cook_time_minutes">Cook Time</option>
                        <option value="rating">Rating</option>
                      </select>
                      <button 
                        className={`app__sort-order ${sortOrder === 'desc' ? 'app__sort-order--desc' : ''}`}
                        onClick={() => setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc')}
                        title={`Sort ${sortOrder === 'asc' ? 'Descending' : 'Ascending'}`}
                      >
                        <svg viewBox="0 0 24 24" fill="currentColor">
                          <path d="M7 14l5-5 5 5z"/>
                        </svg>
                      </button>
                    </div>
                  </div>

                  {getAllTags().length > 0 && (
                    <div className="app__controls-section">
                      <div className="app__filter-header">
                        <h4>Filter by tags:</h4>
                        {selectedTags.length > 0 && (
                          <button className="app__clear-filters" onClick={clearFilters}>
                            Clear all
                          </button>
                        )}
                      </div>
                      <div className="app__tag-filters">
                        {getAllTags().slice(0, 10).map(tag => (
                          <button
                            key={tag}
                            className={`app__tag-filter ${selectedTags.includes(tag) ? 'app__tag-filter--active' : ''}`}
                            onClick={() => toggleTagFilter(tag)}
                          >
                            {tag}
                          </button>
                        ))}
                      </div>
                    </div>
                  )}
                </div>

                {/* Results count after filtering */}
                {(() => {
                  const filteredRecipes = getFilteredAndSortedRecipes();
                  return (
                    <>
                      {selectedTags.length > 0 && (
                        <div className="app__filter-results">
                          <p>Showing {filteredRecipes.length} of {searchState.recipes.length} recipes</p>
                          {selectedTags.length > 0 && (
                            <div className="app__active-filters">
                              <span>Active filters:</span>
                              {selectedTags.map(tag => (
                                <span key={tag} className="app__active-filter">
                                  {tag}
                                  <button onClick={() => toggleTagFilter(tag)}>×</button>
                                </span>
                              ))}
                            </div>
                          )}
                        </div>
                      )}

                      {viewMode === 'grid' ? (
                        <div className="app__recipe-grid">
                          {filteredRecipes.map((recipe, index) => (
                            <RecipeCard
                              key={recipe.id}
                              recipe={recipe}
                              onClick={handleRecipeClick}
                              className="app__recipe-card"
                              style={{ animationDelay: `${index * 0.1}s` }}
                            />
                          ))}
                        </div>
                      ) : (
                        <div className="app__recipe-list">
                          {filteredRecipes.map((recipe, index) => (
                            <RecipeListItem
                              key={recipe.id}
                              recipe={recipe}
                              onClick={handleRecipeClick}
                              className="app__recipe-list-item"
                              style={{ animationDelay: `${index * 0.05}s` }}
                            />
                          ))}
                        </div>
                      )}

                      {filteredRecipes.length === 0 && selectedTags.length > 0 && (
                        <div className="app__no-filter-results">
                          <div className="app__no-filter-results-icon">🏷️</div>
                          <h3>No recipes match your filters</h3>
                          <p>Try removing some tag filters or search for different recipes.</p>
                          <button className="app__clear-filters-btn" onClick={clearFilters}>
                            Clear all filters
                          </button>
                        </div>
                      )}
                    </>
                  );
                })()}
              </div>
            )}

            {!searchState.loading && !searchState.error && searchState.recipes.length === 0 && query.length >= 3 && (
              <div className="app__no-results">
                <div className="app__no-results-icon">🔍</div>
                <h3>No recipes found</h3>
                <p>Try searching with different keywords or check your spelling.</p>
                <div className="app__no-results-suggestions">
                  <p>Popular searches:</p>
                  <div className="app__suggestion-tags">
                    <button className="app__suggestion-tag" onClick={() => {setQuery('pasta'); handleSearch();}}>pasta</button>
                    <button className="app__suggestion-tag" onClick={() => {setQuery('chicken'); handleSearch();}}>chicken</button>
                    <button className="app__suggestion-tag" onClick={() => {setQuery('vegetarian'); handleSearch();}}>vegetarian</button>
                    <button className="app__suggestion-tag" onClick={() => {setQuery('dessert'); handleSearch();}}>dessert</button>
                  </div>
                </div>
              </div>
            )}

            {!searchState.loading && !searchState.error && searchState.recipes.length === 0 && query.length >= 3 && (
              <div className="app__no-results">
                <div className="app__no-results-icon">🔍</div>
                <h3>No recipes found</h3>
                <p>We couldn't find any recipes matching "<strong>{query}</strong>"</p>
                <div className="app__suggestions">
                  <p>Try searching for:</p>
                  <div className="app__suggestion-tags">
                    <button className="app__suggestion-tag" onClick={() => handleQueryChange('pasta')}>pasta</button>
                    <button className="app__suggestion-tag" onClick={() => handleQueryChange('chicken')}>chicken</button>
                    <button className="app__suggestion-tag" onClick={() => handleQueryChange('vegetarian')}>vegetarian</button>
                    <button className="app__suggestion-tag" onClick={() => handleQueryChange('dessert')}>dessert</button>
                    <button className="app__suggestion-tag" onClick={() => handleQueryChange('italian')}>italian</button>
                  </div>
                </div>
              </div>
            )}



            {!searchState.loading && !searchState.error && searchState.recipes.length === 0 && query.length === 0 && hasSearched && (
              <div className="app__empty-state">
                <div className="app__empty-state-icon">🔍</div>
                <h3>Ready to search again?</h3>
                <p>Enter a recipe name or cuisine in the search bar above to find delicious recipes.</p>
                
                {recentSearches.length > 0 && (
                  <div className="app__recent-searches">
                    <div className="app__recent-searches-header">
                      <p>Recent searches:</p>
                      <button 
                        className="app__clear-recent" 
                        onClick={clearRecentSearches}
                        title="Clear recent searches"
                      >
                        ✕
                      </button>
                    </div>
                    <div className="app__suggestion-tags">
                      {recentSearches.map((search, index) => (
                        <button 
                          key={index} 
                          className="app__suggestion-tag app__suggestion-tag--recent" 
                          onClick={() => handleQueryChange(search)}
                        >
                          <span className="app__recent-icon">🕒</span>
                          {search}
                        </button>
                      ))}
                    </div>
                  </div>
                )}
                
                <div className="app__quick-searches">
                  <p>Popular searches:</p>
                  <div className="app__suggestion-tags">
                    <button className="app__suggestion-tag" onClick={() => handleQueryChange('pasta')}>pasta</button>
                    <button className="app__suggestion-tag" onClick={() => handleQueryChange('chicken')}>chicken</button>
                    <button className="app__suggestion-tag" onClick={() => handleQueryChange('vegetarian')}>vegetarian</button>
                    <button className="app__suggestion-tag" onClick={() => handleQueryChange('dessert')}>dessert</button>
                  </div>
                </div>
              </div>
            )}
            </section>
          )}

          {!hasSearched && (
            <section className="app__welcome-section">
              <div className="app__welcome">
                <div className="app__welcome-hero">
                  <h2>Welcome to Recipe Management System</h2>
                  <p>Start by searching for your favorite recipes above!</p>
                </div>
                <div className="app__features">
                  <div className="app__feature">
                    <div className="app__feature-icon">🔍</div>
                    <h4>Smart Search</h4>
                    <p>Full-text search across recipe names and cuisines with intelligent suggestions</p>
                  </div>
                  <div className="app__feature">
                    <div className="app__feature-icon">🌍</div>
                    <h4>Global Cuisine</h4>
                    <p>Discover authentic recipes from different cultures around the world</p>
                  </div>
                  <div className="app__feature">
                    <div className="app__feature-icon">⚡</div>
                    <h4>Fast Results</h4>
                    <p>Powered by advanced search technology for instant recipe discovery</p>
                  </div>
                  <div className="app__feature">
                    <div className="app__feature-icon">📱</div>
                    <h4>Mobile Friendly</h4>
                    <p>Responsive design that works perfectly on all your devices</p>
                  </div>
                  <div className="app__feature">
                    <div className="app__feature-icon">⭐</div>
                    <h4>Rated Recipes</h4>
                    <p>Find the best recipes with community ratings and reviews</p>
                  </div>
                  <div className="app__feature">
                    <div className="app__feature-icon">🏷️</div>
                    <h4>Smart Tags</h4>
                    <p>Organized with intelligent tagging for easy recipe categorization</p>
                  </div>
                </div>
              </div>
            </section>
          )}
        </div>
      </main>

      <footer className="app__footer">
        <div className="app__container">
          <p>&copy; 2024 Recipe Management System. Built with React & Spring Boot.</p>
        </div>
      </footer>
    </div>
  );
}

export default App;
