export const ROLES = {
  CUSTOMER: 'CUSTOMER',
  RESTAURANT_OWNER: 'RESTAURANT_OWNER',
  ADMIN: 'ADMIN',
}

/** Normalize UserRole from string or enum object */
export const normalizeRole = (data) => {
  if (typeof data === 'string') return data
  return data?.name || data?.role || data?.value || String(data)
}

// Must exactly match backend OrderStatus enum
export const ORDER_STATUSES = [
  'CREATED',
  'CONFIRMED',
  'PREPARING',
  'OUT_FOR_DELIVERY',
  'DELIVERED',
  'CANCELED',
]

// Single source of truth for the owner's allowed forward transitions.
// Mirrors ALLOWED_NEXT_STATUS in OrderServiceImpl — keep these in sync.
export const NEXT_STATUS_MAP = {
  CONFIRMED: 'PREPARING',
  PREPARING: 'OUT_FOR_DELIVERY',
  OUT_FOR_DELIVERY: 'DELIVERED',
}

// No further owner action possible once here
export const TERMINAL_STATUSES = ['DELIVERED', 'CANCELED']

// Statuses a customer can still cancel from.
// NOTE: adjust this to match whatever your actual cancel-eligibility rule is on the backend —
// I'm assuming cancellation is blocked once the restaurant starts preparing.
export const CANCELABLE_STATUSES = ['CREATED', 'CONFIRMED']

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