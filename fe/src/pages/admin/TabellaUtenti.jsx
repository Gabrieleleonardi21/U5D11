import { api } from '@/lib/api'
import { useRichiesta } from '@/lib/useRichiesta'
import { sostituisci } from '@/lib/liste'
import { useToast } from '@/toast/useToast'
import { useAuth } from '@/auth/useAuth'
import { Messaggio } from '@/components/Messaggio'
import { SkeletonRighe } from '@/components/Skeleton'

/** Assegna o revoca il ruolo admin. Il pulsante su se stessi e' disabilitato: il server risponderebbe 400. */
export function TabellaUtenti() {
  const { sessione } = useAuth()
  const notifica = useToast()
  const { dati: utenti, setDati, errore, caricamento } = useRichiesta(() => api.utenti.lista())

  async function cambiaRuolo(u) {
    const eAdmin = u.ruoli.includes('ADMIN')
    try {
      let ruoli
      if (eAdmin) {
        await api.utenti.revocaAdmin(u.id)
        ruoli = u.ruoli.filter((r) => r !== 'ADMIN')
        notifica.successo(`${u.username} non e' piu' admin`)
      } else {
        await api.utenti.assegnaAdmin(u.id)
        ruoli = [...u.ruoli, 'ADMIN']
        notifica.successo(`${u.username} ora e' admin`)
      }
      setDati((lista) => sostituisci(lista, u.id, (x) => ({ ...x, ruoli })))
    } catch (e) {
      notifica.errore(e.message)
    }
  }

  return (
    <section className="space-y-4">
      <h2 className="text-xl font-bold">Utenti e ruoli</h2>
      <Messaggio>{errore}</Messaggio>
      {caricamento && <SkeletonRighe quante={3} />}
      {utenti && (
        <div className="card">
          <table className="tabella-adattiva w-full text-sm">
            <thead className="text-left text-xs uppercase text-slate-400">
              <tr>
                <th className="p-3">Username</th>
                <th className="p-3">Email</th>
                <th className="p-3">Ruoli</th>
                <th className="p-3"></th>
              </tr>
            </thead>
            <tbody>
              {utenti.map((u) => {
                const eAdmin = u.ruoli.includes('ADMIN')
                const seStesso = u.id === sessione.id
                return (
                  <tr key={u.id} className="border-t border-white/5 transition hover:bg-white/[0.03]">
                    <td className="font-medium">{u.username}</td>
                    <td data-etichetta="Email" className="break-all text-slate-400">{u.email}</td>
                    <td data-etichetta="Ruoli">{u.ruoli.join(', ')}</td>
                    <td className="md:text-right">
                      <button type="button" onClick={() => cambiaRuolo(u)} disabled={seStesso} className="btn btn-secondario mt-1 w-full md:mt-0 md:w-auto">
                        {eAdmin && 'Revoca admin'}
                        {!eAdmin && 'Rendi admin'}
                      </button>
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
      )}
    </section>
  )
}
