import { createContext } from 'react'

/** Valore: { successo(testo), errore(testo) }. Popolato da ToastProvider. */
export const ToastContext = createContext(null)
