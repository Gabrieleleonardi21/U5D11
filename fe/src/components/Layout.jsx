import { Outlet } from 'react-router-dom'
import { Navbar } from './Navbar'

/** Struttura comune a tutte le pagine: navbar in alto, contenuto della rotta al centro. */
export function Layout() {
  return (
    <div className="min-h-screen bg-slate-950 text-slate-100">
      <Navbar />
      <main className="mx-auto max-w-6xl px-4 py-8">
        <Outlet />
      </main>
      <footer className="mx-auto max-w-6xl px-4 py-8 text-xs text-slate-500">
        Immagini generate da robohash.org · Modello 3D "RobotExpressive" di Tomás Laulhé (CC0)
      </footer>
    </div>
  )
}
