import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '@/auth/useAuth'
import { useToast } from '@/toast/useToast'

/** Classe del link: evidenziato se e' la pagina corrente. */
function classeLink({ isActive }) {
  let classe = 'rounded-lg px-3 py-1.5 text-sm font-medium transition hover:bg-white/10'
  if (isActive) classe += ' bg-white/10 text-cyan-300'
  return classe
}

/** I link cambiano con il livello di accesso: e' solo comodita' per l'utente, il server decide comunque. */
export function Navbar() {
  const { sessione, isAdmin, logout } = useAuth()
  const notifica = useToast()
  const naviga = useNavigate()

  async function esci() {
    await logout()
    notifica.successo('Sei uscito')
    naviga('/')
  }

  return (
    <header className="sticky top-0 z-20 border-b border-white/10 bg-slate-950/80 backdrop-blur">
      <nav className="mx-auto flex max-w-6xl items-center gap-2 px-4 py-3">
        <Link to="/" className="mr-4 text-lg font-bold tracking-tight">
          <span className="text-cyan-400">Robot</span> Vetrina
        </Link>
        <NavLink to="/" end className={classeLink}>Vetrina</NavLink>
        {sessione && <NavLink to="/preferiti" className={classeLink}>Preferiti</NavLink>}
        {isAdmin && <NavLink to="/admin" className={classeLink}>Admin</NavLink>}

        <div className="ml-auto flex items-center gap-2">
          {sessione && (
            <>
              <span className="hidden text-sm text-slate-400 sm:inline">
                {sessione.username}
                {isAdmin && <span className="ml-1 rounded bg-amber-500/20 px-1.5 py-0.5 text-xs text-amber-300">admin</span>}
              </span>
              <button type="button" onClick={esci} className="btn btn-secondario">Esci</button>
            </>
          )}
          {!sessione && (
            <>
              <Link to="/login" className="btn btn-secondario">Accedi</Link>
              <Link to="/registrazione" className="btn btn-primario">Registrati</Link>
            </>
          )}
        </div>
      </nav>
    </header>
  )
}
