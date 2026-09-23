package it.epicode.base.security;

import java.time.Instant;
import java.util.UUID;

/**
 * Principal messo nel SecurityContext dal filtro JWT.
 * Porta con se' jti e scadenza del token, cosi' il logout puo' revocarlo senza rileggere l'header.
 */
public record UtenteAutenticato(UUID id, String username, String jti, Instant scadenza) {
}
