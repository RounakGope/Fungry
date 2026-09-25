import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import * as orderApi from '../api/order'
import { useToast } from '../context/ToastContext'
import { CANCELABLE_STATUSES, formatCurrency, formatDate, getErrorMessage } from '../utils/constants'
import Badge from '../components/Badge'
import Button from '../components/Button'
import Card from '../components/Card'
import LoadingSpinner from '../components/LoadingSpinner'
import Modal from '../components/Modal'

export default function OrderDetail() {
  const { orderId } = useParams()
  const toast = useToast()

  const [order, setOrder] = useState(null)
  const [amount, setAmount] = useState(null)
  const [loading, setLoading] = useState(true)
  const [cancelOpen, setCancelOpen] = useState(false)
  const [cancelling, setCancelling] = useState(false)

  const load = async () => {
    try {
      const [orderData, orderAmount] = await Promise.all([
        orderApi.getOrderByUser(orderId),
        orderApi.getOrderAmount(orderId).catch(() => null),
      ])
      setOrder(orderData)
      setAmount(orderAmount ?? orderData.totalAmt)
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
    const interval = setInterval(async () => {
      try {
        const status = await orderApi.getOrderStatus(orderId)
        setOrder((prev) => (prev ? { ...prev, status } : prev))
      } catch {
        /* polling failure is non-critical */
      }
    }, 10000)
    return () => clearInterval(interval)
  }, [orderId])

  const canCancel = order && CANCELABLE_STATUSES.includes(order.status)

  // Order was created with online payment but the webhook hasn't confirmed it yet.
  // Cancelling here would mark the order cancelled without triggering a refund,
  // since PaymentStatus is still PENDING even though Stripe may have already
  // captured the charge — the backend only refunds when PaymentStatus is SUCCESS.
  const isPaymentProcessing = order?.paymentMode === 'ONLINE' && order?.paymentStatus === 'PENDING'

  const handleCancel = async () => {
    setCancelling(true)
    try {
      await orderApi.cancelOrder(orderId)
      toast.success('Order cancelled')
      setCancelOpen(false)
      await load()
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setCancelling(false)
    }
  }

  if (loading) return <LoadingSpinner />

  if (!order) {
    return <p className="text-sm text-white/70">Order not found.</p>
  }

  return (
    <div className="max-w-2xl">
      <div className="mb-6 flex items-start justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white">Order #{order.orderId}</h1>
          <p className="mt-1 text-sm text-white/70">{formatDate(order.createdTime)}</p>
        </div>
        <Badge status={order.status} />
      </div>

      {isPaymentProcessing && (
        <Card className="mb-4 border-amber-500/40 bg-amber-500/10">
          <p className="text-sm text-amber-200">
            Confirming your payment — this usually takes a few seconds. The page will update automatically.
          </p>
        </Card>
      )}

      <Card className="mb-4">
        <div className="flex justify-between text-sm">
          <span className="text-white/70">Total</span>
          <span className="font-semibold text-white">{formatCurrency(amount)}</span>
        </div>
        {order.restaurantName && (
          <div className="mt-3 flex justify-between text-sm">
            <span className="text-white/70">Restaurant</span>
            <span className="text-white">{order.restaurantName}</span>
          </div>
        )}
        {order.paymentMode && (
          <div className="mt-3 flex justify-between text-sm">
            <span className="text-white/70">Payment</span>
            <span className="text-white">
              {order.paymentMode === 'CASH' ? 'Cash on delivery' : 'Paid online'}
            </span>
          </div>
        )}
      </Card>

      {order.orderItemDTO?.length > 0 && (
        <Card className="mb-4">
          <h2 className="mb-3 text-sm font-semibold text-white">Items</h2>
          <ul className="space-y-2">
            {order.orderItemDTO.map((item, i) => (
              <li key={item.orderItemId || i} className="flex justify-between text-sm">
                <span>{item.name} × {item.quantity}</span>
                <span>{formatCurrency(item.price)}</span>
              </li>
            ))}
          </ul>
        </Card>
      )}

      {canCancel && (
        <Button
          variant="danger"
          disabled={isPaymentProcessing}
          onClick={() => setCancelOpen(true)}
        >
          Cancel order
        </Button>
      )}
      {canCancel && isPaymentProcessing && (
        <p className="mt-2 text-xs text-white/50">
          Please wait until payment is confirmed before cancelling.
        </p>
      )}

      <Modal
        open={cancelOpen}
        onClose={() => setCancelOpen(false)}
        title="Cancel order?"
        confirmLabel="Cancel order"
        variant="danger"
        loading={cancelling}
        onConfirm={handleCancel}
      >
        This action cannot be undone.
      </Modal>
    </div>
  )
}