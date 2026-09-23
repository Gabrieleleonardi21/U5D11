package it.epicode.base.config;

import it.epicode.base.model.CategoriaRobot;
import it.epicode.base.model.Robot;
import it.epicode.base.model.Ruolo;
import it.epicode.base.model.RuoloUtente;
import it.epicode.base.model.Utente;
import it.epicode.base.repository.RobotRepository;
import it.epicode.base.repository.RuoloRepository;
import it.epicode.base.repository.RuoloUtenteRepository;
import it.epicode.base.repository.UtenteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static it.epicode.base.model.CategoriaRobot.BRACCIO_INDUSTRIALE;
import static it.epicode.base.model.CategoriaRobot.DOMESTICO;
import static it.epicode.base.model.CategoriaRobot.DRONE;
import static it.epicode.base.model.CategoriaRobot.EDUCATIVO;
import static it.epicode.base.model.CategoriaRobot.QUADRUPEDE;
import static it.epicode.base.model.CategoriaRobot.UMANOIDE;

/**
 * Seeder eseguito a ogni avvio, ma idempotente: crea i ruoli, l'admin e il catalogo
 * solo se mancano. Le credenziali admin arrivano da application.yml (ADMIN_USERNAME / ADMIN_PASSWORD).
 */
@Component
public class DatiIniziali implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(DatiIniziali.class);
	private static final BigDecimal MARGINE = new BigDecimal("0.70");   // prezzo d'acquisto = 70% del prezzo

	private final RuoloRepository ruoloRepository;
	private final UtenteRepository utenteRepository;
	private final RuoloUtenteRepository ruoloUtenteRepository;
	private final RobotRepository robotRepository;
	private final PasswordEncoder passwordEncoder;
	private final String adminUsername;
	private final String adminPassword;
	private final String adminEmail;

	public DatiIniziali(RuoloRepository ruoloRepository, UtenteRepository utenteRepository,
	                    RuoloUtenteRepository ruoloUtenteRepository, RobotRepository robotRepository,
	                    PasswordEncoder passwordEncoder,
	                    @Value("${app.admin.username}") String adminUsername,
	                    @Value("${app.admin.password}") String adminPassword,
	                    @Value("${app.admin.email}") String adminEmail) {
		this.ruoloRepository = ruoloRepository;
		this.utenteRepository = utenteRepository;
		this.ruoloUtenteRepository = ruoloUtenteRepository;
		this.robotRepository = robotRepository;
		this.passwordEncoder = passwordEncoder;
		this.adminUsername = adminUsername;
		this.adminPassword = adminPassword;
		this.adminEmail = adminEmail;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		Ruolo utente = ruolo(Ruolo.UTENTE);
		Ruolo admin = ruolo(Ruolo.ADMIN);
		creaAdmin(utente, admin);
		if (robotRepository.count() == 0) {
			creaCatalogo();
		}
	}

	private Ruolo ruolo(String nome) {
		return ruoloRepository.findByNome(nome).orElseGet(() -> ruoloRepository.save(new Ruolo(nome)));
	}

	/** L'admin ha entrambi i ruoli: essere autenticato e avere UTENTE e' la stessa cosa. */
	private void creaAdmin(Ruolo ruoloUtente, Ruolo ruoloAdmin) {
		if (utenteRepository.existsByUsername(adminUsername)) {
			return;
		}
		Utente admin = new Utente();
		admin.setUsername(adminUsername);
		admin.setEmail(adminEmail);
		admin.setPassword(passwordEncoder.encode(adminPassword));
		utenteRepository.save(admin);
		ruoloUtenteRepository.save(new RuoloUtente(admin, ruoloUtente));
		ruoloUtenteRepository.save(new RuoloUtente(admin, ruoloAdmin));
		log.info("[seeder] creato admin '{}'", adminUsername);
	}

	private void creaCatalogo() {
		robotRepository.saveAll(List.of(
				// ---- pubblicati ----
				robot("Spot", QUADRUPEDE, "Boston Dynamics", "74500", true, "Boston Dynamics Inc.",
						"Robot quadrupede agile per ispezioni industriali, con telecamere a 360 gradi e braccio opzionale."),
				robot("Unitree Go2", QUADRUPEDE, "Unitree Robotics", "1600", true, "Unitree Europe GmbH",
						"Quadrupede compatto con LiDAR 4D, autonomia di 2 ore e controllo da app."),
				robot("ABB IRB 1200", BRACCIO_INDUSTRIALE, "ABB", "28000", true, "ABB Robotics Italia",
						"Braccio industriale a 6 assi, portata 7 kg, pensato per assemblaggio e movimentazione."),
				robot("Universal Robots UR5e", BRACCIO_INDUSTRIALE, "Universal Robots", "32000", true, "UR Distribution Sud",
						"Cobot collaborativo con portata 5 kg e sensore di forza integrato, programmabile senza codice."),
				robot("DJI Mavic 3 Enterprise", DRONE, "DJI", "2100", true, "DJI Enterprise Italia",
						"Drone professionale con zoom 56x e modulo RTK per mappature centimetriche."),
				robot("iRobot Roomba j7+", DOMESTICO, "iRobot", "599", true, "iRobot Europe",
						"Aspirapolvere robot con riconoscimento ostacoli e svuotamento automatico."),
				robot("Ecovacs Deebot X2 Omni", DOMESTICO, "Ecovacs", "899", true, "Ecovacs Italia",
						"Aspira e lava con stazione tutto-in-uno e navigazione LiDAR."),
				robot("Makeblock mBot Neo", EDUCATIVO, "Makeblock", "129", true, "Campustore",
						"Robot didattico programmabile a blocchi e in Python, con sensori e Wi-Fi."),
				// ---- bozze: li vede solo l'admin ----
				robot("Tesla Optimus", UMANOIDE, "Tesla", "20000", false, "Tesla Inc. (in trattativa)",
						"Umanoide general-purpose, prezzo annunciato ma disponibilita' da confermare."),
				robot("Figure 02", UMANOIDE, "Figure AI", "150000", false, "Figure AI (preordine)",
						"Umanoide per logistica e manifattura, batteria da 5 ore e visione integrata."),
				robot("LEGO Mindstorms Robot Inventor", EDUCATIVO, "LEGO", "399", false, "LEGO Education",
						"Kit fuori produzione: in valutazione se tenerlo a catalogo con scorte residue.")
		));
		log.info("[seeder] creato catalogo di {} robot", robotRepository.count());
	}

	/** Helper per non ripetere i setter: il prezzo d'acquisto viene calcolato dal prezzo. */
	private static Robot robot(String nome, CategoriaRobot categoria, String produttore, String prezzo,
	                           boolean pubblicato, String fornitore, String descrizione) {
		Robot r = new Robot();
		r.setNome(nome);
		r.setCategoria(categoria);
		r.setProduttore(produttore);
		r.setPrezzo(new BigDecimal(prezzo));
		r.setPubblicato(pubblicato);
		r.setFornitore(fornitore);
		r.setDescrizione(descrizione);
		r.setPrezzoAcquisto(new BigDecimal(prezzo).multiply(MARGINE).setScale(2, RoundingMode.HALF_UP));
		return r;
	}
}
