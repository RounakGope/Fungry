import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import * as restaurantApi from '../api/restaurant'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'
import { getErrorMessage } from '../utils/constants'
import Button from '../components/Button'
import Card from '../components/Card'
import Input from '../components/Input'

export default function RestaurantOnboarding() {
  const { user, refreshRestaurant } = useAuth()
  const toast = useToast()
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [form, setForm] = useState({
    name: '',
    description: '',
    cuisine: '',
    address: '',
  })

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      await restaurantApi.createRestaurant(user.userId, form)
      await refreshRestaurant()
      toast.success('Restaurant created')
      navigate('/restaurant-dashboard')
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="max-w-lg">
      <h1 className="mb-2 text-2xl font-bold text-gray-900">Create your restaurant</h1>
      <p className="mb-6 text-sm text-gray-500">Set up your restaurant to start receiving orders.</p>

      <Card>
        <form onSubmit={handleSubmit} className="space-y-4">
          <Input label="Restaurant name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
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
          <Button type="submit" disabled={loading} className="w-full">
            {loading ? 'Creating…' : 'Create restaurant'}
          </Button>
        </form>
      </Card>
    </div>
  )
}
