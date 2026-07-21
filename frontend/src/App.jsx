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

export default function App() {
  const { isAuthenticated, role, loading } = useAuth()

  if (loading) return <LoadingSpinner />

  const isOwner = role === ROLES.RESTAURANT_OWNER
  const isCustomer = role === ROLES.CUSTOMER

  return (
    <Routes>
      {/* Guest routes */}
      <Route
        path="/login"
        element={
          !isAuthenticated
            ? <Login />
            : <Navigate to={isOwner ? '/restaurant-dashboard' : '/home'} replace />
        }
      />
      <Route
        path="/signup"
        element={
          !isAuthenticated
            ? <Signup />
            : <Navigate to={isOwner ? '/restaurant-dashboard' : '/home'} replace />
        }
      />

      {/* Protected routes */}
      <Route
        element={
          !isAuthenticated
            ? <Navigate to="/login" replace />
            : <Layout />
        }
      >
        {/* Index */}
        <Route
          index
          element={<Navigate to={isOwner ? '/restaurant-dashboard' : '/home'} replace />}
        />

        {/* Customer routes */}
        <Route path="home" element={isCustomer ? <Home /> : <Navigate to="/restaurant-dashboard" replace />} />
        <Route path="restaurant/:restId" element={isCustomer ? <RestaurantDetail /> : <Navigate to="/restaurant-dashboard" replace />} />
        <Route path="cart" element={isCustomer ? <Cart /> : <Navigate to="/restaurant-dashboard" replace />} />
        <Route path="checkout" element={isCustomer ? <Checkout /> : <Navigate to="/restaurant-dashboard" replace />} />
        <Route path="orders" element={isCustomer ? <Orders /> : <Navigate to="/restaurant-dashboard" replace />} />
        <Route path="orders/:orderId" element={isCustomer ? <OrderDetail /> : <Navigate to="/restaurant-dashboard" replace />} />

        {/* Owner routes */}
        <Route path="restaurant-dashboard" element={isOwner ? <RestaurantDashboard /> : <Navigate to="/home" replace />} />
        <Route path="owner/orders" element={isOwner ? <OwnerOrders /> : <Navigate to="/home" replace />} />

        {/* Shared */}
        <Route path="profile" element={<Profile />} />
      </Route>

      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  )
}