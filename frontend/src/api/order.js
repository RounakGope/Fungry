import api from './client'

/** @param {number} cartId @param {number} userId @param {number} addressId */
export const placeOrder = (cartId, userId, addressId) =>
  api.post(`/order/${cartId}/${userId}/${addressId}`).then((r) => r.data)

/** @param {number} orderId @param {number} userId */
export const deleteOrder = (orderId, userId) =>
  api.delete(`/order/${orderId}/${userId}`).then((r) => r.data)

/** @param {number} orderId @param {number} userId */
export const getOrderByUser = (orderId, userId) =>
  api.get(`/order/viewOrderByUser/${orderId}/${userId}`).then((r) => r.data)

/** @param {number} restId @param {number} orderId */
export const getOrderByRestaurant = (restId, orderId) =>
  api.get(`/order/viewOrderByRes/${restId}/${orderId}`).then((r) => r.data)

/** @param {number} userId */
export const getAllOrdersByUser = (userId) =>
  api.get(`/order/viewAllOrderUser/${userId}`).then((r) => r.data)

/** @param {number} restId */
export const getAllOrdersByRestaurant = (restId) =>
  api.get(`/order/viewAllOrderByRest/${restId}`).then((r) => r.data)

/** @param {number} orderId @param {number} restId @param {string} orderStatus */
export const updateOrderStatus = (orderId, restId, orderStatus) =>
  api.get(`/order/updateOrderStatus/${orderId}/${restId}`, {
    params: { orderStatus },
  }).then((r) => r.data)

/** @param {number} orderId @param {number} userId */
export const getOrderStatus = (orderId, userId) =>
  api.get(`/order/orderStatus/${orderId}/${userId}`).then((r) => r.data)

/** @param {number} orderId @param {number} userId */
export const getOrderAmount = (orderId, userId) =>
  api.get(`/order/orderAmt/${orderId}/${userId}`).then((r) => r.data)

/** @param {number} orderId @param {number} userId */
export const cancelOrder = (orderId, userId) =>
  api.put(`/order/cancelOrder/${orderId}/${userId}`).then((r) => r.data)
