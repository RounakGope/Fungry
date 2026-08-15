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
        <label htmlFor={inputId} className="block text-sm font-medium text-zinc-200">
          {label}
        </label>
      )}
      <input
        id={inputId}
        className={`w-full min-h-11 rounded-lg border border-border bg-surface px-3 py-2.5 text-base text-zinc-100 placeholder:text-muted focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500/30 sm:text-sm ${error ? 'border-red-400/60' : ''} ${className}`}
        {...props}
      />
      {error && <p className="text-xs text-red-400">{error}</p>}
    </div>
  )
}
