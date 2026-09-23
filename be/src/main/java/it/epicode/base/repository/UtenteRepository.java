package it.epicode.base.repository;

import it.epicode.base.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UtenteRepository extends JpaRepository<Utente, UUID> {

	Optional<Utente> findByUsername(String username);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);
}
