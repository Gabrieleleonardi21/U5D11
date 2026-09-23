package it.epicode.base.repository;

import it.epicode.base.model.Robot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RobotRepository extends JpaRepository<Robot, UUID> {

	/** Per l'admin: tutto, bozze comprese. */
	List<Robot> findAllByOrderByCreatedAtDesc();

	/** Per anonimi e utenti: solo i pubblicati. */
	List<Robot> findByPubblicatoTrueOrderByCreatedAtDesc();

	/** Dettaglio per non-admin: una bozza non viene trovata, quindi 404. */
	Optional<Robot> findByIdAndPubblicatoTrue(UUID id);

	boolean existsByNome(String nome);
}
