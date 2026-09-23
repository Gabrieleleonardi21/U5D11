import { lazy, Suspense } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api } from '@/lib/api'
import { COLORE_CATEGORIA, etichettaCategoria, formattaData, formattaPrezzo, immagineRobot } from '@/lib/formato'
import { useRichiesta } from '@/lib/useRichiesta'
import { sostituisci } from '@/lib/liste'
import { useTogglePreferito } from '@/lib/useTogglePreferito'
import { useAuth } from '@/auth/useAuth'
import { Messaggio } from '@/components/Messaggio'
import { RobotCard } from '@/components/RobotCard'
import { SkeletonDettaglio } from '@/components/Skeleton'

// stesso chunk Three.js dell'hero: si scarica solo quando serve
const RobotViewer3D = lazy(() => import('@/components/RobotViewer3D'))

/** Riga "etichetta: valore" della scheda tecnica. */
function Riga({ etichetta, children }) {
  return (
    <div className="flex justify-between gap-4 border-b border-white/5 py-2 text-sm last:border-0">
      <span className="text-slate-400">{etichetta}</span>
      <span className="text-right font-medium">{children}</span>
    </div>
  )
}

/** Altri robot della stessa categoria (max 4), presi dalla stessa lista della vetrina. */
function RobotSimili({ corrente }) {
  const { sessione } = useAuth()
  const { dati, setDati } = useRichiesta(() => api.robot.lista(), [corrente.id, sessione])
  const togglePreferito = useTogglePreferito((r) => {
    setDati((lista) => sostituisci(lista, r.id, (x) => ({ ...x, preferito: !x.preferito })))
  })

  if (!dati) return null
  const simili = dati
    .filter((r) => r.categoria === corrente.categoria && r.id !== corrente.id)
    .slice(0, 4)
  if (simili.length === 0) return null

  let onToggle
  if (sessione) onToggle = togglePreferito

  return (
    <section>
      <h2 className="mb-4 text-xl font-bold">Altri {etichettaCategoria(corrente.categoria).toLowerCase()}</h2>
      <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-4">
        {simili.map((r, i) => <RobotCard key={r.id} robot={r} onToggle={onToggle} indice={i} />)}
      </div>
    </section>
  )
}

/** Dettaglio: per una bozza il server risponde 404 a chi non e' admin. */
export function RobotDettaglio() {
  const { id } = useParams()
  const { sessione } = useAuth()
  const { dati: robot, setDati, errore, caricamento } = useRichiesta(() => api.robot.dettaglio(id), [id, sessione])
  const togglePreferito = useTogglePreferito(() => {
    setDati((r) => ({ ...r, preferito: !r.preferito }))
  })

  if (caricamento) return <SkeletonDettaglio />
  if (errore) {
    return (
      <div className="space-y-4">
        <Messaggio>{errore}</Messaggio>
        <Link to="/" className="btn btn-secondario">Torna alla vetrina</Link>
      </div>
    )
  }

  // i campi riservati esistono solo nella risposta per l'admin
  const vistaAdmin = 'pubblicato' in robot

  return (
    <div className="space-y-10">
      <div className="grid gap-8 md:grid-cols-2">
        {/* viewer 3D con il corpo del colore della categoria */}
        <div className="card relative h-[320px] overflow-hidden animate-comparsa md:h-[420px]">
          <Suspense fallback={<div className="h-full animate-pulse bg-white/5" />}>
            <RobotViewer3D colore={COLORE_CATEGORIA[robot.categoria]} />
          </Suspense>
          <p className="pointer-events-none absolute inset-x-0 bottom-3 text-center text-xs text-slate-400">
            Trascina per ruotare
          </p>
        </div>

        <div className="animate-comparsa" style={{ animationDelay: '80ms' }}>
          <div className="flex items-center gap-4">
            <img src={immagineRobot(robot.nome, 160)} alt="" className="h-20 w-20 rounded-2xl border border-white/10" />
            <div>
              <span className="text-sm uppercase tracking-wide text-cyan-400">{etichettaCategoria(robot.categoria)}</span>
              <h1 className="text-3xl font-bold">{robot.nome}</h1>
              <p className="text-slate-400">{robot.produttore}</p>
            </div>
          </div>
          <p className="mt-4 text-slate-300">{robot.descrizione}</p>
          <p className="mt-6 text-3xl font-bold">{formattaPrezzo(robot.prezzo)}</p>

          {sessione && (
            <button type="button" onClick={() => togglePreferito(robot)} className="btn btn-primario mt-4">
              {robot.preferito && '♥ Nei preferiti'}
              {!robot.preferito && '♡ Aggiungi ai preferiti'}
            </button>
          )}

          {vistaAdmin && (
            <section className="mt-8 rounded-2xl border border-amber-500/30 bg-amber-500/5 p-4">
              <h2 className="mb-2 font-semibold text-amber-300">Dati riservati (admin)</h2>
              <Riga etichetta="Stato">{robot.pubblicato && 'Pubblicato'}{!robot.pubblicato && 'Bozza'}</Riga>
              <Riga etichetta="Prezzo d'acquisto">{formattaPrezzo(robot.prezzoAcquisto)}</Riga>
              <Riga etichetta="Fornitore">{robot.fornitore ?? '—'}</Riga>
            </section>
          )}
        </div>
      </div>

      <section className="card p-5 animate-comparsa" style={{ animationDelay: '160ms' }}>
        <h2 className="mb-2 text-lg font-semibold">Scheda tecnica</h2>
        <Riga etichetta="Categoria">{etichettaCategoria(robot.categoria)}</Riga>
        <Riga etichetta="Produttore">{robot.produttore}</Riga>
        <Riga etichetta="Prezzo di listino">{formattaPrezzo(robot.prezzo)}</Riga>
        <Riga etichetta="In catalogo dal">{formattaData(robot.createdAt)}</Riga>
        <Riga etichetta="Codice articolo">{robot.id.slice(0, 8).toUpperCase()}</Riga>
      </section>

      <RobotSimili corrente={robot} />
    </div>
  )
}
