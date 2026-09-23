package it.epicode.base.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * In produzione FE e BE stanno su due domini diversi: senza CORS il browser
 * blocca ogni fetch. Le origini ammesse arrivano da ALLOWED_ORIGIN.
 *
 * E' un bean CorsConfigurationSource (non un WebMvcConfigurer) cosi' lo usa anche
 * Spring Security: i preflight OPTIONS vengono gestiti prima dell'autorizzazione.
 */
@Configuration
public class CorsConfig {

	@Bean
	CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.allowed-origins}") List<String> origini) {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(origini);
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/api/**", config);
		return source;
	}
}
