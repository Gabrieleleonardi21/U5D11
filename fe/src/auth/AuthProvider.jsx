import { useCallback, useEffect, useMemo, useState } from 'react'
import { api, cancellaSessione, leggiSessione, salvaSessione } from '@/lib/api'
import { AuthContext } from './authContext'

/**
 * Tiene la sessione nello stato React e la sincronizza con il wrapper api.
 * Il FE usa "ruoli" solo per decidere cosa mostrare: ogni controllo vero lo fa il server.
 */
export function AuthProvider({ children }) {
  const [sessione, setSessione] = useState(leggiSessione)

  // api.js cancella la sessione su 401 e lancia questo evento: qui si aggiorna la UI
  useEffect(() => {
    const scaduta = () => setSessione(null)
    window.addEventListener('sessione-scaduta', scaduta)
    return () => window.removeEventListener('sessione-scaduta', scaduta)
  }, [])

  const entra = useCallback((risposta) => {
    salvaSessione(risposta)
    setSessione(risposta)
  }, [])

  const login = useCallback(async (dati) => entra(await api.auth.login(dati)), [entra])
  const register = useCallback(async (dati) => entra(await api.auth.register(dati)), [entra])

  const logout = useCallback(async () => {
    try {
      await api.auth.logout()   // revoca il token lato server
    } finally {
      cancellaSessione()        // in ogni caso si esce in locale
      setSessione(null)
    }
  }, [])

  const isAdmin = Boolean(sessione) && sessione.ruoli.includes('ADMIN')

  const valore = useMemo(
    () => ({ sessione, login, register, logout, isAdmin }),
    [sessione, login, register, logout, isAdmin],
  )
  return <AuthContext.Provider value={valore}>{children}</AuthContext.Provider>
}
