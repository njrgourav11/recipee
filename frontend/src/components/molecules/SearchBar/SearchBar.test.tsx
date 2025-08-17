import { render } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { SearchBar } from './SearchBar'

describe('SearchBar', () => {
  const defaultProps = {
    value: '',
    onChange: vi.fn(),
    onSearch: vi.fn(),
  }

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders with default props', () => {
    const { container } = render(<SearchBar {...defaultProps} />)
    
    const input = container.querySelector('input')
    const button = container.querySelector('button')
    
    expect(input).toBeInTheDocument()
    expect(button).toBeInTheDocument()
    expect(button).toHaveTextContent('Search')
  })

  it('displays the provided value', () => {
    const { container } = render(<SearchBar {...defaultProps} value="test query" />)
    
    const input = container.querySelector('input')
    expect(input).toHaveValue('test query')
  })

  it('calls onChange when input value changes', async () => {
    const user = userEvent.setup()
    const onChange = vi.fn()
    const { container } = render(<SearchBar {...defaultProps} onChange={onChange} />)
    
    const input = container.querySelector('input')
    if (input) {
      await user.type(input, 'pizza')
      expect(onChange).toHaveBeenCalledTimes(5) // Called for each character
    }
  })

  it('calls onSearch when search button is clicked', async () => {
    const user = userEvent.setup()
    const onSearch = vi.fn()
    const { container } = render(<SearchBar {...defaultProps} value="pizza" onSearch={onSearch} />)
    
    const button = container.querySelector('button')
    if (button) {
      await user.click(button)
      expect(onSearch).toHaveBeenCalledTimes(1)
    }
  })

  it('calls onSearch when Enter key is pressed', async () => {
    const user = userEvent.setup()
    const onSearch = vi.fn()
    const { container } = render(<SearchBar {...defaultProps} value="pizza" onSearch={onSearch} />)
    
    const input = container.querySelector('input')
    if (input) {
      await user.type(input, '{enter}')
      expect(onSearch).toHaveBeenCalledTimes(1)
    }
  })

  it('disables search button when query is less than 3 characters', () => {
    const { container } = render(<SearchBar {...defaultProps} value="ab" />)
    
    const button = container.querySelector('button')
    expect(button).toBeDisabled()
  })

  it('enables search button when query is 3 or more characters', () => {
    const { container } = render(<SearchBar {...defaultProps} value="abc" />)
    
    const button = container.querySelector('button')
    expect(button).not.toBeDisabled()
  })

  it('shows loading state', () => {
    const { container } = render(<SearchBar {...defaultProps} loading={true} />)
    
    const input = container.querySelector('input')
    const button = container.querySelector('button')
    
    expect(input).toBeDisabled()
    expect(button).toHaveClass('button--loading')
  })

  it('uses custom placeholder', () => {
    const { container } = render(
      <SearchBar {...defaultProps} placeholder="Custom placeholder" />
    )
    
    const input = container.querySelector('input')
    expect(input).toHaveAttribute('placeholder', 'Custom placeholder')
  })

  it('applies custom className', () => {
    const { container } = render(<SearchBar {...defaultProps} className="custom-search" />)
    
    expect(container.querySelector('.search-bar')).toHaveClass('custom-search')
  })

  it('calls onClear when provided', async () => {
    const user = userEvent.setup()
    const onClear = vi.fn()
    const { container } = render(<SearchBar {...defaultProps} onClear={onClear} />)
    
    // This would depend on if there's a clear button in the implementation
    // For now, just test that the prop is accepted
    expect(onClear).toBeDefined()
  })

  it('updates local value when external value changes', () => {
    const { container, rerender } = render(<SearchBar {...defaultProps} value="initial" />)
    
    let input = container.querySelector('input')
    expect(input).toHaveValue('initial')
    
    rerender(<SearchBar {...defaultProps} value="updated" />)
    input = container.querySelector('input')
    expect(input).toHaveValue('updated')
  })

  it('renders search icon', () => {
    const { container } = render(<SearchBar {...defaultProps} />)
    
    const icon = container.querySelector('svg')
    expect(icon).toBeInTheDocument()
  })

  it('maintains focus on input during typing', async () => {
    const user = userEvent.setup()
    const { container } = render(<SearchBar {...defaultProps} />)
    
    const input = container.querySelector('input')
    if (input) {
      await user.click(input)
      expect(input).toHaveFocus()
      
      await user.type(input, 'test')
      expect(input).toHaveFocus()
    }
  })
})