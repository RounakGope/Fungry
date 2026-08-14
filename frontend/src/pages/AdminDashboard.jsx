import { useEffect, useState } from 'react'
import * as usersApi from '../api/users'
import * as adminApi from '../api/admin'
import { useToast } from '../context/ToastContext'
import { formatCurrency, formatDate, getErrorMessage, ROLES } from '../utils/constants'
import Button from '../components/Button'
import Card from '../components/Card'
import LoadingSpinner from '../components/LoadingSpinner'
import EmptyState from '../components/EmptyState'
import Badge from '../components/Badge'
import RestaurantsTab from './RestaurantsTab' // adjust path

const ALL_ROLES = ['CUSTOMER', 'RESTAURANT_OWNER', 'ADMIN', 'DELIVERY_GUY']

const TABS = [
  { key: 'users', label: 'Users' },
  { key: 'restaurants', label: 'Restaurants' },
]

export default function AdminDashboard() {
  const [tab, setTab] = useState('users')

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Admin dashboard</h1>
        <p className="text-sm text-white/70">Manage users and restaurants</p>
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

      {tab === 'users' && <UsersTab />}

      {tab === 'restaurants' && <RestaurantsTab />}
      
    </div>
  )
}

// ─── Users Tab ──────────────────────────────────────────────────────────────

function UsersTab() {
  const toast = useToast()
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [roleFilter, setRoleFilter] = useState('')
  const [expandedId, setExpandedId] = useState(null)
  const size = 10

  const load = async () => {
    setLoading(true)
    try {
      // FIXED: getAllUsers lives in admin.js, not users.js
      const data = await adminApi.getAllUsers({
        page,
        size,
        dir: 'asc',
        sort: 'userId',
        ...(roleFilter ? { role: roleFilter } : {}),
      })
      setUsers(data)
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [page, roleFilter])

  const toggleExpand = (userId) => {
    setExpandedId((prev) => (prev === userId ? null : userId))
  }

  return (
    <div>
      <div className="mb-4 flex items-center justify-between">
        <h2 className="text-lg font-semibold text-white">All users</h2>
        <select
          value={roleFilter}
          onChange={(e) => { setRoleFilter(e.target.value); setPage(0); setExpandedId(null) }}
          className="rounded-lg border border-gray-200 px-3 py-1.5 text-sm"
        >
          <option value="">All roles</option>
          {ALL_ROLES.map((r) => (
            <option key={r} value={r}>{r.replace('_', ' ')}</option>
          ))}
        </select>
      </div>

      {loading ? (
        <LoadingSpinner label="Loading users" />
      ) : users.length === 0 ? (
        <EmptyState title="No users found" description="Try a different role filter." />
      ) : (
        <div className="space-y-2">
          {users.map((user) => (
            <Card key={user.userId} className="p-0 overflow-hidden">
              <button
                onClick={() => toggleExpand(user.userId)}
                className="flex w-full items-center justify-between px-4 py-3 text-left hover:bg-gray-50"
              >
                <div>
                  <p className="font-medium text-white">{user.userName}</p>
                  <p className="text-sm text-white/70">{user.userEmail}</p>
                </div>
                <div className="flex items-center gap-3">
                  <Badge status={user.userRole} />
                  <span className="text-white/60">{expandedId === user.userId ? '▲' : '▼'}</span>
                </div>
              </button>

              {expandedId === user.userId && (
                <UserDetailPanel user={user} onUpdated={load} toast={toast} />
              )}
            </Card>
          ))}
        </div>
      )}

      <div className="mt-6 flex justify-center gap-3">
        <button
          disabled={page === 0}
          onClick={() => { setPage((p) => p - 1); setExpandedId(null) }}
          className="rounded-lg border border-gray-200 px-4 py-2 text-sm disabled:opacity-50"
        >
          Previous
        </button>
        <button
          disabled={users.length < size}
          onClick={() => { setPage((p) => p + 1); setExpandedId(null) }}
          className="rounded-lg border border-gray-200 px-4 py-2 text-sm disabled:opacity-50"
        >
          Next
        </button>
      </div>
    </div>
  )
}

// ─── Expanded user detail: edit info, change role, order history ──────────────

function UserDetailPanel({ user, onUpdated, toast }) {
  const [form, setForm] = useState({
    userEmail: user.userEmail || '',
    userName: user.userName || '',
    phoneNumber: user.phoneNumber || '',
    userRole: user.userRole,
  })
  const [saving, setSaving] = useState(false)

  const [orders, setOrders] = useState([])
  const [ordersLoading, setOrdersLoading] = useState(true)

  useEffect(() => {
    setOrdersLoading(true)
    adminApi.getUserOrderHistory(user.userId, { page: 0, size: 5, sortBy: 'createdAt', direction: 'descending' })
      .then(setOrders)
      .catch((err) => toast.error(getErrorMessage(err)))
      .finally(() => setOrdersLoading(false))
  }, [user.userId])

  const handleSave = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      await adminApi.updateUserById(user.userId, form)
      toast.success('User updated')
      onUpdated()
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="border-t border-gray-100 bg-gray-50 px-4 py-4">
      <div className="grid gap-6 sm:grid-cols-2">
        <div>
          <h3 className="mb-3 text-sm font-semibold text-white">Edit user</h3>
          <form onSubmit={handleSave} className="space-y-3">
            <div>
              <label className="block text-xs font-medium text-white/80">Name</label>
              <input
                value={form.userName}
                onChange={(e) => setForm({ ...form, userName: e.target.value })}
                className="mt-1 w-full rounded-lg border border-gray-200 px-3 py-1.5 text-sm"
                required
              />
            </div>
            <div>
              <label className="block text-xs font-medium text-white/80">Email</label>
              <input
                type="email"
                value={form.userEmail}
                onChange={(e) => setForm({ ...form, userEmail: e.target.value })}
                className="mt-1 w-full rounded-lg border border-gray-200 px-3 py-1.5 text-sm"
                required
              />
            </div>
            <div>
              <label className="block text-xs font-medium text-white/80">Phone</label>
              <input
                value={form.phoneNumber}
                onChange={(e) => setForm({ ...form, phoneNumber: e.target.value })}
                className="mt-1 w-full rounded-lg border border-gray-200 px-3 py-1.5 text-sm"
              />
            </div>
            <div>
              <label className="block text-xs font-medium text-white/80">Role</label>
              <select
                value={form.userRole}
                onChange={(e) => setForm({ ...form, userRole: e.target.value })}
                className="mt-1 w-full rounded-lg border border-gray-200 px-3 py-1.5 text-sm"
              >
                {ALL_ROLES.map((r) => (
                  <option key={r} value={r}>{r.replace('_', ' ')}</option>
                ))}
              </select>
            </div>
            <Button type="submit" size="sm" disabled={saving}>
              {saving ? 'Saving…' : 'Save changes'}
            </Button>
          </form>
        </div>

        <div>
          <h3 className="mb-3 text-sm font-semibold text-white">Recent orders</h3>
          {ordersLoading ? (
            <p className="text-sm text-white/70">Loading…</p>
          ) : orders.length === 0 ? (
            <p className="text-sm text-white/70">No orders yet.</p>
          ) : (
            <div className="space-y-2">
              {orders.map((order) => (
                <div key={order.orderId} className="rounded-lg border border-gray-200 bg-white px-3 py-2">
                  <div className="flex items-center justify-between">
                    <p className="text-sm font-medium text-white">Order #{order.orderId}</p>
                    <Badge status={order.status} />
                  </div>
                  <div className="mt-1 flex items-center justify-between text-xs text-white/70">
                    <span>{order.restaurantName}</span>
                    <span>{formatDate(order.createdTime)}</span>
                  </div>
                  <p className="mt-1 text-sm font-semibold text-white">{formatCurrency(order.totalAmt)}</p>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}