package it.epicode.base.dto;

import java.util.List;
import java.util.UUID;

/** Risposta di login e registrazione: il FE salva token e ruoli per decidere cosa mostrare. */
public record AuthResponse(UUID id, String username, List<String> ruoli, String token) {
}
