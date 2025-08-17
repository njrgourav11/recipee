import React from 'react';
import type { RecipeCardProps } from '../../../types/recipe';
import './RecipeCard.scss';

export const RecipeCard: React.FC<RecipeCardProps & {
  style?: React.CSSProperties;
  visibleAttributes?: {
    rating: boolean;
    cookTime: boolean;
    difficulty: boolean;
    cuisine: boolean;
    servings: boolean;
    calories: boolean;
    reviewCount: boolean;
  };
}> = ({
  recipe,
  onClick,
  className = '',
  style,
  visibleAttributes = {
    rating: true,
    cookTime: true,
    difficulty: true,
    cuisine: true,
    servings: true,
    calories: false,
    reviewCount: false,
  },
}) => {
  const handleClick = () => {
    if (onClick) {
      onClick(recipe);
    }
  };

  const handleFavoriteClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    console.log('Add to favorites:', recipe.name);
  };

  const getDifficultyColor = (difficulty: string) => {
    switch (difficulty.toLowerCase()) {
      case 'easy':
        return 'easy';
      case 'medium':
        return 'medium';
      case 'hard':
        return 'hard';
      default:
        return 'medium';
    }
  };

  return (
    <div 
      className={`recipe-card ${className}`}
      onClick={handleClick}
      style={style}
    >
      <div className="recipe-card__image">
        {recipe.image ? (
          <img src={recipe.image} alt={recipe.name} loading="lazy" />
        ) : (
          <div className="recipe-card__placeholder">
            <div className="recipe-card__placeholder-icon">🍽️</div>
            <span>No Image</span>
          </div>
        )}
        <div className="recipe-card__overlay">
          <button 
            className="recipe-card__favorite"
            onClick={handleFavoriteClick}
            aria-label="Add to favorites"
          >
            <svg viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
            </svg>
          </button>
        </div>
      </div>
      
      <div className="recipe-card__content">
        <h3 className="recipe-card__name">{recipe.name}</h3>
        {visibleAttributes.cuisine && (
          <p className="recipe-card__cuisine">{recipe.cuisine}</p>
        )}
        
        <div className="recipe-card__meta">
          {visibleAttributes.difficulty && (
            <span className={`recipe-card__difficulty recipe-card__difficulty--${getDifficultyColor(recipe.difficulty)}`}>
              {recipe.difficulty}
            </span>
          )}
          {visibleAttributes.cookTime && (
            <span className="recipe-card__time">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 2C6.5 2 2 6.5 2 12s4.5 10 10 10 10-4.5 10-10S17.5 2 12 2zm4.2 14.2L11 13V7h1.5v5.2l4.5 2.7-.8 1.3z"/>
              </svg>
              {recipe.cook_time_minutes} min
            </span>
          )}
          {visibleAttributes.rating && recipe.rating && (
            <span className="recipe-card__rating">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/>
              </svg>
              {recipe.rating.toFixed(1)}
            </span>
          )}
          {visibleAttributes.servings && (
            <span className="recipe-card__servings">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
              </svg>
              {recipe.servings} servings
            </span>
          )}
          {visibleAttributes.calories && recipe.calories_per_serving && (
            <span className="recipe-card__calories">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
              </svg>
              {recipe.calories_per_serving} cal
            </span>
          )}
          {visibleAttributes.reviewCount && recipe.review_count && (
            <span className="recipe-card__reviews">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M20 2H4c-1.1 0-1.99.9-1.99 2L2 22l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-7 12h-2v-2h2v2zm0-4h-2V6h2v4z"/>
              </svg>
              {recipe.review_count} reviews
            </span>
          )}
        </div>
        
        <div className="recipe-card__tags">
          {recipe.tags.slice(0, 3).map((tag, index) => (
            <span key={index} className="recipe-card__tag">
              {tag}
            </span>
          ))}
          {recipe.tags.length > 3 && (
            <span className="recipe-card__tag recipe-card__tag--more">
              +{recipe.tags.length - 3}
            </span>
          )}
        </div>
      </div>
    </div>
  );
};

export default RecipeCard;