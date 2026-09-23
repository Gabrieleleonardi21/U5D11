import { useEffect, useMemo, useRef, useState } from 'react'
import { useAnimations, useGLTF } from '@react-three/drei'
import { useFrame } from '@react-three/fiber'
import { LoopOnce } from 'three'
import { clone as clonaConScheletro } from 'three/examples/jsm/utils/SkeletonUtils.js'

// Modello CC0 dagli esempi di three.js, copiato in public/ per non dipendere da CDN esterne.
const MODELLO = '/models/RobotExpressive.glb'
// Animazioni brevi del modello, riprodotte una volta al clic
const EMOTE = ['Wave', 'Jump', 'ThumbsUp', 'Yes', 'Punch', 'Dance']
// Materiale del corpo nel GLB: e' quello che si ricolora per categoria
const MATERIALE_CORPO = 'Main'

/**
 * Il robot animato. Parte in "Idle"; con emoteAlClick un clic fa partire un'emote
 * casuale e alla fine si torna a Idle. Con colore si ricolora il corpo.
 * La scena viene clonata: ogni istanza (hero, dettaglio) ha la sua copia, cosi'
 * ricolorare una non tocca l'altra. Le mesh sono skinned: serve SkeletonUtils, non clone().
 */
export function RobotModello({ colore, emoteAlClick = false, ...props }) {
  const gruppo = useRef(null)
  const { scene, animations } = useGLTF(MODELLO)
  const copia = useMemo(() => clonaConScheletro(scene), [scene])
  const { actions, mixer } = useAnimations(animations, gruppo)
  const [inEmote, setInEmote] = useState(false)

  // colore del corpo: si clona il materiale prima di cambiarlo, l'originale resta com'e'
  useEffect(() => {
    if (!colore) return
    copia.traverse((oggetto) => {
      if (oggetto.isMesh && oggetto.material && oggetto.material.name === MATERIALE_CORPO) {
        oggetto.material = oggetto.material.clone()
        oggetto.material.color.set(colore)
      }
    })
  }, [copia, colore])

  // DEBUG TEMPORANEO
  useEffect(() => {
    let mesh = 0
    copia.traverse((o) => { if (o.isMesh) mesh += 1 })
    console.log('[3D] mount', { colore, mesh, visibile: copia.visible })
    return () => console.log('[3D] unmount', { colore })
  }, [copia, colore])
  const frame = useRef(0)
  useFrame((stato) => {
    frame.current += 1
    if (frame.current % 90 === 1) {
      console.log('[3D] frame', { colore, triangoli: stato.gl.info.render.triangles, figli: stato.scene.children.length, gruppoInScena: Boolean(gruppo.current && gruppo.current.parent) })
    }
  })

  // animazione di base
  useEffect(() => {
    const idle = actions.Idle
    if (!idle) return undefined
    idle.reset().fadeIn(0.5).play()
    return () => { idle.fadeOut(0.3) }
  }, [actions])

  // quando un'emote finisce (LoopOnce emette "finished") si torna a Idle
  useEffect(() => {
    const fine = (evento) => {
      if (evento.action === actions.Idle) return
      evento.action.fadeOut(0.2)
      actions.Idle.reset().fadeIn(0.2).play()
      setInEmote(false)
    }
    mixer.addEventListener('finished', fine)
    return () => mixer.removeEventListener('finished', fine)
  }, [mixer, actions])

  function emote() {
    if (!emoteAlClick || inEmote) return
    const nome = EMOTE[Math.floor(Math.random() * EMOTE.length)]
    const azione = actions[nome]
    if (!azione) return
    setInEmote(true)
    actions.Idle.fadeOut(0.2)
    azione.clampWhenFinished = true
    azione.reset().setLoop(LoopOnce, 1).fadeIn(0.2).play()
  }

  // il cursore a mano dice che il robot e' cliccabile
  function cursore(tipo) {
    if (emoteAlClick) document.body.style.cursor = tipo
  }

  // dispose={null}: geometrie e materiali sono condivisi con la scena in cache di useGLTF;
  // senza, smontando l'hero R3F li distruggerebbe e il viewer del dettaglio resterebbe vuoto.
  return (
    <group
      ref={gruppo}
      {...props}
      dispose={null}
      onClick={emote}
      onPointerOver={() => cursore('pointer')}
      onPointerOut={() => cursore('auto')}
    >
      <primitive object={copia} dispose={null} />
    </group>
  )
}

useGLTF.preload(MODELLO)
