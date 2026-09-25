import { useEffect, useState } from 'react'
import * as orderApi from '../api/order'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'
import {
  formatCurrency,
  formatDate,
  getErrorMessage,
  NEXT_STATUS_MAP,
  TERMINAL_STATUSES,
} from '../utils/constants'
import Badge from '../components/Badge'
import Card from '../components/Card'
import LoadingSpinner from '../components/LoadingSpinner'
import EmptyState from '../components/EmptyState'
import RestaurantOnboarding from './RestaurantOnboarding'

export default function OwnerOrders() {
  const { restaurant } = useAuth()
  const toast = useToast()
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [updatingId, setUpdatingId] = useState(null)
  

  const load = async () => {
    if (!restaurant?.restaurantId) return
    setLoading(true)
    try {
      const data = await orderApi.getOrdersByRestaurant(restaurant.restaurantId)
      setOrders(data)
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
    const interval = setInterval(load, 15000)
    return () => clearInterval(interval)
  }, [restaurant?.restaurantId])

  const handleAdvanceStatus = async (orderId, nextStatus) => {
    setUpdatingId(orderId)
    try {
      await orderApi.updateOrderStatus(orderId, restaurant.restaurantId, nextStatus)
      toast.success(`Marked as ${nextStatus.replace(/_/g, ' ')}`)
      await load()
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setUpdatingId(null)
    }
  }

  if (!restaurant) return <RestaurantOnboarding />
  if (loading) return <LoadingSpinner label="Loading orders" />

  return (
    <div>
      <h1 className="mb-6 text-2xl font-bold text-white">Order history</h1>

      {orders.length === 0 ? (
        <EmptyState title="No orders yet" description="Orders for this restaurant will appear here." />
      ) : (
        <div className="space-y-3">
          {orders.map((order) => {
            const nextStatus = NEXT_STATUS_MAP[order.status]
            const isTerminal = TERMINAL_STATUSES.includes(order.status)
            const isUpdating = updatingId === order.orderId

            return (
              <Card key={order.orderId}>
                <div className="flex flex-wrap items-start justify-between gap-4">
                  <div>
  <p className="font-medium text-white">Order #{order.orderId}</p>
  <p className="mt-1 text-sm text-white/70">{formatDate(order.createdTime)}</p>
  {order.customerName && (
    <p className="mt-1 text-sm text-white/80">
      {order.customerName}{order.customerMobile ? ` · ${order.customerMobile}` : ''}
    </p>
  )}
  <p className="mt-2 text-sm font-semibold text-white">{formatCurrency(order.totalAmt)}</p>
</div>
                  <Badge status={order.status} />
                </div>

                <div className="mt-4 flex items-center gap-2">
                  {isTerminal ? (
                    <span className="text-xs text-white/50">
                      No further action — order is {order.status.toLowerCase()}.
                    </span>
                  ) : nextStatus ? (
                    <button
                      type="button"
                      disabled={isUpdating}
                      onClick={() => handleAdvanceStatus(order.orderId, nextStatus)}
                      className="rounded-md bg-green-600 px-3 py-1.5 text-sm font-medium text-white disabled:opacity-50"
                    >
                      {isUpdating ? 'Updating…' : `Mark as ${nextStatus.replace(/_/g, ' ')}`}
                    </button>
                  ) : (
                    <span className="text-xs text-white/50">Awaiting confirmation</span>
                  )}
                </div>
              </Card>
            )
          })}
        </div>
      )}
    </div>
  )
}