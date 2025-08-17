import { useState, useEffect } from 'react';
import { SearchBar } from './components/molecules/SearchBar/SearchBar';
import { RecipeCard } from './components/molecules/RecipeCard/RecipeCard';
import { Loading } from './components/atoms/Loading/Loading';
import { useRecipeSearch } from './hooks/useRecipeSearch';
import './App.scss';

function App() {
  const [query, setQuery] = useState('');
  const [isVisible, setIsVisible] = useState(false);
  const { searchState, searchRecipes, setQuery: setSearchQuery } = useRecipeSearch('', false);

  // Add entrance animation
  useEffect(() => {
    setIsVisible(true);
  }, []);

  const handleSearch = () => {
    if (query.length >= 3) {
      setSearchQuery(query);
      searchRecipes(query, 0);
    }
  };

  const handleQueryChange = (newQuery: string) => {
    setQuery(newQuery);
  };

  const handleRecipeClick = (recipe: any) => {
    // Future: Open recipe detail modal or navigate to detail page
    console.log('Recipe clicked:', recipe);
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
              loading={searchState.loading}
              placeholder="Search recipes by name or cuisine (min 3 characters)..."
              className={searchState.recipes.length > 0 || searchState.loading ? 'search-bar--full-width' : ''}
            />
          </section>

          <section className={`app__results-section ${(searchState.recipes.length > 0 || searchState.loading || searchState.error || query.length >= 3) ? 'app__results-section--visible' : ''} ${searchState.recipes.length > 0 ? 'app__results-section--full-width' : ''}`}>
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
                    <button className="app__view-toggle app__view-toggle--active">
                      <svg viewBox="0 0 24 24" fill="currentColor">
                        <path d="M3 3h7v7H3V3zm0 11h7v7H3v-7zm11-11h7v7h-7V3zm0 11h7v7h-7v-7z"/>
                      </svg>
                    </button>
                    <button className="app__view-toggle">
                      <svg viewBox="0 0 24 24" fill="currentColor">
                        <path d="M3 13h2v-2H3v2zm0 4h2v-2H3v2zm0-8h2V7H3v2zm4 4h14v-2H7v2zm0 4h14v-2H7v2zM7 7v2h14V7H7z"/>
                      </svg>
                    </button>
                  </div>
                </div>
                <div className="app__recipe-grid">
                  {searchState.recipes.map((recipe, index) => (
                    <RecipeCard
                      key={recipe.id}
                      recipe={recipe}
                      onClick={handleRecipeClick}
                      className="app__recipe-card"
                      style={{ animationDelay: `${index * 0.1}s` }}
                    />
                  ))}
                </div>
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

            {query.length === 0 && (
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
            )}
          </section>
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
