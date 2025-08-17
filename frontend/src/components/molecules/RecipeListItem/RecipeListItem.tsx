import React from 'react';
import type { RecipeCardProps } from '../../../types/recipe';
import './RecipeListItem.scss';

export const RecipeListItem: React.FC<RecipeCardProps & { style?: React.CSSProperties }> = ({
  recipe,
  onClick,
  className = '',
  style,
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
      className={`recipe-list-item ${className}`}
      onClick={handleClick}
      style={style}
    >
      <div className="recipe-list-item__image">
        {recipe.image ? (
          <img src={recipe.image} alt={recipe.name} loading="lazy" />
        ) : (
          <div className="recipe-list-item__placeholder">
            <div className="recipe-list-item__placeholder-icon">🍽️</div>
          </div>
        )}
      </div>
      
      <div className="recipe-list-item__content">
        <div className="recipe-list-item__main">
          <h3 className="recipe-list-item__name">{recipe.name}</h3>
          <p className="recipe-list-item__cuisine">{recipe.cuisine}</p>
        </div>
        
        <div className="recipe-list-item__meta">
          <span className={`recipe-list-item__difficulty recipe-list-item__difficulty--${getDifficultyColor(recipe.difficulty)}`}>
            {recipe.difficulty}
          </span>
          <span className="recipe-list-item__time">
            <svg viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2C6.5 2 2 6.5 2 12s4.5 10 10 10 10-4.5 10-10S17.5 2 12 2zm4.2 14.2L11 13V7h1.5v5.2l4.5 2.7-.8 1.3z"/>
            </svg>
            {recipe.cook_time_minutes} min
          </span>
          {recipe.rating && (
            <span className="recipe-list-item__rating">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/>
              </svg>
              {recipe.rating.toFixed(1)}
            </span>
          )}
        </div>
        
        <div className="recipe-list-item__tags">
          {recipe.tags.slice(0, 5).map((tag, index) => (
            <span key={index} className="recipe-list-item__tag">
              {tag}
            </span>
          ))}
          {recipe.tags.length > 5 && (
            <span className="recipe-list-item__tag recipe-list-item__tag--more">
              +{recipe.tags.length - 5}
            </span>
          )}
        </div>
      </div>

     
    </div>
  );
};

export default RecipeListItem;