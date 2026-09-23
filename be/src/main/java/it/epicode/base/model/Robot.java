package it.epicode.base.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * L'"oggetto" della vetrina. I campi pubblici li vede chiunque (se pubblicato);
 * prezzoAcquisto e fornitore sono riservati all'admin e non entrano mai nel DTO pubblico.
 * Prezzi numeric(10,2): la traccia diceva (6,2) ma un robot puo' costare piu' di 9.999,99.
 */
@Entity
@Table(name = "robot")
@Getter
@Setter
@NoArgsConstructor
public class Robot {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, unique = true, length = 100)
	private String nome;

	@Column(length = 1000)
	private String descrizione;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private CategoriaRobot categoria;

	@Column(nullable = false, length = 100)
	private String produttore;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal prezzo;

	/** false = bozza: visibile solo all'admin. */
	@Column(nullable = false)
	private boolean pubblicato;

	// ---- campi riservati all'admin ----

	@Column(name = "prezzo_acquisto", precision = 10, scale = 2)
	private BigDecimal prezzoAcquisto;

	@Column(length = 100)
	private String fornitore;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;
}
