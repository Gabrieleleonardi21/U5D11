package it.epicode.base.web;

import it.epicode.base.dto.UtenteResponse;
import it.epicode.base.security.UtenteAutenticato;
import it.epicode.base.service.UtenteService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * LIVELLO 2: @PreAuthorize sulla classe, gestire i ruoli spetta all'admin.
 * La lista utenti va oltre la traccia: serve alla UI admin per scegliere a chi assegnare il ruolo.
 */
@RestController
@RequestMapping("/api/utenti")
@PreAuthorize("hasRole('ADMIN')")
public class UtenteController {

	private final UtenteService utenteService;

	public UtenteController(UtenteService utenteService) {
		this.utenteService = utenteService;
	}

	@GetMapping
	public List<UtenteResponse> lista() {
		return utenteService.lista();
	}

	@PostMapping("/{id}/admin")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void assegnaAdmin(@PathVariable UUID id) {
		utenteService.assegnaAdmin(id);
	}

	@DeleteMapping("/{id}/admin")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void revocaAdmin(@PathVariable UUID id, @AuthenticationPrincipal UtenteAutenticato corrente) {
		utenteService.revocaAdmin(id, corrente);
	}
}
