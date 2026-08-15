const statusStyles = {
  PENDING: 'bg-amber-500/10 text-amber-400 border-amber-400/20',
  CONFIRMED: 'bg-emerald-500/10 text-emerald-400 border-emerald-400/20',
  PREPARING: 'bg-sky-500/10 text-sky-400 border-sky-400/20',
  OUT_FOR_DELIVERY: 'bg-violet-500/10 text-violet-400 border-violet-400/20',
  DELIVERED: 'bg-emerald-500/10 text-emerald-400 border-emerald-400/20',
  CANCELLED: 'bg-rose-500/10 text-rose-400 border-rose-400/20',
}

export default function Badge({ status, children }) {
  const label = children || status?.replace(/_/g, ' ')
  const style = statusStyles[status] || 'bg-surface-overlay text-muted border-border'

  return (
    <span className={`inline-flex items-center rounded-full border px-2.5 py-0.5 text-[11px] font-semibold uppercase tracking-[0.2em] ${style}`}>
      {label}
    </span>
  )
}
