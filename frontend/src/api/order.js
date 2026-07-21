import api from './client'

// POST /api-v1.0/order/{cartId}/{userId}/{addressId}
export const createOrder = (cartId, userId, addressId) =>
  api.post(`/order/${cartId}/${userId}/${addressId}`).then((res) => res.data)

// DELETE /api-v1.0/order/{orderId}/{userId}
export const deleteOrder = (orderId, userId) =>
  api.delete(`/order/${orderId}/${userId}`)

// GET /api-v1.0/order/viewOrderByUser/{orderId}/{userId}
export const getOrderByUser = (orderId, userId) =>
  api.get(`/order/viewOrderByUser/${orderId}/${userId}`).then((res) => res.data)

// GET /api-v1.0/order/viewOrderByRes/{restId}/{orderId}
export const getOrderByRestaurant = (restId, orderId) =>
  api.get(`/order/viewOrderByRes/${restId}/${orderId}`).then((res) => res.data)

// GET /api-v1.0/order/viewAllOrderUser/{userId}
export const getOrdersByUser = (userId) =>
  api.get(`/order/viewAllOrderUser/${userId}`).then((res) => res.data)

// GET /api-v1.0/order/viewAllOrderByRest/{restId}
export const getOrdersByRestaurant = (restId) =>
  api.get(`/order/viewAllOrderByRest/${restId}`).then((res) => res.data)

// GET /api-v1.0/order/updateOrderStatus/{orderId}/{restId}?orderStatus=STATUS
// Note: backend uses @GetMapping for this endpoint
export const updateOrderStatus = (orderId, restId, orderStatus) =>
  api
    .get(`/order/updateOrderStatus/${orderId}/${restId}`, { params: { orderStatus } })
    .then((res) => res.data)

// GET /api-v1.0/order/orderStatus/{orderId}/{userId}
export const getOrderStatus = (orderId, userId) =>
  api.get(`/order/orderStatus/${orderId}/${userId}`).then((res) => res.data)

// GET /api-v1.0/order/orderAmt/{orderId}/{userId}
export const getOrderAmount = (orderId, userId) =>
  api.get(`/order/orderAmt/${orderId}/${userId}`).then((res) => res.data)

// PUT /api-v1.0/order/cancelOrder/{orderId}/{userId}
export const cancelOrder = (orderId, userId) =>
  api.put(`/order/cancelOrder/${orderId}/${userId}`)