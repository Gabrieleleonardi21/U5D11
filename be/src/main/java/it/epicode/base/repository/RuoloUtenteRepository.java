package it.epicode.base.repository;

import it.epicode.base.model.RuoloUtente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RuoloUtenteRepository extends JpaRepository<RuoloUtente, UUID> {

	/** Nomi dei ruoli di un utente: letti a ogni richiesta autenticata, cosi' una revoca vale subito. */
	@Query("select ru.ruolo.nome from RuoloUtente ru where ru.utente.id = :idUtente")
	List<String> findNomiRuoli(@Param("idUtente") UUID idUtente);

	/** Tutte le associazioni con utente e ruolo gia' caricati: una sola query per la lista utenti dell'admin. */
	@Query("select ru from RuoloUtente ru join fetch ru.utente join fetch ru.ruolo")
	List<RuoloUtente> findAllConUtenteERuolo();

	boolean existsByUtenteIdAndRuoloNome(UUID idUtente, String nomeRuolo);

	/** Ritorna quante righe ha cancellato: 0 significa che il ruolo non c'era. */
	long deleteByUtenteIdAndRuoloNome(UUID idUtente, String nomeRuolo);
}
