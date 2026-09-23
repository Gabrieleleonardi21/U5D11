package it.epicode.base.exception;

/** 409: username/email gia' registrati, nome robot duplicato, robot gia' nei preferiti. */
public class ConflittoException extends RuntimeException {

	public ConflittoException(String messaggio) {
		super(messaggio);
	}
}
