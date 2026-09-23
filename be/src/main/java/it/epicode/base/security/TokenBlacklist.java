package it.epicode.base.security;

import it.epicode.base.model.TokenRevocato;
import it.epicode.base.repository.TokenRevocatoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Registro dei token invalidati dal logout.
 *
 * Un JWT resta valido fino alla scadenza: per fare logout lato server serve
 * ricordare quali token rifiutare. Il registro sta nella tabella token_revocati,
 * cosi' sopravvive ai riavvii (su Render il piano free riavvia spesso).
 */
@Component
public class TokenBlacklist {

	private final TokenRevocatoRepository repository;

	public TokenBlacklist(TokenRevocatoRepository repository) {
		this.repository = repository;
	}

	/** Idempotente: rifare logout con lo stesso token riscrive la stessa riga. */
	public void revoca(String jti, Instant scadenza) {
		if (jti != null && scadenza != null) {
			repository.save(new TokenRevocato(jti, scadenza));
		}
	}

	public boolean isRevocato(String jti) {
		return jti != null && repository.existsById(jti);
	}

	/** Rimuove le voci ormai scadute: dopo la scadenza il token e' gia' rifiutato dalla firma. */
	@Scheduled(fixedDelay = 600_000)
	@Transactional
	public void pulisci() {
		repository.deleteByScadenzaBefore(Instant.now());
	}
}
