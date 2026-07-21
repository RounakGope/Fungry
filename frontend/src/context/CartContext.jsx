import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'
import * as cartApi from '../api/cart'
import { useAuth } from './AuthContext'
import { ROLES } from '../utils/constants'

const CartContext = createContext(null)

export function CartProvider({ children }) {
  const { user, role } = useAuth()
  const [cart, setCart] = useState(null)
  const [loading, setLoading] = useState(false)

  const fetchCart = useCallback(async () => {
    if (!user?.id || role !== ROLES.CUSTOMER) {
      setCart(null)
      return null
    }
    setLoading(true)
    try {
      const data = await cartApi.getCart(user.id)
      setCart(data)
      return data
    } catch {
      setCart(null)
      return null
    } finally {
      setLoading(false)
    }
  }, [user?.id, role])

  useEffect(() => {
    fetchCart()
  }, [fetchCart])

const addItem = useCallback(async (menuItemId) => {
  if (!user?.id) return
  await cartApi.addToCart(user.id, menuItemId)
  return fetchCart()
}, [user?.id, fetchCart])

const increaseItem = useCallback(async (cartItemId) => {
  if (!user?.id) return
  const data = await cartApi.increaseQuantity(cartItemId, user.id)
  setCart(data)
  return data
}, [user?.id])
 const removeItem = useCallback(async (cartItemId) => {
  if (!user?.id) return
  const data = await cartApi.removeFromCart(cartItemId, user.id)
  setCart(data)
  return data
}, [user?.id])

  const clear = useCallback(async () => {
  if (!user?.id) return
  const data = await cartApi.clearCart(user.id)
  setCart(data)
  return data
}, [user?.id])

  const itemCount = useMemo(() => {
    if (!cart?.cartItemDTOS) return 0
    return cart.cartItemDTOS.reduce((sum, item) => sum + (item.quantity || 0), 0)
  }, [cart])
  const total = cart?.totalAmt ?? 0

  const value = useMemo(
  () => ({ cart, loading, itemCount, total, fetchCart, addItem, increaseItem, removeItem, clear }),
  [cart, loading, itemCount, total, fetchCart, addItem, increaseItem, removeItem, clear]
)

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>
}

export function useCart() {
  const ctx = useContext(CartContext)
  if (!ctx) throw new Error('useCart must be used within CartProvider')
  return ctx
}
