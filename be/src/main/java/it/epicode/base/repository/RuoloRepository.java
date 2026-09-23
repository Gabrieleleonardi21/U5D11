package it.epicode.base.repository;

import it.epicode.base.model.Ruolo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RuoloRepository extends JpaRepository<Ruolo, UUID> {

	Optional<Ruolo> findByNome(String nome);
}
