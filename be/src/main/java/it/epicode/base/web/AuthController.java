package it.epicode.base.web;

import it.epicode.base.dto.AuthResponse;
import it.epicode.base.dto.LoginRequest;
import it.epicode.base.dto.RegistrazioneRequest;
import it.epicode.base.security.UtenteAutenticato;
import it.epicode.base.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** register e login sono aperti (filter chain), logout richiede un utente collegato. */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public AuthResponse register(@Valid @RequestBody RegistrazioneRequest richiesta) {
		return authService.registra(richiesta);
	}

	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest richiesta) {
		return authService.login(richiesta);
	}

	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void logout(@AuthenticationPrincipal UtenteAutenticato utente) {
		authService.logout(utente);
	}
}
