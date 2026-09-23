package it.epicode.base.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

/**
 * Generazione e lettura dei JWT (stesso schema visto nel corso).
 * Nel token stanno solo id e username: i ruoli si leggono dal DB a ogni richiesta.
 */
@Service
public class JwtService {

	private final SecretKey chiave;
	private final long durataMinuti;

	public JwtService(@Value("${app.jwt.secret}") String secret,
	                  @Value("${app.jwt.durata-minuti}") long durataMinuti) {
		this.chiave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.durataMinuti = durataMinuti;
	}

	public String genera(UUID idUtente, String username) {
		Instant adesso = Instant.now();
		return Jwts.builder()
				.id(UUID.randomUUID().toString())   // jti: identifica il token per il logout
				.subject(idUtente.toString())
				.claim("username", username)
				.issuedAt(Date.from(adesso))
				.expiration(Date.from(adesso.plus(durataMinuti, ChronoUnit.MINUTES)))
				.signWith(chiave)
				.compact();
	}

	/** @return i claim se il token e' valido, null se assente, malformato, scaduto o con firma errata. */
	public Claims leggiClaims(String token) {
		if (token == null || token.isBlank()) {
			return null;
		}
		try {
			return Jwts.parser().verifyWith(chiave).build().parseSignedClaims(token).getPayload();
		} catch (JwtException | IllegalArgumentException e) {
			return null;
		}
	}

	/** Estrae il token da "Authorization: Bearer <token>". */
	public String estraiDaHeader(String headerAuthorization) {
		if (headerAuthorization == null || !headerAuthorization.startsWith("Bearer ")) {
			return null;
		}
		return headerAuthorization.substring(7).trim();
	}
}
