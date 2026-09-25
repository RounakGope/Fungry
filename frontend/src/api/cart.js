import api from './client'

/** View the logged-in user's cart. */
export const getCart = () =>
  api.get('/cart/').then((r) => r.data)

/** Add a menu item to the logged-in user's cart. */
export const addToCart = (menuItemId) =>
  api.post(`/cart/add/${menuItemId}`).then((r) => r.data)

/** Increase quantity of a specific cart item by 1. */
export const increaseQuantity = (cartItemId) =>
  api.put(`/cart/increase/${cartItemId}`).then((r) => r.data)

/** Remove a specific item from the cart. */
export const removeFromCart = (cartItemId) =>
  api.delete(`/cart/remove/${cartItemId}`).then((r) => r.data)

/** Clear the entire cart. */
export const clearCart = () =>
  api.delete('/cart/clear').then((r) => r.data)