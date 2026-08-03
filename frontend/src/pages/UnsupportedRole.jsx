export default function UnsupportedRole({ role }) {
  return (
    <div className="max-w-lg">
      <h1 className="mb-2 text-2xl font-bold text-white">No access for this role</h1>
      <p className="text-sm text-white/70">
        Your account role ({role || 'unknown'}) doesn't have a dashboard in this app yet.
      </p>
    </div>
  )
}