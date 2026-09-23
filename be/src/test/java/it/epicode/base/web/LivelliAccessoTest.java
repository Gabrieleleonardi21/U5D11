package it.epicode.base.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Un test per livello sugli stessi URL. Usa il database locale (application.yml):
 * il seeder crea i dati che servono. Su Render i test non girano (Dockerfile con -DskipTests).
 */
@SpringBootTest
@AutoConfigureMockMvc
class LivelliAccessoTest {

	@Autowired
	private MockMvc mvc;

	// ---------- anonimo ----------

	@Test
	void anonimoVedeSoloPubblicatiSenzaCampiRiservati() throws Exception {
		mvc.perform(get("/api/robot"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(greaterThan(0))))
				// il DTO pubblico non ha proprio le chiavi riservate, non le ha a null
				.andExpect(jsonPath("$[0].prezzoAcquisto").doesNotExist())
				.andExpect(jsonPath("$[0].fornitore").doesNotExist())
				.andExpect(jsonPath("$[0].pubblicato").doesNotExist());
	}

	@Test
	void anonimoNonPuoAggiungerePreferiti() throws Exception {
		// livello 1: la filter chain risponde 401 prima ancora di arrivare al controller
		mvc.perform(post("/api/preferiti/" + UUID.randomUUID()))
				.andExpect(status().isUnauthorized());
	}

	// ---------- utente ----------

	@Test
	@WithMockUser(roles = "UTENTE")
	void utenteNonPuoPubblicare() throws Exception {
		// livello 2: @PreAuthorize rifiuta con 403 chi non ha il ruolo ADMIN
		mvc.perform(post("/api/robot")
						.contentType("application/json")
						.content("{\"nome\":\"X\",\"categoria\":\"DRONE\",\"produttore\":\"A\",\"prezzo\":1}"))
				.andExpect(status().isForbidden());
	}

	// ---------- admin ----------

	@Test
	@WithMockUser(roles = {"UTENTE", "ADMIN"})
	void adminVedeBozzeECampiRiservati() throws Exception {
		// stesso URL dell'anonimo, risposta diversa: bozze e campi riservati compresi
		mvc.perform(get("/api/robot"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].prezzoAcquisto").exists())
				.andExpect(jsonPath("$[0].fornitore").exists())
				.andExpect(jsonPath("$[?(@.pubblicato == false)]").isNotEmpty());
	}
}
