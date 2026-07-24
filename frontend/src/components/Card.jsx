export default function Card({ children, className = '', padding = true, ...props }) {
  return (
    <div
      className={`rounded-2xl border border-gray-200 bg-white shadow-[0_8px_30px_rgba(15,23,42,0.04)] ${padding ? 'p-4 sm:p-5' : ''} ${className}`}
      {...props}
    >
      {children}
    </div>
  )
}
