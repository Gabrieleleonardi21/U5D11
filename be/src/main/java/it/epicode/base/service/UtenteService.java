package it.epicode.base.service;

import it.epicode.base.dto.UtenteResponse;
import it.epicode.base.exception.RisorsaNonTrovataException;
import it.epicode.base.model.Ruolo;
import it.epicode.base.model.RuoloUtente;
import it.epicode.base.model.Utente;
import it.epicode.base.repository.RuoloRepository;
import it.epicode.base.repository.RuoloUtenteRepository;
import it.epicode.base.repository.UtenteRepository;
import it.epicode.base.security.UtenteAutenticato;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/** Gestione del ruolo admin. Chi puo' chiamare questi metodi lo decide @PreAuthorize nel controller. */
@Service
public class UtenteService {

	private final UtenteRepository utenteRepository;
	private final RuoloRepository ruoloRepository;
	private final RuoloUtenteRepository ruoloUtenteRepository;

	public UtenteService(UtenteRepository utenteRepository, RuoloRepository ruoloRepository,
	                     RuoloUtenteRepository ruoloUtenteRepository) {
		this.utenteRepository = utenteRepository;
		this.ruoloRepository = ruoloRepository;
		this.ruoloUtenteRepository = ruoloUtenteRepository;
	}

	/** Tutti gli utenti con i loro ruoli, in due query invece di una per utente. */
	public List<UtenteResponse> lista() {
		Map<UUID, List<String>> ruoliPerUtente = ruoloUtenteRepository.findAllConUtenteERuolo().stream()
				.collect(Collectors.groupingBy(ru -> ru.getUtente().getId(),
						Collectors.mapping(ru -> ru.getRuolo().getNome(), Collectors.toList())));

		return utenteRepository.findAll().stream()
				.map(u -> new UtenteResponse(u.getId(), u.getUsername(), u.getEmail(),
						ruoliPerUtente.getOrDefault(u.getId(), List.of())))
				.toList();
	}

	/** Idempotente: se e' gia' admin non succede nulla. */
	@Transactional
	public void assegnaAdmin(UUID idUtente) {
		Utente utente = trovaUtente(idUtente);
		if (ruoloUtenteRepository.existsByUtenteIdAndRuoloNome(idUtente, Ruolo.ADMIN)) {
			return;
		}
		Ruolo admin = ruoloRepository.findByNome(Ruolo.ADMIN)
				.orElseThrow(() -> new IllegalStateException("Ruolo ADMIN mancante: seeder non eseguito"));
		ruoloUtenteRepository.save(new RuoloUtente(utente, admin));
	}

	/** Un admin non puo' togliersi il ruolo da solo: altrimenti il sito potrebbe restare senza admin. */
	@Transactional
	public void revocaAdmin(UUID idUtente, UtenteAutenticato corrente) {
		if (idUtente.equals(corrente.id())) {
			throw new IllegalArgumentException("Non puoi revocare il tuo stesso ruolo admin");
		}
		trovaUtente(idUtente);
		ruoloUtenteRepository.deleteByUtenteIdAndRuoloNome(idUtente, Ruolo.ADMIN);
	}

	private Utente trovaUtente(UUID id) {
		return utenteRepository.findById(id)
				.orElseThrow(() -> new RisorsaNonTrovataException("Utente non trovato"));
	}
}
