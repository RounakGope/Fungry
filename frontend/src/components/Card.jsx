export default function Card({ children, className = '', padding = true, ...props }) {
  return (
    <div
      className={`rounded-lg border border-gray-200 bg-white shadow-sm ${padding ? 'p-5' : ''} ${className}`}
      {...props}
    >
      {children}
    </div>
  )
}
