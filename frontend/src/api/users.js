import api from './client'

/** @param {import('./types').UserCreateDTO} data */
export const createUser = (data) =>
  api.post('/users', data).then((r) => r.data)

/** @param {number} id */
export const getUser = (id) =>
  api.get(`/users/${id}`).then((r) => r.data)

/** @param {number} id @param {import('./types').UserDTO} data */
export const updateUser = (id, data) =>
  api.put(`/users/${id}`, data).then((r) => r.data)

/** @param {number} id */
export const deleteUser = (id) =>
  api.delete(`/users/${id}`).then((r) => r.data)

/** @param {number} id @returns {Promise<import('./types').UserRole>} */
export const getUserRole = (id) =>
  api.get(`/users/userRole/${id}`).then((r) => r.data)

/** @param {number} id @param {import('./types').PhoneDTO} data */
export const updatePhone = (id, data) =>
  api.put(`/users/updatePhone/${id}`, data).then((r) => r.data)

/** @param {number} id @param {import('./types').PasswordUpdateDTO} data */
export const updatePassword = (id, data) =>
  api.put(`/users/updatePassword/${id}`, data).then((r) => r.data)
/**
 * @param {{ page?: number, size?: number, dir?: string, sort?: string, role?: import('./types').UserRole }} params
 * @returns {Promise<import('./types').UserDTO[]>}
 */
export const getAllUsers = (params = {}) =>
  api.get('/users/all', { params }).then((r) => r.data)

/**
 * @param {number} id
 * @param {{ page?: number, size?: number, sortBy?: string, direction?: string }} params
 * @returns {Promise<import('./types').OrderHistoryDTO[]>}
 */
export const getOrderHistory = (id, params = {}) =>
  api.get(`/users/orderHistory/${id}`, { params }).then((r) => r.data)
