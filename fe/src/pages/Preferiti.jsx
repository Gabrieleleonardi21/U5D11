import { Link } from 'react-router-dom'
import { api } from '@/lib/api'
import { useRichiesta } from '@/lib/useRichiesta'
import { useTogglePreferito } from '@/lib/useTogglePreferito'
import { RobotCard } from '@/components/RobotCard'
import { Messaggio } from '@/components/Messaggio'
import { SkeletonGriglia } from '@/components/Skeleton'

/** I preferiti dell'utente collegato: il server li filtra per proprietario, il FE non manda nessun id utente. */
export function Preferiti() {
  const { dati: robot, setDati, errore, caricamento } = useRichiesta(() => api.preferiti.lista())

  // qui sono tutti preferiti: il toggle puo' solo togliere, e la card sparisce
  const togli = useTogglePreferito((r) => {
    setDati((lista) => lista.filter((x) => x.id !== r.id))
  })

  return (
    <>
      <h1 className="mb-6 text-2xl font-bold">I miei preferiti</h1>
      <Messaggio>{errore}</Messaggio>
      {caricamento && <SkeletonGriglia quante={4} />}
      {robot && robot.length === 0 && (
        <p className="text-slate-400">
          Nessun preferito. <Link to="/" className="text-cyan-300">Vai alla vetrina</Link> e tocca il cuore.
        </p>
      )}
      {robot && robot.length > 0 && (
        <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-4">
          {robot.map((r, i) => <RobotCard key={r.id} robot={r} onToggle={togli} indice={i} />)}
        </div>
      )}
    </>
  )
}
