export default function LoadingSpinner({ label = 'Loading...' }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 gap-4">
      {/* Loading Track */}
      <div className="relative h-1 w-32 overflow-hidden rounded-full bg-zinc-800">
        {/* Pulsing Teal Indicator */}
        <div className="absolute left-0 top-0 h-full w-1/2 animate-pulse rounded-full bg-[#00d09c]" />
      </div>
      
      {/* Label */}
      <span className="text-sm font-medium tracking-wide text-white/60">
        {label}
      </span>
    </div>
  )
}