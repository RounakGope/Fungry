// src/components/StripePayment/index.jsx
import { useEffect, useRef, useState } from 'react'
import { Elements } from '@stripe/react-stripe-js'
import { stripePromise } from '../../lib/stripe'
import * as paymentApi from '../../api/payment'
import { getErrorMessage } from '../../utils/constants'
import LoadingSpinner from '../LoadingSpinner'
import PaymentForm from './PaymentForm'

export default function StripePayment({ orderId, onConfirmed }) {
  const [clientSecret, setClientSecret] = useState(null)
  const [error, setError] = useState(null)
  const requestRef = useRef(null)

  useEffect(() => {
    let cancelled = false

    if (!requestRef.current) {
      requestRef.current = paymentApi.createPaymentIntent(orderId)
    }

    requestRef.current
      .then((data) => { if (!cancelled) setClientSecret(data.clientSecret) })
      .catch((err) => { if (!cancelled) setError(getErrorMessage(err)) })

    return () => { cancelled = true }
  }, [orderId])

  if (error) return <p className="text-red-400">{error}</p>
  if (!clientSecret) return <LoadingSpinner label="Setting up payment" />

  const options = { clientSecret, appearance: { theme: 'night' } }

  return (
    <Elements stripe={stripePromise} options={options}>
      <PaymentForm orderId={orderId} onConfirmed={onConfirmed} />
    </Elements>
  )
}