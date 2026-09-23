package it.epicode.base.security;

import io.jsonwebtoken.Claims;
import it.epicode.base.repository.RuoloUtenteRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Legge il Bearer token e, se valido e non revocato, mette l'utente nel SecurityContext
 * con i suoi ruoli. Non blocca mai la richiesta: se il token manca o e' scaduto la
 * richiesta prosegue come anonima e sara' la filter chain a rispondere 401 dove serve.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final TokenBlacklist blacklist;
	private final RuoloUtenteRepository ruoloUtenteRepository;

	public JwtAuthFilter(JwtService jwtService, TokenBlacklist blacklist, RuoloUtenteRepository ruoloUtenteRepository) {
		this.jwtService = jwtService;
		this.blacklist = blacklist;
		this.ruoloUtenteRepository = ruoloUtenteRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String token = jwtService.estraiDaHeader(request.getHeader("Authorization"));
		Claims claims = jwtService.leggiClaims(token);

		if (claims != null && !blacklist.isRevocato(claims.getId())) {
			UUID idUtente = UUID.fromString(claims.getSubject());
			UtenteAutenticato utente = new UtenteAutenticato(
					idUtente, claims.get("username", String.class), claims.getId(), claims.getExpiration().toInstant());

			// Ruoli dal DB, non dal token: revocare l'admin ha effetto immediato.
			List<SimpleGrantedAuthority> authorities = ruoloUtenteRepository.findNomiRuoli(idUtente).stream()
					.map(nome -> new SimpleGrantedAuthority("ROLE_" + nome))
					.toList();

			SecurityContextHolder.getContext()
					.setAuthentication(new UsernamePasswordAuthenticationToken(utente, null, authorities));
		}
		chain.doFilter(request, response);
	}
}
