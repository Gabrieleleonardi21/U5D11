package it.epicode.base.repository;

import it.epicode.base.model.Preferito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Ogni metodo filtra per id_utente: e' la query a decidere di chi e' il preferito.
 * Il ruolo dice se puoi chiamare l'endpoint, non su quale riga.
 */
public interface PreferitoRepository extends JpaRepository<Preferito, UUID> {

	/** Id dei robot preferiti di un utente: serve per il flag "preferito" nella vetrina. */
	@Query("select p.robot.id from Preferito p where p.utente.id = :idUtente")
	Set<UUID> findIdRobotByUtenteId(@Param("idUtente") UUID idUtente);

	/** Preferiti con il robot gia' caricato; una bozza "spubblicata" dopo non trapela. */
	@Query("""
			select p from Preferito p join fetch p.robot r
			where p.utente.id = :idUtente and r.pubblicato = true
			order by p.createdAt desc
			""")
	List<Preferito> findByUtenteIdConRobotPubblicato(@Param("idUtente") UUID idUtente);

	boolean existsByUtenteIdAndRobotId(UUID idUtente, UUID idRobot);

	/** Cancella solo se la riga appartiene all'utente: 0 righe = non era suo o non esisteva. */
	long deleteByUtenteIdAndRobotId(UUID idUtente, UUID idRobot);

	/** Usata dall'admin quando elimina un robot: via i preferiti di tutti, altrimenti la FK blocca la cancellazione. */
	void deleteByRobotId(UUID idRobot);
}
