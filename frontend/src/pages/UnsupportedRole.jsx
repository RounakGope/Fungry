export default function UnsupportedRole({ role }) {
  return (
    <div className="max-w-lg">
      <h1 className="mb-2 text-2xl font-bold text-gray-900">No access for this role</h1>
      <p className="text-sm text-gray-500">
        Your account role ({role || 'unknown'}) doesn't have a dashboard in this app yet.
      </p>
    </div>
  )
}