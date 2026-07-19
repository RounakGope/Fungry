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

export default function RestaurantDashboard() {
  const { user, restaurant, refreshRestaurant, setRestaurant } = useAuth()
  const toast = useToast()

  const [loading, setLoading] = useState(!restaurant)
  const [menuItems, setMenuItems] = useState([])
  const [restForm, setRestForm] = useState({ name: '', description: '', cuisine: '', address: '' })
  const [itemForm, setItemForm] = useState(emptyItem)
  const [editingItemId, setEditingItemId] = useState(null)
  const [deleteItemId, setDeleteItemId] = useState(null)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    if (!restaurant) {
      refreshRestaurant().finally(() => setLoading(false))
    } else {
      setRestForm({
        name: restaurant.name || '',
        description: restaurant.description || '',
        cuisine: restaurant.cuisine || '',
        address: restaurant.address || '',
      })
      loadMenu()
    }
  }, [restaurant?.id])

  const loadMenu = async () => {
    if (!restaurant?.id) return
    try {
      const items = await restaurantApi.getMenuItems(restaurant.id)
      setMenuItems(items)
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  if (loading) return <LoadingSpinner />
  if (!restaurant) return <RestaurantOnboarding />

  const handleUpdateRestaurant = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      const updated = await restaurantApi.updateRestaurant(restaurant.id, user.userId, restForm)
      setRestaurant(updated)
      toast.success('Restaurant updated')
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setSaving(false)
    }
  }

  const handleSaveItem = async (e) => {
    e.preventDefault()
    setSaving(true)
    const payload = { ...itemForm, price: Number(itemForm.price) }
    try {
      if (editingItemId) {
        await restaurantApi.updateMenuItem(user.userId, editingItemId, payload)
        toast.success('Item updated')
      } else {
        await restaurantApi.addMenuItem(restaurant.id, user.userId, payload)
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
      await restaurantApi.deleteMenuItem(restaurant.id, user.userId, deleteItemId)
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

  return (
    <div className="space-y-8">
      <h1 className="text-2xl font-bold text-gray-900">Restaurant dashboard</h1>

      <Card>
        <h2 className="mb-4 text-sm font-semibold text-gray-900">Restaurant details</h2>
        <form onSubmit={handleUpdateRestaurant} className="space-y-3">
          <Input label="Name" value={restForm.name} onChange={(e) => setRestForm({ ...restForm, name: e.target.value })} required />
          <Input label="Cuisine" value={restForm.cuisine} onChange={(e) => setRestForm({ ...restForm, cuisine: e.target.value })} />
          <Input label="Address" value={restForm.address} onChange={(e) => setRestForm({ ...restForm, address: e.target.value })} />
          <div>
            <label className="block text-sm font-medium text-gray-700">Description</label>
            <textarea
              value={restForm.description}
              onChange={(e) => setRestForm({ ...restForm, description: e.target.value })}
              rows={3}
              className="mt-1 w-full rounded-lg border border-gray-200 px-3 py-2 text-sm focus:border-primary-600 focus:outline-none focus:ring-1 focus:ring-primary-600"
            />
          </div>
          <Button type="submit" size="sm" disabled={saving}>Save changes</Button>
        </form>
      </Card>

      <section>
        <h2 className="mb-4 text-lg font-semibold text-gray-900">Menu items</h2>

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
      </section>

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
