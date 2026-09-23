package it.epicode.base.dto;

import it.epicode.base.model.CategoriaRobot;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Modifica parziale (solo admin): i campi null restano com'erano. */
public record RobotPatchRequest(
		@Size(min = 1, max = 100, message = "nome tra 1 e 100 caratteri")
		String nome,

		@Size(max = 1000, message = "descrizione troppo lunga")
		String descrizione,

		CategoriaRobot categoria,

		@Size(min = 1, max = 100, message = "produttore tra 1 e 100 caratteri")
		String produttore,

		@PositiveOrZero(message = "prezzo non negativo")
		@Digits(integer = 8, fraction = 2, message = "prezzo: massimo 8 cifre intere e 2 decimali")
		BigDecimal prezzo,

		Boolean pubblicato,

		@PositiveOrZero(message = "prezzo d'acquisto non negativo")
		@Digits(integer = 8, fraction = 2, message = "prezzo d'acquisto: massimo 8 cifre intere e 2 decimali")
		BigDecimal prezzoAcquisto,

		@Size(max = 100, message = "fornitore troppo lungo")
		String fornitore
) {
}
