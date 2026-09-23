import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '@/auth/useAuth'
import { Campo } from '@/components/Campo'
import { Messaggio } from '@/components/Messaggio'

export function Login() {
  const { login } = useAuth()
  const naviga = useNavigate()
  const posizione = useLocation()
  const [errore, setErrore] = useState(null)
  const [inCorso, setInCorso] = useState(false)

  async function invia(evento) {
    evento.preventDefault()
    setErrore(null)
    setInCorso(true)
    const dati = Object.fromEntries(new FormData(evento.currentTarget))
    try {
      await login(dati)
      naviga(posizione.state?.from ?? '/', { replace: true })   // torna dove si voleva andare
    } catch (e) {
      setErrore(e.message)
    } finally {
      setInCorso(false)
    }
  }

  return (
    <form onSubmit={invia} className="card mx-auto max-w-sm space-y-4 p-6">
      <h1 className="text-2xl font-bold">Accedi</h1>
      <Messaggio>{errore}</Messaggio>
      <Campo etichetta="Username">
        <input name="username" required autoComplete="username" className="campo" />
      </Campo>
      <Campo etichetta="Password">
        <input name="password" type="password" required autoComplete="current-password" className="campo" />
      </Campo>
      <button type="submit" disabled={inCorso} className="btn btn-primario w-full">Entra</button>
      <p className="text-center text-sm text-slate-400">
        Non hai un account? <Link to="/registrazione" className="text-cyan-300">Registrati</Link>
      </p>
    </form>
  )
}
