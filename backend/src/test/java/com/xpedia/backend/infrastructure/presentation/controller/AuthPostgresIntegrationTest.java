package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.port.ContraseniaPort;
import com.xpedia.backend.support.PostgresRepositoryTestSupport;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.xpedia.backend.support.UsuarioTestData.CONTRASENIA;
import static com.xpedia.backend.support.UsuarioTestData.EMAIL;
import static com.xpedia.backend.support.UsuarioTestData.USUARIO_ID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/db/rutas-test.sql", "/db/usuarios-test.sql"})
class AuthPostgresIntegrationTest extends PostgresRepositoryTestSupport {

    private static final String LOGIN = """
            {"email":"persona@example.com","contrasenia":"Una contraseña segura"}
            """;
    private static final String REGISTRO = """
            {"nombre":"Cuenta nueva","email":" NUEVA@Example.com ","contrasenia":"Una contraseña segura",
             "tipo":"ADMIN_XPEDIA","estado":"SUSPENDIDO"}
            """;

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ContraseniaPort contraseniaPort;

    @Autowired
    private ObjectMapper objectMapper;

    private HttpClient httpClient;

    private CookieManager cookieManager;

    @BeforeEach
    void setUp() {
        cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        httpClient = HttpClient.newBuilder().cookieHandler(cookieManager).build();
        String hash = contraseniaPort.codificar(CONTRASENIA);
        jdbcTemplate.update("UPDATE usuario SET hash_contrasenia = ? WHERE email <> ' Anterior@Example.COM '", hash);
    }

    @Test
    @DisplayName("Registro persiste persona activa normalizada y no inicia sesión automáticamente")
    void registroShouldPersistPersonaWithoutAutoLogin() throws Exception {
        HttpResponse<String> response = post("/api/auth/registro", REGISTRO, csrf());

        thenRegistroShouldPersistPersonaWithoutAutoLogin(response);
    }

    @Test
    @DisplayName("Registro duplicado devuelve 409 y conserva la cuenta original")
    void registroShouldReturn409WhenEmailAlreadyExists() throws Exception {
        String json = REGISTRO.replace("NUEVA@Example.com", "PERSONA@EXAMPLE.COM");
        HttpResponse<String> response = post("/api/auth/registro", json, csrf());

        thenRegistroShouldReturn409WhenEmailAlreadyExists(response);
    }

    @Test
    @DisplayName("Registro rechaza una contraseña con más de 72 bytes Unicode")
    void registroShouldRejectTooManyUtf8Bytes() throws Exception {
        String json = REGISTRO.replace(CONTRASENIA, "ñ".repeat(37));
        HttpResponse<String> response = post("/api/auth/registro", json, csrf());

        thenRegistroShouldRejectTooManyUtf8Bytes(response);
    }

    @Test
    @DisplayName("Login rota la sesión anónima y persiste identidad en PostgreSQL")
    void loginShouldRotateSessionAndPersistIdentity() throws Exception {
        String token = csrf();
        String previousCookie = cookie();
        HttpResponse<String> response = post("/api/auth/login", LOGIN, token);

        thenLoginShouldRotateSessionAndPersistIdentity(previousCookie, response);
    }

    @Test
    @DisplayName("La cookie autenticada permite consultar al usuario actual en otra petición")
    void actualShouldReturnUsuarioFromPersistedSession() throws Exception {
        givenLogin();
        HttpResponse<String> response = get("/api/auth/actual");

        thenActualShouldReturnUsuarioFromPersistedSession(response);
    }

    @Test
    @DisplayName("Sin cookie no se obtiene el usuario de otra sesión")
    void actualShouldReturn401WhenAnonymous() throws Exception {
        HttpResponse<String> response = get("/api/auth/actual");

        thenActualShouldReturn401WhenAnonymous(response);
    }

    @Test
    @DisplayName("Suspender al usuario invalida una sesión que ya estaba autenticada")
    void actualShouldRejectUsuarioSuspendedAfterLogin() throws Exception {
        givenLogin();
        jdbcTemplate.update("UPDATE usuario SET estado = 'SUSPENDIDO' WHERE id = ?", USUARIO_ID);
        HttpResponse<String> response = get("/api/auth/actual");

        thenActualShouldRejectUsuarioSuspendedAfterLogin(response);
    }

    @Test
    @DisplayName("La sesión expirada ya no autentica")
    void actualShouldRejectExpiredSession() throws Exception {
        givenLogin();
        jdbcTemplate.update("UPDATE spring_session SET last_access_time = 0, expiry_time = 0");

        thenActualShouldRejectExpiredSession();
    }

    @Test
    @DisplayName("Logout invalida la sesión persistida y la cookie")
    void logoutShouldRevokePersistedSession() throws Exception {
        givenLogin();
        HttpResponse<String> response = post("/api/auth/logout", "{}", csrf());

        thenLogoutShouldRevokePersistedSession(response);
    }

    @Test
    @DisplayName("Logout anónimo con CSRF válido es idempotente")
    void logoutShouldBeIdempotentWhenAnonymous() throws Exception {
        HttpResponse<String> response = post("/api/auth/logout", "{}", csrf());

        thenLogoutShouldBeIdempotentWhenAnonymous(response);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/auth/registro", "/api/auth/login", "/api/auth/logout"})
    @DisplayName("Las escrituras de acceso exigen token CSRF")
    void postShouldReturn403WhenCsrfIsMissing(String path) throws Exception {
        HttpResponse<String> response = post(path, LOGIN, null);

        assertThat(response.statusCode()).isEqualTo(403);
        assertThat(json(response).get("message").asText()).isEqualTo("Acceso no permitido");
    }

    @Test
    @DisplayName("Un token CSRF de otra sesión no autoriza la escritura")
    void loginShouldRejectForeignCsrfToken() throws Exception {
        String foreignToken = csrf();
        cookieManager.getCookieStore().removeAll();
        csrf();
        HttpResponse<String> response = post("/api/auth/login", LOGIN, foreignToken);

        thenLoginShouldRejectForeignCsrfToken(response);
    }

    @Test
    @DisplayName("El token anterior al login no autoriza logout")
    void logoutShouldRejectPreLoginCsrfToken() throws Exception {
        String previousToken = csrf();

        thenLogoutShouldRejectPreLoginCsrfToken(previousToken);
    }

    @Test
    @DisplayName("Contraseña incorrecta devuelve 401 genérico sin exponer el cuerpo")
    void loginShouldRejectWrongPassword() throws Exception {
        HttpResponse<String> response = post("/api/auth/login", LOGIN.replace(CONTRASENIA, "Otra contraseña"), csrf());

        thenLoginShouldRejectWrongPassword(response);
    }

    @Test
    @DisplayName("Email inexistente devuelve el mismo 401 genérico")
    void loginShouldRejectMissingUsuario() throws Exception {
        HttpResponse<String> response = post("/api/auth/login", LOGIN.replace(EMAIL, "ausente@example.com"), csrf());

        thenLoginShouldRejectMissingUsuario(response);
    }

    @Test
    @DisplayName("Usuario sin hash devuelve el mismo 401 genérico")
    void loginShouldRejectUsuarioWithoutHash() throws Exception {
        jdbcTemplate.update("UPDATE usuario SET hash_contrasenia = NULL WHERE id = ?", USUARIO_ID);
        HttpResponse<String> response = post("/api/auth/login", LOGIN, csrf());

        thenLoginShouldRejectUsuarioWithoutHash(response);
    }

    @Test
    @DisplayName("Usuario suspendido devuelve el mismo 401 genérico")
    void loginShouldRejectSuspendedUsuario() throws Exception {
        jdbcTemplate.update("UPDATE usuario SET estado = 'SUSPENDIDO' WHERE id = ?", USUARIO_ID);
        HttpResponse<String> response = post("/api/auth/login", LOGIN, csrf());

        thenLoginShouldRejectSuspendedUsuario(response);
    }

    @Test
    @DisplayName("La cuenta persona no puede usar el ABM de puestos")
    void puestosShouldReturn403WhenPersona() throws Exception {
        givenLogin();

        thenPuestosShouldReturn403WhenPersona();
    }

    @Test
    @DisplayName("La cuenta administradora puede consultar el ABM de puestos")
    void puestosShouldAllowAdminXpedia() throws Exception {
        thenPuestosShouldAllowAdminXpedia();
    }

    @Test
    @DisplayName("El catálogo sigue siendo público sin sesión")
    void rutasShouldRemainPublic() throws Exception {
        thenRutasShouldRemainPublic();
    }

    @Test
    @DisplayName("OpenAPI expone los cinco endpoints de acceso")
    void openApiShouldDescribeAccessEndpoints() throws Exception {
        JsonNode paths = json(get("/v3/api-docs")).get("paths");

        thenOpenApiShouldDescribeAccessEndpoints(paths);
    }

    @Test
    @DisplayName("CORS permite credenciales y el header CSRF solo al origen configurado")
    void preflightShouldAllowConfiguredOriginAndCsrfHeader() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(uri("/api/auth/login"))
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "POST")
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        thenPreflightShouldAllowConfiguredOriginAndCsrfHeader(response);
    }

    @Test
    @DisplayName("CORS no autoriza un origen ajeno")
    void preflightShouldNotAuthorizeForeignOrigin() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(uri("/api/auth/login")).header("Origin", "https://ajeno.example")
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        thenPreflightShouldNotAuthorizeForeignOrigin(response);
    }

    @Test
    @DisplayName("Dos registros simultáneos del mismo email crean una sola cuenta")
    void registroShouldCreateOnlyOneUsuarioWhenRequestsRace() throws Exception {
        HttpClient first = HttpClient.newBuilder()
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL)).build();
        HttpClient second = HttpClient.newBuilder()
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL)).build();
        HttpRequest firstRequest = registroRequest(first);
        HttpRequest secondRequest = registroRequest(second);
        CompletableFuture<HttpResponse<String>> firstResponse =
                first.sendAsync(firstRequest, HttpResponse.BodyHandlers.ofString());
        CompletableFuture<HttpResponse<String>> secondResponse =
                second.sendAsync(secondRequest, HttpResponse.BodyHandlers.ofString());

        thenRegistroShouldCreateOnlyOneUsuarioWhenRequestsRace(firstResponse, secondResponse);
    }

    // --- arrange ---
    private void givenLogin() throws Exception {
        assertThat(post("/api/auth/login", LOGIN, csrf()).statusCode()).isEqualTo(200);
    }

    // --- act ---
    private HttpResponse<String> get(String path) throws Exception {
        return httpClient.send(HttpRequest.newBuilder(uri(path)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(String path, String body, String csrf) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri(path)).header("Content-Type", "application/json");
        if (csrf != null) {
            builder.header("X-CSRF-TOKEN", csrf);
        }
        return httpClient.send(builder.POST(HttpRequest.BodyPublishers.ofString(body)).build(),
                HttpResponse.BodyHandlers.ofString());
    }

    // --- assert ---
    private void thenGeneric401(HttpResponse<String> response) {
        assertThat(response.statusCode()).isEqualTo(401);
        assertThat(json(response).get("message").asText()).isEqualTo("No se pudo autenticar el acceso");
        assertThat(response.body()).doesNotContain(CONTRASENIA, "hashContrasenia", "{bcrypt}");
    }

    private void thenRegistroShouldPersistPersonaWithoutAutoLogin(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(201);
        JsonNode body = json(response);
        assertThat(body.get("email").asText()).isEqualTo("nueva@example.com");
        assertThat(body.get("nombre").asText()).isEqualTo("Cuenta nueva");
        assertThat(body.get("tipo").asText()).isEqualTo("PERSONA");
        assertThat(body.has("hashContrasenia")).isFalse();
        assertThat(body.has("contrasenia")).isFalse();
        assertThat(jdbcTemplate.queryForObject(
                "SELECT estado FROM usuario WHERE email = 'nueva@example.com'", String.class)).isEqualTo("ACTIVO");
        String hash = jdbcTemplate.queryForObject(
                "SELECT hash_contrasenia FROM usuario WHERE email = 'nueva@example.com'", String.class);
        assertThat(hash).startsWith("{bcrypt}");
        assertThat(contraseniaPort.coincide(CONTRASENIA, hash)).isTrue();
        assertThat(get("/api/auth/actual").statusCode()).isEqualTo(401);
    }

    private void thenRegistroShouldReturn409WhenEmailAlreadyExists(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(409);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM usuario WHERE lower(trim(email)) = ?", Integer.class, EMAIL)).isEqualTo(1);
    }

    private void thenRegistroShouldRejectTooManyUtf8Bytes(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(jdbcTemplate.queryForObject("SELECT count(*) FROM usuario", Integer.class)).isEqualTo(3);
    }

    private void thenLoginShouldRotateSessionAndPersistIdentity(
            String previousCookie, HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(cookie()).isNotEqualTo(previousCookie);
        assertThat(json(response).get("id").asText()).isEqualTo(USUARIO_ID.toString());
        assertThat(response.headers().firstValue("Cache-Control")).contains("no-store");
        assertThat(response.headers().allValues("Set-Cookie").toString()).contains("HttpOnly", "SameSite=Lax");
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM spring_session WHERE principal_name = ?", Integer.class, USUARIO_ID.toString()))
                .isEqualTo(1);
    }

    private void thenActualShouldReturnUsuarioFromPersistedSession(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(json(response).get("id").asText()).isEqualTo(USUARIO_ID.toString());
        assertThat(response.body()).doesNotContain("hashContrasenia", CONTRASENIA);
    }

    private void thenActualShouldReturn401WhenAnonymous(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(401);
        assertThat(json(response).get("status").asInt()).isEqualTo(401);
        assertThat(response.headers().firstValue("Location")).isEmpty();
    }

    private void thenActualShouldRejectUsuarioSuspendedAfterLogin(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(401);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM spring_session WHERE principal_name = ?", Integer.class, USUARIO_ID.toString()))
                .isZero();
    }

    private void thenActualShouldRejectExpiredSession() throws Exception {
        assertThat(get("/api/auth/actual").statusCode()).isEqualTo(401);
    }

    private void thenLogoutShouldRevokePersistedSession(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(204);
        assertThat(response.headers().allValues("Set-Cookie").toString()).contains("Max-Age=0");
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM spring_session WHERE principal_name = ?", Integer.class, USUARIO_ID.toString()))
                .isZero();
        assertThat(get("/api/auth/actual").statusCode()).isEqualTo(401);
    }

    private void thenLogoutShouldBeIdempotentWhenAnonymous(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(204);
    }

    private void thenLoginShouldRejectForeignCsrfToken(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(403);
    }

    private void thenLogoutShouldRejectPreLoginCsrfToken(String previousToken) throws Exception {
        assertThat(post("/api/auth/login", LOGIN, previousToken).statusCode()).isEqualTo(200);
        HttpResponse<String> response = post("/api/auth/logout", "{}", previousToken);
        assertThat(response.statusCode()).isEqualTo(403);
    }

    private void thenLoginShouldRejectWrongPassword(HttpResponse<String> response) throws Exception {
        thenGeneric401(response);
    }

    private void thenLoginShouldRejectMissingUsuario(HttpResponse<String> response) throws Exception {
        thenGeneric401(response);
    }

    private void thenLoginShouldRejectUsuarioWithoutHash(HttpResponse<String> response) throws Exception {
        thenGeneric401(response);
    }

    private void thenLoginShouldRejectSuspendedUsuario(HttpResponse<String> response) throws Exception {
        thenGeneric401(response);
    }

    private void thenPuestosShouldReturn403WhenPersona() throws Exception {
        assertThat(get("/api/puestos").statusCode()).isEqualTo(403);
    }

    private void thenPuestosShouldAllowAdminXpedia() throws Exception {
        assertThat(post("/api/auth/login", LOGIN.replace(EMAIL, "admin@example.com"),
                csrf()).statusCode()).isEqualTo(200);
        assertThat(get("/api/puestos").statusCode()).isEqualTo(200);
    }

    private void thenRutasShouldRemainPublic() throws Exception {
        assertThat(get("/api/rutas").statusCode()).isEqualTo(200);
    }

    private void thenOpenApiShouldDescribeAccessEndpoints(JsonNode paths) throws Exception {
        assertThat(paths.has("/api/auth/registro")).isTrue();
        assertThat(paths.has("/api/auth/login")).isTrue();
        assertThat(paths.has("/api/auth/actual")).isTrue();
        assertThat(paths.has("/api/auth/logout")).isTrue();
        assertThat(paths.has("/api/auth/csrf")).isTrue();
    }

    private void thenPreflightShouldAllowConfiguredOriginAndCsrfHeader(HttpResponse<String> response) throws Exception {
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Access-Control-Allow-Credentials")).contains("true");
        assertThat(response.headers().firstValue("Access-Control-Allow-Headers").orElseThrow())
                .contains("X-CSRF-TOKEN");
    }

    private void thenPreflightShouldNotAuthorizeForeignOrigin(HttpResponse<String> response) throws Exception {
        assertThat(response.headers().firstValue("Access-Control-Allow-Origin")).isEmpty();
    }

    private void thenRegistroShouldCreateOnlyOneUsuarioWhenRequestsRace(
            CompletableFuture<HttpResponse<String>> firstResponse,
            CompletableFuture<HttpResponse<String>> secondResponse) throws Exception {
        assertThat(List.of(firstResponse.join().statusCode(), secondResponse.join().statusCode()))
                .containsExactlyInAnyOrder(201, 409);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM usuario WHERE email = 'nueva@example.com'", Integer.class)).isEqualTo(1);
    }

    // --- helpers ---
    private HttpRequest registroRequest(HttpClient client) throws Exception {
        HttpResponse<String> response = client.send(HttpRequest.newBuilder(uri("/api/auth/csrf")).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        String token = json(response).get("token").asText();
        return HttpRequest.newBuilder(uri("/api/auth/registro"))
                .header("Content-Type", "application/json").header("X-CSRF-TOKEN", token)
                .POST(HttpRequest.BodyPublishers.ofString(REGISTRO)).build();
    }

    private String csrf() throws Exception {
        HttpResponse<String> response = get("/api/auth/csrf");
        assertThat(response.statusCode()).isEqualTo(200);
        return json(response).get("token").asText();
    }

    private String cookie() {
        return cookieManager.getCookieStore().getCookies().stream()
                .filter(cookie -> cookie.getName().equals("SESSION")).findFirst().orElseThrow().getValue();
    }

    private JsonNode json(HttpResponse<String> response) {
        return objectMapper.readTree(response.body());
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + path);
    }
}
