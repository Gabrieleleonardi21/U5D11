import { useState } from 'react'
import { api } from '@/lib/api'
import { CATEGORIE } from '@/lib/formato'
import { Campo } from '@/components/Campo'
import { Messaggio } from '@/components/Messaggio'

/** Trasforma i valori del form nel body atteso dal backend: numeri veri e null per i campi vuoti. */
function costruisciBody(form) {
  const dati = Object.fromEntries(new FormData(form))
  let prezzoAcquisto = null
  if (dati.prezzoAcquisto !== '') prezzoAcquisto = Number(dati.prezzoAcquisto)
  let fornitore = null
  if (dati.fornitore !== '') fornitore = dati.fornitore
  return {
    nome: dati.nome,
    produttore: dati.produttore,
    categoria: dati.categoria,
    prezzo: Number(dati.prezzo),
    descrizione: dati.descrizione,
    pubblicato: dati.pubblicato === 'on',
    prezzoAcquisto,
    fornitore,
  }
}

/**
 * Crea (robot assente) o modifica (robot presente) un elemento del catalogo.
 * onSalvato riceve il robot restituito dal server, onAnnulla chiude il form.
 */
export function RobotForm({ robot, onSalvato, onAnnulla }) {
  const [errore, setErrore] = useState(null)
  const [dettagli, setDettagli] = useState([])
  const [inCorso, setInCorso] = useState(false)
  const inModifica = Boolean(robot)

  async function invia(evento) {
    evento.preventDefault()
    setErrore(null)
    setDettagli([])
    setInCorso(true)
    const body = costruisciBody(evento.currentTarget)
    try {
      let salvato
      if (inModifica) {
        salvato = await api.robot.modifica(robot.id, body)
      } else {
        salvato = await api.robot.crea(body)
      }
      onSalvato(salvato)
    } catch (e) {
      setErrore(e.message)
      setDettagli(e.dettagli ?? [])
    } finally {
      setInCorso(false)
    }
  }

  return (
    <form onSubmit={invia} className="card space-y-4 p-5">
      <h3 className="text-lg font-semibold">
        {inModifica && `Modifica "${robot.nome}"`}
        {!inModifica && 'Nuovo robot'}
      </h3>
      <Messaggio dettagli={dettagli}>{errore}</Messaggio>

      <div className="grid gap-4 sm:grid-cols-2">
        <Campo etichetta="Nome">
          <input name="nome" required maxLength={100} defaultValue={robot?.nome} className="campo" />
        </Campo>
        <Campo etichetta="Produttore">
          <input name="produttore" required maxLength={100} defaultValue={robot?.produttore} className="campo" />
        </Campo>
        <Campo etichetta="Categoria">
          <select name="categoria" required defaultValue={robot?.categoria ?? 'UMANOIDE'} className="campo">
            {Object.entries(CATEGORIE).map(([chiave, etichetta]) => (
              <option key={chiave} value={chiave}>{etichetta}</option>
            ))}
          </select>
        </Campo>
        <Campo etichetta="Prezzo di vendita (€)">
          <input name="prezzo" type="number" step="0.01" min="0" required defaultValue={robot?.prezzo} className="campo" />
        </Campo>
        <Campo etichetta="Prezzo d'acquisto (€, riservato)">
          <input name="prezzoAcquisto" type="number" step="0.01" min="0" defaultValue={robot?.prezzoAcquisto ?? ''} className="campo" />
        </Campo>
        <Campo etichetta="Fornitore (riservato)">
          <input name="fornitore" maxLength={100} defaultValue={robot?.fornitore ?? ''} className="campo" />
        </Campo>
      </div>
      <Campo etichetta="Descrizione">
        <textarea name="descrizione" rows={3} maxLength={1000} defaultValue={robot?.descrizione ?? ''} className="campo" />
      </Campo>
      <label className="flex items-center gap-2 text-sm">
        <input name="pubblicato" type="checkbox" defaultChecked={robot?.pubblicato ?? false} />
        Pubblicato (se spento resta una bozza visibile solo agli admin)
      </label>

      <div className="flex gap-2">
        <button type="submit" disabled={inCorso} className="btn btn-primario flex-1 sm:flex-none">Salva</button>
        <button type="button" onClick={onAnnulla} className="btn btn-secondario flex-1 sm:flex-none">Annulla</button>
      </div>
    </form>
  )
}
