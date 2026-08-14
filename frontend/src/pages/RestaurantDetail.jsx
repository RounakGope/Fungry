import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import * as restaurantApi from '../api/restaurant'
import { useAuth } from '../context/AuthContext'
import { useCart } from '../context/CartContext'
import { useToast } from '../context/ToastContext'
import { formatCurrency, getErrorMessage } from '../utils/constants'
import Button from '../components/Button'
import Card from '../components/Card'
import LoadingSpinner from '../components/LoadingSpinner'
import EmptyState from '../components/EmptyState'

export default function RestaurantDetail() {
  const { restId } = useParams()
  const { user, isAuthenticated, isCustomer } = useAuth()
  const { addItem } = useCart()
  const toast = useToast()

  const [restaurant, setRestaurant] = useState(null)
  const [menuItems, setMenuItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [sortBy, setSortBy] = useState('price')
  const [direction, setDirection] = useState('asc')
  const [rating, setRating] = useState(5)
  const [addingId, setAddingId] = useState(null)

  const load = async () => {
    setLoading(true)
    try {
      const [rest, items] = await Promise.all([
        restaurantApi.viewRestaurant(restId),
        restaurantApi.getMenuItems(restId, sortBy, direction),
      ])
      setRestaurant(rest)
      setMenuItems(items)
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [restId, sortBy, direction])

  const handleAddToCart = async (itemId) => {
    if (!isAuthenticated) {
      toast.error('Sign in to add items to cart')
      return
    }
    setAddingId(itemId)
    try {
      await addItem(itemId)
      toast.success('Added to cart')
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setAddingId(null)
    }
  }

  const handleRate = async () => {
    if (!isAuthenticated || !isCustomer || !user?.id) return
    try {
      // FIXED: rateRestaurant(restId, rate) — no userId param, identity comes from session.
      // Previously called as rateRestaurant(user.id, restId, rating), which shifted
      // user.id into restId, restId into rate, and dropped the real rating entirely.
      const updated = await restaurantApi.rateRestaurant(restId, rating)
      setRestaurant(updated)
      toast.success('Rating submitted')
    } catch (err) {
      toast.error(getErrorMessage(err))
    }
  }

  if (loading) return <LoadingSpinner />

  if (!restaurant) {
    return <EmptyState title="Restaurant not found" description="This restaurant may have been removed." />
  }

  return (
    <div>
      <div className="mb-8 border-b border-gray-200 pb-6">
        <h1 className="text-2xl font-bold text-white">{restaurant.name}</h1>
        {restaurant.cuisine && <p className="mt-1 text-sm text-white/70">{restaurant.cuisine}</p>}
        {restaurant.description && <p className="mt-3 text-sm text-white/80">{restaurant.description}</p>}
        {restaurant.rating != null && (
          <p className="mt-2 text-sm font-medium text-white/90">{Number(restaurant.rating).toFixed(1)} rating</p>
        )}

        {isCustomer && (
          <div className="mt-4 flex items-center gap-3">
            <label className="text-sm text-white/80">Rate:</label>
            <select
              value={rating}
              onChange={(e) => setRating(Number(e.target.value))}
              className="rounded-lg border border-gray-200 px-2 py-1 text-sm"
            >
              {[1, 2, 3, 4, 5].map((n) => (
                <option key={n} value={n}>{n}</option>
              ))}
            </select>
            <Button size="sm" variant="secondary" onClick={handleRate}>Submit</Button>
          </div>
        )}
      </div>

      <div className="mb-4 flex items-center justify-between">
        <h2 className="text-lg font-semibold text-white">Menu</h2>
        <div className="flex gap-2">
          <select
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value)}
            className="rounded-lg border border-gray-200 px-3 py-1.5 text-sm"
          >
            <option value="price">Price</option>
            <option value="rating">Rating</option>
            <option value="name">Name</option>
          </select>
          <select
            value={direction}
            onChange={(e) => setDirection(e.target.value)}
            className="rounded-lg border border-gray-200 px-3 py-1.5 text-sm"
          >
            <option value="asc">Ascending</option>
            <option value="desc">Descending</option>
          </select>
        </div>
      </div>

      {menuItems.length === 0 ? (
        <EmptyState title="No menu items" description="This restaurant hasn't added items yet." />
      ) : (
        <div className="space-y-3">

          {menuItems.map((item) => (
            <Card key={item.menuItemId} className="flex items-start justify-between gap-4">
              <div>
                <h3 className="font-medium text-white">{item.foodName}</h3>
                <p className="mt-2 text-sm font-semibold text-white">{formatCurrency(item.price)}</p>
              </div>
              {isCustomer && (
                <Button
                  size="sm"
                  onClick={() => handleAddToCart(item.menuItemId)}
                  disabled={addingId === item.menuItemId}
                >
                  {addingId === item.menuItemId ? 'Adding…' : 'Add to cart'}
                </Button>
              )}
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}