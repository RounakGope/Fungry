import api from './client'

/** Create a Stripe PaymentIntent for an order. Returns { clientSecret }. */
export const createPaymentIntent = (orderId) =>
  api.post(`/payment/create-intent/${orderId}`).then((r) => r.data)