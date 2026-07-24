import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import * as restaurantApi from '../api/restaurant'
import { getErrorMessage } from '../utils/constants'
import Card from '../components/Card'
import Input from '../components/Input'
import EmptyState from '../components/EmptyState'
import LoadingSpinner from '../components/LoadingSpinner'

export default function Home() {
  const [restaurants, setRestaurants] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [search, setSearch] = useState('')

  useEffect(() => {
    restaurantApi.getAllRestaurants()
      .then(setRestaurants)
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false))
  }, [])

  const filtered = useMemo(() => {
    const q = search.trim().toLowerCase()
    if (!q) return restaurants
    return restaurants.filter(
      (r) =>
        r.name?.toLowerCase().includes(q) ||
        r.cuisine?.toLowerCase().includes(q)
    )
  }, [restaurants, search])

  if (loading) return <LoadingSpinner label="Loading restaurants" />
  console.log('restaurants:', restaurants)

  return (
    <div>
      <div className="mb-6 sm:mb-8">
        <h1 className="text-2xl font-bold text-gray-900">Restaurants</h1>
        <p className="mt-1 text-sm text-gray-500">Browse and order from local restaurants</p>
      </div>

      <div className="mb-6 w-full max-w-xl">
        <Input
          placeholder="Search by name or cuisine…"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {error && (
        <div className="mb-6 rounded-lg border border-gray-200 bg-primary-100 px-4 py-3 text-sm text-primary-700">
          Could not load restaurants. Ensure the backend exposes <code className="font-mono">GET /restaurant/all</code>. {error}
        </div>
      )}

      {filtered.length === 0 ? (
        <EmptyState
          title="No restaurants found"
          description={search ? 'Try a different search term.' : 'No restaurants are available yet.'}
        />
      ) : (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {filtered.map((restaurant) => (
            <Link key={restaurant.restaurantId} to={`/restaurant/${restaurant.restaurantId}`} className="block">
              <Card className="transition-colors hover:border-primary-600/40">
                <h2 className="font-semibold text-gray-900">{restaurant.name}</h2>
                {restaurant.cuisine && (
                  <p className="mt-1 text-sm text-gray-500">{restaurant.cuisine}</p>
                )}
                {restaurant.rating != null && (
                  <p className="mt-3 text-sm font-medium text-gray-700">
                    {Number(restaurant.rating).toFixed(1)} rating
                  </p>
                )}
              </Card>
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}
