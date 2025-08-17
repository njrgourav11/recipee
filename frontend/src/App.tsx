import { useState } from 'react';
import { SearchBar } from './components/molecules/SearchBar/SearchBar';
import { useRecipeSearch } from './hooks/useRecipeSearch';
import './App.scss';

function App() {
  const [query, setQuery] = useState('');
  const { searchState, searchRecipes, setQuery: setSearchQuery } = useRecipeSearch('', false);

  const handleSearch = () => {
    if (query.length >= 3) {
      setSearchQuery(query);
      searchRecipes(query, 0);
    }
  };

  const handleQueryChange = (newQuery: string) => {
    setQuery(newQuery);
  };

  return (
    <div className="app">
      <header className="app__header">
        <div className="app__container">
          <h1 className="app__title">Recipe Management System</h1>
          <p className="app__subtitle">
            Search and discover delicious recipes from around the world
          </p>
        </div>
      </header>

      <main className="app__main">
        <div className="app__container">
          <section className="app__search-section">
            <SearchBar
              value={query}
              onChange={handleQueryChange}
              onSearch={handleSearch}
              loading={searchState.loading}
              placeholder="Search recipes by name or cuisine (min 3 characters)..."
            />
          </section>

          <section className="app__results-section">
            {searchState.loading && (
              <div className="app__loading">
                <div className="app__spinner"></div>
                <p>Searching for recipes...</p>
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
                <h2>Found {searchState.totalElements} recipes</h2>
                <div className="app__recipe-grid">
                  {searchState.recipes.map((recipe) => (
                    <div key={recipe.id} className="app__recipe-card">
                      <div className="app__recipe-image">
                        {recipe.image ? (
                          <img src={recipe.image} alt={recipe.name} />
                        ) : (
                          <div className="app__recipe-placeholder">
                            <span>No Image</span>
                          </div>
                        )}
                      </div>
                      <div className="app__recipe-content">
                        <h3 className="app__recipe-name">{recipe.name}</h3>
                        <p className="app__recipe-cuisine">{recipe.cuisine}</p>
                        <div className="app__recipe-meta">
                          <span className="app__recipe-difficulty">{recipe.difficulty}</span>
                          <span className="app__recipe-time">
                            {recipe.cook_time_minutes} min
                          </span>
                          {recipe.rating && (
                            <span className="app__recipe-rating">
                              ⭐ {recipe.rating.toFixed(1)}
                            </span>
                          )}
                        </div>
                        <div className="app__recipe-tags">
                          {recipe.tags.slice(0, 3).map((tag, index) => (
                            <span key={index} className="app__recipe-tag">
                              {tag}
                            </span>
                          ))}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {!searchState.loading && !searchState.error && searchState.recipes.length === 0 && query.length >= 3 && (
              <div className="app__no-results">
                <h3>No recipes found</h3>
                <p>Try searching with different keywords or check your spelling.</p>
              </div>
            )}

            {query.length === 0 && (
              <div className="app__welcome">
                <h2>Welcome to Recipe Management System</h2>
                <p>Start by searching for your favorite recipes above!</p>
                <div className="app__features">
                  <div className="app__feature">
                    <h4>🔍 Smart Search</h4>
                    <p>Full-text search across recipe names and cuisines</p>
                  </div>
                  <div className="app__feature">
                    <h4>🌍 Global Cuisine</h4>
                    <p>Discover recipes from different cultures</p>
                  </div>
                  <div className="app__feature">
                    <h4>⚡ Fast Results</h4>
                    <p>Powered by advanced search technology</p>
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
