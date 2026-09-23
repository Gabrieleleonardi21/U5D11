package it.epicode.base.web;

import it.epicode.base.dto.RobotPubblico;
import it.epicode.base.security.UtenteAutenticato;
import it.epicode.base.service.PreferitoService;
import org.springframework.http.HttpStatus;
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
 * La filter chain garantisce che qui arrivi solo un utente collegato (LIVELLO 1).
 * Il suo id viene dal token e passa al service, che lo usa in ogni query (LIVELLO 3).
 */
@RestController
@RequestMapping("/api/preferiti")
public class PreferitoController {

	private final PreferitoService preferitoService;

	public PreferitoController(PreferitoService preferitoService) {
		this.preferitoService = preferitoService;
	}

	@GetMapping
	public List<RobotPubblico> lista(@AuthenticationPrincipal UtenteAutenticato utente) {
		return preferitoService.lista(utente.id());
	}

	@PostMapping("/{idRobot}")
	@ResponseStatus(HttpStatus.CREATED)
	public RobotPubblico aggiungi(@PathVariable UUID idRobot, @AuthenticationPrincipal UtenteAutenticato utente) {
		return preferitoService.aggiungi(utente.id(), idRobot);
	}

	@DeleteMapping("/{idRobot}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void rimuovi(@PathVariable UUID idRobot, @AuthenticationPrincipal UtenteAutenticato utente) {
		preferitoService.rimuovi(utente.id(), idRobot);
	}
}
