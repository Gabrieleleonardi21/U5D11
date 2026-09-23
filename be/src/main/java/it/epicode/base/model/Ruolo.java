package it.epicode.base.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/** Ruolo applicativo. Il seeder crea UTENTE e ADMIN; il nome diventa l'authority "ROLE_<nome>". */
@Entity
@Table(name = "ruoli")
@Getter
@Setter
@NoArgsConstructor
public class Ruolo {

	public static final String UTENTE = "UTENTE";
	public static final String ADMIN = "ADMIN";

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, unique = true, length = 30)
	private String nome;

	public Ruolo(String nome) {
		this.nome = nome;
	}
}
