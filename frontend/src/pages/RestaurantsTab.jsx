import { useEffect, useState } from 'react'
import * as restaurantsApi from '../api/restaurant' // matches AuthContext's import path
import * as adminApi from '../api/admin'
import { useToast } from '../context/ToastContext'
import { useAuth } from '../context/AuthContext'
import { getErrorMessage } from '../utils/constants'
import Button from '../components/Button'
import Card from '../components/Card'
import LoadingSpinner from '../components/LoadingSpinner'
import EmptyState from '../components/EmptyState'

export default function RestaurantsTab() {
  const toast = useToast()
  const { user } = useAuth() // user.id — the acting admin
  const [restaurants, setRestaurants] = useState([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [expandedId, setExpandedId] = useState(null)
  const [showAddForm, setShowAddForm] = useState(false)
  const size = 10

  const load = async () => {
    setLoading(true)
    try {
      const data = await restaurantsApi.getAllRestaurants(page, size, 'asc', 'restaurantId')
      setRestaurants(data)
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [page])

  const toggleExpand = (id) => {
    setExpandedId((prev) => (prev === id ? null : id))
  }

  const handleDelete = async (restId) => {
    if (!window.confirm('Delete this restaurant? This cannot be undone.')) return
    try {
      // FIXED: deleteRestaurant(restId) — no userId param
      await restaurantsApi.deleteRestaurant(restId)
      toast.success('Restaurant deleted')
      if (expandedId === restId) setExpandedId(null)
      load()
    } catch (err) {
      toast.error(getErrorMessage(err))
    }
  }

  return (
    <div>
      <div className="mb-4 flex items-center justify-between">
        <h2 className="text-lg font-semibold text-white">All restaurants</h2>
        <Button size="sm" onClick={() => setShowAddForm((s) => !s)}>
          {showAddForm ? 'Cancel' : '+ Add restaurant'}
        </Button>
      </div>

      {showAddForm && (
        <div className="mb-4">
          <AddRestaurantForm
            onCreated={() => { setShowAddForm(false); load() }}
            toast={toast}
          />
        </div>
      )}

      {loading ? (
        <LoadingSpinner label="Loading restaurants" />
      ) : restaurants.length === 0 ? (
        <EmptyState title="No restaurants found" description="Nothing here yet." />
      ) : (
        <div className="space-y-2">
          {restaurants.map((rest) => (
            <Card key={rest.restaurantId} className="p-0 overflow-hidden">
              <button
                onClick={() => toggleExpand(rest.restaurantId)}
                className="flex w-full items-center justify-between px-4 py-3 text-left hover:bg-surface-overlay"
              >
                <div>
                  <p className="font-medium text-white">{rest.name}</p>
                  <p className="text-sm text-white/70">{rest.cuisine}</p>
                </div>
                <div className="flex items-center gap-3">
                  <span className="text-sm text-white/80">
                    {rest.rating != null ? `★ ${rest.rating.toFixed(1)}` : 'No rating'}
                  </span>
                  <span className="text-white/60">{expandedId === rest.restaurantId ? '▲' : '▼'}</span>
                </div>
              </button>

              {expandedId === rest.restaurantId && (
                <RestaurantDetailPanel
                  restaurant={rest}
                  onUpdated={load}
                  onDelete={() => handleDelete(rest.restaurantId)}
                  toast={toast}
                />
              )}
            </Card>
          ))}
        </div>
      )}

      <div className="mt-6 flex justify-center gap-3">
        <button
          disabled={page === 0}
          onClick={() => { setPage((p) => p - 1); setExpandedId(null) }}
          className="rounded-lg border border-border px-4 py-2 text-sm disabled:opacity-50"
        >
          Previous
        </button>
        <button
          disabled={restaurants.length < size}
          onClick={() => { setPage((p) => p + 1); setExpandedId(null) }}
          className="rounded-lg border border-border px-4 py-2 text-sm disabled:opacity-50"
        >
          Next
        </button>
      </div>
    </div>
  )
}

// ─── Add restaurant form ───────────────────────────────────────────────────

function AddRestaurantForm({ onCreated, toast }) {
  const [owners, setOwners] = useState([])
  const [ownersLoading, setOwnersLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [form, setForm] = useState({
    name: '',
    cuisine: '',
    description: '',
    ownerId: '',
    restaurantAddressDTO: { street: '', area: '', city: '', state: '', zipcode: '' },
  })

  useEffect(() => {
    // FIXED: getAllUsers lives in admin.js, not users.js
    adminApi.getAllUsers({ page: 0, size: 100, dir: 'asc', sort: 'userId', role: 'RESTAURANT_OWNER' })
      .then(setOwners)
      .catch((err) => toast.error(getErrorMessage(err)))
      .finally(() => setOwnersLoading(false))
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!form.ownerId) {
      toast.error('Please select an owner')
      return
    }
    setSaving(true)
    try {
      const payload = {
        name: form.name,
        cuisine: form.cuisine,
        description: form.description,
        restaurantAddressDTO: {
          ...form.restaurantAddressDTO,
          zipcode: form.restaurantAddressDTO.zipcode ? Number(form.restaurantAddressDTO.zipcode) : null,
        },
      }
      // FIXED: addRestaurant(ownerId, restaurant) — no adminId param;
      // admin identity comes from the session, only the target owner is passed
      await restaurantsApi.addRestaurant(form.ownerId, payload)
      toast.success('Restaurant created')
      onCreated()
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setSaving(false)
    }
  }

  return (
    <Card>
      <h3 className="mb-3 text-sm font-semibold text-white">New restaurant</h3>
      <form onSubmit={handleSubmit} className="space-y-3">
        <div>
          <label className="block text-xs font-medium text-white/80">Owner</label>
          <select
            value={form.ownerId}
            onChange={(e) => setForm({ ...form, ownerId: e.target.value })}
            className="mt-1 w-full rounded-lg border border-border px-3 py-1.5 text-sm"
            disabled={ownersLoading}
            required
          >
            <option value="">{ownersLoading ? 'Loading owners…' : 'Select an owner'}</option>
            {owners.map((o) => (
              <option key={o.userId} value={o.userId}>{o.userName} ({o.userEmail})</option>
            ))}
          </select>
          {!ownersLoading && owners.length === 0 && (
            <p className="mt-1 text-xs text-amber-600">No RESTAURANT_OWNER users found — create one first.</p>
          )}
        </div>

        <div>
          <label className="block text-xs font-medium text-white/80">Name</label>
          <input
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
            className="mt-1 w-full rounded-lg border border-border px-3 py-1.5 text-sm"
            required
          />
        </div>
        <div>
          <label className="block text-xs font-medium text-white/80">Cuisine</label>
          <input
            value={form.cuisine}
            onChange={(e) => setForm({ ...form, cuisine: e.target.value })}
            className="mt-1 w-full rounded-lg border border-border px-3 py-1.5 text-sm"
          />
        </div>
        <div>
          <label className="block text-xs font-medium text-white/80">Description</label>
          <textarea
            value={form.description}
            onChange={(e) => setForm({ ...form, description: e.target.value })}
            className="mt-1 w-full rounded-lg border border-border px-3 py-1.5 text-sm"
            rows={2}
          />
        </div>

        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="block text-xs font-medium text-white/80">Street</label>
            <input
              value={form.restaurantAddressDTO.street}
              onChange={(e) => setForm({ ...form, restaurantAddressDTO: { ...form.restaurantAddressDTO, street: e.target.value } })}
              className="mt-1 w-full rounded-lg border border-border px-3 py-1.5 text-sm"
              required
            />
          </div>
          <div>
            <label className="block text-xs font-medium text-white/80">Area</label>
            <input
              value={form.restaurantAddressDTO.area}
              onChange={(e) => setForm({ ...form, restaurantAddressDTO: { ...form.restaurantAddressDTO, area: e.target.value } })}
              className="mt-1 w-full rounded-lg border border-border px-3 py-1.5 text-sm"
            />
          </div>
          <div>
            <label className="block text-xs font-medium text-white/80">City</label>
            <input
              value={form.restaurantAddressDTO.city}
              onChange={(e) => setForm({ ...form, restaurantAddressDTO: { ...form.restaurantAddressDTO, city: e.target.value } })}
              className="mt-1 w-full rounded-lg border border-border px-3 py-1.5 text-sm"
              required
            />
          </div>
          <div>
            <label className="block text-xs font-medium text-white/80">State</label>
            <input
              value={form.restaurantAddressDTO.state}
              onChange={(e) => setForm({ ...form, restaurantAddressDTO: { ...form.restaurantAddressDTO, state: e.target.value } })}
              className="mt-1 w-full rounded-lg border border-border px-3 py-1.5 text-sm"
              required
            />
          </div>
          <div>
            <label className="block text-xs font-medium text-white/80">Zipcode</label>
            <input
              type="number"
              value={form.restaurantAddressDTO.zipcode}
              onChange={(e) => setForm({ ...form, restaurantAddressDTO: { ...form.restaurantAddressDTO, zipcode: e.target.value } })}
              className="mt-1 w-full rounded-lg border border-border px-3 py-1.5 text-sm"
              min={100000}
              max={999999}
              required
            />
          </div>
        </div>

        <Button type="submit" size="sm" disabled={saving}>
          {saving ? 'Creating…' : 'Create restaurant'}
        </Button>
      </form>
    </Card>
  )
}

// ─── Expanded restaurant detail: edit info + delete ───────────────────────

function RestaurantDetailPanel({ restaurant, onUpdated, onDelete, toast }) {
  const existingAddress = restaurant.restaurantAddressDTO || restaurant.addressDTO || {}

  const [form, setForm] = useState({
    name: restaurant.name || '',
    description: restaurant.description || '',
    cuisine: restaurant.cuisine || '',
    addressDTO: {
      street: existingAddress.street || '',
      area: existingAddress.area || '',
      city: existingAddress.city || '',
      state: existingAddress.state || '',
      zipcode: existingAddress.zipcode ?? '',
    },
  })
  const [saving, setSaving] = useState(false)

  const handleSave = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      const payload = {
        ...form,
        addressDTO: {
          ...form.addressDTO,
          zipcode: form.addressDTO.zipcode ? Number(form.addressDTO.zipcode) : null,
        },
      }
      // FIXED: updateRestaurant(restId, restaurant) — no adminUserId param
      await restaurantsApi.updateRestaurant(restaurant.restaurantId, payload)
      toast.success('Restaurant updated')
      onUpdated()
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="border-t border-border bg-surface px-4 py-4">
      <form onSubmit={handleSave} className="space-y-3">
        <div>
          <label className="block text-xs font-medium text-white/80">Name</label>
          <input
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
            className="mt-1 w-full rounded-lg border border-border px-3 py-1.5 text-sm"
            required
          />
        </div>
        <div>
          <label className="block text-xs font-medium text-white/80">Cuisine</label>
          <input
            value={form.cuisine}
            onChange={(e) => setForm({ ...form, cuisine: e.target.value })}
            className="mt-1 w-full rounded-lg border border-border px-3 py-1.5 text-sm"
          />
        </div>
        <div>
          <label className="block text-xs font-medium text-white/80">Description</label>
          <textarea
            value={form.description}
            onChange={(e) => setForm({ ...form, description: e.target.value })}
            className="mt-1 w-full rounded-lg border border-border px-3 py-1.5 text-sm"
            rows={2}
          />
        </div>

        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="block text-xs font-medium text-white/80">Street</label>
            <input
              value={form.addressDTO.street}
              onChange={(e) => setForm({ ...form, addressDTO: { ...form.addressDTO, street: e.target.value } })}
              className="mt-1 w-full rounded-lg border border-border px-3 py-1.5 text-sm"
            />
          </div>
          <div>
            <label className="block text-xs font-medium text-white/80">Area</label>
            <input
              value={form.addressDTO.area}
              onChange={(e) => setForm({ ...form, addressDTO: { ...form.addressDTO, area: e.target.value } })}
              className="mt-1 w-full rounded-lg border border-border px-3 py-1.5 text-sm"
            />
          </div>
          <div>
            <label className="block text-xs font-medium text-white/80">City</label>
            <input
              value={form.addressDTO.city}
              onChange={(e) => setForm({ ...form, addressDTO: { ...form.addressDTO, city: e.target.value } })}
              className="mt-1 w-full rounded-lg border border-border px-3 py-1.5 text-sm"
              required
            />
          </div>
          <div>
            <label className="block text-xs font-medium text-white/80">State</label>
            <input
              value={form.addressDTO.state}
              onChange={(e) => setForm({ ...form, addressDTO: { ...form.addressDTO, state: e.target.value } })}
              className="mt-1 w-full rounded-lg border border-border px-3 py-1.5 text-sm"
              required
            />
          </div>
          <div>
            <label className="block text-xs font-medium text-white/80">Zipcode</label>
            <input
              type="number"
              value={form.addressDTO.zipcode}
              onChange={(e) => setForm({ ...form, addressDTO: { ...form.addressDTO, zipcode: e.target.value } })}
              className="mt-1 w-full rounded-lg border border-border px-3 py-1.5 text-sm"
              min={100000}
              max={999999}
              required
            />
          </div>
        </div>

        <div className="flex items-center justify-between pt-2">
          <Button type="submit" size="sm" disabled={saving}>
            {saving ? 'Saving…' : 'Save changes'}
          </Button>
          <button
            type="button"
            onClick={onDelete}
            className="text-sm font-medium text-red-600 hover:text-red-700"
          >
            Delete restaurant
          </button>
        </div>
      </form>
    </div>
  )
}