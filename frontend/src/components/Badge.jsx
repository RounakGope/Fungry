const statusStyles = {
  PENDING: 'bg-primary-100 text-primary-700 border-primary-600/20',
  CONFIRMED: 'bg-primary-100 text-primary-700 border-primary-600/20',
  PREPARING: 'bg-primary-100 text-primary-700 border-primary-600/20',
  OUT_FOR_DELIVERY: 'bg-primary-100 text-primary-700 border-primary-600/20',
  DELIVERED: 'bg-gray-100 text-gray-700 border-gray-200',
  CANCELLED: 'bg-gray-100 text-gray-500 border-gray-200',
}

export default function Badge({ status, children }) {
  const label = children || status?.replace(/_/g, ' ')
  const style = statusStyles[status] || 'bg-gray-100 text-gray-700 border-gray-200'

  return (
    <span className={`inline-flex items-center rounded-lg border px-2.5 py-0.5 text-xs font-medium uppercase tracking-wide ${style}`}>
      {label}
    </span>
  )
}
