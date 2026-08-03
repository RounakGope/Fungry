const variants = {
  primary: 'bg-[#00d09c] text-black hover:bg-[#00e6ac] border border-transparent font-semibold',
  secondary: 'bg-[#232323] text-white hover:bg-[#2c2c2c] border border-zinc-700',
  danger: 'bg-[#232323] text-red-500 hover:bg-red-500/10 border border-red-500/30',
  ghost: 'bg-transparent text-white/60 hover:bg-[#232323] hover:text-white border border-transparent',
}

const sizes = {
  sm: 'px-3 py-1.5 text-sm',
  md: 'px-4 py-2.5 text-sm',
  lg: 'px-6 py-3 text-base',
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
      className={`inline-flex min-h-11 items-center justify-center rounded-xl transition-all duration-200 touch-manipulation active:scale-[0.98] disabled:cursor-not-allowed disabled:opacity-50 ${variants[variant]} ${sizes[size]} ${className}`}
      {...props}
    >
      {children}
    </button>
  )
}