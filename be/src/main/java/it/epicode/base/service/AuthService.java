package it.epicode.base.service;

import it.epicode.base.dto.AuthResponse;
import it.epicode.base.dto.LoginRequest;
import it.epicode.base.dto.RegistrazioneRequest;
import it.epicode.base.exception.ConflittoException;
import it.epicode.base.exception.CredenzialiNonValideException;
import it.epicode.base.model.Ruolo;
import it.epicode.base.model.RuoloUtente;
import it.epicode.base.model.Utente;
import it.epicode.base.repository.RuoloRepository;
import it.epicode.base.repository.RuoloUtenteRepository;
import it.epicode.base.repository.UtenteRepository;
import it.epicode.base.security.JwtService;
import it.epicode.base.security.TokenBlacklist;
import it.epicode.base.security.UtenteAutenticato;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Registrazione, login e logout. Registrarsi assegna in automatico il ruolo UTENTE. */
@Service
public class AuthService {

	private final UtenteRepository utenteRepository;
	private final RuoloRepository ruoloRepository;
	private final RuoloUtenteRepository ruoloUtenteRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final TokenBlacklist blacklist;

	public AuthService(UtenteRepository utenteRepository, RuoloRepository ruoloRepository,
	                   RuoloUtenteRepository ruoloUtenteRepository, PasswordEncoder passwordEncoder,
	                   JwtService jwtService, TokenBlacklist blacklist) {
		this.utenteRepository = utenteRepository;
		this.ruoloRepository = ruoloRepository;
		this.ruoloUtenteRepository = ruoloUtenteRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.blacklist = blacklist;
	}

	@Transactional
	public AuthResponse registra(RegistrazioneRequest richiesta) {
		if (utenteRepository.existsByUsername(richiesta.username())) {
			throw new ConflittoException("Username gia' in uso");
		}
		if (utenteRepository.existsByEmail(richiesta.email())) {
			throw new ConflittoException("Email gia' registrata");
		}
		Utente utente = new Utente();
		utente.setUsername(richiesta.username());
		utente.setEmail(richiesta.email());
		utente.setPassword(passwordEncoder.encode(richiesta.password()));
		try {
			// flush subito: se due registrazioni uguali arrivano insieme, il vincolo UNIQUE fa da arbitro.
			utenteRepository.saveAndFlush(utente);
		} catch (DataIntegrityViolationException e) {
			throw new ConflittoException("Username o email gia' in uso");
		}

		Ruolo ruoloUtente = ruoloRepository.findByNome(Ruolo.UTENTE)
				.orElseThrow(() -> new IllegalStateException("Ruolo UTENTE mancante: seeder non eseguito"));
		ruoloUtenteRepository.save(new RuoloUtente(utente, ruoloUtente));

		return risposta(utente, List.of(Ruolo.UTENTE));
	}

	public AuthResponse login(LoginRequest richiesta) {
		// Stesso errore per utente inesistente e password errata: non si aiuta chi tenta a caso.
		Utente utente = utenteRepository.findByUsername(richiesta.username())
				.orElseThrow(CredenzialiNonValideException::new);
		if (!passwordEncoder.matches(richiesta.password(), utente.getPassword())) {
			throw new CredenzialiNonValideException();
		}
		return risposta(utente, ruoloUtenteRepository.findNomiRuoli(utente.getId()));
	}

	/** Il token resta firmato e valido fino alla scadenza: si annota il suo jti come revocato. */
	public void logout(UtenteAutenticato utente) {
		blacklist.revoca(utente.jti(), utente.scadenza());
	}

	private AuthResponse risposta(Utente utente, List<String> ruoli) {
		String token = jwtService.genera(utente.getId(), utente.getUsername());
		return new AuthResponse(utente.getId(), utente.getUsername(), ruoli, token);
	}
}
