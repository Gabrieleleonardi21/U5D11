import { useState } from 'react'
import { Link } from 'react-router-dom'
import { etichettaCategoria, formattaPrezzo, immagineRobot } from '@/lib/formato'

/**
 * Card della vetrina.
 * - onToggle arriva solo se c'e' un utente collegato: senza, niente cuore.
 * - "pubblicato" esiste solo nel DTO admin: il badge Bozza compare solo a lui.
 * - indice serve a far comparire le card una dopo l'altra (ritardo crescente).
 */
export function RobotCard({ robot, onToggle, indice = 0 }) {
  const [cuoreBatte, setCuoreBatte] = useState(false)

  function clicCuore() {
    setCuoreBatte(true)
    onToggle(robot)
  }

  let classeCuore = 'text-slate-400'
  if (robot.preferito) classeCuore = 'text-rose-400'
  if (cuoreBatte) classeCuore += ' animate-battito'

  return (
    <article
      className="card group relative flex flex-col animate-comparsa transition duration-300 hover:-translate-y-1 hover:border-cyan-400/30 hover:shadow-lg hover:shadow-cyan-500/10"
      style={{ animationDelay: `${Math.min(indice, 12) * 40}ms` }}
    >
      {robot.pubblicato === false && (
        <span className="absolute left-3 top-3 z-10 rounded-full bg-amber-500 px-2 py-0.5 text-xs font-semibold text-slate-950">
          Bozza
        </span>
      )}
      {onToggle && (
        <button
          type="button"
          onClick={clicCuore}
          aria-label={robot.preferito ? 'Togli dai preferiti' : 'Aggiungi ai preferiti'}
          className="absolute right-3 top-3 z-10 rounded-full bg-slate-950/70 p-2 text-xl leading-none transition hover:scale-110 active:scale-90"
        >
          <span className={`inline-block ${classeCuore}`} onAnimationEnd={() => setCuoreBatte(false)}>
            {robot.preferito && '♥'}
            {!robot.preferito && '♡'}
          </span>
        </button>
      )}

      <Link to={`/robot/${robot.id}`} className="block overflow-hidden rounded-t-2xl">
        <img
          src={immagineRobot(robot.nome)}
          alt={robot.nome}
          loading="lazy"
          className="aspect-square w-full object-cover transition duration-500 group-hover:scale-105"
        />
      </Link>

      <div className="flex flex-1 flex-col gap-1 p-4">
        <span className="text-xs uppercase tracking-wide text-cyan-400">{etichettaCategoria(robot.categoria)}</span>
        <Link to={`/robot/${robot.id}`} className="font-semibold hover:text-cyan-300">{robot.nome}</Link>
        <span className="text-sm text-slate-400">{robot.produttore}</span>
        <span className="mt-auto pt-2 text-lg font-bold">{formattaPrezzo(robot.prezzo)}</span>
      </div>
    </article>
  )
}
