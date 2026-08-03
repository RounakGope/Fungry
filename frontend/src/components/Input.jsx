export default function Input({
  label,
  error,
  id,
  className = '',
  ...props
}) {
  const inputId = id || props.name

  return (
    <div className="space-y-1">
      {label && (
        <label htmlFor={inputId} className="block text-sm font-medium text-white/80">
          {label}
        </label>
      )}
      <input
        id={inputId}
        className={`w-full min-h-11 rounded-xl border border-zinc-800 bg-zinc-950/70 px-3 py-2.5 text-base text-zinc-100 shadow-sm placeholder:text-white/70 focus:border-teal-400/70 focus:outline-none focus:ring-2 focus:ring-teal-400/20 sm:text-sm ${error ? 'border-red-400/60' : ''} ${className}`}
        {...props}
      />
      {error && <p className="text-xs text-red-400">{error}</p>}
    </div>
  )
}
