const variants = {
  primary: 'bg-primary-600 text-white hover:bg-primary-700 border border-primary-600',
  secondary: 'bg-white text-primary-700 hover:bg-primary-50 border border-primary-600',
  danger: 'bg-white text-red-600 hover:bg-red-50 border border-red-200',
  ghost: 'bg-transparent text-gray-600 hover:bg-gray-100 border border-transparent',
}

const sizes = {
  sm: 'px-3 py-1.5 text-sm',
  md: 'px-4 py-2.5 text-sm',
  lg: 'px-5 py-3 text-base',
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
      className={`inline-flex min-h-11 items-center justify-center rounded-xl font-medium shadow-sm transition-all duration-150 touch-manipulation active:translate-y-px disabled:cursor-not-allowed disabled:opacity-50 ${variants[variant]} ${sizes[size]} ${className}`}
      {...props}
    >
      {children}
    </button>
  )
}
