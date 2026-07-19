export default function LoadingSpinner({ label = 'Loading' }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 gap-3">
      <div className="h-1 w-24 overflow-hidden rounded-lg bg-primary-100">
        <div className="h-full w-1/2 animate-pulse rounded-lg bg-primary-600" />
      </div>
      <span className="text-sm text-gray-500">{label}</span>
    </div>
  )
}
