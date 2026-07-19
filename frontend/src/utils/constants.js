export const ROLES = {
  CUSTOMER: 'CUSTOMER',
  RESTAURANT_OWNER: 'RESTAURANT_OWNER',
}

/** Normalize UserRole from string or enum object */
export const normalizeRole = (data) => {
  if (typeof data === 'string') return data
  return data?.name || data?.role || data?.value || String(data)
}

export const ORDER_STATUSES = [
  'PENDING',
  'CONFIRMED',
  'PREPARING',
  'OUT_FOR_DELIVERY',
  'DELIVERED',
  'CANCELLED',
]

export const CANCELABLE_STATUSES = ['PENDING', 'CONFIRMED']

export const formatCurrency = (amount) => {
  if (amount == null) return '—'
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    minimumFractionDigits: 0,
  }).format(amount)
}

export const formatDate = (dateStr) => {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleDateString('en-IN', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

export const getErrorMessage = (error) => {
  return (
    error.response?.data?.message ||
    error.response?.data?.error ||
    error.message ||
    'Something went wrong'
  )
}
