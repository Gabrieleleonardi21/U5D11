import { useEffect, useState } from 'react'

/**
 * Esegue una chiamata all'API al montaggio (e quando cambiano le dipendenze),
 * gestendo caricamento ed errore. setDati permette di aggiornare la lista in locale
 * dopo un'azione (es. toggle preferito) senza ricaricare tutto.
 */
export function useRichiesta(funzione, dipendenze = []) {
  const [dati, setDati] = useState(null)
  const [errore, setErrore] = useState(null)
  const [caricamento, setCaricamento] = useState(true)

  useEffect(() => {
    let attivo = true   // evita di aggiornare lo stato se il componente e' stato smontato
    setCaricamento(true)
    setErrore(null)
    funzione()
      .then((risultato) => { if (attivo) setDati(risultato) })
      .catch((e) => { if (attivo) setErrore(e.message) })
      .finally(() => { if (attivo) setCaricamento(false) })
    return () => { attivo = false }
  }, dipendenze) // eslint-disable-line react-hooks/exhaustive-deps

  return { dati, setDati, errore, caricamento }
}
