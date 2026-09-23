import { lazy, Suspense } from 'react'
import { api } from '@/lib/api'
import { useRichiesta } from '@/lib/useRichiesta'
import { sostituisci } from '@/lib/liste'
import { useTogglePreferito } from '@/lib/useTogglePreferito'
import { useAuth } from '@/auth/useAuth'
import { RobotCard } from '@/components/RobotCard'
import { Messaggio } from '@/components/Messaggio'
import { SkeletonGriglia } from '@/components/Skeleton'

// Three.js pesa piu' di tutto il resto: si scarica in un chunk a parte, dopo la pagina
const Hero3D = lazy(() => import('@/components/Hero3D'))

/** Stesso ingombro dell'hero, cosi' il catalogo non "salta" quando arriva il 3D. */
function HeroSegnaposto() {
  return <div className="mb-10 h-[260px] rounded-3xl border border-white/10 bg-slate-900/60 md:h-[400px]" aria-hidden="true" />
}

/**
 * La vetrina. Chiama sempre GET /api/robot: e' il server a rispondere con i soli pubblicati
 * (anonimo/utente) o con tutto, bozze e campi riservati compresi (admin).
 */
export function Home() {
  const { sessione } = useAuth()
  // si ricarica quando cambia la sessione: la stessa URL risponde in modo diverso
  const { dati: robot, setDati, errore, caricamento } = useRichiesta(() => api.robot.lista(), [sessione])

  // dopo il toggle si inverte solo il flag del robot toccato, senza ricaricare
  const togglePreferito = useTogglePreferito((r) => {
    setDati((lista) => sostituisci(lista, r.id, (x) => ({ ...x, preferito: !x.preferito })))
  })

  let onToggle
  if (sessione) onToggle = togglePreferito

  return (
    <>
      <Suspense fallback={<HeroSegnaposto />}>
        <Hero3D />
      </Suspense>
      <div className="mb-6 flex items-end justify-between">
        <h2 className="text-2xl font-bold">Catalogo</h2>
        {robot && <span className="text-sm text-slate-400">{robot.length} robot</span>}
      </div>
      <Messaggio>{errore}</Messaggio>
      {caricamento && <SkeletonGriglia />}
      {robot && (
        <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-4">
          {robot.map((r, i) => <RobotCard key={r.id} robot={r} onToggle={onToggle} indice={i} />)}
        </div>
      )}
    </>
  )
}
