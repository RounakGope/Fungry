import { useNavigate } from 'react-router-dom'
import { useCart } from '../context/CartContext'
import { useToast } from '../context/ToastContext'
import { formatCurrency, getErrorMessage } from '../utils/constants'
import Button from '../components/Button'
import Card from '../components/Card'
import EmptyState from '../components/EmptyState'
import LoadingSpinner from '../components/LoadingSpinner'
import Modal from '../components/Modal'
import { useState } from 'react'

export default function Cart() {
  const { cart, loading, total, increaseItem, removeItem, clear } = useCart()
  const toast = useToast()
  const navigate = useNavigate()
  const [clearOpen, setClearOpen] = useState(false)
  const [actionLoading, setActionLoading] = useState(false)

  const items = cart?.cartItemDTOS || []

  const handleIncrease = async (cartItemId) => {
    try {
      await increaseItem(cartItemId)
    } catch (err) {
      toast.error(getErrorMessage(err))
    }
  }

  const handleRemove = async (cartItemId) => {
    try {
      await removeItem(cartItemId)
      toast.success('Item removed')
    } catch (err) {
      toast.error(getErrorMessage(err))
    }
  }

  const handleClear = async () => {
    setActionLoading(true)
    try {
      await clear()
      toast.success('Cart cleared')
      setClearOpen(false)
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setActionLoading(false)
    }
  }

  if (loading) return <LoadingSpinner label="Loading cart" />

  if (items.length === 0) {
    return (
      <EmptyState
        title="Your cart is empty"
        description="Browse restaurants and add items to get started."
        action={<Button onClick={() => navigate('/')}>Browse restaurants</Button>}
      />
    )
  }

  return (
    <div>
      <div className="mb-6 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <h1 className="text-2xl font-semibold tracking-tight text-gray-900">Cart</h1>
        <Button variant="danger" size="sm" className="w-full sm:w-auto" onClick={() => setClearOpen(true)}>Clear cart</Button>
      </div>

      <div className="space-y-3">
        {items.map((item) => (
          <Card key={item.cartItemId} className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h3 className="font-medium text-gray-900">{item.itemName}</h3>
              <p className="mt-1 text-sm text-gray-500">Qty: {item.quantity}</p>
              {item.price != null && (
                <p className="mt-1 text-sm font-medium text-gray-900">
                  {formatCurrency(item.price * item.quantity)}
                </p>
              )}
            </div>
            <div className="flex items-center gap-2">
              <Button size="sm" variant="secondary" className="flex-1 sm:flex-none" onClick={() => handleIncrease(item.cartItemId)}>+</Button>
              <Button size="sm" variant="danger" className="flex-1 sm:flex-none" onClick={() => handleRemove(item.cartItemId)}>Remove</Button>
            </div>
          </Card>
        ))}
      </div>

      <Card className="mt-6 p-4 sm:p-5">
        <div className="flex items-center justify-between">
          <span className="text-base font-semibold text-gray-900">Total</span>
          <span className="text-lg font-bold text-gray-900">{formatCurrency(total)}</span>
        </div>
        <Button className="mt-4 w-full" onClick={() => navigate('/checkout')}>
          Proceed to checkout
        </Button>
      </Card>

      <Modal
        open={clearOpen}
        onClose={() => setClearOpen(false)}
        title="Clear cart?"
        confirmLabel="Clear"
        variant="danger"
        loading={actionLoading}
        onConfirm={handleClear}
      >
        All items will be removed from your cart.
      </Modal>
    </div>
  )
}
