package it.epicode.base.security;

import it.epicode.base.model.Ruolo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Accesso all'utente della richiesta corrente dai service.
 * Sugli endpoint pubblici l'utente puo' non esserci: per questo opzionale().
 */
public final class UtenteCorrente {

	private UtenteCorrente() {
	}

	/** Vuoto se la richiesta e' anonima (Spring mette un AnonymousAuthenticationToken, non il nostro record). */
	public static Optional<UtenteAutenticato> opzionale() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof UtenteAutenticato utente) {
			return Optional.of(utente);
		}
		return Optional.empty();
	}

	public static boolean isAdmin() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) {
			return false;
		}
		return auth.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_" + Ruolo.ADMIN));
	}
}
