// Segnaposto animati mostrati durante il caricamento: stessa forma del contenuto vero,
// cosi' la pagina non "salta" quando arrivano i dati.

function Barra({ classe }) {
  return <div className={`rounded bg-white/10 ${classe}`} />
}

export function SkeletonCard() {
  return (
    <div className="card animate-pulse">
      <div className="aspect-square rounded-t-2xl bg-white/5" />
      <div className="space-y-2 p-4">
        <Barra classe="h-3 w-1/3" />
        <Barra classe="h-4 w-2/3" />
        <Barra classe="h-3 w-1/2" />
        <Barra classe="mt-3 h-5 w-1/3" />
      </div>
    </div>
  )
}

/** Griglia di card fantasma, stessa griglia della vetrina. */
export function SkeletonGriglia({ quante = 8 }) {
  return (
    <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-4" aria-busy="true">
      {Array.from({ length: quante }, (_, i) => <SkeletonCard key={i} />)}
    </div>
  )
}

/** Righe fantasma per le tabelle dell'admin. */
export function SkeletonRighe({ quante = 5 }) {
  return (
    <div className="card animate-pulse divide-y divide-white/5" aria-busy="true">
      {Array.from({ length: quante }, (_, i) => (
        <div key={i} className="flex items-center gap-6 p-4">
          <Barra classe="h-4 w-1/4" />
          <Barra classe="h-4 w-1/6" />
          <Barra classe="h-4 w-1/6" />
          <Barra classe="ml-auto h-7 w-24" />
        </div>
      ))}
    </div>
  )
}

/** Pagina di dettaglio fantasma: viewer a sinistra, testo a destra. */
export function SkeletonDettaglio() {
  return (
    <div className="grid animate-pulse gap-8 md:grid-cols-2" aria-busy="true">
      <div className="card h-[320px]" />
      <div className="space-y-3">
        <Barra classe="h-3 w-1/4" />
        <Barra classe="h-8 w-2/3" />
        <Barra classe="h-4 w-1/3" />
        <Barra classe="mt-6 h-4 w-full" />
        <Barra classe="h-4 w-5/6" />
        <Barra classe="mt-6 h-8 w-1/3" />
      </div>
    </div>
  )
}
