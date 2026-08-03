export default function Card({ children, className = '', padding = true, ...props }) {
  return (
    <div
      className={`rounded-2xl border border-zinc-800 bg-zinc-900/90 shadow-[0_10px_30px_rgba(0,0,0,0.35)] ${padding ? 'p-4 sm:p-5' : ''} ${className}`}
      {...props}
    >
      {children}
    </div>
  )
}
