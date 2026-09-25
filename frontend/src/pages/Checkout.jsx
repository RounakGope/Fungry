import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import * as addressApi from '../api/address'
import * as orderApi from '../api/order'
import { useCart } from '../context/CartContext'
import { useToast } from '../context/ToastContext'
import { formatCurrency, getErrorMessage } from '../utils/constants'
import Button from '../components/Button'
import Card from '../components/Card'
import Input from '../components/Input'
import LoadingSpinner from '../components/LoadingSpinner'
import EmptyState from '../components/EmptyState'
import StripePayment from '../components/StripePayment'

const emptyAddressForm = { houseNumber: '', address: '', landMark: '', state: '', country: '', zipCode: '' }
export default function Checkout() {
  const { total, fetchCart } = useCart()
  const toast = useToast()
  const navigate = useNavigate()

  const [addresses, setAddresses] = useState([])
  const [selectedAddressId, setSelectedAddressId] = useState(null)
  const [loading, setLoading] = useState(true)

  const [step, setStep] = useState('address') // 'address' | 'payment-method' | 'cod' | 'online'
  const [orderId, setOrderId] = useState(null)
  const [orderTotal, setOrderTotal] = useState(0)
  const [submitting, setSubmitting] = useState(false)

  // Inline add-address form state
  const [showAddForm, setShowAddForm] = useState(false)
  const [addressForm, setAddressForm] = useState(emptyAddressForm)
  const [savingAddress, setSavingAddress] = useState(false)

  const loadAddresses = async () => {
    const data = await addressApi.getUserAddresses()
    setAddresses(data)
    return data
  }

  useEffect(() => {
    let cancelled = false

    const init = async () => {
      try {
        const data = await loadAddresses()
        if (cancelled) return
        if (data.length > 0) {
          setSelectedAddressId(data[0].addressId)
        } else {
          setShowAddForm(true) // no saved addresses — open the form right away
        }
      } catch (err) {
        if (!cancelled) toast.error(getErrorMessage(err))
      } finally {
        if (!cancelled) setLoading(false)
      }
    }

    init()
    return () => {
      cancelled = true
    }
  }, [])

  const handleAddAddress = async (e) => {
    e.preventDefault()
    setSavingAddress(true)
    try {
    const payload = {
  houseNumber: Number(addressForm.houseNumber),
  address: addressForm.address,
  landMark: addressForm.landMark,
  state: addressForm.state,
  country: addressForm.country,
  zipCode: Number(addressForm.zipCode),
}
      const created = await addressApi.createAddress(payload)
      toast.success('Address added')
      setAddressForm(emptyAddressForm)
      setShowAddForm(false)
      const data = await loadAddresses()
      // select the newly created address if we can identify it, else fall back to first
      const newId = created?.addressId ?? data[data.length - 1]?.addressId
      if (newId) setSelectedAddressId(newId)
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setSavingAddress(false)
    }
  }

  const handleContinueToPayment = async () => {
    if (!selectedAddressId) {
      toast.error('Select a delivery address')
      return
    }
    setSubmitting(true)
    try {
      const order = await orderApi.createOrder()
      await orderApi.setOrderAddress(order.orderId, selectedAddressId)
      setOrderTotal(total) // snapshot before cart clears
      await fetchCart() // cart is now cleared server-side
      setOrderId(order.orderId)
      setStep('payment-method')
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setSubmitting(false)
    }
  }

  const handleConfirmCod = async () => {
    setSubmitting(true)
    try {
      await orderApi.confirmCodOrder(orderId)
      toast.success('Order confirmed')
      navigate(`/orders/${orderId}`)
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) return <LoadingSpinner />

  if (step === 'address') {
    return (
      <div className="max-w-2xl">
        <h1 className="mb-6 text-2xl font-semibold text-zinc-50">Select delivery address</h1>

        {addresses.length === 0 && !showAddForm ? (
          <EmptyState
            title="No saved addresses"
            description="Add a delivery address to continue."
            action={<Button onClick={() => setShowAddForm(true)}>Add address</Button>}
          />
        ) : (
          <div className="space-y-3 mb-4">
            {addresses.map((addr) => (
              <Card
                key={addr.addressId}
                className={`cursor-pointer ${selectedAddressId === addr.addressId ? 'ring-2 ring-green-500' : ''}`}
                onClick={() => setSelectedAddressId(addr.addressId)}
              >
                <p className="font-medium text-zinc-50">
                  {addr.houseNumber}, {addr.address}
                </p>
                {addr.landMark && (
                  <p className="text-sm text-muted">Landmark: {addr.landMark}</p>
                )}
                <p className="text-sm text-muted">{addr.state} - {addr.zipCode}</p>
              </Card>
            ))}
          </div>
        )}

        {addresses.length > 0 && !showAddForm && (
          <Button variant="secondary" size="sm" className="mb-4" onClick={() => setShowAddForm(true)}>
            + Add new address
          </Button>
        )}

        {showAddForm && (
          <Card className="mb-4">
            <h3 className="mb-3 text-sm font-semibold text-zinc-50">Add address</h3>
            <form onSubmit={handleAddAddress} className="space-y-3">
              <div className="grid gap-3 sm:grid-cols-2">
                <Input
                  label="House / flat no."
                  type="number"
                  value={addressForm.houseNumber}
                  onChange={(e) => setAddressForm({ ...addressForm, houseNumber: e.target.value })}
                  required
                />
                <Input
                  label="ZIP code"
                  type="number"
                  value={addressForm.zipCode}
                  onChange={(e) => setAddressForm({ ...addressForm, zipCode: e.target.value })}
                  required
                />
              </div>
              <Input
                label="Address"
                value={addressForm.address}
                onChange={(e) => setAddressForm({ ...addressForm, address: e.target.value })}
                required
              />
              <Input
                label="Landmark (optional)"
                value={addressForm.landMark}
                onChange={(e) => setAddressForm({ ...addressForm, landMark: e.target.value })}
              />
              <Input
                label="State"
                value={addressForm.state}
                onChange={(e) => setAddressForm({ ...addressForm, state: e.target.value })}
                required
              />
              <div className="grid gap-3 sm:grid-cols-2">

  <Input
    label="Country"
    value={addressForm.country}
    onChange={(e) => setAddressForm({ ...addressForm, country: e.target.value })}
    required
  />
</div>
              <div className="flex gap-2">
                <Button type="submit" size="sm" disabled={savingAddress}>
                  {savingAddress ? 'Saving…' : 'Save address'}
                </Button>
                {addresses.length > 0 && (
                  <Button
                    type="button"
                    size="sm"
                    variant="secondary"
                    onClick={() => { setShowAddForm(false); setAddressForm(emptyAddressForm) }}
                  >
                    Cancel
                  </Button>
                )}
              </div>
            </form>
          </Card>
        )}

        <Card>
          <div className="flex items-center justify-between">
            <span className="font-semibold text-zinc-50">Order total</span>
            <span className="text-lg font-bold">{formatCurrency(total)}</span>
          </div>
          <Button
            className="mt-4 w-full"
            disabled={submitting || !selectedAddressId}
            onClick={handleContinueToPayment}
          >
            {submitting ? 'Creating order…' : 'Continue to payment'}
          </Button>
        </Card>
      </div>
    )
  }

  if (step === 'payment-method') {
    return (
      <div className="max-w-2xl">
        <h1 className="mb-6 text-2xl font-semibold text-zinc-50">Choose payment method</h1>
        <div className="space-y-3">
          <Card className="cursor-pointer" onClick={() => setStep('cod')}>
            <p className="font-medium text-zinc-50">Cash on delivery</p>
            <p className="text-sm text-muted">Pay when your order arrives</p>
          </Card>
          <Card className="cursor-pointer" onClick={() => setStep('online')}>
            <p className="font-medium text-zinc-50">Pay online</p>
            <p className="text-sm text-muted">Card, UPI, or other methods</p>
          </Card>
        </div>
      </div>
    )
  }

  if (step === 'cod') {
    return (
      <Card className="max-w-md">
        <p className="mb-4 text-zinc-50">Confirm cash on delivery for {formatCurrency(orderTotal)}?</p>
        <Button className="w-full" disabled={submitting} onClick={handleConfirmCod}>
          {submitting ? 'Confirming…' : 'Confirm order'}
        </Button>
      </Card>
    )
  }

  if (step === 'online') {
    return (
      <div className="max-w-md">
        <h1 className="mb-6 text-2xl font-semibold text-zinc-50">Pay {formatCurrency(orderTotal)}</h1>
        <Card>
          <StripePayment
            orderId={orderId}
            onConfirmed={() => navigate(`/orders/${orderId}`)}
          />
        </Card>
      </div>
    )
  }
}