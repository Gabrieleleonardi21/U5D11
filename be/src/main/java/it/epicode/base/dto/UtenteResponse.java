package it.epicode.base.dto;

import java.util.List;
import java.util.UUID;

/** Riga della lista utenti dell'admin: serve per scegliere a chi assegnare o revocare il ruolo. */
public record UtenteResponse(UUID id, String username, String email, List<String> ruoli) {
}
