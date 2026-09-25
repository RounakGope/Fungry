// src/hooks/useOrderStatusPoll.js
import { useEffect, useState } from 'react'
import * as orderApi from '../api/order'

const POLL_INTERVAL_MS = 2000
const TIMEOUT_MS = 45000

/**
 * Polls order status while `active` is true.
 * Returns 'CONFIRMED' once the webhook has confirmed the order, or 'timeout'
 * after TIMEOUT_MS. A failed request (network blip, backend waking up on
 * Render's free tier) does not stop polling; it just tries again.
 */
export function useOrderStatusPoll(orderId, active) {
  const [status, setStatus] = useState(null) // null | 'CONFIRMED' | 'timeout'

  useEffect(() => {
    if (!active || !orderId) return

    const startedAt = Date.now()
    let cancelled = false
    let timer = null

    const tick = async () => {
      if (cancelled) return
      try {
        const orderStatus = await orderApi.getOrderStatus(orderId)
        if (cancelled) return
        if (orderStatus === 'CONFIRMED') {
          setStatus('CONFIRMED')
          return // stop polling
        }
      } catch {
        if (cancelled) return
        // transient failure: fall through and retry until the timeout
      }
      if (Date.now() - startedAt > TIMEOUT_MS) {
        setStatus('timeout')
        return
      }
      timer = setTimeout(tick, POLL_INTERVAL_MS)
    }

    tick()
    return () => {
      cancelled = true
      clearTimeout(timer)
    }
  }, [orderId, active])

  return status
}
