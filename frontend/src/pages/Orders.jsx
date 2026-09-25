
import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import * as usersApi from '../api/users'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'
import { formatCurrency, formatDate, getErrorMessage } from '../utils/constants'
import Badge from '../components/Badge'
import Button from '../components/Button'
import Card from '../components/Card'
import LoadingSpinner from '../components/LoadingSpinner'
import EmptyState from '../components/EmptyState'

export default function Orders() {
  const { user } = useAuth()
  const toast = useToast()
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [sortBy, setSortBy] = useState('createdAt')
  const [direction, setDirection] = useState('desc')
  const size = 10

  useEffect(() => {
  setLoading(true)
  usersApi.getOrderHistory({ page, size, sortBy, direction })
    .then(setOrders)
    .catch((err) => toast.error(getErrorMessage(err)))
    .finally(() => setLoading(false))
}, [page, sortBy, direction])

  if (loading) return <LoadingSpinner label="Loading orders" />

  return (
    <div>
      <div className="mb-6 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <h1 className="text-2xl font-semibold text-zinc-50">Order history</h1>
        <div className="flex gap-2">
          <select
            value={sortBy}
            onChange={(e) => { setSortBy(e.target.value); setPage(0) }}
          >
            <option value="createdAt">Date</option>
            <option value="totalAmt">Amount</option>
            <option value="status">Status</option>
          </select>
          <select
            value={direction}
            onChange={(e) => { setDirection(e.target.value); setPage(0) }}
          >
            <option value="desc">Newest first</option>
            <option value="asc">Oldest first</option>
          </select>
        </div>
      </div>

      {orders.length === 0 ? (
        <EmptyState title="No orders yet" description="Your order history will appear here." />
      ) : (
        <div className="flex flex-col gap-4">
          {orders.map((order) => (
            <Link key={order.orderId} to={`/orders/${order.orderId}`} className="block">
              <Card className="flex items-center justify-between hover:border-primary-500/40">
                <div>
                  <p className="font-medium text-zinc-50">Order #{order.orderId}</p>
                  {order.restaurantName && (
                    <p className="mt-0.5 text-sm text-muted">{order.restaurantName}</p>
                  )}
                  <p className="mt-1 text-sm text-muted">{formatDate(order.createdAt)}</p>
                </div>
                <div className="text-right">
                  <Badge status={order.status} />
                  <p className="mt-2 text-sm font-semibold text-zinc-50">
                    {formatCurrency(order.totalAmt)}
                  </p>
                </div>
              </Card>
            </Link>
          ))}
        </div>
      )}

      <div className="mt-6 flex justify-center gap-3">
        <Button
          variant="secondary"
          size="sm"
          disabled={page === 0}
          onClick={() => setPage((p) => p - 1)}
        >
          Previous
        </Button>
        <Button
          variant="secondary"
          size="sm"
          disabled={orders.length < size}
          onClick={() => setPage((p) => p + 1)}
        >
          Next
        </Button>
      </div>
    </div>
  )
}
