import { Link, NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useCart } from '../context/CartContext'
import { ROLES } from '../utils/constants'
import Button from './Button'

const linkClass = ({ isActive }) =>
  `text-sm font-medium transition-colors ${isActive ? 'text-primary-700' : 'text-gray-600 hover:text-gray-900'}`

function Navbar() {
  const { user, role, logout, isAuthenticated } = useAuth()
  const { itemCount } = useCart()

  if (!isAuthenticated) return null

  const isOwner = role === ROLES.RESTAURANT_OWNER

  return (
    <header className="border-b border-gray-200 bg-white">
      <div className="mx-auto flex h-14 max-w-6xl items-center justify-between px-4">
        <Link to={isOwner ? '/restaurant-dashboard' : '/'} className="text-lg font-bold text-gray-900">
          Fungry
        </Link>

        <nav className="flex items-center gap-6">
          {isOwner ? (
            <>
              <NavLink to="/restaurant-dashboard" className={linkClass}>Dashboard</NavLink>
              <NavLink to="/owner/orders" className={linkClass}>Orders</NavLink>
            </>
          ) : (
            <>
              <NavLink to="/" className={linkClass}>Home</NavLink>
              <NavLink to="/cart" className={linkClass}>
                Cart{itemCount > 0 && (
                  <span className="ml-1.5 inline-flex min-w-5 items-center justify-center rounded-lg bg-primary-600 px-1.5 py-0.5 text-xs font-semibold text-white">
                    {itemCount}
                  </span>
                )}
              </NavLink>
              <NavLink to="/orders" className={linkClass}>Orders</NavLink>
            </>
          )}
          <NavLink to="/profile" className={linkClass}>Profile</NavLink>
        </nav>

        <div className="flex items-center gap-3">
          <span className="hidden text-sm text-gray-500 sm:inline">{user?.username || user?.email}</span>
          <Button variant="secondary" size="sm" onClick={logout}>Sign out</Button>
        </div>
      </div>
    </header>
  )
}

export function AuthLayout({ children }) {
  return (
    <div className="min-h-screen bg-gray-50">
      <div className="flex min-h-screen flex-col items-center justify-center px-4 py-12">
        <Link to="/" className="mb-8 text-2xl font-bold text-gray-900">Fungry</Link>
        {children}
      </div>
    </div>
  )
}

export default function Layout() {
  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <main className="mx-auto max-w-6xl px-4 py-8">
        <Outlet />
      </main>
    </div>
  )
}
