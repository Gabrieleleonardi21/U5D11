package it.epicode.base.model;

import jakarta.persistence.Column;
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
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Preferito di un utente. Appartiene a qualcuno: si legge e si cancella sempre
 * filtrando per id_utente nella query, mai per il solo id della riga.
 */
@Entity
@Table(name = "utenti_robot_preferiti",
		uniqueConstraints = @UniqueConstraint(name = "uk_preferiti_utente_robot", columnNames = {"id_utente", "id_robot"}))
@Getter
@Setter
@NoArgsConstructor
public class Preferito {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_utente", nullable = false)
	private Utente utente;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_robot", nullable = false)
	private Robot robot;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	public Preferito(Utente utente, Robot robot) {
		this.utente = utente;
		this.robot = robot;
	}
}
