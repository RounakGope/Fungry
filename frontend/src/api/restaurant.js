import api from './client'

/** Public: paginated list of all restaurants. */
export const getAllRestaurants = (page = 0, size = 10, dir = 'asc', sort = 'restaurantId') =>
  api.get('/restaurant/all', { params: { page, size, dir, sort } }).then((r) => r.data)

/** Public: view a single restaurant. */
export const viewRestaurant = (restId) =>
  api.get(`/restaurant/viewRestaurant/${restId}`).then((r) => r.data)

/** Public: menu items for a restaurant. */
export const getMenuItems = (restId, sortBy = 'menuItemId', direction = 'asc') =>
  api.get(`/restaurant/menuItems/${restId}`, { params: { sortBy, direction } }).then((r) => r.data)

/** Admin-only: create a restaurant and assign it to ownerId. Admin identity comes from session. */
export const addRestaurant = (ownerId, restaurant) =>
  api.post(`/restaurant/${ownerId}`, restaurant).then((r) => r.data)

/** Self-scoped: the logged-in owner's own restaurant. */
export const getRestaurantByOwner = () =>
  api.get('/restaurant/owner').then((r) => r.data)

/** Owner/admin: update a restaurant. Caller identity comes from session; ownership checked server-side. */
export const updateRestaurant = (restId, restaurant) =>
  api.put(`/restaurant/${restId}`, restaurant).then((r) => r.data)

/** Admin: delete a restaurant. */
export const deleteRestaurant = (restId) =>
  api.delete(`/restaurant/${restId}`).then((r) => r.data)

/** Logged-in user rates a restaurant. */
export const rateRestaurant = (restId, rate) =>
  api.post(`/restaurant/rate/${restId}/${rate}`).then((r) => r.data)

/** Owner/admin: add a menu item to a restaurant. */
export const addMenuItem = (restId, item) =>
  api.post(`/restaurant/addItem/${restId}`, item).then((r) => r.data)

/** Owner/admin: delete a menu item from a restaurant. */
export const deleteMenuItem = (restId, itemId) =>
  api.delete(`/restaurant/deleteItem/${restId}/${itemId}`).then((r) => r.data)

/** Owner/admin: update a menu item. restId is derived server-side from itemId, so it's not in the path. */
export const updateMenuItem = (itemId, item) =>
  api.put(`/restaurant/updateItem/${itemId}`, item).then((r) => r.data)