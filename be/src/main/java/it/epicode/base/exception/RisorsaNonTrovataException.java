package it.epicode.base.exception;

/** 404: la risorsa non esiste, oppure esiste ma chi chiede non ha il diritto di vederla (es. bozza). */
public class RisorsaNonTrovataException extends RuntimeException {

	public RisorsaNonTrovataException(String messaggio) {
		super(messaggio);
	}
}
