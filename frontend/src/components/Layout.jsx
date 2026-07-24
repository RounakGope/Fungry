import { Link, NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useCart } from '../context/CartContext'
import { ROLES } from '../utils/constants'
import Button from './Button'

const linkClass = ({ isActive }) =>
  `rounded-full px-3 py-1.5 text-sm font-medium transition-all ${isActive ? 'bg-primary-50 text-primary-700 shadow-sm' : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900'}`

function Navbar() {
  const { user, role, logout, isAuthenticated } = useAuth()
  const { itemCount } = useCart()

  if (!isAuthenticated) return null

  const isOwner = role === ROLES.RESTAURANT_OWNER
  const isAdmin = role === ROLES.ADMIN

  return (
    <header className="border-b border-gray-200 bg-white/90 backdrop-blur">
      <div className="mx-auto flex max-w-6xl flex-col gap-2 px-3 py-3 sm:flex-row sm:items-center sm:justify-between sm:px-4">
        <div className="flex items-center justify-between gap-3">
          <Link
            to={isOwner ? '/restaurant-dashboard' : isAdmin ? '/admin' : '/home'}
            className="text-lg font-semibold tracking-tight text-gray-900"
          >
            Fungry
          </Link>
          <div className="flex items-center gap-2 sm:hidden">
            <span className="text-xs text-gray-500">{user?.username || user?.email}</span>
            <Button variant="secondary" size="sm" onClick={logout}>Out</Button>
          </div>
        </div>

        <nav className="flex flex-wrap items-center gap-1.5 sm:gap-3">
          {isOwner ? (
            <>
              <NavLink to="/restaurant-dashboard" className={linkClass}>Dashboard</NavLink>
              <NavLink to="/owner/orders" className={linkClass}>Orders</NavLink>
            </>
          ) : isAdmin ? (
            <NavLink to="/admin" className={linkClass}>Admin</NavLink>
          ) : (
            <>
              <NavLink to="/home" className={linkClass}>Home</NavLink>
              <NavLink to="/cart" className={linkClass}>
                Cart{itemCount > 0 && (
                  <span className="ml-1.5 inline-flex min-w-5 items-center justify-center rounded-lg bg-primary-600 px-1.5 py-0.5 text-[11px] font-semibold text-white">
                    {itemCount}
                  </span>
                )}
              </NavLink>
              <NavLink to="/orders" className={linkClass}>Orders</NavLink>
            </>
          )}
          <NavLink to="/profile" className={linkClass}>Profile</NavLink>
        </nav>

        <div className="hidden items-center gap-3 sm:flex">
          <span className="text-sm text-gray-500">{user?.username || user?.email}</span>
          <Button variant="secondary" size="sm" onClick={logout}>Sign out</Button>
        </div>
      </div>
    </header>
  )
}

export function AuthLayout({ children }) {
  return (
    <div className="min-h-screen bg-[radial-gradient(circle_at_top_left,_rgba(22,163,74,0.08),_transparent_35%)] bg-gray-50">
      <div className="flex min-h-screen flex-col items-center justify-center px-4 py-12">
        <Link to="/" className="mb-8 text-2xl font-semibold tracking-tight text-gray-900">Fungry</Link>
        {children}
      </div>
    </div>
  )
}

export default function Layout() {
  return (
    <div className="min-h-screen bg-[radial-gradient(circle_at_top_left,_rgba(22,163,74,0.08),_transparent_35%)] bg-gray-50">
      <Navbar />
      <main className="mx-auto max-w-6xl px-3 py-6 sm:px-4 sm:py-8">
        <Outlet />
      </main>
    </div>
  )
}