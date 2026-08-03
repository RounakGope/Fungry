import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import * as addressApi from '../api/address'
import * as orderApi from '../api/order'
import { useAuth } from '../context/AuthContext'
import { useCart } from '../context/CartContext'
import { useToast } from '../context/ToastContext'
import { formatCurrency, getErrorMessage } from '../utils/constants'
import Button from '../components/Button'
import Card from '../components/Card'
import Input from '../components/Input'
import LoadingSpinner from '../components/LoadingSpinner'
import EmptyState from '../components/EmptyState'

// Matches AddressDTO: addressId, zipcode, address, landmark, houseNumber, state
// Change zipcode to zipCode and landmark to landMark
const emptyAddress = { address: '', houseNumber: '', landMark: '', state: '', zipCode: '' }

export default function Checkout() {
  const { user } = useAuth()
  const { cart, total, fetchCart } = useCart()
  const toast = useToast()
  const navigate = useNavigate()

  const [addresses, setAddresses] = useState([])
  const [selectedAddressId, setSelectedAddressId] = useState(null)
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState(emptyAddress)
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    addressApi.getUserAddresses(user.id)
      .then((data) => {
        setAddresses(data)
        const defaultAddr = data.find((a) => a.isDefault) || data[0]
        if (defaultAddr) setSelectedAddressId(defaultAddr.addressId)
      })
      .catch((err) => toast.error(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }, [user.id])

  const handleAddAddress = async (e) => {
    e.preventDefault()
    try {
      const created = await addressApi.createAddress(user.id, form)
      setAddresses((prev) => [...prev, created])
      setSelectedAddressId(created.addressId)
      setForm(emptyAddress)
      setShowForm(false)
      toast.success('Address added')
    } catch (err) {
      toast.error(getErrorMessage(err))
    }
  }

  const handlePlaceOrder = async () => {
    if (!selectedAddressId) {
      toast.error('Select a delivery address')
      return
    }
    if (!cart?.cartId) {
      toast.error('Cart is empty')
      return
    }
    setSubmitting(true)
    try {
     const order = await orderApi.createOrder(cart.cartId, user.id, selectedAddressId)
      await fetchCart()
      toast.success('Order placed')
      navigate(`/orders/${order.orderId}`)
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) return <LoadingSpinner />

  return (
    <div className="max-w-2xl">
      <h1 className="mb-6 text-2xl font-bold text-white">Checkout</h1>

      <section className="mb-8">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-lg font-semibold text-white">Delivery address</h2>
          <Button size="sm" variant="secondary" onClick={() => setShowForm(!showForm)}>
            {showForm ? 'Cancel' : 'Add address'}
          </Button>
        </div>

        {showForm && (
          <Card className="mb-4">
            <form onSubmit={handleAddAddress} className="space-y-3">
              <Input
                label="Address"
                value={form.address}
                onChange={(e) => setForm({ ...form, address: e.target.value })}
                required
              />
              <div className="grid gap-3 sm:grid-cols-2">
                <Input
                  label="House number"
                  type="number"
                  value={form.houseNumber}
                  onChange={(e) => setForm({ ...form, houseNumber: e.target.value })}
                  required
                />
                <Input
                  label="Landmark"
                  value={form.landMark}
                  onChange={(e) => setForm({ ...form, landMark: e.target.value })}
                />
              </div>
              <div className="grid gap-3 sm:grid-cols-2">
                <Input
                  label="State"
                  value={form.state}
                  onChange={(e) => setForm({ ...form, state: e.target.value })}
                  required
                />
                <Input
                  label="Zip code"
                  type="number"
                  value={form.zipCode}
                  onChange={(e) => setForm({ ...form, zipCode: e.target.value })}
                  required
                />
              </div>
              <Button type="submit" size="sm">Save address</Button>
            </form>
          </Card>
        )}

        {addresses.length === 0 ? (
          <EmptyState title="No saved addresses" description="Add a delivery address to continue." />
        ) : (
          <div className="space-y-2">
            {addresses.map((addr) => (
              <label
                key={addr.addressId}
                className={`flex cursor-pointer items-start gap-3 rounded-lg border p-4 ${selectedAddressId === addr.addressId ? 'border-primary-600 bg-primary-50' : 'border-gray-200 bg-white'
                  }`}
              >
                <input
                  type="radio"
                  name="address"
                  checked={selectedAddressId === addr.addressId}
                  onChange={() => setSelectedAddressId(addr.addressId)}
                  className="mt-1"
                />
                <div className="text-sm">
                  <p className="font-medium text-white">
                    {addr.houseNumber ? `${addr.houseNumber}, ` : ''}{addr.address}
                  </p>
                  {addr.landmark && <p className="text-white/70">Near {addr.landmark}</p>}
                  <p className="text-white/70">{addr.state} {addr.zipcode}</p>
                </div>
              </label>
            ))}
          </div>
        )}
      </section>

      <Card>
        <div className="flex items-center justify-between">
          <span className="font-semibold text-white">Order total</span>
          <span className="text-lg font-bold">{formatCurrency(total)}</span>
        </div>
        <Button
          className="mt-4 w-full"
          disabled={submitting || !selectedAddressId}
          onClick={handlePlaceOrder}
        >
          {submitting ? 'Placing order…' : 'Place order'}
        </Button>
      </Card>
    </div>
  )
}