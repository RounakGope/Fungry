import Button from './Button'

export default function Modal({ open, onClose, title, children, confirmLabel = 'Confirm', onConfirm, loading = false, variant = 'primary' }) {
  if (!open) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div className="absolute inset-0 bg-black/60 backdrop-blur-sm transition-opacity" onClick={onClose} />
      <div className="relative w-full max-w-md rounded-2xl border border-zinc-800 bg-[#1e1e1e] p-5 shadow-2xl sm:p-6">
        <h2 className="text-xl font-semibold text-white">{title}</h2>
        <div className="mt-3 text-sm text-white/80 leading-relaxed">{children}</div>
        <div className="mt-6 flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
          <Button variant="secondary" className="w-full sm:w-auto" onClick={onClose} disabled={loading}>
            Cancel
          </Button>
          <Button variant={variant} className="w-full sm:w-auto" onClick={onConfirm} disabled={loading}>
            {loading ? 'Please wait…' : confirmLabel}
          </Button>
        </div>
      </div>
    </div>
  )
}