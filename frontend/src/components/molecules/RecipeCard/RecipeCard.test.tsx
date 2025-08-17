import { render } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, it, expect, vi } from 'vitest'
import { RecipeCard } from './RecipeCard'
import type { Recipe } from '../../../types/recipe'

const mockRecipe: Recipe = {
  id: 1,
  name: 'Test Recipe',
  cuisine: 'Italian',
  difficulty: 'Medium',
  rating: 4.5,
  cook_time_minutes: 30,
  prep_time_minutes: 15,
  total_time_minutes: 45,
  servings: 4,
  ingredients: ['ingredient1', 'ingredient2'],
  instructions: ['step1', 'step2'],
  tags: ['tag1', 'tag2', 'tag3', 'tag4'],
  image: 'https://example.com/image.jpg',
  review_count: 10,
  calories_per_serving: 250,
  created_at: '2024-01-01T00:00:00Z',
  updated_at: '2024-01-01T00:00:00Z'
}

describe('RecipeCard', () => {
  it('renders recipe information correctly', () => {
    const { container } = render(<RecipeCard recipe={mockRecipe} />)
    
    expect(container).toHaveTextContent('Test Recipe')
    expect(container).toHaveTextContent('Italian')
    expect(container).toHaveTextContent('Medium')
    expect(container).toHaveTextContent('4.5')
    expect(container).toHaveTextContent('30 min')
  })

  it('renders recipe image when provided', () => {
    const { container } = render(<RecipeCard recipe={mockRecipe} />)
    
    const image = container.querySelector('img[alt="Test Recipe"]')
    expect(image).toBeInTheDocument()
    expect(image).toHaveAttribute('src', 'https://example.com/image.jpg')
    expect(image).toHaveAttribute('loading', 'lazy')
  })

  it('renders placeholder when no image provided', () => {
    const recipeWithoutImage = { ...mockRecipe, image: '' }
    const { container } = render(<RecipeCard recipe={recipeWithoutImage} />)
    
    expect(container).toHaveTextContent('No Image')
    expect(container).toHaveTextContent('🍽️')
  })

  it('displays difficulty with correct color class', () => {
    const { container, rerender } = render(<RecipeCard recipe={{ ...mockRecipe, difficulty: 'Easy' }} />)
    expect(container.querySelector('.recipe-card__difficulty--easy')).toBeInTheDocument()

    rerender(<RecipeCard recipe={{ ...mockRecipe, difficulty: 'Hard' }} />)
    expect(container.querySelector('.recipe-card__difficulty--hard')).toBeInTheDocument()

    rerender(<RecipeCard recipe={{ ...mockRecipe, difficulty: 'Medium' }} />)
    expect(container.querySelector('.recipe-card__difficulty--medium')).toBeInTheDocument()
  })

  it('displays first 3 tags and shows count for additional tags', () => {
    const { container } = render(<RecipeCard recipe={mockRecipe} />)
    
    expect(container).toHaveTextContent('tag1')
    expect(container).toHaveTextContent('tag2')
    expect(container).toHaveTextContent('tag3')
    expect(container).toHaveTextContent('+1')
  })

  it('does not show additional tag count when 3 or fewer tags', () => {
    const recipeWithFewTags = { ...mockRecipe, tags: ['tag1', 'tag2'] }
    const { container } = render(<RecipeCard recipe={recipeWithFewTags} />)
    
    expect(container).toHaveTextContent('tag1')
    expect(container).toHaveTextContent('tag2')
    expect(container.textContent).not.toMatch(/^\+/)
  })

  it('handles recipe click', async () => {
    const user = userEvent.setup()
    const handleClick = vi.fn()
    const { container } = render(<RecipeCard recipe={mockRecipe} onClick={handleClick} />)
    
    const card = container.querySelector('.recipe-card')
    if (card) await user.click(card)
    expect(handleClick).toHaveBeenCalledWith(mockRecipe)
  })

  it('handles favorite button click without propagating to recipe click', async () => {
    const user = userEvent.setup()
    const handleClick = vi.fn()
    const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {})
    
    const { container } = render(<RecipeCard recipe={mockRecipe} onClick={handleClick} />)
    
    const favoriteButton = container.querySelector('[aria-label="Add to favorites"]')
    if (favoriteButton) await user.click(favoriteButton)
    
    expect(consoleSpy).toHaveBeenCalledWith('Add to favorites:', 'Test Recipe')
    expect(handleClick).not.toHaveBeenCalled()
    
    consoleSpy.mockRestore()
  })

  it('applies custom className', () => {
    const { container } = render(
      <RecipeCard
        recipe={mockRecipe}
        className="custom-class"
      />
    )
    
    const card = container.querySelector('.recipe-card')
    expect(card).toHaveClass('custom-class')
  })

  it('renders rating when provided', () => {
    const { container } = render(<RecipeCard recipe={mockRecipe} />)
    
    expect(container).toHaveTextContent('4.5')
  })

  it('renders with all required accessibility attributes', () => {
    const { container } = render(<RecipeCard recipe={mockRecipe} />)
    
    const favoriteButton = container.querySelector('[aria-label="Add to favorites"]')
    expect(favoriteButton).toBeInTheDocument()
    
    const image = container.querySelector('img[alt="Test Recipe"]')
    expect(image).toBeInTheDocument()
  })
})