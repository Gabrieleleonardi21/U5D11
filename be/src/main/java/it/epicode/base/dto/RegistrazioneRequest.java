package it.epicode.base.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrazioneRequest(
		@NotBlank(message = "username obbligatorio")
		@Size(min = 3, max = 50, message = "username tra 3 e 50 caratteri")
		String username,

		@NotBlank(message = "email obbligatoria")
		@Email(message = "email non valida")
		@Size(max = 120, message = "email troppo lunga")
		String email,

		@NotBlank(message = "password obbligatoria")
		@Size(min = 8, max = 100, message = "password tra 8 e 100 caratteri")
		String password
) {
}
