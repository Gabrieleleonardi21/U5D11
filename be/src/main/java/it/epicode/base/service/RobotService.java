package it.epicode.base.service;

import it.epicode.base.dto.RobotAdmin;
import it.epicode.base.dto.RobotPatchRequest;
import it.epicode.base.dto.RobotPubblico;
import it.epicode.base.dto.RobotRequest;
import it.epicode.base.dto.RobotResponse;
import it.epicode.base.exception.ConflittoException;
import it.epicode.base.exception.RisorsaNonTrovataException;
import it.epicode.base.model.Robot;
import it.epicode.base.repository.PreferitoRepository;
import it.epicode.base.repository.RobotRepository;
import it.epicode.base.security.UtenteCorrente;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * La vetrina. Gli URL GET sono gli stessi per tutti: e' qui che il server decide
 * quali robot mostrare (bozze o no) e con quali campi (DTO pubblico o admin).
 */
@Service
public class RobotService {

	private final RobotRepository robotRepository;
	private final PreferitoRepository preferitoRepository;

	public RobotService(RobotRepository robotRepository, PreferitoRepository preferitoRepository) {
		this.robotRepository = robotRepository;
		this.preferitoRepository = preferitoRepository;
	}

	public List<RobotResponse> lista() {
		boolean admin = UtenteCorrente.isAdmin();
		Set<UUID> preferiti = idPreferitiUtenteCorrente();

		List<Robot> robot;
		if (admin) {
			robot = robotRepository.findAllByOrderByCreatedAtDesc();
		} else {
			robot = robotRepository.findByPubblicatoTrueOrderByCreatedAtDesc();
		}
		return robot.stream().map(r -> aDto(r, admin, preferiti.contains(r.getId()))).toList();
	}

	public RobotResponse dettaglio(UUID id) {
		boolean admin = UtenteCorrente.isAdmin();

		// Per chi non e' admin una bozza semplicemente non esiste: la query non la trova.
		Optional<Robot> trovato;
		if (admin) {
			trovato = robotRepository.findById(id);
		} else {
			trovato = robotRepository.findByIdAndPubblicatoTrue(id);
		}
		Robot robot = trovato.orElseThrow(() -> new RisorsaNonTrovataException("Robot non trovato"));
		return aDto(robot, admin, idPreferitiUtenteCorrente().contains(id));
	}

	public RobotAdmin crea(RobotRequest richiesta) {
		if (robotRepository.existsByNome(richiesta.nome())) {
			throw new ConflittoException("Esiste gia' un robot con questo nome");
		}
		Robot robot = new Robot();
		robot.setNome(richiesta.nome());
		robot.setDescrizione(richiesta.descrizione());
		robot.setCategoria(richiesta.categoria());
		robot.setProduttore(richiesta.produttore());
		robot.setPrezzo(richiesta.prezzo());
		robot.setPubblicato(Boolean.TRUE.equals(richiesta.pubblicato()));   // assente o null = bozza
		robot.setPrezzoAcquisto(richiesta.prezzoAcquisto());
		robot.setFornitore(richiesta.fornitore());
		return RobotAdmin.from(robotRepository.save(robot), false);
	}

	/** Modifica parziale: si toccano solo i campi presenti nel body. */
	public RobotAdmin modifica(UUID id, RobotPatchRequest patch) {
		Robot robot = trovaRobot(id);
		if (patch.nome() != null && !patch.nome().equals(robot.getNome()) && robotRepository.existsByNome(patch.nome())) {
			throw new ConflittoException("Esiste gia' un robot con questo nome");
		}
		seNonNullo(patch.nome(), robot::setNome);
		seNonNullo(patch.descrizione(), robot::setDescrizione);
		seNonNullo(patch.categoria(), robot::setCategoria);
		seNonNullo(patch.produttore(), robot::setProduttore);
		seNonNullo(patch.prezzo(), robot::setPrezzo);
		seNonNullo(patch.pubblicato(), robot::setPubblicato);
		seNonNullo(patch.prezzoAcquisto(), robot::setPrezzoAcquisto);
		seNonNullo(patch.fornitore(), robot::setFornitore);
		return RobotAdmin.from(robotRepository.save(robot), false);
	}

	@Transactional
	public void elimina(UUID id) {
		Robot robot = trovaRobot(id);
		preferitoRepository.deleteByRobotId(id);   // prima i preferiti che lo puntano, poi il robot
		robotRepository.delete(robot);
	}

	// ---- helper ----

	/** Sceglie la forma della risposta: campi completi per l'admin, solo pubblici per gli altri. */
	private static RobotResponse aDto(Robot robot, boolean admin, boolean preferito) {
		if (admin) {
			return RobotAdmin.from(robot, preferito);
		}
		return RobotPubblico.from(robot, preferito);
	}

	/** Id dei robot preferiti dell'utente collegato; insieme vuoto per l'anonimo. */
	private Set<UUID> idPreferitiUtenteCorrente() {
		return UtenteCorrente.opzionale()
				.map(u -> preferitoRepository.findIdRobotByUtenteId(u.id()))
				.orElse(Set.of());
	}

	private Robot trovaRobot(UUID id) {
		return robotRepository.findById(id)
				.orElseThrow(() -> new RisorsaNonTrovataException("Robot non trovato"));
	}

	private static <T> void seNonNullo(T valore, Consumer<T> setter) {
		if (valore != null) {
			setter.accept(valore);
		}
	}
}
