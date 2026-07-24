import Button from './Button'

export default function Modal({ open, onClose, title, children, confirmLabel = 'Confirm', onConfirm, loading = false, variant = 'primary' }) {
  if (!open) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div className="absolute inset-0 bg-black/30" onClick={onClose} />
      <div className="relative w-full max-w-md rounded-2xl border border-gray-200 bg-white p-5 shadow-sm sm:p-6">
        <h2 className="text-lg font-semibold text-gray-900">{title}</h2>
        <div className="mt-3 text-sm text-gray-600">{children}</div>
        <div className="mt-6 flex flex-col-reverse gap-2 sm:flex-row sm:justify-end">
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
