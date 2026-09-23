package it.epicode.base.exception;

/** 401 al login: messaggio generico, non si rivela se e' sbagliato lo username o la password. */
public class CredenzialiNonValideException extends RuntimeException {

	public CredenzialiNonValideException() {
		super("Credenziali non valide");
	}
}
