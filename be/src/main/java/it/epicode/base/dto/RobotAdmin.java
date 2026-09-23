package it.epicode.base.dto;

import it.epicode.base.model.CategoriaRobot;
import it.epicode.base.model.Robot;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Tutti i campi, compresi stato di pubblicazione, prezzo d'acquisto e fornitore: solo per l'admin. */
public record RobotAdmin(
		UUID id,
		String nome,
		String descrizione,
		CategoriaRobot categoria,
		String produttore,
		BigDecimal prezzo,
		Instant createdAt,
		boolean preferito,
		boolean pubblicato,
		BigDecimal prezzoAcquisto,
		String fornitore
) implements RobotResponse {

	public static RobotAdmin from(Robot r, boolean preferito) {
		return new RobotAdmin(r.getId(), r.getNome(), r.getDescrizione(), r.getCategoria(),
				r.getProduttore(), r.getPrezzo(), r.getCreatedAt(), preferito,
				r.isPubblicato(), r.getPrezzoAcquisto(), r.getFornitore());
	}
}
