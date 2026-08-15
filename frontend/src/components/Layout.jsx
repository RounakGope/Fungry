import { Link, NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useCart } from '../context/CartContext'
import { ROLES } from '../utils/constants'
import Button from './Button'

const linkClass = ({ isActive }) =>
  `px-1 py-5 text-[15px] font-medium border-b-2 ${
    isActive
      ? 'text-zinc-50 border-primary-500'
      : 'text-muted border-transparent hover:text-zinc-100'
  }`

function LogoMark({ size = 'md' }) {
  const dim = size === 'lg' ? 'h-10 w-10 text-lg' : 'h-8 w-8 text-sm'
  return (
    <div className={`flex ${dim} items-center justify-center rounded-lg bg-primary-600 font-bold text-white`}>
      F
    </div>
  )
}

function Navbar() {
  const { user, role, logout, isAuthenticated } = useAuth()
  const { itemCount } = useCart()

  if (!isAuthenticated) return null

  const isOwner = role === ROLES.RESTAURANT_OWNER
  const isAdmin = role === ROLES.ADMIN

  return (
    <header className="sticky top-0 z-40 w-full border-b border-border bg-surface">
      <div className="mx-auto flex max-w-7xl items-center justify-between px-4 sm:px-6">
        <div className="flex items-center gap-8">
          <Link
            to={isOwner ? '/restaurant-dashboard' : isAdmin ? '/admin' : '/home'}
            className="flex items-center gap-2.5 text-xl font-semibold tracking-tight text-zinc-50"
          >
            <LogoMark />
            Fungry
          </Link>

          <nav className="hidden items-center gap-6 md:flex">
            {isOwner ? (
              <>
                <NavLink to="/restaurant-dashboard" className={linkClass}>Dashboard</NavLink>
                <NavLink to="/owner/orders" className={linkClass}>Orders</NavLink>
              </>
            ) : isAdmin ? (
              <NavLink to="/admin" className={linkClass}>Admin</NavLink>
            ) : (
              <>
                <NavLink to="/home" className={linkClass}>Explore</NavLink>
                <NavLink to="/orders" className={linkClass}>Orders</NavLink>
              </>
            )}
          </nav>
        </div>

        <div className="mx-6 hidden max-w-lg flex-1 items-center rounded-lg border border-border bg-surface-raised px-3 py-2 focus-within:border-primary-500/50 lg:flex">
          <svg className="h-5 w-5 text-muted" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input
            type="text"
            placeholder="Search Fungry..."
            className="ml-2 w-full bg-transparent text-sm text-zinc-100 placeholder:text-muted outline-none"
          />
        </div>

        <div className="flex items-center gap-5 py-3 sm:gap-6">
          {!isOwner && !isAdmin && (
            <Link to="/cart" className="relative text-muted hover:text-zinc-100">
              <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
              </svg>
              {itemCount > 0 && (
                <span className="absolute -right-1.5 -top-1.5 flex h-4 w-4 items-center justify-center rounded-full bg-primary-600 text-[10px] font-bold text-white">
                  {itemCount}
                </span>
              )}
            </Link>
          )}

          <div className="flex items-center gap-3">
            <Link to="/profile" className="h-8 w-8 overflow-hidden rounded-lg border border-border bg-surface-raised">
              <img
                src={`https://ui-avatars.com/api/?name=${user?.username || 'User'}&background=166534&color=fff`}
                alt="Profile avatar"
                className="h-full w-full object-cover"
              />
            </Link>
            <Button variant="ghost" size="sm" onClick={logout} className="hidden sm:inline-flex">
              Log out
            </Button>
          </div>
        </div>
      </div>
    </header>
  )
}

export function AuthLayout({ children }) {
  return (
    <div className="min-h-screen bg-surface text-zinc-100">
      <div className="flex min-h-screen flex-col items-center justify-center px-4 py-12">
        <Link to="/" className="mb-8 flex items-center gap-3 text-3xl font-bold tracking-tight text-zinc-50">
          <LogoMark size="lg" />
          Fungry
        </Link>
        {children}
      </div>
    </div>
  )
}

export default function Layout() {
  return (
    <div className="min-h-screen bg-surface text-zinc-100 selection:bg-primary-600/30">
      <Navbar />
      <main className="mx-auto max-w-7xl px-4 py-6 sm:px-6 sm:py-8">
        <Outlet />
      </main>
    </div>
  )
}
