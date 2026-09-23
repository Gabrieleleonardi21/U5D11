import { ScenaRobot } from './tre/ScenaRobot'
import { RobotModello } from './tre/RobotModello'

/**
 * Hero della home: robot 3D interattivo a sinistra (si ruota trascinando, un clic
 * fa partire un'emote), testo a destra. Su telefono le due colonne si impilano.
 * Export default perche' Home lo carica con React.lazy: Three.js finisce in un
 * chunk separato e la vetrina si apre senza aspettarlo.
 */
export default function Hero3D() {
  return (
    <section className="mb-10 grid overflow-hidden rounded-3xl border border-white/10 bg-gradient-to-br from-slate-900 via-slate-950 to-cyan-950 md:grid-cols-2">
      <div className="relative h-[260px] md:h-[400px]">
        <ScenaRobot autoRotate>
          <RobotModello emoteAlClick position={[0, -1.7, 0]} scale={0.6} />
        </ScenaRobot>
        <p className="pointer-events-none absolute inset-x-0 bottom-3 text-center text-xs text-slate-400">
          Trascina per ruotare · tocca il robot per un saluto
        </p>
      </div>

      <div className="flex flex-col justify-center p-6 md:p-8">
        <h1 className="text-2xl font-bold leading-tight sm:text-3xl md:text-4xl">
          Robot per l'industria, la casa e la scuola
        </h1>
        <p className="mt-3 max-w-md text-slate-300">
          Una vetrina dove il server decide cosa mostrare: chi passa vede il catalogo, chi accede salva i
          preferiti, chi amministra pubblica.
        </p>
      </div>
    </section>
  )
}
