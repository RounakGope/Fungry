import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import * as authApi from '../api/auth'
import * as usersApi from '../api/users'
import * as restaurantApi from '../api/restaurant'
import { setUnauthorizedHandler } from '../api/client'
import { ROLES, normalizeRole } from '../utils/constants'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [role, setRole] = useState(null)
  const [restaurant, setRestaurant] = useState(null)
  const [loading, setLoading] = useState(true)
  const navigate = useNavigate()

  const clearAuth = useCallback(() => {
    setUser(null)
    setRole(null)
    setRestaurant(null)
  }, [])

  const fetchRoleAndRestaurant = useCallback(async (userData) => {
    const userRole = normalizeRole(await usersApi.getUserRole(userData.id))
    setRole(userRole)

    if (userRole === ROLES.RESTAURANT_OWNER) {
      try {
        const rest = await restaurantApi.getRestaurantByOwner(userData.id)
        setRestaurant(rest)
        if (rest?.id && !userData.restaurantId) {
          setUser((prev) => ({ ...prev, restaurantId: rest.id }))
        }
      } catch {
        // Owner has no restaurant assigned yet — not an auth failure.
        setRestaurant(null)
      }
    } else {
      setRestaurant(null)
    }

    return userRole
  }, [])

  const refreshMe = useCallback(async () => {
    try {
      const userData = await authApi.getMe()
      setUser(userData)
      await fetchRoleAndRestaurant(userData)
      return userData
    } catch {
      clearAuth()
      return null
    }
  }, [clearAuth, fetchRoleAndRestaurant])

  useEffect(() => {
    setUnauthorizedHandler(() => {
      clearAuth()
      navigate('/login', { replace: true })
    })
  }, [clearAuth, navigate])

  useEffect(() => {
    refreshMe().finally(() => setLoading(false))
  }, [refreshMe])

  const login = async (credentials) => {
    const userData = await authApi.login(credentials)
    setUser(userData)
    const userRole = await fetchRoleAndRestaurant(userData)
    return { user: userData, role: userRole }
  }

  const logout = async () => {
    try {
      await authApi.logout()
    } finally {
      clearAuth()
      navigate('/login', { replace: true })
    }
  }

  const refreshRestaurant = async () => {
    if (!user?.id) return null
    try {
      const rest = await restaurantApi.getRestaurantByOwner(user.id)
      setRestaurant(rest)
      if (rest?.id) {
        setUser((prev) => ({ ...prev, restaurantId: rest.id }))
      }
      return rest
    } catch {
      setRestaurant(null)
      return null
    }
  }

  const value = useMemo(
    () => ({
      user,
      role,
      restaurant,
      loading,
      isAuthenticated: !!user,
      isCustomer: role === ROLES.CUSTOMER,
      isOwner: role === ROLES.RESTAURANT_OWNER,
      login,
      logout,
      refreshMe,
      refreshRestaurant,
      setRestaurant,
    }),
    [user, role, restaurant, loading, refreshMe]
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}