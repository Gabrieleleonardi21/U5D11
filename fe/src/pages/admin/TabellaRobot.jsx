import { useState } from 'react'
import { api } from '@/lib/api'
import { etichettaCategoria, formattaPrezzo } from '@/lib/formato'
import { useRichiesta } from '@/lib/useRichiesta'
import { sostituisci } from '@/lib/liste'
import { useToast } from '@/toast/useToast'
import { Messaggio } from '@/components/Messaggio'
import { SkeletonRighe } from '@/components/Skeleton'
import { RobotForm } from './RobotForm'

/**
 * Catalogo completo per l'admin: stessa GET /api/robot della vetrina, ma il server
 * risponde con bozze, prezzo d'acquisto e fornitore.
 */
export function TabellaRobot() {
  const notifica = useToast()
  const { dati: robot, setDati, errore, caricamento } = useRichiesta(() => api.robot.lista())
  const [form, setForm] = useState(null)              // null = chiuso, 'nuovo' o il robot da modificare
  const [daConfermare, setDaConfermare] = useState(null)   // id in attesa di conferma per l'eliminazione

  /** Esegue un'azione; se va bene aggiorna la lista con `aggiorna(risultato)` e mostra il toast. */
  async function esegui(azione, aggiorna, messaggio) {
    try {
      const risultato = await azione()
      setDati((lista) => aggiorna(lista, risultato))
      notifica.successo(messaggio)
    } catch (e) {
      notifica.errore(e.message)
    }
  }

  function togglePubblicato(r) {
    let messaggio = `"${r.nome}" pubblicato`
    if (r.pubblicato) messaggio = `"${r.nome}" ritirato: ora e' una bozza`
    esegui(
      () => api.robot.modifica(r.id, { pubblicato: !r.pubblicato }),
      (lista, salvato) => sostituisci(lista, r.id, () => salvato),
      messaggio,
    )
  }

  function elimina(r) {
    setDaConfermare(null)
    esegui(
      () => api.robot.elimina(r.id),
      (lista) => lista.filter((x) => x.id !== r.id),
      `"${r.nome}" eliminato`,
    )
  }

  function salvato(salvatoDalServer) {
    const esiste = robot.some((x) => x.id === salvatoDalServer.id)
    if (esiste) {
      setDati((lista) => sostituisci(lista, salvatoDalServer.id, () => salvatoDalServer))
    } else {
      setDati((lista) => [salvatoDalServer, ...lista])
    }
    setForm(null)
    notifica.successo(`"${salvatoDalServer.nome}" salvato`)
  }

  return (
    <section className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-bold">Catalogo (bozze comprese)</h2>
        {!form && <button type="button" onClick={() => setForm('nuovo')} className="btn btn-primario">+ Nuovo robot</button>}
      </div>
      <Messaggio>{errore}</Messaggio>

      {form === 'nuovo' && <RobotForm onSalvato={salvato} onAnnulla={() => setForm(null)} />}
      {form && form !== 'nuovo' && <RobotForm robot={form} onSalvato={salvato} onAnnulla={() => setForm(null)} />}

      {caricamento && <SkeletonRighe quante={6} />}
      {robot && (
        <div className="card overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="text-left text-xs uppercase text-slate-400">
              <tr>
                <th className="p-3">Nome</th>
                <th className="p-3">Categoria</th>
                <th className="p-3">Prezzo</th>
                <th className="p-3">Acquisto</th>
                <th className="p-3">Fornitore</th>
                <th className="p-3">Stato</th>
                <th className="p-3"></th>
              </tr>
            </thead>
            <tbody>
              {robot.map((r) => (
                <tr key={r.id} className="border-t border-white/5 transition hover:bg-white/[0.03]">
                  <td className="p-3 font-medium">{r.nome}<div className="text-xs text-slate-500">{r.produttore}</div></td>
                  <td className="p-3">{etichettaCategoria(r.categoria)}</td>
                  <td className="p-3">{formattaPrezzo(r.prezzo)}</td>
                  <td className="p-3 text-amber-200">{formattaPrezzo(r.prezzoAcquisto)}</td>
                  <td className="p-3 text-amber-200">{r.fornitore ?? '—'}</td>
                  <td className="p-3">
                    {r.pubblicato && <span className="rounded-full bg-emerald-500/20 px-2 py-0.5 text-xs text-emerald-300">Pubblicato</span>}
                    {!r.pubblicato && <span className="rounded-full bg-amber-500/20 px-2 py-0.5 text-xs text-amber-300">Bozza</span>}
                  </td>
                  <td className="p-3">
                    <div className="flex flex-wrap justify-end gap-1">
                      <button type="button" onClick={() => togglePubblicato(r)} className="btn btn-secondario">
                        {r.pubblicato && 'Ritira'}
                        {!r.pubblicato && 'Pubblica'}
                      </button>
                      <button type="button" onClick={() => setForm(r)} className="btn btn-secondario">Modifica</button>
                      {daConfermare !== r.id && (
                        <button type="button" onClick={() => setDaConfermare(r.id)} className="btn btn-pericolo">Elimina</button>
                      )}
                      {daConfermare === r.id && (
                        <button type="button" onClick={() => elimina(r)} className="btn btn-pericolo">Conferma?</button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  )
}
