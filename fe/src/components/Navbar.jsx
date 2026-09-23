import { useEffect, useState } from 'react'
import { Link, NavLink, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '@/auth/useAuth'
import { useToast } from '@/toast/useToast'

/** Classe del link: evidenziato se e' la pagina corrente. */
function classeLink({ isActive }) {
  let classe = 'rounded-lg px-3 py-2 text-sm font-medium transition hover:bg-white/10 md:py-1.5'
  if (isActive) classe += ' bg-white/10 text-cyan-300'
  return classe
}

/** Icona del menu mobile: tre righe se chiuso, una X se aperto. SVG inline, niente dipendenze. */
function IconaMenu({ aperto }) {
  let tracciato = 'M4 6h16M4 12h16M4 18h16'
  if (aperto) tracciato = 'M6 6l12 12M6 18L18 6'
  return (
    <svg viewBox="0 0 24 24" className="h-6 w-6" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" aria-hidden="true">
      <path d={tracciato} />
    </svg>
  )
}

/**
 * I link cambiano con il livello di accesso: e' solo comodita' per l'utente, il server decide comunque.
 * Mobile first: su telefono i link di navigazione stanno in un menu a tendina aperto dal
 * bottone hamburger, da md in su sono inline accanto al logo.
 */
export function Navbar() {
  const { sessione, isAdmin, logout } = useAuth()
  const notifica = useToast()
  const naviga = useNavigate()
  const posizione = useLocation()
  const [menuAperto, setMenuAperto] = useState(false)

  // cambiando pagina il menu mobile si richiude da solo
  useEffect(() => {
    setMenuAperto(false)
  }, [posizione.pathname])

  async function esci() {
    await logout()
    notifica.successo('Sei uscito')
    naviga('/')
  }

  // sotto md il contenitore dei link e' visibile solo se il menu e' aperto; da md in su sempre
  let classeMenu = 'hidden'
  if (menuAperto) classeMenu = 'flex'

  return (
    <header className="sticky top-0 z-20 border-b border-white/10 bg-slate-950/80 backdrop-blur">
      <nav className="mx-auto flex max-w-6xl flex-wrap items-center gap-2 px-4 py-3">
        <Link to="/" className="text-lg font-bold tracking-tight md:mr-4">
          <span className="text-cyan-400">Robot</span> Vetrina
        </Link>

        {/* order-last: su telefono il menu va sotto la riga logo + azioni, occupando tutta la larghezza */}
        <div
          id="menu-principale"
          className={`${classeMenu} order-last w-full flex-col gap-1 border-t border-white/10 pt-3 md:order-none md:flex md:w-auto md:flex-row md:items-center md:border-0 md:pt-0`}
        >
          <NavLink to="/" end className={classeLink}>Vetrina</NavLink>
          {sessione && <NavLink to="/preferiti" className={classeLink}>Preferiti</NavLink>}
          {isAdmin && <NavLink to="/admin" className={classeLink}>Admin</NavLink>}
          {/* nome utente: nel menu su telefono, a destra su schermi larghi */}
          {sessione && (
            <span className="px-3 py-2 text-sm text-slate-400 md:hidden">
              {sessione.username}
              {isAdmin && <span className="ml-1 rounded bg-amber-500/20 px-1.5 py-0.5 text-xs text-amber-300">admin</span>}
            </span>
          )}
        </div>

        <div className="ml-auto flex items-center gap-2">
          {sessione && (
            <>
              <span className="hidden text-sm text-slate-400 md:inline">
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
          <button
            type="button"
            onClick={() => setMenuAperto((aperto) => !aperto)}
            aria-expanded={menuAperto}
            aria-controls="menu-principale"
            aria-label="Apri o chiudi il menu"
            className="rounded-lg p-2 transition hover:bg-white/10 md:hidden"
          >
            <IconaMenu aperto={menuAperto} />
          </button>
        </div>
      </nav>
    </header>
  )
}
