import { TabellaRobot } from './admin/TabellaRobot'
import { TabellaUtenti } from './admin/TabellaUtenti'

/** Pannello admin: catalogo completo e gestione dei ruoli. Raggiungibile solo con ruolo ADMIN. */
export function Admin() {
  return (
    <div className="space-y-12">
      <TabellaRobot />
      <TabellaUtenti />
    </div>
  )
}
