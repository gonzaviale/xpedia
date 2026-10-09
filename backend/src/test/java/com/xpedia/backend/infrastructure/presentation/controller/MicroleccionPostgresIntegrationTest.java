package com.xpedia.backend.infrastructure.presentation.controller;

import com.jayway.jsonpath.JsonPath;
import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/db/rutas-test.sql", "/db/microlecciones-test.sql"})
class MicroleccionPostgresIntegrationTest extends PostgresRepositoryTestSupport {

    private static final String RUTA_ID = "00000000-0000-0000-0000-000000000201";
    private static final String NODO_ID = "00000000-0000-0000-0000-000000000501";
    private static final String NODO_SIN_MATERIAL_ID = "00000000-0000-0000-0000-000000000502";
    private static final String NODO_SIN_HITO_ID = "00000000-0000-0000-0000-000000000504";
    private static final String MICROLECCION_INICIAL_ID = "00000000-0000-0000-0000-000000000911";
    private static final String FUENTE_GLOBAL_ID = "00000000-0000-0000-0000-000000000901";
    private static final String RUTA_PILOTO_ID = "b1000000-0000-4000-8000-000000000001";
    private static final String NODO_PILOTO_ID = "b4000000-0000-4000-8000-000000000003";
    private static final String PILOTO_SCRIPT = "db/dev/ruta-piloto.sql";
    private static final String OPENAPI_PATH_KEY = "/api/rutas/{rutaId}/nodos/{nodoId}/microlecciones";
    private static final String TITULO_INICIAL = "Lección inicial";
    private static final String TITULO_POSTERIOR = "Lección posterior";
    private static final int EXTRA_ACTIVIDADES = 20;
    private static final int TOTAL_CON_EXTRAS = 22;
    private static final long CONSULTAS_ESPERADAS = 4;

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private final HttpClient http = HttpClient.newHttpClient();

    @AfterEach
    void tearDown() {
        entityManagerFactory.unwrap(SessionFactory.class).getStatistics().setStatisticsEnabled(false);
    }

    @Test
    @DisplayName("Devuelve 200 con las microlecciones aprobadas ordenadas por nivel")
    void listarShouldReturnApprovedMaterialOrderedByNivel() throws Exception {
        HttpResponse<String> response = getMicrolecciones(RUTA_ID, NODO_ID);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(readList(response, "$[*].titulo")).containsExactly(TITULO_INICIAL, TITULO_POSTERIOR);
    }

    @Test
    @DisplayName("Entrega el contenido jsonb anidado con sus caracteres especiales")
    void listarShouldReturnNestedJsonContent() throws Exception {
        HttpResponse<String> response = getMicrolecciones(RUTA_ID, NODO_ID);

        assertThat(readString(response, "$[0].contenido.texto")).isEqualTo("Comunicación ñ");
        assertThat(readList(response, "$[0].contenido.pasos")).containsExactly("saludo", "acción");
    }

    @Test
    @DisplayName("Entrega las fuentes globales con su licencia, url y permisos")
    void listarShouldReturnGlobalFuentesWithCitationData() throws Exception {
        HttpResponse<String> response = getMicrolecciones(RUTA_ID, NODO_ID);

        thenFuentesHaveCitationData(response);
    }

    @Test
    @DisplayName("Entrega nulos y sin uso comercial para la fuente solo enlace")
    void listarShouldReturnNullsForLinkOnlyFuente() throws Exception {
        HttpResponse<String> response = getMicrolecciones(RUTA_ID, NODO_ID);

        thenLinkOnlyFuenteHasNullsAndNoCommercialUse(response);
    }

    @Test
    @DisplayName("No expone datos privados ni material no aprobado")
    void listarShouldNotExposePrivateData() throws Exception {
        HttpResponse<String> response = getMicrolecciones(RUTA_ID, NODO_ID);

        assertThat(response.body())
                .doesNotContain("secreta", "secreto", "Confidencial", "revisadoPor", "organizacionId", "correcta");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "00000000-0000-0000-0000-000000000505",
            "00000000-0000-0000-0000-000000000506",
            "00000000-0000-0000-0000-000000000507",
            "00000000-0000-0000-0000-000000000999"})
    @DisplayName("Devuelve 404 cuando el nodo es ajeno, está oculto, es incompatible o no existe")
    void listarShouldReturn404WhenNodoIsNotVisible(String nodoId) throws Exception {
        HttpResponse<String> response = getMicrolecciones(RUTA_ID, nodoId);

        assertThat(response.statusCode()).isEqualTo(404);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "00000000-0000-0000-0000-000000000205",
            "00000000-0000-0000-0000-000000000206",
            "00000000-0000-0000-0000-000000000207",
            "00000000-0000-0000-0000-000000000208",
            "00000000-0000-0000-0000-000000000999"})
    @DisplayName("Devuelve 404 cuando la ruta está oculta o no existe")
    void listarShouldReturn404WhenRutaIsNotVisible(String rutaId) throws Exception {
        HttpResponse<String> response = getMicrolecciones(rutaId, NODO_ID);

        assertThat(response.statusCode()).isEqualTo(404);
    }

    @Test
    @DisplayName("Devuelve 200 con un array vacío cuando el nodo válido no tiene material")
    void listarShouldReturn200WithEmptyArrayWhenNodoHasNoMaterial() throws Exception {
        HttpResponse<String> response = getMicrolecciones(RUTA_ID, NODO_SIN_MATERIAL_ID);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(readList(response, "$")).isEmpty();
    }

    @Test
    @DisplayName("Admite una microlección sin hito en un nodo sin hito")
    void listarShouldAllowMicroleccionWithoutHitoInNodoWithoutHito() throws Exception {
        givenMicroleccionMovedToNodoWithoutHito();

        HttpResponse<String> response = getMicrolecciones(RUTA_ID, NODO_SIN_HITO_ID);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(readList(response, "$[*].titulo")).containsExactly(TITULO_INICIAL);
    }

    @Test
    @DisplayName("Resuelve las fuentes en lote con cuatro consultas aunque haya muchas actividades")
    void listarShouldKeepFourQueriesWithManyActividades() throws Exception {
        givenManyExtraMicrolecciones();
        Statistics statistics = givenStatisticsEnabled();

        HttpResponse<String> response = getMicrolecciones(RUTA_ID, NODO_ID);

        thenFourQueriesWereExecuted(response, statistics);
    }

    @Test
    @DisplayName("Documenta en OpenAPI los dos ids de la ruta")
    void openApiShouldDocumentRutaAndNodoIds() throws Exception {
        HttpResponse<String> response = get("/v3/api-docs");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(readList(response, "$.paths['" + OPENAPI_PATH_KEY + "'].get.parameters[*].name"))
                .containsExactlyInAnyOrder("rutaId", "nodoId");
    }

    @Test
    @DisplayName("Documenta en OpenAPI el contenido de la microlección y de la fuente")
    void openApiShouldDocumentMicroleccionAndFuenteSchemas() throws Exception {
        HttpResponse<String> response = get("/v3/api-docs");

        assertThat(response.body()).contains("FuenteMicroleccionResponse", "permiteUsoComercial", "MicroleccionResponse");
    }

    @Test
    @DisplayName("Consulta la microlección del piloto aunque el script se cargue dos veces")
    void listarShouldReturnPilotMicroleccionAfterLoadingScriptTwice() throws Exception {
        givenPilotScriptLoadedTwice();

        HttpResponse<String> response = getMicrolecciones(RUTA_PILOTO_ID, NODO_PILOTO_ID);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(readList(response, "$")).hasSize(1);
    }

    @Test
    @DisplayName("Entrega el contenido y la licencia de la microlección del piloto")
    void listarShouldReturnPilotContentAndLicense() throws Exception {
        givenPilotScriptLoadedTwice();

        HttpResponse<String> response = getMicrolecciones(RUTA_PILOTO_ID, NODO_PILOTO_ID);

        assertThat(JsonPath.<Boolean>read(response.body(), "$[0].contenido.demo")).isTrue();
        assertThat(readString(response, "$[0].fuentes[0].licencia"))
                .isEqualTo("Material de prueba; sin licencia editorial asignada");
    }

    // --- arrange ---
    private void givenMicroleccionMovedToNodoWithoutHito() {
        jdbc.update("UPDATE actividad SET nodo_id=?::uuid, hito_id=NULL WHERE id=?::uuid",
                NODO_SIN_HITO_ID, MICROLECCION_INICIAL_ID);
    }

    private void givenManyExtraMicrolecciones() {
        for (int i = 0; i < EXTRA_ACTIVIDADES; i++) {
            insertExtraMicroleccion(UUID.randomUUID());
        }
    }

    private void insertExtraMicroleccion(UUID id) {
        jdbc.update("""
                INSERT INTO actividad (id,ruta_id,nodo_id,tipo,titulo,contenido,origen,estado_revision)
                VALUES (?,?::uuid,?::uuid,'MICROLECCION','Extra','{}'::jsonb,'IA','APROBADA')
                """, id, RUTA_ID, NODO_ID);
        jdbc.update("INSERT INTO actividad_fuente (actividad_id,fuente_id) VALUES (?,?::uuid)", id, FUENTE_GLOBAL_ID);
    }

    private Statistics givenStatisticsEnabled() {
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();
        return statistics;
    }

    private void givenPilotScriptLoadedTwice() throws Exception {
        for (int i = 0; i < 2; i++) {
            try (Connection connection = jdbc.getDataSource().getConnection()) {
                ScriptUtils.executeSqlScript(connection, new ClassPathResource(PILOTO_SCRIPT));
            }
        }
    }

    // --- act ---
    private HttpResponse<String> getMicrolecciones(String rutaId, String nodoId) throws Exception {
        return get("/api/rutas/" + rutaId + "/nodos/" + nodoId + "/microlecciones");
    }

    private HttpResponse<String> get(String uri) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + uri)).GET().build();
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    // --- assert ---
    private void thenFuentesHaveCitationData(HttpResponse<String> response) {
        assertThat(readList(response, "$[0].fuentes[*].titulo")).containsExactly("A Fuente global", "B Enlace global");
        assertThat(readString(response, "$[0].fuentes[0].licencia")).isEqualTo("Licencia de prueba");
        assertThat(readString(response, "$[0].fuentes[0].url")).isEqualTo("https://example.org/material");
        assertThat(JsonPath.<Boolean>read(response.body(), "$[0].fuentes[0].permiteUsoComercial")).isTrue();
    }

    private void thenLinkOnlyFuenteHasNullsAndNoCommercialUse(HttpResponse<String> response) {
        assertThat(JsonPath.<Object>read(response.body(), "$[0].fuentes[1].url")).isNull();
        assertThat(JsonPath.<Object>read(response.body(), "$[0].fuentes[1].ubicacion")).isNull();
        assertThat(JsonPath.<Boolean>read(response.body(), "$[0].fuentes[1].permiteUsoComercial")).isFalse();
    }

    private void thenFourQueriesWereExecuted(HttpResponse<String> response, Statistics statistics) {
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(readList(response, "$")).hasSize(TOTAL_CON_EXTRAS);
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(CONSULTAS_ESPERADAS);
    }

    private List<Object> readList(HttpResponse<String> response, String path) {
        return JsonPath.read(response.body(), path);
    }

    private String readString(HttpResponse<String> response, String path) {
        return JsonPath.read(response.body(), path);
    }
}
