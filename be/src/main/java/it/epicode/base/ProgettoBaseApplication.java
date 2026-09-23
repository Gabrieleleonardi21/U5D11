package it.epicode.base;

import it.epicode.base.config.DatabaseUrl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Punto di ingresso. @EnableScheduling serve alla pulizia periodica della blacklist dei token. */
@SpringBootApplication
@EnableScheduling
public class ProgettoBaseApplication {

	public static void main(String[] args) {
		// Su Render le credenziali arrivano in DATABASE_URL, formato non JDBC:
		// la traduzione va fatta prima che parta il contesto Spring.
		DatabaseUrl.applicaSePresente();

		SpringApplication.run(ProgettoBaseApplication.class, args);
	}
}
