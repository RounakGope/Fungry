import { useEffect, useRef, useState } from 'react'
import * as restaurantApi from '../api/restaurant'
import * as orderApi from '../api/order'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'
import { formatCurrency, getErrorMessage } from '../utils/constants'
import Button from '../components/Button'
import Card from '../components/Card'
import Input from '../components/Input'
import Modal from '../components/Modal'
import LoadingSpinner from '../components/LoadingSpinner'
import RestaurantOnboarding from './RestaurantOnboarding'
const FOOD_CATEGORIES = ['BREAD', 'MAIN_COURSE', 'INDIAN', 'CONTINENTAL', 'CHINESE', 'DESERT']
const FOOD_TYPES = ['VEG', 'NON_VEG']

const emptyItem = {
  foodName: '',
  price: '',
  availableQuantity: '',
  foodCategory: '',
  foodType: '',
  isAvailable: true,
}

const TABS = [
  { key: 'orders', label: 'Current orders' },
  { key: 'menu', label: 'Menu' },
  { key: 'details', label: 'Restaurant details' },
]

const ORDER_FLOW = ['PLACED', 'CONFIRMED', 'PREPARING', 'OUT_FOR_DELIVERY', 'DELIVERED']

const STATUS_STYLES = {
  PLACED: 'bg-amber-100 text-amber-800',
  CREATED: 'bg-amber-100 text-amber-800',
  PAYMENT_PENDING: 'bg-amber-100 text-amber-800',
  CONFIRMED: 'bg-blue-100 text-blue-800',
  PREPARING: 'bg-blue-100 text-blue-800',
  OUT_FOR_DELIVERY: 'bg-blue-100 text-blue-800',
  DELIVERED: 'bg-green-100 text-green-800',
  CANCELED: 'bg-red-100 text-red-700',
}

export default function RestaurantDashboard() {
  const { user, restaurant, refreshRestaurant, setRestaurant } = useAuth()
  const toast = useToast()
  const fetchedRef = useRef(false)

  const [loading, setLoading] = useState(!restaurant)
  const [tab, setTab] = useState('orders')

  useEffect(() => {
    if (fetchedRef.current) return
    fetchedRef.current = true
    if (!restaurant) {
      refreshRestaurant().finally(() => setLoading(false))
    } else {
      setLoading(false)
    }
  }, [])

  if (loading) return <LoadingSpinner />
  if (!restaurant) return <RestaurantOnboarding />

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">{restaurant.name}</h1>
        <p className="text-sm text-white/70">Owner dashboard</p>
      </div>

      <div className="flex gap-1 border-b border-gray-200">
        {TABS.map((t) => (
          <button
            key={t.key}
            onClick={() => setTab(t.key)}
            className={`px-4 py-2 text-sm font-medium border-b-2 -mb-px transition-colors ${
              tab === t.key
                ? 'border-primary-600 text-primary-600'
                : 'border-transparent text-white/70 hover:text-white/90'
            }`}
          >
            {t.label}
          </button>
        ))}
      </div>

      {tab === 'orders' && (
        <OrdersTab restaurant={restaurant} toast={toast} />
      )}
      {tab === 'menu' && (
        <MenuTab restaurant={restaurant} userId={user.id} toast={toast} />
      )}
      {tab === 'details' && (
        <DetailsTab
          restaurant={restaurant}
          setRestaurant={setRestaurant}
          userId={user.id}
          toast={toast}
        />
      )}
    </div>
  )
}

// ─── Orders Tab ───────────────────────────────────────────────────────────────

function OrdersTab({ restaurant, toast }) {
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [updatingId, setUpdatingId] = useState(null)

  const loadOrders = async () => {
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
    loadOrders()
    const interval = setInterval(loadOrders, 15000)
    return () => clearInterval(interval)
  }, [restaurant.restaurantId])

  const nextStatus = (status) => {
    const idx = ORDER_FLOW.indexOf(status)
    return idx >= 0 && idx < ORDER_FLOW.length - 1 ? ORDER_FLOW[idx + 1] : null
  }

  const handleAdvance = async (order) => {
    const next = nextStatus(order.status)
    if (!next) return
    setUpdatingId(order.orderId)
    try {
      await orderApi.updateOrderStatus(order.orderId, restaurant.restaurantId, next)
      toast.success(`Order marked ${next.toLowerCase()}`)
      await loadOrders()
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setUpdatingId(null)
    }
  }

  const handleCancel = async (order) => {
    setUpdatingId(order.orderId)
    try {
      await orderApi.updateOrderStatus(order.orderId, restaurant.restaurantId, 'CANCELLED')
      toast.success('Order cancelled')
      await loadOrders()
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setUpdatingId(null)
    }
  }

  if (loading) return <LoadingSpinner />

  const active = orders.filter((o) => !['COMPLETED', 'CANCELLED'].includes(o.status))
  const past = orders.filter((o) => ['COMPLETED', 'CANCELLED'].includes(o.status))

  return (
    <div className="space-y-6">
      <section>
        <h2 className="mb-3 text-sm font-semibold text-white">
          Active orders ({active.length})
        </h2>
        {active.length === 0 ? (
          <Card>
            <p className="text-sm text-white/70">No active orders right now.</p>
          </Card>
        ) : (
          <div className="space-y-3">
            {active.map((order) => (
              <Card key={order.orderId}>
                <div className="flex items-start justify-between">
                  <div>
                    <div className="flex items-center gap-2">
                      <p className="font-medium text-white">Order #{order.orderId}</p>
                      <span
                        className={`rounded-full px-2 py-0.5 text-xs font-medium ${
                          STATUS_STYLES[order.status] || 'bg-gray-100 text-white/80'
                        }`}
                      >
                        {order.status}
                      </span>
                    </div>
                    {order.expecetedTimeInMinutes && (
                      <p className="text-xs text-white/60">
                        ETA: {order.expecetedTimeInMinutes} mins
                      </p>
                    )}
                    <p className="text-xs text-white/60">
                      {new Date(order.createdTime).toLocaleTimeString()}
                    </p>
                  </div>
                  <p className="font-semibold text-white">
                    {formatCurrency(order.totalAmt)}
                  </p>
                </div>

                <ul className="mt-3 space-y-1 border-t border-gray-100 pt-3 text-sm text-white/80">
                  {order.orderItemDTO?.map((it) => (
                    <li key={it.orderItemId}>
                      {it.quantity}× {it.name} — {formatCurrency(it.price)}
                    </li>
                  ))}
                </ul>

                <div className="mt-3 flex gap-2">
                  {nextStatus(order.status) && (
                    <Button
                      size="sm"
                      disabled={updatingId === order.orderId}
                      onClick={() => handleAdvance(order)}
                    >
                      Mark {nextStatus(order.status).toLowerCase()}
                    </Button>
                  )}
                  {order.status === 'PENDING' && (
                    <Button
                      size="sm"
                      variant="danger"
                      disabled={updatingId === order.orderId}
                      onClick={() => handleCancel(order)}
                    >
                      Reject
                    </Button>
                  )}
                </div>
              </Card>
            ))}
          </div>
        )}
      </section>

      {past.length > 0 && (
        <section>
          <h2 className="mb-3 text-sm font-semibold text-white">Past orders</h2>
          <div className="space-y-2">
            {past.map((order) => (
              <Card key={order.orderId} className="flex items-center justify-between">
                <div>
                  <p className="font-medium text-white">Order #{order.orderId}</p>
                  <p className="text-xs text-white/60">
                    {new Date(order.createdTime).toLocaleDateString()}
                  </p>
                </div>
                <span
                  className={`rounded-full px-2 py-0.5 text-xs font-medium ${
                    STATUS_STYLES[order.status]
                  }`}
                >
                  {order.status}
                </span>
              </Card>
            ))}
          </div>
        </section>
      )}
    </div>
  )
}

// ─── Menu Tab ─────────────────────────────────────────────────────────────────

function MenuTab({ restaurant, userId, toast }) {
  const [menuItems, setMenuItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [itemForm, setItemForm] = useState(emptyItem)
  const [editingItemId, setEditingItemId] = useState(null)
  const [deleteItemId, setDeleteItemId] = useState(null)
  const [saving, setSaving] = useState(false)

  const loadMenu = async () => {
    try {
      const items = await restaurantApi.getMenuItems(restaurant.restaurantId, 'menuItemId', 'asc')
      setMenuItems(items)
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadMenu()
  }, [restaurant.restaurantId])

  const handleSaveItem = async (e) => {
    e.preventDefault()
    setSaving(true)
    const payload = {
      foodName: itemForm.foodName,
      price: Number(itemForm.price),
      availableQuantity: Number(itemForm.availableQuantity),
      foodCategory: itemForm.foodCategory,
      foodType: itemForm.foodType,
      isAvailable: itemForm.isAvailable,
    }
    try {
      if (editingItemId) {
        await restaurantApi.updateMenuItem(userId, editingItemId, payload)
        toast.success('Item updated')
      } else {
        await restaurantApi.addMenuItem(restaurant.restaurantId, userId, payload)
        toast.success('Item added')
      }
      setItemForm(emptyItem)
      setEditingItemId(null)
      await loadMenu()
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setSaving(false)
    }
  }

  const handleDeleteItem = async () => {
    setSaving(true)
    try {
      await restaurantApi.deleteMenuItem(restaurant.restaurantId, userId, deleteItemId)
      toast.success('Item deleted')
      setDeleteItemId(null)
      await loadMenu()
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setSaving(false)
    }
  }

  const startEditItem = (item) => {
    setEditingItemId(item.menuItemId)
    setItemForm({
      foodName: item.foodName,
      price: String(item.price),
      availableQuantity: String(item.availableQuantity),
      foodCategory: item.foodCategory || '',
      foodType: item.foodType || '',
      isAvailable: item.isAvailable ?? true,
    })
  }

  if (loading) return <LoadingSpinner />

  return (
    <div>
      <Card className="mb-4">
        <h3 className="mb-3 text-sm font-semibold text-white">
          {editingItemId ? 'Edit item' : 'Add item'}
        </h3>
        <form onSubmit={handleSaveItem} className="space-y-3">
          <Input
            label="Food name"
            value={itemForm.foodName}
            onChange={(e) => setItemForm({ ...itemForm, foodName: e.target.value })}
            required
          />
          <Input
            label="Price (₹)"
            type="number"
            min="0"
            step="1"
            value={itemForm.price}
            onChange={(e) => setItemForm({ ...itemForm, price: e.target.value })}
            required
          />
          <Input
            label="Available quantity"
            type="number"
            min="0"
            value={itemForm.availableQuantity}
            onChange={(e) => setItemForm({ ...itemForm, availableQuantity: e.target.value })}
            required
          />
          <div className="grid grid-cols-2 gap-3">
  <div>
    <label className="block text-sm font-medium text-white/90">Category</label>
    <select
      value={itemForm.foodCategory}
      onChange={(e) => setItemForm({ ...itemForm, foodCategory: e.target.value })}
      required
      className="mt-1 w-full rounded-lg border border-gray-200 px-3 py-2 text-sm focus:border-primary-600 focus:outline-none focus:ring-1 focus:ring-primary-600"
    >
      <option value="" disabled>Select category</option>
      {FOOD_CATEGORIES.map((c) => (
        <option key={c} value={c}>{c.replace('_', ' ')}</option>
      ))}
    </select>
  </div>
  <div>
    <label className="block text-sm font-medium text-white/90">Type</label>
    <select
      value={itemForm.foodType}
      onChange={(e) => setItemForm({ ...itemForm, foodType: e.target.value })}
      required
      className="mt-1 w-full rounded-lg border border-gray-200 px-3 py-2 text-sm focus:border-primary-600 focus:outline-none focus:ring-1 focus:ring-primary-600"
    >
      <option value="" disabled>Select type</option>
      {FOOD_TYPES.map((t) => (
        <option key={t} value={t}>{t.replace('_', ' ')}</option>
      ))}
    </select>
  </div>
</div>
          <label className="flex items-center gap-2 text-sm text-white/90">
            <input
              type="checkbox"
              checked={itemForm.isAvailable}
              onChange={(e) => setItemForm({ ...itemForm, isAvailable: e.target.checked })}
              className="rounded border-gray-300"
            />
            Available
          </label>
          <div className="flex gap-2">
            <Button type="submit" size="sm" disabled={saving}>
              {editingItemId ? 'Update' : 'Add item'}
            </Button>
            {editingItemId && (
              <Button
                type="button"
                size="sm"
                variant="secondary"
                onClick={() => {
                  setEditingItemId(null)
                  setItemForm(emptyItem)
                }}
              >
                Cancel
              </Button>
            )}
          </div>
        </form>
      </Card>

      <div className="space-y-2">
        {menuItems.map((item) => (
          <Card key={item.menuItemId} className="flex items-center justify-between">
            <div>
              <div className="flex items-center gap-2">
                <p className="font-medium text-white">{item.foodName}</p>
                {!item.isAvailable && (
                  <span className="rounded-full bg-red-100 px-2 py-0.5 text-xs text-red-600">
                    Unavailable
                  </span>
                )}
              </div>
              <p className="text-xs text-white/60">
                {item.foodCategory} · {item.foodType} · Stock: {item.availableQuantity}
              </p>
              <p className="mt-1 text-sm font-semibold text-white">
                {formatCurrency(item.price)}
              </p>
            </div>
            <div className="flex gap-2">
              <Button size="sm" variant="secondary" onClick={() => startEditItem(item)}>
                Edit
              </Button>
              <Button size="sm" variant="danger" onClick={() => setDeleteItemId(item.menuItemId)}>
                Delete
              </Button>
            </div>
          </Card>
        ))}
      </div>

      <Modal
        open={!!deleteItemId}
        onClose={() => setDeleteItemId(null)}
        title="Delete menu item?"
        confirmLabel="Delete"
        variant="danger"
        loading={saving}
        onConfirm={handleDeleteItem}
      >
        This item will be removed from your menu.
      </Modal>
    </div>
  )
}

// ─── Details Tab ──────────────────────────────────────────────────────────────

function DetailsTab({ restaurant, setRestaurant, userId, toast }) {
  const [form, setForm] = useState({
    name: restaurant.name || '',
    description: restaurant.description || '',
    cuisine: restaurant.cuisine || '',
    addressDTO: {
      street: '',
      area: '',
      city: '',
      state: '',
      zipcode: '',
    },
  })
  const [saving, setSaving] = useState(false)

  const setAddr = (field, value) =>
    setForm((prev) => ({ ...prev, addressDTO: { ...prev.addressDTO, [field]: value } }))

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      const payload = {
        id: restaurant.restaurantId,
        name: form.name,
        description: form.description,
        cuisine: form.cuisine,
        addressDTO: {
          ...form.addressDTO,
          zipcode: Number(form.addressDTO.zipcode),
        },
      }
      const updated = await restaurantApi.updateRestaurant(restaurant.restaurantId, userId, payload)
      setRestaurant(updated)
      toast.success('Restaurant updated')
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setSaving(false)
    }
  }

  return (
    <Card className="max-w-lg">
      <form onSubmit={handleSubmit} className="space-y-3">
        <Input label="Name" value={form.name}
          onChange={(e) => setForm({ ...form, name: e.target.value })} required />
        <Input label="Cuisine" value={form.cuisine}
          onChange={(e) => setForm({ ...form, cuisine: e.target.value })} />
        <div>
          <label className="block text-sm font-medium text-white/90">Description</label>
          <textarea value={form.description}
            onChange={(e) => setForm({ ...form, description: e.target.value })}
            rows={3}
            className="mt-1 w-full rounded-lg border border-gray-200 px-3 py-2 text-sm focus:border-primary-600 focus:outline-none focus:ring-1 focus:ring-primary-600"
          />
        </div>

        <p className="pt-1 text-sm font-medium text-white/90">Address</p>
        <Input label="Street" value={form.addressDTO.street}
          onChange={(e) => setAddr('street', e.target.value)} required />
        <Input label="Area" value={form.addressDTO.area}
          onChange={(e) => setAddr('area', e.target.value)} />
        <div className="grid grid-cols-2 gap-3">
          <Input label="City" value={form.addressDTO.city}
            onChange={(e) => setAddr('city', e.target.value)} required />
          <Input label="State" value={form.addressDTO.state}
            onChange={(e) => setAddr('state', e.target.value)} required />
        </div>
        <Input label="Zipcode" type="number" value={form.addressDTO.zipcode}
          onChange={(e) => setAddr('zipcode', e.target.value)} required />

        <Button type="submit" size="sm" disabled={saving}>Save changes</Button>
      </form>
    </Card>
  )
}