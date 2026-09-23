import { Suspense } from 'react'
import { Canvas } from '@react-three/fiber'
import { OrbitControls } from '@react-three/drei'

/**
 * Scena Three.js condivisa da hero e viewer: luci, camera e controlli orbitali.
 * Il contenitore deve avere un'altezza esplicita: il Canvas riempie il genitore.
 * Il div .scena-3d serve alla regola CSS che su touch lascia lo scroll verticale alla
 * pagina (vedi index.css): senza, il robot "catturerebbe" il dito e la home non scorrerebbe.
 * Niente <Environment> di drei: scaricherebbe una HDR da CDN a ogni apertura.
 */
export function ScenaRobot({ children, autoRotate = false }) {
  return (
    <div className="scena-3d h-full w-full">
      <Canvas camera={{ position: [0, 0.8, 5.5], fov: 40 }} dpr={[1, 1.5]}>
        <ambientLight intensity={0.9} />
        <directionalLight position={[3, 5, 4]} intensity={1.8} />
        <directionalLight position={[-4, 2, -2]} intensity={0.6} color="#67e8f9" />
        <Suspense fallback={null}>{children}</Suspense>
        {/* solo rotazione: niente zoom ne' pan, e l'angolo verticale resta "a misura d'uomo" */}
        <OrbitControls
          enableZoom={false}
          enablePan={false}
          autoRotate={autoRotate}
          autoRotateSpeed={1.2}
          minPolarAngle={Math.PI / 3}
          maxPolarAngle={Math.PI / 1.9}
          target={[0, -0.4, 0]}
        />
      </Canvas>
    </div>
  )
}
