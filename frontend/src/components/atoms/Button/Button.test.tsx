import { render } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, it, expect, vi } from 'vitest'
import { Button } from './Button'

describe('Button', () => {
  it('renders with default props', () => {
    const { container } = render(<Button>Click me</Button>)
    const button = container.querySelector('button')
    expect(button).toBeInTheDocument()
    expect(button).toHaveClass('button', 'button--primary', 'button--medium')
    expect(button).toHaveTextContent('Click me')
  })

  it('renders with different variants', () => {
    const { container, rerender } = render(<Button variant="secondary">Secondary</Button>)
    expect(container.querySelector('button')).toHaveClass('button--secondary')

    rerender(<Button variant="outline">Outline</Button>)
    expect(container.querySelector('button')).toHaveClass('button--outline')

    rerender(<Button variant="ghost">Ghost</Button>)
    expect(container.querySelector('button')).toHaveClass('button--ghost')
  })

  it('renders with different sizes', () => {
    const { container, rerender } = render(<Button size="small">Small</Button>)
    expect(container.querySelector('button')).toHaveClass('button--small')

    rerender(<Button size="large">Large</Button>)
    expect(container.querySelector('button')).toHaveClass('button--large')
  })

  it('renders as full width when specified', () => {
    const { container } = render(<Button fullWidth>Full Width</Button>)
    expect(container.querySelector('button')).toHaveClass('button--full-width')
  })

  it('shows loading state', () => {
    const { container } = render(<Button loading>Loading</Button>)
    const button = container.querySelector('button')
    expect(button).toHaveClass('button--loading')
    expect(button).toBeDisabled()
    expect(button?.querySelector('.button__spinner')).toBeInTheDocument()
    expect(button?.querySelector('.button__content--hidden')).toBeInTheDocument()
  })

  it('is disabled when disabled prop is true', () => {
    const { container } = render(<Button disabled>Disabled</Button>)
    expect(container.querySelector('button')).toBeDisabled()
  })

  it('handles click events', async () => {
    const user = userEvent.setup()
    const handleClick = vi.fn()
    const { container } = render(<Button onClick={handleClick}>Click me</Button>)
    
    const button = container.querySelector('button')
    if (button) await user.click(button)
    expect(handleClick).toHaveBeenCalledTimes(1)
  })

  it('does not handle click when disabled', async () => {
    const user = userEvent.setup()
    const handleClick = vi.fn()
    const { container } = render(<Button onClick={handleClick} disabled>Disabled</Button>)
    
    const button = container.querySelector('button')
    if (button) await user.click(button)
    expect(handleClick).not.toHaveBeenCalled()
  })


  it('applies custom className', () => {
    const { container } = render(<Button className="custom-class">Custom</Button>)
    expect(container.querySelector('button')).toHaveClass('custom-class')
  })

  it('passes through additional props', () => {
    const { container } = render(<Button type="submit" data-testid="submit-btn">Submit</Button>)
    const button = container.querySelector('[data-testid="submit-btn"]')
    expect(button).toHaveAttribute('type', 'submit')
  })
})