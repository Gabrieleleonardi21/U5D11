package it.epicode.base.security;

import it.epicode.base.exception.ErroreResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * I 401/403 decisi dalla filter chain nascono prima dei controller, quindi il
 * @RestControllerAdvice non li vede: qui si scrive lo stesso JSON di ErroreResponse.
 */
@Component
public class RispostaErroreJson implements AuthenticationEntryPoint, AccessDeniedHandler {

	private final ObjectMapper mapper;

	public RispostaErroreJson(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	/** Richiesta senza token (o token non valido) su un URL che lo richiede. */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
			throws IOException {
		scrivi(response, HttpStatus.UNAUTHORIZED, "Autenticazione richiesta");
	}

	/** Utente autenticato ma senza il ruolo richiesto dalla filter chain. */
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e)
			throws IOException {
		scrivi(response, HttpStatus.FORBIDDEN, "Operazione riservata a un altro ruolo");
	}

	private void scrivi(HttpServletResponse response, HttpStatus stato, String messaggio) throws IOException {
		response.setStatus(stato.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		ErroreResponse corpo = ErroreResponse.di(stato.value(), stato.getReasonPhrase(), messaggio);
		response.getWriter().write(mapper.writeValueAsString(corpo));
	}
}
