package it.epicode.base.service;

import it.epicode.base.dto.RobotPubblico;
import it.epicode.base.exception.ConflittoException;
import it.epicode.base.exception.RisorsaNonTrovataException;
import it.epicode.base.model.Preferito;
import it.epicode.base.model.Robot;
import it.epicode.base.model.Utente;
import it.epicode.base.repository.PreferitoRepository;
import it.epicode.base.repository.RobotRepository;
import it.epicode.base.repository.UtenteRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * LIVELLO 3: di chi e' il preferito lo decide la query, non il ruolo.
 * L'id utente arriva sempre dal token (principal), mai dal body o dall'URL.
 */
@Service
public class PreferitoService {

	private final PreferitoRepository preferitoRepository;
	private final RobotRepository robotRepository;
	private final UtenteRepository utenteRepository;

	public PreferitoService(PreferitoRepository preferitoRepository, RobotRepository robotRepository,
	                        UtenteRepository utenteRepository) {
		this.preferitoRepository = preferitoRepository;
		this.robotRepository = robotRepository;
		this.utenteRepository = utenteRepository;
	}

	public List<RobotPubblico> lista(UUID idUtente) {
		return preferitoRepository.findByUtenteIdConRobotPubblicato(idUtente).stream()
				.map(p -> RobotPubblico.from(p.getRobot(), true))
				.toList();
	}

	@Transactional
	public RobotPubblico aggiungi(UUID idUtente, UUID idRobot) {
		// Una bozza non e' visibile: per chi chiede "non esiste", quindi 404 e non 403.
		Robot robot = robotRepository.findByIdAndPubblicatoTrue(idRobot)
				.orElseThrow(() -> new RisorsaNonTrovataException("Robot non trovato"));
		if (preferitoRepository.existsByUtenteIdAndRobotId(idUtente, idRobot)) {
			throw new ConflittoException("Robot gia' nei preferiti");
		}
		Utente utente = utenteRepository.getReferenceById(idUtente);   // solo la FK, senza caricare l'utente
		try {
			preferitoRepository.saveAndFlush(new Preferito(utente, robot));
		} catch (DataIntegrityViolationException e) {
			throw new ConflittoException("Robot gia' nei preferiti");
		}
		return RobotPubblico.from(robot, true);
	}

	/** Cancella filtrando per proprietario: il preferito di un altro utente non viene mai toccato. */
	@Transactional
	public void rimuovi(UUID idUtente, UUID idRobot) {
		long cancellati = preferitoRepository.deleteByUtenteIdAndRobotId(idUtente, idRobot);
		if (cancellati == 0) {
			throw new RisorsaNonTrovataException("Preferito non trovato");
		}
	}
}
