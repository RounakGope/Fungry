import api from './client'

/** Create an order from the logged-in user's cart. Address is set separately. */
export const createOrder = () =>
  api.post('/order/').then((r) => r.data)

/** Attach a delivery address to an existing order. */
export const setOrderAddress = (orderId, addressId) =>
  api.put(`/order/${orderId}/address/${addressId}`).then((r) => r.data)

/** Confirm an order as cash-on-delivery. */
export const confirmCodOrder = (orderId) =>
  api.post(`/order/confirmCod/${orderId}`).then((r) => r.data)

/** Delete an order (must belong to the logged-in user). */
export const deleteOrder = (orderId) =>
  api.delete(`/order/${orderId}`).then((r) => r.data)

/** View a specific order as its owning user. */
export const getOrderByUser = (orderId) =>
  api.get(`/order/viewOrderByUser/${orderId}`).then((r) => r.data)

/** View a specific order in the context of a restaurant. */
export const getOrderByRestaurant = (restId, orderId) =>
  api.get(`/order/viewOrderByRes/${restId}/${orderId}`).then((r) => r.data)

/** All orders placed by the logged-in user. */
export const getOrdersByUser = () =>
  api.get('/order/viewAllOrderUser').then((r) => r.data)

/** All orders for a given restaurant (caller must be authorized for that restaurant). */
export const getOrdersByRestaurant = (restId) =>
  api.get(`/order/viewAllOrderByRest/${restId}`).then((r) => r.data)

/** Update an order's status (caller must own the restaurant, or be an admin). */
export const updateOrderStatus = (orderId, restId, orderStatus) =>
  api
    .put(`/order/updateOrderStatus/${orderId}/${restId}`, null, { params: { orderStatus } })
    .then((r) => r.data)

/** Get the status of a specific order (must belong to the logged-in user). */
export const getOrderStatus = (orderId) =>
  api.get(`/order/orderStatus/${orderId}`).then((r) => r.data)

/** Get the total amount of a specific order (must belong to the logged-in user). */
export const getOrderAmount = (orderId) =>
  api.get(`/order/orderAmt/${orderId}`).then((r) => r.data)

/** Cancel an order (must belong to the logged-in user). */
export const cancelOrder = (orderId) =>
  api.put(`/order/cancelOrder/${orderId}`).then((r) => r.data)