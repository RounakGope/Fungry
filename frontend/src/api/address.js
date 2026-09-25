import api from './client'

/** Create an address for the logged-in user. */
export const createAddress = (data) =>
  api.post('/address/', data).then((r) => r.data)

/** Update one of the logged-in user's addresses. */
export const updateAddress = (addressId, data) =>
  api.put(`/address/${addressId}`, data).then((r) => r.data)

/** Delete one of the logged-in user's addresses. */
export const deleteAddress = (addressId) =>
  api.delete(`/address/${addressId}`).then((r) => r.data)

/** Get a single address belonging to the logged-in user. */
export const getAddress = (addressId) =>
  api.get(`/address/${addressId}`).then((r) => r.data)

/** Get all addresses belonging to the logged-in user. */
export const getUserAddresses = () =>
  api.get('/address/user').then((r) => r.data)