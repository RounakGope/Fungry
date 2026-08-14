import api from './client'

/** Create a new user (signup) — no auth required yet, so id doesn't apply here */
export const createUser = (data) =>
  api.post('/users', data).then((r) => r.data)

/** Get the logged-in user's own profile. Id comes from the session, not the client. */
export const getUser = () =>
  api.get('/users/fetch').then((r) => r.data)

/** Update the logged-in user's own profile. */
export const updateUser = (data) =>
  api.put('/users/update', data).then((r) => r.data)

/** Delete the logged-in user's own account. */
export const deleteUser = () =>
  api.delete('/users/delete').then((r) => r.data)

/** @returns {Promise<import('./types').UserRole>} */
export const getUserRole = () =>
  api.get('/users/userRole').then((r) => r.data)

/** @param {import('./types').PhoneDTO} data */
export const updatePhone = (data) =>
  api.put('/users/updatePhone', data).then((r) => r.data)

/** @param {import('./types').PasswordUpdateDTO} data */
export const updatePassword = (data) =>
  api.put('/users/updatePassword', data).then((r) => r.data)

/**
 * @param {{ page?: number, size?: number, sortBy?: string, direction?: string }} params
 * @returns {Promise<import('./types').OrderHistoryDTO[]>}
 */
export const getOrderHistory = (params = {}) =>
  api.get('/users/orderHistory', { params }).then((r) => r.data)