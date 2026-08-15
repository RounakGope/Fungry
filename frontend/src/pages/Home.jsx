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

  return (
    <div className="space-y-6">
      <div className="space-y-1 text-center sm:text-left">
        <h1 className="text-2xl font-semibold tracking-tight text-zinc-50">Restaurants</h1>
        <p className="text-sm text-muted">Browse and order from local restaurants</p>
      </div>

      <div className="mx-auto w-full max-w-xl">
        <Input
          placeholder="Search by name or cuisine…"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {error && (
        <div className="rounded-lg border border-border bg-surface-raised px-4 py-3 text-sm text-muted">
          Could not load restaurants. Ensure the backend exposes <code className="font-mono text-zinc-200">GET /restaurant/all</code>. {error}
        </div>
      )}

      {filtered.length === 0 ? (
        <EmptyState
          title="No restaurants found"
          description={search ? 'Try a different search term.' : 'No restaurants are available yet.'}
        />
      ) : (
        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
          {filtered.map((restaurant) => (
            <Link key={restaurant.restaurantId} to={`/restaurant/${restaurant.restaurantId}`} className="block">
              <Card className="h-full hover:border-primary-500/40">
                <div className="mb-4 flex h-24 items-center justify-center rounded-lg bg-surface">
                  <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary-600/20 text-lg font-semibold text-primary-400">
                    {restaurant.name?.charAt(0)?.toUpperCase() || 'R'}
                  </div>
                </div>
                <div className="space-y-1">
                  <h2 className="font-semibold text-zinc-50">{restaurant.name}</h2>
                  {restaurant.cuisine && (
                    <p className="text-sm text-muted">{restaurant.cuisine}</p>
                  )}
                  <div className="flex items-center justify-between border-t border-border pt-3">
                    {restaurant.rating != null ? (
                      <p className="text-sm font-medium text-zinc-200">
                        {Number(restaurant.rating).toFixed(1)} rating
                      </p>
                    ) : (
                      <span className="text-sm text-muted">New on Fungry</span>
                    )}
                    <span className="text-sm font-medium text-primary-400">View menu</span>
                  </div>
                </div>
              </Card>
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}
