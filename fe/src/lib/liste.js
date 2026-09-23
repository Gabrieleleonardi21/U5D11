/**
 * Ritorna una nuova lista in cui l'elemento con quell'id e' sostituito dal risultato di `aggiorna(elemento)`.
 * Serve per aggiornare lo stato React dopo un'azione senza ricaricare tutto dal server.
 */
export function sostituisci(lista, id, aggiorna) {
  return lista.map((elemento) => {
    if (elemento.id !== id) return elemento
    return aggiorna(elemento)
  })
}
