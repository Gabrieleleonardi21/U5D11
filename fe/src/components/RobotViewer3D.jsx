import { ScenaRobot } from './tre/ScenaRobot'
import { RobotModello } from './tre/RobotModello'

/**
 * Viewer 3D della pagina di dettaglio: lo stesso robot dell'hero, con il corpo
 * del colore della categoria, in rotazione lenta. Caricato con React.lazy.
 */
export default function RobotViewer3D({ colore }) {
  return (
    <ScenaRobot autoRotate>
      <RobotModello colore={colore} position={[0, -1.7, 0]} scale={0.6} />
    </ScenaRobot>
  )
}
