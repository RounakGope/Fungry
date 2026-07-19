import { Navigate, Route, Routes } from 'react-router-dom'
import { useAuth } from './context/AuthContext'
import { ROLES } from './utils/constants'
import Layout from './components/Layout'
import ProtectedRoute from './components/ProtectedRoute'
import RoleRoute from './components/RoleRoute'
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

function GuestRoute({ children }) {
  const { isAuthenticated, loading, role } = useAuth()
  if (loading) return <LoadingSpinner />
  if (isAuthenticated) {
    return <Navigate to={role === ROLES.RESTAURANT_OWNER ? '/restaurant-dashboard' : '/'} replace />
  }
  return children
}

function DefaultRedirect() {
  const { role, loading } = useAuth()
  if (loading) return <LoadingSpinner />
  if (role === ROLES.RESTAURANT_OWNER) return <Navigate to="/restaurant-dashboard" replace />
  return <Navigate to="/" replace />
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<GuestRoute><Login /></GuestRoute>} />
      <Route path="/signup" element={<GuestRoute><Signup /></GuestRoute>} />

      <Route element={<ProtectedRoute />}>
        <Route element={<Layout />}>
          {/* Customer routes */}
          <Route element={<RoleRoute allowedRoles={[ROLES.CUSTOMER]} redirectTo="/restaurant-dashboard" />}>
            <Route index element={<Home />} />
            <Route path="restaurant/:restId" element={<RestaurantDetail />} />
            <Route path="cart" element={<Cart />} />
            <Route path="checkout" element={<Checkout />} />
            <Route path="orders" element={<Orders />} />
            <Route path="orders/:orderId" element={<OrderDetail />} />
          </Route>

          {/* Owner routes */}
          <Route element={<RoleRoute allowedRoles={[ROLES.RESTAURANT_OWNER]} redirectTo="/" />}>
            <Route path="restaurant-dashboard" element={<RestaurantDashboard />} />
            <Route path="owner/orders" element={<OwnerOrders />} />
          </Route>

          {/* Shared */}
          <Route path="profile" element={<Profile />} />
        </Route>
      </Route>

      <Route path="*" element={<DefaultRedirect />} />
    </Routes>
  )
}
