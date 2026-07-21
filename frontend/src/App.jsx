import { Navigate, Route, Routes, useLocation } from 'react-router-dom'
import { useAuth } from './context/AuthContext'
import { ROLES } from './utils/constants'
import Layout from './components/Layout'
import LoadingSpinner from './components/LoadingSpinner'

import Login from './pages/Login'
import Signup from './pages/Signup'
import Home from './pages/Home'
import RestaurantDetail from './pages/RestaurantDetail'
import Cart from './pages/Cart'
import Checkout from './pages/Checkout'
import Orders from './pages/Orders'
import OrderDetail from './pages/OrderDetail'
import Profile from './pages/Profile'
import RestaurantDashboard from './pages/RestaurantDashboard'
import OwnerOrders from './pages/OwnerOrders'
import AdminDashboard from './pages/AdminDashboard'
import UnsupportedRole from './pages/UnsupportedRole'

export default function App() {
  const { isAuthenticated, role, loading } = useAuth()

  if (loading) return <LoadingSpinner />

  const isOwner = role === ROLES.RESTAURANT_OWNER
  const isCustomer = role === ROLES.CUSTOMER
  const isAdmin = role === ROLES.ADMIN
  const hasKnownRole = isOwner || isCustomer || isAdmin

  const homeFor = isOwner ? '/restaurant-dashboard' : isAdmin ? '/admin' : isCustomer ? '/home' : '/unsupported-role'

  return (
    <Routes>
      {/* Guest routes */}
      <Route
        path="/login"
        element={!isAuthenticated ? <Login /> : <Navigate to={homeFor} replace />}
      />
      <Route
        path="/signup"
        element={!isAuthenticated ? <Signup /> : <Navigate to={homeFor} replace />}
      />

      {/* Protected routes */}
      <Route
        element={!isAuthenticated ? <Navigate to="/login" replace /> : <Layout />}
      >
        {/* Index */}
        <Route index element={<Navigate to={homeFor} replace />} />

        {/* Customer routes */}
        <Route path="home" element={isCustomer ? <Home /> : <Navigate to={hasKnownRole ? homeFor : '/unsupported-role'} replace />} />
        <Route path="restaurant/:restId" element={isCustomer ? <RestaurantDetail /> : <Navigate to={hasKnownRole ? homeFor : '/unsupported-role'} replace />} />
        <Route path="cart" element={isCustomer ? <Cart /> : <Navigate to={hasKnownRole ? homeFor : '/unsupported-role'} replace />} />
        <Route path="checkout" element={isCustomer ? <Checkout /> : <Navigate to={hasKnownRole ? homeFor : '/unsupported-role'} replace />} />
        <Route path="orders" element={isCustomer ? <Orders /> : <Navigate to={hasKnownRole ? homeFor : '/unsupported-role'} replace />} />
        <Route path="orders/:orderId" element={isCustomer ? <OrderDetail /> : <Navigate to={hasKnownRole ? homeFor : '/unsupported-role'} replace />} />

        {/* Owner routes */}
        <Route path="restaurant-dashboard" element={isOwner ? <RestaurantDashboard /> : <Navigate to={hasKnownRole ? homeFor : '/unsupported-role'} replace />} />
        <Route path="owner/orders" element={isOwner ? <OwnerOrders /> : <Navigate to={hasKnownRole ? homeFor : '/unsupported-role'} replace />} />

        {/* Admin routes */}
        <Route path="admin" element={isAdmin ? <AdminDashboard /> : <Navigate to={hasKnownRole ? homeFor : '/unsupported-role'} replace />} />

        {/* Fallback for unrecognized roles */}
        <Route path="unsupported-role" element={<UnsupportedRole role={role} />} />

        {/* Shared */}
        <Route path="profile" element={<Profile />} />
      </Route>

      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  )
}