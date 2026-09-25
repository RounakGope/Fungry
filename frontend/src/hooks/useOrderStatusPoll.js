// src/hooks/useOrderStatusPoll.js
import { useEffect, useRef, useState } from 'react'
import * as orderApi from '../api/order'

const POLL_INTERVAL_MS = 2000
const TIMEOUT_MS = 30000

/**
 * Polls order status while `active` is true.
 * Stops on reaching CONFIRMED, or after TIMEOUT_MS with 'timeout'.
 */
export function useOrderStatusPoll(orderId, active) {
  const [status, setStatus] = useState(null) // null | 'CONFIRMED' | 'timeout' | 'error'
  const startedAt = useRef(null)

  useEffect(() => {
    if (!active || !orderId) return

    startedAt.current = Date.now()
    let cancelled = false

    const tick = async () => {
      if (cancelled) return
      try {
        const orderStatus = await orderApi.getOrderStatus(orderId)
        if (cancelled) return

        if (orderStatus === 'CONFIRMED') {
          setStatus('CONFIRMED')
          return // stop polling
        }
        if (Date.now() - startedAt.current > TIMEOUT_MS) {
          setStatus('timeout')
          return
        }
        setTimeout(tick, POLL_INTERVAL_MS)
      } catch {
        if (!cancelled) setStatus('error')
      }
    }

    tick()
    return () => { cancelled = true }
  }, [orderId, active])

  return status
}