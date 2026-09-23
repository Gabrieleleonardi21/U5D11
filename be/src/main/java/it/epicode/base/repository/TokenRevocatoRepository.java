package it.epicode.base.repository;

import it.epicode.base.model.TokenRevocato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface TokenRevocatoRepository extends JpaRepository<TokenRevocato, String> {

	/** Pulizia: via le righe di token ormai scaduti. */
	long deleteByScadenzaBefore(Instant istante);
}
