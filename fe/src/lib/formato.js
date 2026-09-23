// Formattazioni condivise: prezzi, etichette delle categorie, immagini.

const euro = new Intl.NumberFormat('it-IT', { style: 'currency', currency: 'EUR' })

export function formattaPrezzo(valore) {
  if (valore === null || valore === undefined) return '—'
  return euro.format(valore)
}

/** Stesse chiavi dell'enum CategoriaRobot del backend. */
export const CATEGORIE = {
  UMANOIDE: 'Umanoide',
  QUADRUPEDE: 'Quadrupede',
  BRACCIO_INDUSTRIALE: 'Braccio industriale',
  DRONE: 'Drone',
  DOMESTICO: 'Domestico',
  EDUCATIVO: 'Educativo',
}

export function etichettaCategoria(categoria) {
  return CATEGORIE[categoria] ?? categoria
}

/** Colore del corpo del robot 3D per categoria (il giallo originale resta ai quadrupedi). */
export const COLORE_CATEGORIA = {
  UMANOIDE: '#22d3ee',
  QUADRUPEDE: '#f5b820',
  BRACCIO_INDUSTRIALE: '#f97316',
  DRONE: '#60a5fa',
  DOMESTICO: '#34d399',
  EDUCATIVO: '#a78bfa',
}

const dataItaliana = new Intl.DateTimeFormat('it-IT', { day: 'numeric', month: 'long', year: 'numeric' })

export function formattaData(iso) {
  if (!iso) return '—'
  return dataItaliana.format(new Date(iso))
}

/** RoboHash genera un robot unico a partire dal nome: nessuna immagine da ospitare. */
export function immagineRobot(nome, dimensione = 300) {
  return `https://robohash.org/${encodeURIComponent(nome)}.png?set=set1&bgset=bg1&size=${dimensione}x${dimensione}`
}
