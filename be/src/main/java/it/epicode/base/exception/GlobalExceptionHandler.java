package it.epicode.base.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Traduce le eccezioni in risposte JSON uniformi ({@link ErroreResponse}).
 *
 * Non c'e' un handler generico su Exception: catturerebbe anche le eccezioni
 * di Spring Security e i 401/403 diventerebbero 500.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CredenzialiNonValideException.class)
	public ResponseEntity<ErroreResponse> credenziali(CredenzialiNonValideException e) {
		return risposta(HttpStatus.UNAUTHORIZED, e.getMessage(), List.of());
	}

	@ExceptionHandler(RisorsaNonTrovataException.class)
	public ResponseEntity<ErroreResponse> nonTrovata(RisorsaNonTrovataException e) {
		return risposta(HttpStatus.NOT_FOUND, e.getMessage(), List.of());
	}

	@ExceptionHandler(ConflittoException.class)
	public ResponseEntity<ErroreResponse> conflitto(ConflittoException e) {
		return risposta(HttpStatus.CONFLICT, e.getMessage(), List.of());
	}

	/** Lanciata da @PreAuthorize quando il ruolo non basta (es. utente che prova a pubblicare). */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErroreResponse> accessoNegato(AccessDeniedException e) {
		return risposta(HttpStatus.FORBIDDEN, "Operazione riservata a un altro ruolo", List.of());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErroreResponse> argomentoNonValido(IllegalArgumentException e) {
		return risposta(HttpStatus.BAD_REQUEST, e.getMessage(), List.of());
	}

	/** JSON malformato o valore non ammesso per un enum (es. categoria inesistente). */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErroreResponse> corpoNonLeggibile(HttpMessageNotReadableException e) {
		return risposta(HttpStatus.BAD_REQUEST, "Corpo della richiesta non valido", List.of());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErroreResponse> validazione(MethodArgumentNotValidException e) {
		List<String> dettagli = e.getBindingResult().getFieldErrors().stream()
				.map(FieldError::getDefaultMessage)
				.toList();
		return risposta(HttpStatus.BAD_REQUEST, "Richiesta non valida", dettagli);
	}

	/** Costruisce la risposta una volta sola: status, frase standard HTTP e messaggio. */
	private ResponseEntity<ErroreResponse> risposta(HttpStatus stato, String messaggio, List<String> dettagli) {
		return ResponseEntity.status(stato)
				.body(ErroreResponse.di(stato.value(), stato.getReasonPhrase(), messaggio, dettagli));
	}
}
