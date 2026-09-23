package it.epicode.base.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * LIVELLO 1: la filter chain ragiona su metodo e percorso.
 * Dice che la vetrina (GET) e' pubblica e che i preferiti vogliono un utente collegato.
 * Non sa nulla di ruoli admin (LIVELLO 2, @PreAuthorize nei controller)
 * ne' di chi possiede cosa (LIVELLO 3, le query dei repository).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter, RispostaErroreJson errori)
			throws Exception {
		http
				// API stateless con token nell'header: niente sessione, niente CSRF.
				.csrf(csrf -> csrf.disable())
				.cors(Customizer.withDefaults())   // usa il bean CorsConfigurationSource di CorsConfig
				.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(e -> e.authenticationEntryPoint(errori).accessDeniedHandler(errori))
				.authorizeHttpRequests(auth -> auth
						// preflight CORS del browser
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						// chi non ha ancora un account
						.requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register").permitAll()
						// la vetrina e' pubblica: e' il service a decidere COSA mostrare a chi
						.requestMatchers(HttpMethod.GET, "/api/robot/**", "/api/stato", "/actuator/health/**").permitAll()
						// i preferiti appartengono a qualcuno: serve un utente collegato
						.requestMatchers("/api/preferiti/**", "/api/auth/logout").authenticated()
						// tutto il resto (pubblicare, gestire ruoli...) almeno autenticato; il ruolo lo controlla @PreAuthorize
						.anyRequest().authenticated())
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
