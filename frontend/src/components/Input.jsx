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
        <label htmlFor={inputId} className="block text-sm font-medium text-gray-700">
          {label}
        </label>
      )}
      <input
        id={inputId}
        className={`w-full min-h-11 rounded-xl border border-gray-200 bg-white px-3 py-2.5 text-base text-gray-900 shadow-sm placeholder:text-gray-400 focus:border-primary-600 focus:outline-none focus:ring-2 focus:ring-primary-600/20 sm:text-sm ${error ? 'border-red-300' : ''} ${className}`}
        {...props}
      />
      {error && <p className="text-xs text-red-600">{error}</p>}
    </div>
  )
}
