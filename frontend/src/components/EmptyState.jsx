export default function EmptyState({ title, description, action }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center">
      <h3 className="text-base font-semibold text-zinc-50">{title}</h3>
      {description && <p className="mt-1 max-w-sm text-sm text-white/70">{description}</p>}
      {action && <div className="mt-4">{action}</div>}
    </div>
  )
}
