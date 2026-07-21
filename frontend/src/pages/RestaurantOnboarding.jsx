import { useAuth } from '../context/AuthContext'
import Card from '../components/Card'

export default function RestaurantOnboarding() {
  const { user } = useAuth()

  return (
    <div className="max-w-lg">
      <h1 className="mb-2 text-2xl font-bold text-gray-900">No restaurant assigned yet</h1>
      <p className="mb-6 text-sm text-gray-500">
        An admin needs to set up your restaurant before you can manage orders and menu items.
      </p>

      <Card>
        <p className="text-sm text-gray-700">
          Signed in as <span className="font-medium">{user?.name || user?.email}</span>.
        </p>
        <p className="mt-2 text-sm text-gray-500">
          Once an admin creates your restaurant and assigns you as the owner, it'll show up here automatically.
        </p>
      </Card>
    </div>
  )
}