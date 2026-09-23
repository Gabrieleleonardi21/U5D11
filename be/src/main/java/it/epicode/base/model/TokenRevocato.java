package it.epicode.base.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Token invalidato dal logout. La chiave e' il jti del JWT; la scadenza serve solo
 * alla pulizia periodica (dopo, il token e' gia' rifiutato dalla firma).
 * Su DB e non in memoria: un riavvio del servizio non riabilita i token usciti.
 */
@Entity
@Table(name = "token_revocati")
@Getter
@Setter
@NoArgsConstructor
public class TokenRevocato {

	@Id
	@Column(length = 36)
	private String jti;

	@Column(nullable = false)
	private Instant scadenza;

	public TokenRevocato(String jti, Instant scadenza) {
		this.jti = jti;
		this.scadenza = scadenza;
	}
}
