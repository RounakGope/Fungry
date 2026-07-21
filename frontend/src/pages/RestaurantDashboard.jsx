import { useEffect, useState } from 'react'
import * as restaurantApi from '../api/restaurant'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'
import { formatCurrency, getErrorMessage } from '../utils/constants'
import Button from '../components/Button'
import Card from '../components/Card'
import Input from '../components/Input'
import Modal from '../components/Modal'
import LoadingSpinner from '../components/LoadingSpinner'
import RestaurantOnboarding from './RestaurantOnboarding'

const emptyItem = { name: '', description: '', price: '', available: true }

const TABS = [
  { key: 'orders', label: 'Current orders' },
  { key: 'menu', label: 'Menu' },
  { key: 'details', label: 'Restaurant details' },
]

const ORDER_FLOW = ['PENDING', 'ACCEPTED', 'PREPARING', 'READY', 'COMPLETED']

const STATUS_STYLES = {
  PENDING: 'bg-amber-100 text-amber-800',
  ACCEPTED: 'bg-blue-100 text-blue-800',
  PREPARING: 'bg-blue-100 text-blue-800',
  READY: 'bg-green-100 text-green-800',
  COMPLETED: 'bg-gray-100 text-gray-600',
  CANCELLED: 'bg-red-100 text-red-700',
}

export default function RestaurantDashboard() {
  const { user, restaurant, refreshRestaurant, setRestaurant } = useAuth()
  const toast = useToast()

  const [loading, setLoading] = useState(!restaurant)
  const [tab, setTab] = useState('orders')

  useEffect(() => {
    if (!restaurant) refreshRestaurant().finally(() => setLoading(false))
    else setLoading(false)
  }, [restaurant?.id])

  if (loading) return <LoadingSpinner />
  if (!restaurant) return <RestaurantOnboarding />

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">{restaurant.name}</h1>
        <p className="text-sm text-gray-500">Owner dashboard</p>
      </div>

      <div className="flex gap-1 border-b border-gray-200">
        {TABS.map((t) => (
          <button
            key={t.key}
            onClick={() => setTab(t.key)}
            className={`px-4 py-2 text-sm font-medium border-b-2 -mb-px transition-colors ${
              tab === t.key
                ? 'border-primary-600 text-primary-600'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            {t.label}
          </button>
        ))}
      </div>

      {tab === 'orders' && <OrdersTab restaurant={restaurant} userId={user.userId} toast={toast} />}
      {tab === 'menu' && <MenuTab restaurant={restaurant} userId={user.userId} toast={toast} />}
      {tab === 'details' && (
        <DetailsTab restaurant={restaurant} setRestaurant={setRestaurant} userId={user.userId} toast={toast} />
      )}
    </div>
  )
}

function OrdersTab({ restaurant, userId, toast }) {
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [updatingId, setUpdatingId] = useState(null)

  const loadOrders = async () => {
    try {
      const data = await restaurantApi.getRestaurantOrders(restaurant.id)
      setOrders(data)
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadOrders()
    const interval = setInterval(loadOrders, 15000) // poll for new orders
    return () => clearInterval(interval)
  }, [restaurant.id])

  const nextStatus = (status) => {
    const idx = ORDER_FLOW.indexOf(status)
    return idx >= 0 && idx < ORDER_FLOW.length - 1 ? ORDER_FLOW[idx + 1] : null
  }

  const handleAdvance = async (order) => {
    const next = nextStatus(order.status)
    if (!next) return
    setUpdatingId(order.id)
    try {
      await restaurantApi.updateOrderStatus(order.id, userId, next)
      toast.success(`Order marked ${next.toLowerCase()}`)
      await loadOrders()
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setUpdatingId(null)
    }
  }

  const handleCancel = async (order) => {
    setUpdatingId(order.id)
    try {
      await restaurantApi.updateOrderStatus(order.id, userId, 'CANCELLED')
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
        <h2 className="mb-3 text-sm font-semibold text-gray-900">Active orders ({active.length})</h2>
        {active.length === 0 ? (
          <Card><p className="text-sm text-gray-500">No active orders right now.</p></Card>
        ) : (
          <div className="space-y-3">
            {active.map((order) => (
              <Card key={order.id}>
                <div className="flex items-start justify-between">
                  <div>
                    <div className="flex items-center gap-2">
                      <p className="font-medium text-gray-900">Order #{order.id}</p>
                      <span className={`rounded-full px-2 py-0.5 text-xs font-medium ${STATUS_STYLES[order.status] || 'bg-gray-100 text-gray-600'}`}>
                        {order.status}
                      </span>
                    </div>
                    <p className="text-sm text-gray-500">{order.customerName}</p>
                    <p className="text-xs text-gray-400">{new Date(order.createdAt).toLocaleTimeString()}</p>
                  </div>
                  <p className="font-semibold text-gray-900">{formatCurrency(order.totalAmount)}</p>
                </div>

                <ul className="mt-3 space-y-1 border-t border-gray-100 pt-3 text-sm text-gray-600">
                  {order.items?.map((it, i) => (
                    <li key={i}>{it.quantity}× {it.name}</li>
                  ))}
                </ul>

                <div className="mt-3 flex gap-2">
                  {nextStatus(order.status) && (
                    <Button size="sm" disabled={updatingId === order.id} onClick={() => handleAdvance(order)}>
                      Mark {nextStatus(order.status).toLowerCase()}
                    </Button>
                  )}
                  {order.status === 'PENDING' && (
                    <Button size="sm" variant="danger" disabled={updatingId === order.id} onClick={() => handleCancel(order)}>
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
          <h2 className="mb-3 text-sm font-semibold text-gray-900">Past orders</h2>
          <div className="space-y-2">
            {past.map((order) => (
              <Card key={order.id} className="flex items-center justify-between">
                <div>
                  <p className="font-medium text-gray-900">Order #{order.id}</p>
                  <p className="text-xs text-gray-400">{new Date(order.createdAt).toLocaleDateString()}</p>
                </div>
                <span className={`rounded-full px-2 py-0.5 text-xs font-medium ${STATUS_STYLES[order.status]}`}>
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

function MenuTab({ restaurant, userId, toast }) {
  const [menuItems, setMenuItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [itemForm, setItemForm] = useState(emptyItem)
  const [editingItemId, setEditingItemId] = useState(null)
  const [deleteItemId, setDeleteItemId] = useState(null)
  const [saving, setSaving] = useState(false)

  const loadMenu = async () => {
    try {
      const items = await restaurantApi.getMenuItems(restaurant.id)
      setMenuItems(items)
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { loadMenu() }, [restaurant.id])

  const handleSaveItem = async (e) => {
    e.preventDefault()
    setSaving(true)
    const payload = { ...itemForm, price: Number(itemForm.price) }
    try {
      if (editingItemId) {
        await restaurantApi.updateMenuItem(userId, editingItemId, payload)
        toast.success('Item updated')
      } else {
        await restaurantApi.addMenuItem(restaurant.id, userId, payload)
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
      await restaurantApi.deleteMenuItem(restaurant.id, userId, deleteItemId)
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
    setEditingItemId(item.id)
    setItemForm({
      name: item.name,
      description: item.description || '',
      price: String(item.price),
      available: item.available ?? true,
    })
  }

  if (loading) return <LoadingSpinner />

  return (
    <div>
      <Card className="mb-4">
        <h3 className="mb-3 text-sm font-semibold text-gray-900">{editingItemId ? 'Edit item' : 'Add item'}</h3>
        <form onSubmit={handleSaveItem} className="space-y-3">
          <Input label="Name" value={itemForm.name} onChange={(e) => setItemForm({ ...itemForm, name: e.target.value })} required />
          <Input label="Description" value={itemForm.description} onChange={(e) => setItemForm({ ...itemForm, description: e.target.value })} />
          <Input label="Price" type="number" min="0" step="0.01" value={itemForm.price} onChange={(e) => setItemForm({ ...itemForm, price: e.target.value })} required />
          <div className="flex gap-2">
            <Button type="submit" size="sm" disabled={saving}>{editingItemId ? 'Update' : 'Add item'}</Button>
            {editingItemId && (
              <Button type="button" size="sm" variant="secondary" onClick={() => { setEditingItemId(null); setItemForm(emptyItem) }}>Cancel</Button>
            )}
          </div>
        </form>
      </Card>

      <div className="space-y-2">
        {menuItems.map((item) => (
          <Card key={item.id} className="flex items-center justify-between">
            <div>
              <p className="font-medium text-gray-900">{item.name}</p>
              {item.description && <p className="text-sm text-gray-500">{item.description}</p>}
              <p className="mt-1 text-sm font-semibold">{formatCurrency(item.price)}</p>
            </div>
            <div className="flex gap-2">
              <Button size="sm" variant="secondary" onClick={() => startEditItem(item)}>Edit</Button>
              <Button size="sm" variant="danger" onClick={() => setDeleteItemId(item.id)}>Delete</Button>
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

function DetailsTab({ restaurant, setRestaurant, userId, toast }) {
  const [form, setForm] = useState({
    name: restaurant.name || '',
    description: restaurant.description || '',
    cuisine: restaurant.cuisine || '',
    address: restaurant.address || '',
  })
  const [saving, setSaving] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      const updated = await restaurantApi.updateRestaurant(restaurant.id, userId, form)
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
        <Input label="Name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
        <Input label="Cuisine" value={form.cuisine} onChange={(e) => setForm({ ...form, cuisine: e.target.value })} />
        <Input label="Address" value={form.address} onChange={(e) => setForm({ ...form, address: e.target.value })} />
        <div>
          <label className="block text-sm font-medium text-gray-700">Description</label>
          <textarea
            value={form.description}
            onChange={(e) => setForm({ ...form, description: e.target.value })}
            rows={3}
            className="mt-1 w-full rounded-lg border border-gray-200 px-3 py-2 text-sm focus:border-primary-600 focus:outline-none focus:ring-1 focus:ring-primary-600"
          />
        </div>
        <Button type="submit" size="sm" disabled={saving}>Save changes</Button>
      </form>
    </Card>
  )
}