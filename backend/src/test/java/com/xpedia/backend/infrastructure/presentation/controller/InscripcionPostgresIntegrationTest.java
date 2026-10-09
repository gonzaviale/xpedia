package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.port.ContraseniaPort;
import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.xpedia.backend.support.InscripcionTestData.HITO_ID;
import static com.xpedia.backend.support.InscripcionTestData.RUTA_ID;
import static com.xpedia.backend.support.InscripcionTestData.USUARIO_ID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/db/inscripciones-cleanup.sql", "/db/rutas-test.sql", "/db/usuarios-test.sql"})
class InscripcionPostgresIntegrationTest extends PostgresRepositoryTestSupport {

    private static final String BODY = """
            {"rutaId":"00000000-0000-0000-0000-000000000201","objetivo":"CAMBIAR",
             "metaPersonal":"  Conseguir trabajo remoto  ","ritmoMin":20}
            """;
    private static final String PASSWORD = "Una contraseña segura";

    @LocalServerPort
    private int port;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ContraseniaPort contraseniaPort;
    @Autowired
    private ObjectMapper objectMapper;

    private HttpClient httpClient;

    @AfterEach
    void cleanup() {
        jdbcTemplate.execute("DELETE FROM inscripcion");
    }

    @BeforeEach
    void setUp() {
        httpClient = HttpClient.newBuilder()
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                .build();
        jdbcTemplate.update("UPDATE usuario SET hash_contrasenia = ?", contraseniaPort.codificar(PASSWORD));
    }

    @Test
    @DisplayName("POST crea inscripción propia y progreso completo según prerrequisitos del catálogo")
    void crearShouldPersistEnrollmentAndInitialProgress() throws Exception {
        givenLogin("persona@example.com");
        String json = BODY.replace("\"ritmoMin\":20",
                "\"ritmoMin\":20,\"usuarioId\":\"b9000000-0000-4000-8000-000000000002\"");

        HttpResponse<String> response = crear(json);

        thenInitialContractAndDatabase(response);
    }

    @Test
    @DisplayName("Otra inscripción abierta devuelve 409 sin reiniciar ni duplicar progreso")
    void crearShouldRejectDuplicateWithoutResettingProgress() throws Exception {
        givenLogin("persona@example.com");
        JsonNode original = json(crear(BODY));
        jdbcTemplate.update("UPDATE progreso_nodo SET dominio = 0.60 WHERE inscripcion_id = ? AND nodo_id = ?",
                UUID.fromString(original.get("id").asText()), UUID.fromString("00000000-0000-0000-0000-000000000501"));

        HttpResponse<String> response = crear(BODY);

        thenDuplicatePreservesProgress(response);
    }

    @Test
    @DisplayName("Una inscripción pausada también impide otra abierta")
    void crearShouldRejectPausedEnrollment() throws Exception {
        givenLogin("persona@example.com");
        crear(BODY);
        jdbcTemplate.update("UPDATE inscripcion SET estado = 'PAUSADA'");

        HttpResponse<String> response = crear(BODY);

        thenCrearShouldRejectPausedEnrollment(response);
    }

    @Test
    @DisplayName("Una inscripción terminada permite otra con progreso independiente")
    void crearShouldAllowNewEnrollmentAfterCompletion() throws Exception {
        givenLogin("persona@example.com");
        String originalId = json(crear(BODY)).get("id").asText();
        jdbcTemplate.update("UPDATE inscripcion SET estado = 'TERMINADA'");

        HttpResponse<String> response = crear(BODY);

        thenCrearShouldAllowNewEnrollmentAfterCompletion(originalId, response);
    }

    @ParameterizedTest
    @ValueSource(strings = {"205", "206", "207", "208", "999"})
    @DisplayName("Ruta privada, no publicada o inexistente devuelve 404 sin escribir datos")
    void crearShouldHideUnavailableRoutes(String suffix) throws Exception {
        givenLogin("persona@example.com");

        HttpResponse<String> response = crear(BODY.replace("000000000201", "000000000" + suffix));

        thenCrearShouldHideUnavailableRoutes(response);
    }

    @Test
    @DisplayName("Ruta publicada sin recorrido devuelve 400 sin inscripción parcial")
    void crearShouldRejectRouteWithoutContent() throws Exception {
        givenLogin("persona@example.com");

        HttpResponse<String> response = crear(BODY.replace("000000000201", "000000000202"));

        thenCrearShouldRejectRouteWithoutContent(response);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "1441", "20.5", "null"})
    @DisplayName("Rechaza ritmos no enteros, ausentes o fuera del día")
    void crearShouldRejectInvalidRhythm(String ritmo) throws Exception {
        givenLogin("persona@example.com");

        HttpResponse<String> response = crear(BODY.replace("\"ritmoMin\":20", "\"ritmoMin\":" + ritmo));

        thenCrearShouldRejectInvalidRhythm(response);
    }

    @Test
    @DisplayName("Sin sesión devuelve 401 aun con CSRF válido")
    void crearShouldRequireAuthentication() throws Exception {
        HttpResponse<String> response = post("/api/inscripciones", BODY, csrf());

        thenCrearShouldRequireAuthentication(response);
    }

    @Test
    @DisplayName("Con sesión y sin CSRF devuelve 403")
    void crearShouldRequireCsrf() throws Exception {
        givenLogin("persona@example.com");

        HttpResponse<String> response = post("/api/inscripciones", BODY, null);

        thenCrearShouldRequireCsrf(response);
    }

    @Test
    @DisplayName("Una cuenta suspendida no puede crear inscripciones")
    void crearShouldRejectSuspendedUser() throws Exception {
        givenLogin("persona@example.com");
        String token = csrf();
        jdbcTemplate.update("UPDATE usuario SET estado = 'SUSPENDIDO' WHERE id = ?", USUARIO_ID);

        HttpResponse<String> response = post("/api/inscripciones", BODY, token);

        thenCrearShouldRejectSuspendedUser(response);
    }

    @Test
    @DisplayName("Si falla la persistencia de un progreso se revierte también la inscripción")
    void crearShouldRollbackEnrollmentWhenProgressFails() throws Exception {
        givenLogin("persona@example.com");
        jdbcTemplate.execute("ALTER TABLE progreso_nodo ADD CONSTRAINT fallo_test CHECK (estado <> 'BLOQUEADO')");

        HttpResponse<String> response;
        try {
            response = crear(BODY);
        } finally {
            jdbcTemplate.execute("ALTER TABLE progreso_nodo DROP CONSTRAINT fallo_test");
        }

        thenCrearShouldRollbackEnrollmentWhenProgressFails(response);
    }

    @Test
    @DisplayName("Dos solicitudes simultáneas producen una inscripción y un conflicto")
    void crearShouldPreventConcurrentDuplicates() throws Exception {
        givenLogin("persona@example.com");
        String token = csrf();
        HttpRequest request = postRequest("/api/inscripciones", BODY, token);

        CompletableFuture<HttpResponse<String>> first = httpClient.sendAsync(
                request, HttpResponse.BodyHandlers.ofString());
        CompletableFuture<HttpResponse<String>> second = httpClient.sendAsync(
                request, HttpResponse.BodyHandlers.ofString());
        List<Integer> statuses = List.of(first.join().statusCode(), second.join().statusCode());

        thenCrearShouldPreventConcurrentDuplicates(statuses);
    }

    @Test
    @DisplayName("Una ruta sin duración conserva la llegada desconocida")
    void crearShouldKeepUnknownEstimate() throws Exception {
        givenLogin("persona@example.com");
        jdbcTemplate.update("UPDATE ruta SET horas_estimadas = NULL WHERE id = ?", RUTA_ID);

        HttpResponse<String> response = crear(BODY);

        thenCrearShouldKeepUnknownEstimate(response);
    }

    @Test
    @DisplayName("Una duración publicada no positiva devuelve 400 sin guardar")
    void crearShouldRejectInvalidDuration() throws Exception {
        givenLogin("persona@example.com");
        jdbcTemplate.update("UPDATE ruta SET horas_estimadas = 0 WHERE id = ?", RUTA_ID);

        HttpResponse<String> response = crear(BODY);

        thenCrearShouldRejectInvalidDuration(response);
    }

    @Test
    @DisplayName("GET recupera los progresos guardados y sus cambios posteriores")
    void actualShouldReturnPersistedProgress() throws Exception {
        givenLogin("persona@example.com");
        String id = json(crear(BODY)).get("id").asText();
        jdbcTemplate.update("UPDATE progreso_nodo SET dominio = 0.55, estado = 'EN_CURSO' WHERE nodo_id = ?",
                UUID.fromString("00000000-0000-0000-0000-000000000501"));

        HttpResponse<String> response = get("/api/inscripciones/actual");

        thenActualContract(response, id);
    }

    @Test
    @DisplayName("GET sin inscripción activa devuelve 404")
    void actualShouldReturn404WhenNotEnrolled() throws Exception {
        givenLogin("persona@example.com");

        HttpResponse<String> response = get("/api/inscripciones/actual");

        thenActualShouldReturn404WhenNotEnrolled(response);
    }

    @Test
    @DisplayName("GET sin sesión devuelve 401")
    void actualShouldReturn401WhenAnonymous() throws Exception {
        HttpResponse<String> response = get("/api/inscripciones/actual");

        thenActualShouldReturn401WhenAnonymous(response);
    }

    @Test
    @DisplayName("Una cuenta no obtiene la inscripción de otra")
    void actualShouldNotExposeOtherUsersEnrollment() throws Exception {
        givenLogin("persona@example.com");
        crear(BODY);
        givenLogin("admin@example.com");

        HttpResponse<String> response = get("/api/inscripciones/actual");

        thenActualShouldNotExposeOtherUsersEnrollment(response);
    }

    @Test
    @DisplayName("GET de una inscripción pausada devuelve 404")
    void actualShouldIgnorePausedEnrollment() throws Exception {
        givenLogin("persona@example.com");
        crear(BODY);
        jdbcTemplate.update("UPDATE inscripcion SET estado = 'PAUSADA'");

        HttpResponse<String> response = get("/api/inscripciones/actual");

        thenActualShouldIgnorePausedEnrollment(response);
    }

    @Test
    @DisplayName("GET oculta una ruta archivada después de la inscripción")
    void actualShouldHideArchivedRoute() throws Exception {
        givenLogin("persona@example.com");
        crear(BODY);
        jdbcTemplate.update("UPDATE ruta SET estado = 'ARCHIVADA' WHERE id = ?", RUTA_ID);

        HttpResponse<String> response = get("/api/inscripciones/actual");

        thenActualShouldHideArchivedRoute(response);
    }

    @Test
    @DisplayName("OpenAPI expone los contratos de inscripción con sesión y CSRF")
    void openApiShouldDocumentEnrollmentEndpoints() throws Exception {
        HttpResponse<String> response = get("/v3/api-docs");

        thenOpenApiContract(json(response));
    }

    // --- arrange ---
    private void givenLogin(String email) throws Exception {
        String body = "{\"email\":\"" + email + "\",\"contrasenia\":\"" + PASSWORD + "\"}";
        assertThat(post("/api/auth/login", body, csrf()).statusCode()).isEqualTo(200);
    }

    // --- act ---
    private HttpResponse<String> crear(String body) throws Exception {
        return post("/api/inscripciones", body, csrf());
    }

    private HttpResponse<String> get(String path) throws Exception {
        return httpClient.send(HttpRequest.newBuilder(uri(path)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(String path, String body, String token) throws Exception {
        return httpClient.send(postRequest(path, body, token), HttpResponse.BodyHandlers.ofString());
    }

    private HttpRequest postRequest(String path, String body, String token) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri(path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body));
        if (token != null) {
            builder.header("X-CSRF-TOKEN", token);
        }
        return builder.build();
    }

    private String csrf() throws Exception {
        return json(get("/api/auth/csrf")).get("token").asText();
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + path);
    }

    private JsonNode json(HttpResponse<String> response) {
        return objectMapper.readTree(response.body());
    }

    private int count(String table) {
        return jdbcTemplate.queryForObject("SELECT count(*) FROM " + table, Integer.class);
    }

    // --- assert ---
    private void thenInitialContractAndDatabase(HttpResponse<String> response) {
        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.headers().firstValue("Cache-Control")).contains("no-store");
        JsonNode result = json(response);
        assertThat(result.get("rutaId").asText()).isEqualTo(RUTA_ID.toString());
        assertThat(result.get("rutaSlug").asText()).isEqualTo("tecnica-a");
        assertThat(result.get("objetivo").asText()).isEqualTo("CAMBIAR");
        assertThat(result.get("metaPersonal").asText()).isEqualTo("Conseguir trabajo remoto");
        assertThat(result.get("estado").asText()).isEqualTo("ACTIVA");
        assertThat(result.get("hitoActualId").asText()).isEqualTo(HITO_ID.toString());
        assertThat(result.get("ritmoMin").asInt()).isEqualTo(20);
        LocalDate iniciada = jdbcTemplate.queryForObject(
                "SELECT iniciada_en::date FROM inscripcion", LocalDate.class);
        assertThat(result.get("fechaLlegadaEstimada").asText()).isEqualTo(iniciada.plusDays(32).toString());
        assertThat(result.has("usuarioId")).isFalse();
        assertThat(result.has("diagnostico")).isFalse();
        assertThat(result.get("progreso").size()).isEqualTo(4);
        assertThat(result.get("progreso").get(0).get("nodoId").asText())
                .isEqualTo("00000000-0000-0000-0000-000000000501");
        assertThat(result.get("progreso").get(1).get("estado").asText()).isEqualTo("DISPONIBLE");
        assertThat(result.get("progreso").get(2).get("estado").asText()).isEqualTo("BLOQUEADO");
        assertThat(result.get("progreso").get(3).get("estado").asText()).isEqualTo("BLOQUEADO");
        assertThat(count("inscripcion")).isEqualTo(1);
        assertThat(count("progreso_nodo")).isEqualTo(4);
        assertThat(jdbcTemplate.queryForObject("SELECT usuario_id FROM inscripcion", UUID.class)).isEqualTo(USUARIO_ID);
        assertThat(jdbcTemplate.queryForList("SELECT dominio FROM progreso_nodo", Double.class))
                .containsOnly(0.0);
        assertThat(jdbcTemplate.queryForList("SELECT nivel FROM progreso_nodo", Integer.class)).containsOnly(0);
        assertThat(jdbcTemplate.queryForList("SELECT cantidad_fallos FROM progreso_nodo", Integer.class))
                .containsOnly(0);
        assertThat(jdbcTemplate.queryForObject("SELECT diagnostico FROM inscripcion", String.class)).isNull();
    }

    private void thenDuplicatePreservesProgress(HttpResponse<String> response) {
        assertThat(response.statusCode()).isEqualTo(409);
        assertThat(count("inscripcion")).isEqualTo(1);
        assertThat(count("progreso_nodo")).isEqualTo(4);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT max(dominio) FROM progreso_nodo", Double.class)).isEqualTo(0.60);
    }

    private void thenNoEnrollmentWasPersisted() {
        assertThat(count("inscripcion")).isZero();
        assertThat(count("progreso_nodo")).isZero();
    }

    private void thenActualContract(HttpResponse<String> response, String id) {
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Cache-Control")).contains("no-store");
        JsonNode result = json(response);
        assertThat(result.get("id").asText()).isEqualTo(id);
        assertThat(result.get("metaPersonal").asText()).isEqualTo("Conseguir trabajo remoto");
        assertThat(result.get("progreso").get(0).get("estado").asText()).isEqualTo("EN_CURSO");
        assertThat(result.get("progreso").get(0).get("dominio").asDouble()).isEqualTo(0.55);
        assertThat(result.get("progreso").size()).isEqualTo(4);
    }

    private void thenOpenApiContract(JsonNode document) {
        JsonNode post = document.get("paths").get("/api/inscripciones").get("post");
        assertThat(post.get("security").size()).isEqualTo(1);
        assertThat(post.get("security").get(0).has("csrf")).isTrue();
        assertThat(post.get("security").get(0).has("sesion")).isTrue();
        assertThat(document.get("paths").get("/api/inscripciones/actual").get("get").get("security").toString())
                .contains("sesion");
        JsonNode properties = document.get("components").get("schemas").get("CrearInscripcionWebRequest")
                .get("properties");
        assertThat(properties.has("rutaId")).isTrue();
        assertThat(properties.has("usuarioId")).isFalse();
        assertThat(properties.has("respuestas")).isFalse();
    }

    private void thenCrearShouldRejectPausedEnrollment(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(409);
        assertThat(count("inscripcion")).isEqualTo(1);
    }

    private void thenCrearShouldAllowNewEnrollmentAfterCompletion(
            String originalId,
            HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(json(response).get("id").asText()).isNotEqualTo(originalId);
        assertThat(count("inscripcion")).isEqualTo(2);
        assertThat(count("progreso_nodo")).isEqualTo(8);
    }

    private void thenCrearShouldHideUnavailableRoutes(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(404);
        thenNoEnrollmentWasPersisted();
    }

    private void thenCrearShouldRejectRouteWithoutContent(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(400);
        thenNoEnrollmentWasPersisted();
    }

    private void thenCrearShouldRejectInvalidRhythm(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(400);
        thenNoEnrollmentWasPersisted();
    }

    private void thenCrearShouldRequireAuthentication(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(401);
        thenNoEnrollmentWasPersisted();
    }

    private void thenCrearShouldRequireCsrf(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(403);
        thenNoEnrollmentWasPersisted();
    }

    private void thenCrearShouldRejectSuspendedUser(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(401);
        thenNoEnrollmentWasPersisted();
    }

    private void thenCrearShouldRollbackEnrollmentWhenProgressFails(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(409);
        thenNoEnrollmentWasPersisted();
    }

    private void thenCrearShouldPreventConcurrentDuplicates(List<Integer> statuses) throws Exception {
        assertThat(statuses).containsExactlyInAnyOrder(201, 409);
        assertThat(count("inscripcion")).isEqualTo(1);
        assertThat(count("progreso_nodo")).isEqualTo(4);
    }

    private void thenCrearShouldKeepUnknownEstimate(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(json(response).get("fechaLlegadaEstimada").isNull()).isTrue();
    }

    private void thenCrearShouldRejectInvalidDuration(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(400);
        thenNoEnrollmentWasPersisted();
    }

    private void thenActualShouldReturn404WhenNotEnrolled(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(404);
    }

    private void thenActualShouldReturn401WhenAnonymous(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(401);
    }

    private void thenActualShouldNotExposeOtherUsersEnrollment(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(404);
    }

    private void thenActualShouldIgnorePausedEnrollment(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(404);
    }

    private void thenActualShouldHideArchivedRoute(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(count("inscripcion")).isEqualTo(1);
    }
}
