import api from './client'

/**
 * NOTE: Backend may not expose a list-all endpoint.
 * Tries GET /restaurant/all — add this endpoint on the backend if missing.
 * @returns {Promise<import('./types').RestaurantDTO[]>}
 */
export const getAllRestaurants = () =>
  api.get('/restaurant/all').then((r) => r.data)

/**
 * NOTE: Backend may not expose GET-by-owner.
 * Tries GET /restaurant/owner/{userId} — add if missing for owner onboarding flow.
 * @param {number} userId
 * @returns {Promise<import('./types').RestaurantDTO | null>}
 */
export const getRestaurantByOwner = async (userId) => {
  try {
    const data = await api.get(`/restaurant/owner/${userId}`).then((r) => r.data)
    return data
  } catch (err) {
    if (err.response?.status === 404) return null
    throw err
  }
}

/** @param {number} id @param {{ sortBy?: string, direction?: string }} params */
export const getMenuItems = (id, params = {}) =>
  api.get(`/restaurant/menuItems/${id}`, { params }).then((r) => r.data)

/** @param {number} userId @param {import('./types').RestaurantCreateDTO} data */
export const createRestaurant = (userId, data) =>
  api.post(`/restaurant/${userId}`, data).then((r) => r.data)

/** @param {number} restId */
export const getRestaurant = (restId) =>
  api.get(`/restaurant/viewRestaurant/${restId}`).then((r) => r.data)

/** @param {number} restId @param {number} userId @param {import('./types').RestaurantUpdateDTO} data */
export const updateRestaurant = (restId, userId, data) =>
  api.put(`/restaurant/${restId}/${userId}`, data).then((r) => r.data)

/** @param {number} restId @param {number} userId */
export const deleteRestaurant = (restId, userId) =>
  api.delete(`/restaurant/${restId}/${userId}`).then((r) => r.data)

/** @param {number} userId @param {number} restId @param {number} rate */
export const rateRestaurant = (userId, restId, rate) =>
  api.post(`/restaurant/rate/${userId}/${restId}/${rate}`).then((r) => r.data)

/** @param {number} restId @param {number} userId @param {import('./types').MenuItemDTO} data */
export const addMenuItem = (restId, userId, data) =>
  api.post(`/restaurant/addItem/${restId}/${userId}`, data).then((r) => r.data)

/** @param {number} userId @param {number} itemId @param {import('./types').MenuItemDTO} data */
export const updateMenuItem = (userId, itemId, data) =>
  api.put(`/restaurant/updateItem/${userId}/${itemId}`, data).then((r) => r.data)

/** @param {number} restId @param {number} userId @param {number} itemId */
export const deleteMenuItem = (restId, userId, itemId) =>
  api.delete(`/restaurant/deleteItem/${restId}/${userId}/${itemId}`).then((r) => r.data)
