import api from './client'

export const getAllRestaurants = (page = 0, size = 10, dir = 'asc', sort = 'restaurantId') =>
  api.get('/restaurant/all', { params: { page, size, dir, sort } }).then((res) => res.data)

export const viewRestaurant = (restId) =>
  api.get(`/restaurant/viewRestaurant/${restId}`).then((res) => res.data)

export const getMenuItems = (restId, sortBy = 'menuItemId', direction = 'asc') =>
  api.get(`/restaurant/menuItems/${restId}`, { params: { sortBy, direction } }).then((res) => res.data)

export const addMenuItem = (restId, userId, item) =>
  api.post(`/restaurant/addItem/${restId}/${userId}`, item)

export const deleteMenuItem = (restId, userId, itemId) =>
  api.delete(`/restaurant/deleteItem/${restId}/${userId}/${itemId}`)

export const updateMenuItem = (userId, itemId, item) =>
  api.put(`/restaurant/updateItem/${userId}/${itemId}`, item).then((res) => res.data)

export const updateRestaurant = (restId, userId, restaurant) =>
  api.put(`/restaurant/${restId}/${userId}`, restaurant).then((res) => res.data)

export const deleteRestaurant = (restId, userId) =>
  api.delete(`/restaurant/${restId}/${userId}`)

export const rateRestaurant = (userId, restId, rate) =>
  api.post(`/restaurant/rate/${userId}/${restId}/${rate}`).then((res) => res.data)

export const addRestaurant = (adminId, userId, restaurant) =>
  api.post(`/restaurant/${adminId}/${userId}`, restaurant).then((res) => res.data)

export const getRestaurantByOwner = (userId) =>
  api.get(`/restaurant/owner/${userId}`).then((res) => res.data)