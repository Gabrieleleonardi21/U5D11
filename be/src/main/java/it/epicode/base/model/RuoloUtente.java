package it.epicode.base.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/** Associazione utente-ruolo con id proprio, come da traccia. Un utente non puo' avere due volte lo stesso ruolo. */
@Entity
@Table(name = "ruoli_utenti",
		uniqueConstraints = @UniqueConstraint(name = "uk_ruoli_utenti", columnNames = {"id_utente", "id_ruolo"}))
@Getter
@Setter
@NoArgsConstructor
public class RuoloUtente {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_utente", nullable = false)
	private Utente utente;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_ruolo", nullable = false)
	private Ruolo ruolo;

	public RuoloUtente(Utente utente, Ruolo ruolo) {
		this.utente = utente;
		this.ruolo = ruolo;
	}
}
