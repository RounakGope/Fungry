import api from './client'

/** @param {number} userId @returns {Promise<import('./types').CartDTO>} */
export const getCart = (userId) =>
  api.get(`/cart/${userId}`).then((r) => r.data)

/** @param {number} userId @param {number} menuItemId */
export const addToCart = (userId, menuItemId) =>
  api.post(`/cart/add/${userId}/${menuItemId}`).then((r) => r.data)

/** @param {number} cartItemId @param {number} userId */
export const increaseQuantity = (cartItemId, userId) =>
  api.put(`/cart/increase/${cartItemId}/${userId}`).then((r) => r.data)

/** @param {number} cartItemId @param {number} userId */
export const removeFromCart = (cartItemId, userId) =>
  api.delete(`/cart/remove/${cartItemId}/${userId}`).then((r) => r.data)

/** @param {number} userId */
export const clearCart = (userId) =>
  api.delete(`/cart/clear/${userId}`).then((r) => r.data)
