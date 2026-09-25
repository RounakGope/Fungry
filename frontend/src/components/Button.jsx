const variants = {
  primary: 'bg-primary-600 text-white hover:bg-primary-500 border border-primary-600 font-medium',
  secondary: 'bg-surface-overlay text-zinc-100 hover:bg-surface-raised border border-border',
  danger: 'bg-surface-overlay text-red-400 hover:bg-red-500/10 border border-red-500/30',
  ghost: 'bg-transparent text-muted hover:bg-surface-overlay hover:text-zinc-100 border border-transparent',
}

const sizes = {
  sm: 'px-3 py-1.5 text-sm min-h-9',
  md: 'px-4 py-2.5 text-sm min-h-11',
  lg: 'px-6 py-3 text-base min-h-12',
}

export default function Button({
  children,
  variant = 'primary',
  size = 'md',
  className = '',
  disabled = false,
  type = 'button',
  ...props
}) {
  return (
    <button
      type={type}
      disabled={disabled}
      className={`inline-flex items-center justify-center rounded-lg transition-colors touch-manipulation disabled:cursor-not-allowed disabled:opacity-50 ${variants[variant]} ${sizes[size]} ${className}`}
      {...props}
    >
      {children}
    </button>
  )
}
