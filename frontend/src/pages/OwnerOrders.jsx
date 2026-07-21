import { useEffect, useState } from 'react'
import * as orderApi from '../api/order'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'
import { formatCurrency, formatDate, getErrorMessage, ORDER_STATUSES } from '../utils/constants'
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

  const handleStatusUpdate = async (orderId, orderStatus) => {
    setUpdatingId(orderId)
    try {
      await orderApi.updateOrderStatus(orderId, restaurant.restaurantId, orderStatus)
      toast.success('Status updated')
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
      <h1 className="mb-6 text-2xl font-bold text-gray-900">Order history</h1>

      {orders.length === 0 ? (
        <EmptyState title="No orders yet" description="Orders for this restaurant will appear here." />
      ) : (
        <div className="space-y-3">
          {orders.map((order) => (
            <Card key={order.orderId}>
              <div className="flex flex-wrap items-start justify-between gap-4">
                <div>
                  <p className="font-medium text-gray-900">Order #{order.orderId}</p>
                  <p className="mt-1 text-sm text-gray-500">{formatDate(order.createdTime)}</p>
                  <p className="mt-2 text-sm font-semibold text-gray-900">{formatCurrency(order.totalAmt)}</p>
                </div>
                <Badge status={order.status} />
              </div>
              <div className="mt-4 flex items-center gap-2">
                <select
                  defaultValue={order.status}
                  disabled={updatingId === order.orderId}
                  onChange={(e) => handleStatusUpdate(order.orderId, e.target.value)}
                  className="rounded-lg border border-gray-200 px-3 py-1.5 text-sm"
                >
                  {ORDER_STATUSES.filter((s) => s !== 'CANCELED').map((status) => (
                    <option key={status} value={status}>{status.replace(/_/g, ' ')}</option>
                  ))}
                </select>
                {updatingId === order.orderId && (
                  <span className="text-xs text-gray-500">Updating…</span>
                )}
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}