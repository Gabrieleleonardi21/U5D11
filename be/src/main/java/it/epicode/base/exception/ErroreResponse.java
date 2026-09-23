package it.epicode.base.exception;

import java.time.Instant;
import java.util.List;

/** Corpo JSON di ogni risposta di errore, uguale per controller e filtri di sicurezza. */
public record ErroreResponse(int status, String errore, String messaggio, List<String> dettagli, Instant timestamp) {

	public static ErroreResponse di(int status, String errore, String messaggio) {
		return new ErroreResponse(status, errore, messaggio, List.of(), Instant.now());
	}

	public static ErroreResponse di(int status, String errore, String messaggio, List<String> dettagli) {
		return new ErroreResponse(status, errore, messaggio, dettagli, Instant.now());
	}
}
