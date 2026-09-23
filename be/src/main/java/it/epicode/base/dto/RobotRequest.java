package it.epicode.base.dto;

import it.epicode.base.model.CategoriaRobot;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Creazione di un robot (solo admin). "pubblicato" assente = bozza. */
public record RobotRequest(
		@NotBlank(message = "nome obbligatorio")
		@Size(max = 100, message = "nome troppo lungo")
		String nome,

		@Size(max = 1000, message = "descrizione troppo lunga")
		String descrizione,

		@NotNull(message = "categoria obbligatoria")
		CategoriaRobot categoria,

		@NotBlank(message = "produttore obbligatorio")
		@Size(max = 100, message = "produttore troppo lungo")
		String produttore,

		@NotNull(message = "prezzo obbligatorio")
		@PositiveOrZero(message = "prezzo non negativo")
		@Digits(integer = 8, fraction = 2, message = "prezzo: massimo 8 cifre intere e 2 decimali")
		BigDecimal prezzo,

		// Boolean e non boolean: Jackson 3 rifiuta un primitivo assente nel JSON. null = bozza.
		Boolean pubblicato,

		@PositiveOrZero(message = "prezzo d'acquisto non negativo")
		@Digits(integer = 8, fraction = 2, message = "prezzo d'acquisto: massimo 8 cifre intere e 2 decimali")
		BigDecimal prezzoAcquisto,

		@Size(max = 100, message = "fornitore troppo lungo")
		String fornitore
) {
}
