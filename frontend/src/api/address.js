import api from './client'

/** @param {number} userId @param {import('./types').AddressCreateDTO} data */
export const createAddress = (userId, data) =>
  api.post(`/address/${userId}`, data).then((r) => r.data)

/** @param {number} addressId @param {number} userId @param {import('./types').AddressDTO} data */
export const updateAddress = (addressId, userId, data) =>
  api.put(`/address/${addressId}/${userId}`, data).then((r) => r.data)

/** @param {number} addressId @param {number} userId */
export const deleteAddress = (addressId, userId) =>
  api.delete(`/address/${addressId}/${userId}`).then((r) => r.data)

/** @param {number} addressId @param {number} userId */
export const getAddress = (addressId, userId) =>
  api.get(`/address/${addressId}/${userId}`).then((r) => r.data)

/** @param {number} userId @returns {Promise<import('./types').AddressDTO[]>} */
export const getUserAddresses = (userId) =>
  api.get(`/address/user/${userId}`).then((r) => r.data)
