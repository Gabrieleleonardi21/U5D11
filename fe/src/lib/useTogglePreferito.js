import { api } from '@/lib/api'
import { useToast } from '@/toast/useToast'

/**
 * Aggiunge o toglie un preferito, con toast di esito. La pagina passa solo cosa
 * fare con il proprio stato in caso di successo (invertire il flag, togliere dalla lista...).
 * Usato da Home, Preferiti, RobotDettaglio e dai robot simili: niente try/catch ripetuti.
 */
export function useTogglePreferito(dopoSuccesso) {
  const notifica = useToast()

  return async function toggle(robot) {
    try {
      if (robot.preferito) {
        await api.preferiti.togli(robot.id)
        notifica.successo(`"${robot.nome}" tolto dai preferiti`)
      } else {
        await api.preferiti.aggiungi(robot.id)
        notifica.successo(`"${robot.nome}" aggiunto ai preferiti`)
      }
      dopoSuccesso(robot)
    } catch (e) {
      notifica.errore(e.message)
    }
  }
}
