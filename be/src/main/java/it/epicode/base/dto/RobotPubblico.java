package it.epicode.base.dto;

import it.epicode.base.model.CategoriaRobot;
import it.epicode.base.model.Robot;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Campi visibili a tutti. "preferito" e' true solo per l'utente collegato che lo ha salvato. */
public record RobotPubblico(
		UUID id,
		String nome,
		String descrizione,
		CategoriaRobot categoria,
		String produttore,
		BigDecimal prezzo,
		Instant createdAt,
		boolean preferito
) implements RobotResponse {

	public static RobotPubblico from(Robot r, boolean preferito) {
		return new RobotPubblico(r.getId(), r.getNome(), r.getDescrizione(), r.getCategoria(),
				r.getProduttore(), r.getPrezzo(), r.getCreatedAt(), preferito);
	}
}
