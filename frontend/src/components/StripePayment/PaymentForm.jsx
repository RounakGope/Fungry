// src/components/StripePayment/PaymentForm.jsx
import { useEffect, useRef, useState } from 'react'
import { PaymentElement, useElements, useStripe } from '@stripe/react-stripe-js'
import Button from '../Button'
import { useOrderStatusPoll } from '../../hooks/useOrderStatusPoll'

export default function PaymentForm({ orderId, onConfirmed }) {
  const stripe = useStripe()
  const elements = useElements()
  const [submitting, setSubmitting] = useState(false)
  const [errorMsg, setErrorMsg] = useState(null)
  const [waitingOnWebhook, setWaitingOnWebhook] = useState(false)

  const pollStatus = useOrderStatusPoll(orderId, waitingOnWebhook)
  const confirmedRef = useRef(false)

  // Navigate away in an effect, never during render, and only once.
  useEffect(() => {
    if (pollStatus === 'CONFIRMED' && !confirmedRef.current) {
      confirmedRef.current = true
      onConfirmed()
    }
  }, [pollStatus, onConfirmed])

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!stripe || !elements) return

    setSubmitting(true)
    setErrorMsg(null)

    const { error, paymentIntent } = await stripe.confirmPayment({
      elements,
      redirect: 'if_required', // stays inline — backend disabled redirect-based methods anyway
    })

    setSubmitting(false)

    if (error) {
      // Card declined, insufficient funds, etc. User can retry — same Elements instance.
      setErrorMsg(error.message)
      return
    }

    // paymentIntent.status here is Stripe's client-side view.
    // It does NOT mean the order is confirmed — only the webhook confirms that.
    if (paymentIntent?.status === 'succeeded' || paymentIntent?.status === 'processing') {
      setWaitingOnWebhook(true)
    }
  }

  if (waitingOnWebhook) {
    if (pollStatus === 'timeout') {
      return (
        <p className="text-zinc-300">
          Payment received — confirming your order is taking longer than usual.
          We'll update your order status shortly.
        </p>
      )
    }
    return <p className="text-zinc-300">Payment received, confirming your order…</p>
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <PaymentElement />
      {errorMsg && <p className="text-sm text-red-400">{errorMsg}</p>}
      <Button type="submit" className="w-full" disabled={!stripe || submitting}>
        {submitting ? 'Processing…' : 'Pay now'}
      </Button>
    </form>
  )
}