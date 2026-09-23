import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from './useAuth'

/** Rotta di layout: senza sessione manda al login, ricordando da dove si veniva. */
export function RichiedeAuth() {
  const { sessione } = useAuth()
  const posizione = useLocation()
  if (!sessione) {
    return <Navigate to="/login" replace state={{ from: posizione.pathname }} />
  }
  return <Outlet />
}
