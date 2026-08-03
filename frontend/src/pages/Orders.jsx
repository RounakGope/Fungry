import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import * as usersApi from '../api/users'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'
import { formatCurrency, formatDate, getErrorMessage } from '../utils/constants'
import Badge from '../components/Badge'
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
    usersApi.getOrderHistory(user.id, { page, size, sortBy, direction })
      .then(setOrders)
      .catch((err) => toast.error(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }, [user.id, page, sortBy, direction])

  if (loading) return <LoadingSpinner label="Loading orders" />

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div className="space-y-1">
          <h1 className="text-2xl font-semibold tracking-tight text-zinc-50">Order history</h1>
          <p className="text-sm text-white/70">Track your current and past deliveries.</p>
        </div>
        <div className="flex flex-wrap gap-2">
          <select
            value={sortBy}
            onChange={(e) => { setSortBy(e.target.value); setPage(0) }}
            className="rounded-lg border border-zinc-800 bg-zinc-900 px-3 py-2 text-sm text-zinc-100"
          >
            <option value="createdAt">Date</option>
            <option value="totalAmt">Amount</option>
            <option value="status">Status</option>
          </select>
          <select
            value={direction}
            onChange={(e) => { setDirection(e.target.value); setPage(0) }}
            className="rounded-lg border border-zinc-800 bg-zinc-900 px-3 py-2 text-sm text-zinc-100"
          >
            <option value="desc">Newest first</option>
            <option value="asc">Oldest first</option>
          </select>
        </div>
      </div>

      {orders.length === 0 ? (
        <EmptyState title="No orders yet" description="Your order history will appear here." />
      ) : (
        <div className="space-y-3">
          {orders.map((order) => {
            const summaryItems = Array.isArray(order.orderItems) ? order.orderItems.slice(0, 2) : []
            const summaryText = summaryItems.length
              ? summaryItems.map((item) => item.name || item.itemName).join(', ')
              : 'Order placed successfully'

            return (
              <Link key={order.orderId} to={`/orders/${order.orderId}`}>
                <Card className="flex flex-col gap-4 transition-colors hover:border-zinc-700 sm:flex-row sm:items-center sm:justify-between">
                  <div className="min-w-0">
                    <div className="flex flex-wrap items-center gap-2">
                      <p className="font-semibold text-zinc-50">Order #{order.orderId}</p>
                      <Badge status={order.status} />
                    </div>
                    {order.restaurantName && (
                      <p className="mt-1 text-sm text-white/80">{order.restaurantName}</p>
                    )}
                    <p className="mt-1 text-sm text-white/70">{summaryText}</p>
                    <p className="mt-2 text-sm text-white/70">{formatDate(order.createdAt)}</p>
                  </div>
                  <div className="text-left sm:text-right">
                    <p className="text-sm font-semibold text-zinc-50">{formatCurrency(order.totalAmt)}</p>
                    <p className="mt-1 text-sm text-white/70">Tap for details</p>
                  </div>
                </Card>
              </Link>
            )
          })}
        </div>
      )}

      <div className="mt-6 flex justify-center gap-3">
        <button
          disabled={page === 0}
          onClick={() => setPage((p) => p - 1)}
          className="rounded-lg border border-zinc-800 bg-zinc-900 px-4 py-2 text-sm text-white/90 disabled:cursor-not-allowed disabled:opacity-50"
        >
          Previous
        </button>
        <button
          disabled={orders.length < size}
          onClick={() => setPage((p) => p + 1)}
          className="rounded-lg border border-zinc-800 bg-zinc-900 px-4 py-2 text-sm text-white/90 disabled:cursor-not-allowed disabled:opacity-50"
        >
          Next
        </button>
      </div>
    </div>
  )
}