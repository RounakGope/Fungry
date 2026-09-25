export default function LoadingSpinner({ label = 'Loading...' }) {
  return (
    <div className="flex flex-col items-center justify-center gap-3 py-16">
      <div className="h-8 w-8 rounded-full border-2 border-border border-t-primary-500 animate-spin" />
      <span className="text-sm text-muted">{label}</span>
    </div>
  )
}
