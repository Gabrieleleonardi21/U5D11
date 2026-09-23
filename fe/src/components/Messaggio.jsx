// Riquadro per errori e conferme. tipo: 'errore' | 'successo' | 'info'
const STILI = {
  errore: 'border-red-500/40 bg-red-500/10 text-red-200',
  successo: 'border-emerald-500/40 bg-emerald-500/10 text-emerald-200',
  info: 'border-cyan-500/40 bg-cyan-500/10 text-cyan-100',
}

export function Messaggio({ tipo = 'errore', dettagli = [], children }) {
  if (!children) return null
  return (
    <div className={`rounded-xl border px-4 py-3 text-sm ${STILI[tipo]}`} role="alert">
      <p>{children}</p>
      {dettagli.length > 0 && (
        <ul className="mt-1 list-disc pl-5 opacity-90">
          {dettagli.map((d) => <li key={d}>{d}</li>)}
        </ul>
      )}
    </div>
  )
}
