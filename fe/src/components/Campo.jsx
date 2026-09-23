/** Etichetta + controllo di form, per non ripetere lo stesso markup in ogni pagina. */
export function Campo({ etichetta, children }) {
  return (
    <label className="block space-y-1.5 text-sm">
      <span className="font-medium text-slate-300">{etichetta}</span>
      {children}
    </label>
  )
}
