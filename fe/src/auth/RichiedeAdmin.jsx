import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from './useAuth'

/** Rotta di layout: chi non e' admin torna alla vetrina. Il server rifiuterebbe comunque le chiamate. */
export function RichiedeAdmin() {
  const { isAdmin } = useAuth()
  if (!isAdmin) {
    return <Navigate to="/" replace />
  }
  return <Outlet />
}
