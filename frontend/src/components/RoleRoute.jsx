import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import LoadingSpinner from './LoadingSpinner'

export default function RoleRoute({ allowedRoles, redirectTo = '/' }) {
  const { role, loading } = useAuth()

  if (loading) return <LoadingSpinner />
  if (!role || !allowedRoles.includes(role)) {
    return <Navigate to={redirectTo} replace />
  }

  return <Outlet />
}
