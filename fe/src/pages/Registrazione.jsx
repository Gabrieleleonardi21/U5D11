import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '@/auth/useAuth'
import { Campo } from '@/components/Campo'
import { Messaggio } from '@/components/Messaggio'

/** La registrazione assegna il ruolo UTENTE e fa subito entrare (il server ritorna il token). */
export function Registrazione() {
  const { register } = useAuth()
  const naviga = useNavigate()
  const [errore, setErrore] = useState(null)
  const [dettagli, setDettagli] = useState([])
  const [inCorso, setInCorso] = useState(false)

  async function invia(evento) {
    evento.preventDefault()
    setErrore(null)
    setDettagli([])
    setInCorso(true)
    const dati = Object.fromEntries(new FormData(evento.currentTarget))
    try {
      await register(dati)
      naviga('/', { replace: true })
    } catch (e) {
      setErrore(e.message)
      setDettagli(e.dettagli ?? [])
    } finally {
      setInCorso(false)
    }
  }

  return (
    <form onSubmit={invia} className="card mx-auto max-w-sm space-y-4 p-6">
      <h1 className="text-2xl font-bold">Crea un account</h1>
      <Messaggio dettagli={dettagli}>{errore}</Messaggio>
      <Campo etichetta="Username">
        <input name="username" required minLength={3} maxLength={50} autoComplete="username" className="campo" />
      </Campo>
      <Campo etichetta="Email">
        <input name="email" type="email" required autoComplete="email" className="campo" />
      </Campo>
      <Campo etichetta="Password (almeno 8 caratteri)">
        <input name="password" type="password" required minLength={8} autoComplete="new-password" className="campo" />
      </Campo>
      <button type="submit" disabled={inCorso} className="btn btn-primario w-full">Registrati</button>
      <p className="text-center text-sm text-slate-400">
        Hai gia' un account? <Link to="/login" className="text-cyan-300">Accedi</Link>
      </p>
    </form>
  )
}
