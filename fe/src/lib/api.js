// Tutte le chiamate al backend passano da qui: base URL, JSON, token e errori in un unico punto.
// In sviluppo VITE_API_URL e' vuota e il proxy di Vite inoltra /api al backend.
const BASE = (import.meta.env.VITE_API_URL ?? '').replace(/\/$/, '')
const CHIAVE_SESSIONE = 'robot.sessione'

/** Errore HTTP con lo status e il messaggio/dettagli dell'ErroreResponse del backend. */
export class ErroreApi extends Error {
  constructor(status, messaggio, dettagli) {
    super(messaggio)
    this.status = status
    this.dettagli = dettagli
  }
}

// ---- sessione: { id, username, ruoli, token } in memoria e in localStorage ----

let sessione = leggiSessione()

export function leggiSessione() {
  // localStorage puo' non essere disponibile (navigazione privata, dati bloccati)
  try {
    const salvata = localStorage.getItem(CHIAVE_SESSIONE)
    if (!salvata) return null
    return JSON.parse(salvata)
  } catch {
    return null
  }
}

export function salvaSessione(nuova) {
  sessione = nuova
  try {
    localStorage.setItem(CHIAVE_SESSIONE, JSON.stringify(nuova))
  } catch {
    // senza storage la sessione dura finche' la pagina resta aperta
  }
}

export function cancellaSessione() {
  sessione = null
  try {
    localStorage.removeItem(CHIAVE_SESSIONE)
  } catch {
    // niente da fare
  }
}

// ---- chiamata generica ----

/** Header JSON piu' il Bearer token se c'e' una sessione; gli header passati in opzioni vengono fusi, non persi. */
function intestazioni(opzioni) {
  const headers = { 'Content-Type': 'application/json', ...opzioni.headers }
  if (sessione) {
    headers.Authorization = `Bearer ${sessione.token}`
  }
  return headers
}

async function chiama(percorso, opzioni = {}) {
  const risposta = await fetch(`${BASE}${percorso}`, { ...opzioni, headers: intestazioni(opzioni) })
  if (risposta.status === 204) return undefined

  let corpo = null
  try {
    corpo = await risposta.json()
  } catch {
    corpo = null
  }

  if (!risposta.ok) {
    // 401 con una sessione attiva = token scaduto o revocato: si esce e si avvisa l'AuthProvider
    if (risposta.status === 401 && sessione) {
      cancellaSessione()
      window.dispatchEvent(new Event('sessione-scaduta'))
    }
    let messaggio = `Errore ${risposta.status}`
    let dettagli = []
    if (corpo && corpo.messaggio) messaggio = corpo.messaggio
    if (corpo && corpo.dettagli) dettagli = corpo.dettagli
    throw new ErroreApi(risposta.status, messaggio, dettagli)
  }
  return corpo
}

/** Scorciatoia per le chiamate con body JSON. */
function conBody(metodo, percorso, body) {
  return chiama(percorso, { method: metodo, body: JSON.stringify(body) })
}

export const api = {
  auth: {
    register: (dati) => conBody('POST', '/api/auth/register', dati),
    login: (dati) => conBody('POST', '/api/auth/login', dati),
    logout: () => chiama('/api/auth/logout', { method: 'POST' }),
  },
  robot: {
    lista: () => chiama('/api/robot'),
    dettaglio: (id) => chiama(`/api/robot/${id}`),
    crea: (dati) => conBody('POST', '/api/robot', dati),
    modifica: (id, dati) => conBody('PATCH', `/api/robot/${id}`, dati),
    elimina: (id) => chiama(`/api/robot/${id}`, { method: 'DELETE' }),
  },
  preferiti: {
    lista: () => chiama('/api/preferiti'),
    aggiungi: (idRobot) => chiama(`/api/preferiti/${idRobot}`, { method: 'POST' }),
    togli: (idRobot) => chiama(`/api/preferiti/${idRobot}`, { method: 'DELETE' }),
  },
  utenti: {
    lista: () => chiama('/api/utenti'),
    assegnaAdmin: (id) => chiama(`/api/utenti/${id}/admin`, { method: 'POST' }),
    revocaAdmin: (id) => chiama(`/api/utenti/${id}/admin`, { method: 'DELETE' }),
  },
}
