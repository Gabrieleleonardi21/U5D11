package it.epicode.base.web;

import it.epicode.base.dto.RobotAdmin;
import it.epicode.base.dto.RobotPatchRequest;
import it.epicode.base.dto.RobotRequest;
import it.epicode.base.dto.RobotResponse;
import it.epicode.base.service.RobotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Le GET sono aperte a tutti (filter chain) ma rispondono in modo diverso a seconda di chi chiede.
 * LIVELLO 2: pubblicare, modificare ed eliminare spetta all'admin (@PreAuthorize sul metodo).
 */
@RestController
@RequestMapping("/api/robot")
public class RobotController {

	private final RobotService robotService;

	public RobotController(RobotService robotService) {
		this.robotService = robotService;
	}

	@GetMapping
	public List<RobotResponse> lista() {
		return robotService.lista();
	}

	@GetMapping("/{id}")
	public RobotResponse dettaglio(@PathVariable UUID id) {
		return robotService.dettaglio(id);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	public RobotAdmin crea(@Valid @RequestBody RobotRequest richiesta) {
		return robotService.crea(richiesta);
	}

	@PatchMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public RobotAdmin modifica(@PathVariable UUID id, @Valid @RequestBody RobotPatchRequest patch) {
		return robotService.modifica(id, patch);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void elimina(@PathVariable UUID id) {
		robotService.elimina(id);
	}
}
