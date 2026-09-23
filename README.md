# Robot Vetrina — BE + FE (JSX) + PostgreSQL

Sito vetrina di robot con **tre livelli di accesso agli stessi indirizzi**. Non e' la pagina a
decidere cosa mostrare: e' il server.

| Livello | Chi | Cosa puo' fare |
|---|---|---|
| Anonimo | chiunque | vede solo i robot **pubblicati**, e di ognuno solo i campi pubblici |
| Utente | chi ha fatto login (ruolo `UTENTE`, assegnato alla registrazione) | come l'anonimo, piu' aggiungere e togliere i **preferiti** |
| Admin | ruolo `ADMIN` | pubblica, modifica ed elimina robot; vede anche le **bozze**, il **prezzo d'acquisto** e il **fornitore**; assegna e revoca il ruolo admin |

| Parte | Tecnologia | In locale | Su Render |
|---|---|---|---|
| Backend | Spring Boot 4.1.1, Java 25, Spring Security 7, JWT (jjwt 0.12.6), Maven wrapper | `be` sulla 8080 | Web Service (Docker) |
| Frontend | React 19, Vite, JavaScript (JSX), Tailwind 4, React Router 7, Three.js (@react-three/fiber) | `fe` sulla 5173 | Static Site |
| Database | PostgreSQL | locale sulla 5432 | Render PostgreSQL |
| Immagini | [RoboHash](https://robohash.org): genera un robot unico dal nome, senza chiave API | | |

## Relazione: chi protegge cosa

Tre meccanismi, ognuno con un compito solo. Nessuno dei primi due sa di chi sia un preferito:
quella domanda ha una risposta sola e sta nella query.

1. **Filter chain** (`security/SecurityConfig.java`): ragiona su **metodo e percorso**. Dice che
   la vetrina (GET) e' pubblica e che i preferiti vogliono un utente collegato.
2. **`@PreAuthorize`** (`web/RobotController.java`, `web/UtenteController.java`): ragiona
   sull'**operazione**. Dice che pubblicare un robot o gestire i ruoli spetta all'admin.
3. **La query** (`repository/PreferitoRepository.java`): decide **di chi e' la riga**. I preferiti
   si leggono e si cancellano filtrando per `id_utente` (`deleteByUtenteIdAndRobotId`), mai per il
   solo id. Il ruolo dice se puoi, non su quale riga.

| Metodo e URL | Livello richiesto | Meccanismo che lo protegge |
|---|---|---|
| POST `/api/auth/register`, POST `/api/auth/login` | anonimo | filter chain: `permitAll` |
| POST `/api/auth/logout` | utente | filter chain: `authenticated` |
| GET `/api/robot`, GET `/api/robot/{id}` | anonimo, **ma la risposta cambia col livello** | filter chain: `permitAll`; poi `RobotService` sceglie query e DTO in base a chi chiede (bozze e campi riservati solo all'admin, flag `preferito` solo per l'utente) |
| POST `/api/robot`, PATCH `/api/robot/{id}`, DELETE `/api/robot/{id}` | admin | `@PreAuthorize("hasRole('ADMIN')")` |
| GET `/api/preferiti` | utente | filter chain `authenticated` + query filtrata per proprietario |
| POST `/api/preferiti/{idRobot}`, DELETE `/api/preferiti/{idRobot}` | utente | filter chain `authenticated` + `deleteByUtenteIdAndRobotId` (0 righe = 404) |
| GET `/api/utenti` | admin | `@PreAuthorize` sulla classe (*) |
| POST `/api/utenti/{id}/admin`, DELETE `/api/utenti/{id}/admin` | admin | `@PreAuthorize` sulla classe; l'auto-revoca da' 400 |

Codici: 201 crea/aggiungi, 204 elimina/rimuovi/logout/ruoli, 400 dati non validi, 401 token
assente, scaduto o revocato, 403 ruolo insufficiente, 404 non trovato (o bozza per chi non e'
admin), 409 duplicati.

(*) `GET /api/utenti` e' l'unica aggiunta oltre la traccia: senza, la pagina admin non saprebbe a
chi assegnare il ruolo.

### Scelte da conoscere

- **L'id utente arriva sempre dal token**, mai dal body o dall'URL (`@AuthenticationPrincipal`).
- **I ruoli si leggono dal DB a ogni richiesta** (`JwtAuthFilter`), non dal token: revocare
  l'admin ha effetto immediato, non al prossimo login.
- **Logout con JWT**: il token resta firmato fino alla scadenza, quindi il suo `jti` finisce
  nella tabella `token_revocati` (`TokenBlacklist`), che sopravvive ai riavvii. Ogni 10 minuti
  vengono tolte le righe dei token ormai scaduti.
- **Prezzi `numeric(10,2)`** invece del `DECIMAL(6,2)` della traccia: un robot industriale costa
  piu' di 9.999,99 euro.
- **Una bozza non esiste per chi non e' admin**: dettaglio e preferito rispondono 404, non 403,
  cosi' non si rivela nemmeno che c'e'.

## Dati iniziali

Il seeder (`config/DatiIniziali.java`) e' idempotente e crea, se mancano:

- i ruoli `UTENTE` e `ADMIN`;
- l'admin `admin` / `Admin123!` (con entrambi i ruoli), sovrascrivibile con `ADMIN_USERNAME` e
  `ADMIN_PASSWORD`;
- 11 robot: 8 pubblicati e 3 bozze.

## Avvio in locale

1. PostgreSQL sulla 5432 e database creato:
   ```
   createdb -U postgres progetto_base
   ```
   Credenziali diverse da `postgres` / `admin`: variabili `DB_URL`, `DB_USERNAME`,
   `DB_PASSWORD`, oppure `be/src/main/resources/application.yml`.
2. Doppio clic su `avvia.cmd` (Windows) o `./avvia.sh` (macOS/Linux), oppure:
   ```
   cd be && ./mvnw spring-boot:run
   cd fe && npm install && npm run dev
   ```
3. http://localhost:5173 — entra come `admin` / `Admin123!` per vedere le bozze.

Test (usano il database locale): `cd be && ./mvnw test` — `LivelliAccessoTest` verifica i tre
livelli sugli stessi URL.

## Deploy su Render

1. Repository Git con `be/`, `fe/`, `render.yaml` nella radice.
2. **New > Blueprint**, si sceglie la repo: nascono `robot-vetrina-db`, `robot-vetrina-be`,
   `robot-vetrina-fe`. `JWT_SECRET` viene generata da Render.
3. Dopo la prima build si impostano le variabili `sync: false`, senza `/` finale:

   | Servizio | Variabile | Valore |
   |---|---|---|
   | `robot-vetrina-be` | `ALLOWED_ORIGIN` | `https://robot-vetrina-fe.onrender.com` |
   | `robot-vetrina-be` | `ADMIN_PASSWORD` | una password diversa da quella di sviluppo |
   | `robot-vetrina-fe` | `VITE_API_URL` | `https://robot-vetrina-be.onrender.com` |

4. **Manual Deploy** di entrambi (`VITE_API_URL` e' letta in fase di build).

## Struttura

```
render.yaml                 blueprint: database + backend + frontend
avvia.cmd / avvia.sh        avvio locale (Windows / macOS-Linux)
be/
  Dockerfile                usato solo da Render
  src/main/java/it/epicode/base/
    config/    DatabaseUrl (DATABASE_URL -> JDBC), CorsConfig, DatiIniziali (seeder)
    security/  SecurityConfig (filter chain), JwtAuthFilter, JwtService, TokenBlacklist,
               UtenteAutenticato (principal), UtenteCorrente, RispostaErroreJson (401/403 JSON)
    model/     Utente, Ruolo, RuoloUtente, Robot, CategoriaRobot, Preferito
    repository/ query JPA; PreferitoRepository filtra sempre per proprietario
    dto/       record di richiesta (con validazione) e risposta (RobotPubblico / RobotAdmin)
    service/   AuthService, RobotService (risposta per livello), PreferitoService, UtenteService
    web/       AuthController, RobotController, PreferitoController, UtenteController
    exception/ GlobalExceptionHandler + ErroreResponse
fe/
  public/models/RobotExpressive.glb   modello 3D (CC0, Tomás Laulhé) per l'hero
  src/
    lib/        api.js (fetch + token + errori), formato.js, useRichiesta.js, liste.js
    auth/       AuthProvider, useAuth, RichiedeAuth, RichiedeAdmin (guard di rotta)
    components/ Layout, Navbar, RobotCard, Hero3D (Three.js), Messaggio, Campo
    pages/      Home, RobotDettaglio, Login, Registrazione, Preferiti, Admin (+ admin/)
```
