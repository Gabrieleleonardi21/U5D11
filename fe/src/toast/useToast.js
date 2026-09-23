import { useContext } from 'react'
import { ToastContext } from './toastContext'

/** Notifiche a scomparsa per l'esito delle azioni: `const notifica = useToast(); notifica.successo('...')`. */
export function useToast() {
  return useContext(ToastContext)
}
