import { useCallback, useMemo, useState } from 'react'
import { ToastContext } from './toastContext'

const DURATA_MS = 3500

const STILI = {
  successo: 'border-emerald-500/40 bg-emerald-950/90 text-emerald-100',
  errore: 'border-red-500/40 bg-red-950/90 text-red-100',
}

const ICONE = {
  successo: '✓',
  errore: '!',
}

/**
 * Coda di notifiche in basso a destra. Ogni toast sparisce da solo dopo DURATA_MS
 * o al clic. Sostituisce i riquadri inline per l'esito delle azioni (preferiti,
 * pubblicazione, ruoli); i form tengono il riquadro perche' devono elencare i campi errati.
 */
export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([])

  const rimuovi = useCallback((id) => {
    setToasts((lista) => lista.filter((t) => t.id !== id))
  }, [])

  const mostra = useCallback((tipo, testo) => {
    const id = `${Date.now()}-${Math.random()}`
    setToasts((lista) => [...lista, { id, tipo, testo }])
    setTimeout(() => rimuovi(id), DURATA_MS)
  }, [rimuovi])

  const notifica = useMemo(() => ({
    successo: (testo) => mostra('successo', testo),
    errore: (testo) => mostra('errore', testo),
  }), [mostra])

  return (
    <ToastContext.Provider value={notifica}>
      {children}
      <div className="pointer-events-none fixed inset-x-4 bottom-4 z-50 flex flex-col gap-2 sm:inset-x-auto sm:right-4 sm:w-80">
        {toasts.map((t) => (
          <button
            key={t.id}
            type="button"
            onClick={() => rimuovi(t.id)}
            role="status"
            className={`pointer-events-auto flex items-start gap-3 rounded-xl border px-4 py-3 text-left text-sm shadow-lg backdrop-blur animate-scivola ${STILI[t.tipo]}`}
          >
            <span className="mt-0.5 font-bold" aria-hidden="true">{ICONE[t.tipo]}</span>
            <span>{t.testo}</span>
          </button>
        ))}
      </div>
    </ToastContext.Provider>
  )
}
