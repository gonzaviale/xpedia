package com.xpedia.backend.infrastructure.presentation.controller;

import com.jayway.jsonpath.JsonPath;
import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptStatementFailedException;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RutaPostgresIntegrationTest extends PostgresRepositoryTestSupport {

    private static final String ID_PREFIX = "00000000-0000-0000-0000-000000000";
    private static final String RUTA_ID = ID_PREFIX + "201";
    private static final String RUTA_SIN_CONTENIDO_ID = ID_PREFIX + "202";
    private static final String RUTA_CON_DETALLE_NULO_ID = ID_PREFIX + "204";
    private static final String HITO_SEGUNDO_ID = ID_PREFIX + "402";
    private static final String HITO_SIN_NODOS_ID = ID_PREFIX + "405";
    private static final String NODO_A1_ID = ID_PREFIX + "501";
    private static final String NODO_A2_ID = ID_PREFIX + "502";
    private static final String RAMA_GLOBAL_ID = ID_PREFIX + "601";
    private static final String HABILIDAD_GLOBAL_ID = ID_PREFIX + "701";
    private static final String HITO_ID_PARAM = "/nodos?hitoId=";
    private static final int NODOS_EXTRA_DESDE = 800;
    private static final int NODOS_EXTRA_HASTA = 820;
    private static final int NODOS_TOTALES_CON_EXTRAS = 24;
    private static final int CONSULTAS_ESPERADAS_EN_LOTE = 4;

    private static final String PILOTO_SCRIPT = "db/dev/ruta-piloto.sql";
    private static final String PILOTO_RUTA_ID = "b1000000-0000-4000-8000-000000000001";
    private static final String PILOTO_HITO_1_ID = "b2000000-0000-4000-8000-000000000001";
    private static final String PILOTO_HITO_2_ID = "b2000000-0000-4000-8000-000000000002";
    private static final String PILOTO_NODO_2_ID = "b4000000-0000-4000-8000-000000000002";
    private static final String PILOTO_SLUG = "demo-atencion-cliente-remota";
    private static final String TITULO_EDITADO = "Título editado";

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private final HttpClient http = HttpClient.newHttpClient();

    @Test
    @DisplayName("Ejecuta la migración V1 y habilita la extensión vector en Postgres")
    void flywayShouldRunV1AndEnableVectorExtension() {
        assertThat(jdbc.queryForObject("SELECT success FROM flyway_schema_history WHERE version = '1'", Boolean.class))
                .isTrue();
        assertThat(jdbc.queryForObject("SELECT extname FROM pg_extension WHERE extname = 'vector'", String.class))
                .isEqualTo("vector");
    }

    @Test
    @DisplayName("Lista solo rutas globales publicadas con orden estable y paginación")
    void listarShouldReturnOnlyPublishedGlobalRutasWithStableOrderAndPaging() throws Exception {
        HttpResponse<String> response = consultar("/api/rutas?size=1&page=1");

        thenListIsSecondPageOfPublishedGlobalRutas(response);
    }

    @Test
    @DisplayName("Filtra por tipo y objetivo combinados")
    void listarShouldFilterByTipoAndObjetivoCombined() throws Exception {
        HttpResponse<String> response = consultar("/api/rutas?tipo=CAMBIO_RUBRO&objetivo=CAMBIAR");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(slugs(response)).containsExactly("atencion-test");
    }

    @Test
    @DisplayName("Filtra por tipo sin objetivo")
    void listarShouldFilterByTipoWithoutObjetivo() throws Exception {
        HttpResponse<String> response = consultar("/api/rutas?tipo=TECNICA");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<Number>read(response.body(), "$.totalElements").intValue()).isEqualTo(2);
    }

    @Test
    @DisplayName("Filtra por objetivo sin tipo")
    void listarShouldFilterByObjetivoWithoutTipo() throws Exception {
        HttpResponse<String> response = consultar("/api/rutas?objetivo=MEJORAR");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(slugs(response)).containsExactly("oratoria-test");
    }

    @ParameterizedTest
    @ValueSource(strings = {"?tipo=TECNICA&objetivo=CAMBIAR", "?page=10&size=1"})
    @DisplayName("Devuelve la página vacía cuando no hay resultados")
    void listarShouldReturnEmptyPageWhenThereAreNoResults(String query) throws Exception {
        HttpResponse<String> response = consultar("/api/rutas" + query);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<Object>>read(response.body(), "$.content")).isEmpty();
    }

    @Test
    @DisplayName("El detalle incluye la duración decimal y los valores del esquema sin datos internos")
    void obtenerShouldIncludeDecimalDurationAndSchemaValuesWithoutInternalFields() throws Exception {
        HttpResponse<String> response = consultar("/api/rutas/" + RUTA_ID);

        thenDetailHasSchemaValues(response);
    }

    @Test
    @DisplayName("El detalle conserva los nulos y la validación humana")
    void obtenerShouldKeepNullsAndHumanValidation() throws Exception {
        HttpResponse<String> response = consultar("/api/rutas/" + RUTA_CON_DETALLE_NULO_ID);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<String>read(response.body(), "$.validacion")).isEqualTo("REVISADA");
        assertThat(JsonPath.<Object>read(response.body(), "$.horasEstimadas")).isNull();
        assertThat(JsonPath.<String>read(response.body(), "$.revisadaEn")).isNotBlank();
    }

    @ParameterizedTest
    @ValueSource(strings = {"205", "206", "207", "208", "999"})
    @DisplayName("Devuelve 404 para rutas privadas, no publicadas o inexistentes")
    void obtenerShouldReturn404WhenRutaIsPrivateUnpublishedOrMissing(String suffix) throws Exception {
        HttpResponse<String> response = consultar("/api/rutas/" + ID_PREFIX + suffix);

        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(JsonPath.<Number>read(response.body(), "$.status").intValue()).isEqualTo(404);
    }

    @Test
    @DisplayName("OpenAPI documenta los endpoints de rutas y sus filtros")
    void openApiShouldDocumentRutasEndpointsAndFilters() throws Exception {
        HttpResponse<String> response = consultar("/v3/api-docs");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("/api/rutas", "/api/rutas/{id}");
        assertThat(JsonPath.<List<String>>read(response.body(), "$.paths['/api/rutas'].get.parameters[*].name"))
                .contains("tipo", "objetivo", "page", "size");
    }

    @Test
    @DisplayName("Lista los hitos en orden con evidencia y nulos, sin datos editoriales")
    void listarHitosShouldReturnOrderedHitosWithoutEditorialData() throws Exception {
        HttpResponse<String> response = consultar("/api/rutas/" + RUTA_ID + "/hitos");

        thenHitosAreOrderedWithoutEditorialData(response);
    }

    @Test
    @DisplayName("Lista los nodos con orden, temas sin hito y arrays de Postgres")
    void listarNodosShouldReturnOrderedNodosWithTemasWithoutHitoAndPostgresArrays() throws Exception {
        HttpResponse<String> response = consultar("/api/rutas/" + RUTA_ID + "/nodos");

        thenNodosAreOrderedWithArraysAndTemaWithoutHito(response);
    }

    @Test
    @DisplayName("Filtrar por hito conserva los prerrequisitos de hitos anteriores")
    void listarNodosShouldKeepPrerequisitesFromPreviousHitosWhenFilteringByHito() throws Exception {
        HttpResponse<String> response = consultar("/api/rutas/" + RUTA_ID + HITO_ID_PARAM + HITO_SEGUNDO_ID);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<String>>read(response.body(), "$[*].codigo")).containsExactly("B1");
        assertThat(JsonPath.<List<String>>read(response.body(), "$[0].prerrequisitoIds"))
                .containsExactly(NODO_A1_ID, NODO_A2_ID);
    }

    @Test
    @DisplayName("No expone referencias de otra ruta ni habilidades privadas")
    void listarNodosShouldNotExposeForeignReferencesNorPrivateSkills() throws Exception {
        HttpResponse<String> response = consultar("/api/rutas/" + RUTA_ID + "/nodos");

        thenNodosOmitForeignReferences(response);
    }

    @ParameterizedTest
    @ValueSource(strings = {"403", "404", "999"})
    @DisplayName("Devuelve 404 cuando el hito es de otra ruta o no existe")
    void listarNodosShouldReturn404WhenHitoIsForeignOrMissing(String suffix) throws Exception {
        HttpResponse<String> response = consultar("/api/rutas/" + RUTA_ID + HITO_ID_PARAM + ID_PREFIX + suffix);

        assertThat(response.statusCode()).isEqualTo(404);
    }

    @ParameterizedTest
    @ValueSource(strings = {"hitos", "nodos"})
    @DisplayName("Devuelve un array vacío cuando la ruta publicada no tiene contenido")
    void contenidoShouldReturnEmptyArrayWhenRutaHasNoContent(String resource) throws Exception {
        HttpResponse<String> response = consultar("/api/rutas/" + RUTA_SIN_CONTENIDO_ID + "/" + resource);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<Object>>read(response.body(), "$")).isEmpty();
    }

    @Test
    @DisplayName("Devuelve un array vacío cuando el hito no tiene nodos")
    void listarNodosShouldReturnEmptyArrayWhenHitoHasNoNodos() throws Exception {
        HttpResponse<String> response = consultar("/api/rutas/" + RUTA_ID + HITO_ID_PARAM + HITO_SIN_NODOS_ID);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<Object>>read(response.body(), "$")).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"205", "206", "207", "208", "999"})
    @DisplayName("Devuelve 404 en los hitos de rutas ocultas o inexistentes")
    void listarHitosShouldReturn404WhenRutaIsHiddenOrMissing(String suffix) throws Exception {
        HttpResponse<String> response = consultar("/api/rutas/" + ID_PREFIX + suffix + "/hitos");

        assertThat(response.statusCode()).isEqualTo(404);
    }

    @ParameterizedTest
    @ValueSource(strings = {"205", "206", "207", "208", "999"})
    @DisplayName("Devuelve 404 en los nodos de rutas ocultas o inexistentes")
    void listarNodosShouldReturn404WhenRutaIsHiddenOrMissing(String suffix) throws Exception {
        HttpResponse<String> response = consultar("/api/rutas/" + ID_PREFIX + suffix + "/nodos");

        assertThat(response.statusCode()).isEqualTo(404);
    }

    @Test
    @DisplayName("Carga prerrequisitos y referencias en lote sin una consulta por nodo")
    void listarNodosShouldLoadPrerequisitesAndReferencesInBatch() throws Exception {
        givenExtraNodos();
        Statistics statistics = givenStatisticsEnabled();

        HttpResponse<String> response = consultar("/api/rutas/" + RUTA_ID + "/nodos");

        thenNodosWereLoadedInFixedNumberOfQueries(response, statistics);
    }

    @Test
    @DisplayName("OpenAPI documenta las consultas de contenido y el filtro por hito")
    void openApiShouldDocumentContentQueriesAndHitoFilter() throws Exception {
        HttpResponse<String> response = consultar("/v3/api-docs");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("/api/rutas/{rutaId}/hitos", "/api/rutas/{rutaId}/nodos");
        assertThat(JsonPath.<List<String>>read(response.body(),
                "$.paths['/api/rutas/{rutaId}/nodos'].get.parameters[*].name")).contains("rutaId", "hitoId");
    }

    @Test
    @DisplayName("El piloto puede cargarse dos veces sin duplicar ni borrar datos existentes")
    void pilotoShouldBeLoadableTwiceWithoutDuplicatingNorDeletingData() throws Exception {
        cargarPiloto();
        OffsetDateTime creadoEn = fechaCreacionPiloto();

        cargarPiloto();

        thenPilotoIsNotDuplicated(creadoEn);
    }

    @Test
    @DisplayName("Repetir la carga del piloto conserva las ediciones locales")
    void pilotoShouldKeepLocalEditsWhenLoadedAgain() throws Exception {
        cargarPiloto();
        editarTituloDelPrimerHito();

        cargarPiloto();

        assertThat(jdbc.queryForObject("SELECT titulo FROM hito WHERE id = '" + PILOTO_HITO_1_ID + "'", String.class))
                .isEqualTo(TITULO_EDITADO);
    }

    @Test
    @DisplayName("Un error en el piloto revierte toda la carga")
    void pilotoShouldRollBackWholeLoadWhenScriptFails() throws Exception {
        String sql = new ClassPathResource(PILOTO_SCRIPT).getContentAsString(StandardCharsets.UTF_8);

        cargarPilotoRoto(sql);

        assertThat(jdbc.queryForObject("SELECT count(*) FROM ruta", Integer.class)).isEqualTo(8);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM nodo WHERE id::text LIKE 'b4000000-%'", Integer.class))
                .isZero();
    }

    @Test
    @DisplayName("Swagger puede recorrer el piloto con los ids documentados")
    void swaggerShouldWalkPilotoWithDocumentedIds() throws Exception {
        cargarPiloto();
        String ruta = "/api/rutas/" + PILOTO_RUTA_ID;

        HttpResponse<String> detalle = consultar(ruta);
        HttpResponse<String> hitos = consultar(ruta + "/hitos");
        HttpResponse<String> nodos = consultar(ruta + HITO_ID_PARAM + PILOTO_HITO_2_ID);

        thenPilotoIsWalkable(detalle, hitos, nodos);
    }

    // --- arrange ---
    private void givenExtraNodos() {
        for (int i = NODOS_EXTRA_DESDE; i < NODOS_EXTRA_HASTA; i++) {
            jdbc.update("""
                    INSERT INTO nodo (id, ruta_id, hito_id, codigo, titulo, posicion)
                    VALUES (?::uuid, '%s', '%s', ?, 'Nodo extra', 2)
                    """.formatted(RUTA_ID, ID_PREFIX + "401"), ID_PREFIX + i, "EXTRA-" + i);
        }
    }

    private Statistics givenStatisticsEnabled() {
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();
        return statistics;
    }

    private void editarTituloDelPrimerHito() {
        jdbc.update("UPDATE hito SET titulo = ? WHERE id = ?::uuid", TITULO_EDITADO, PILOTO_HITO_1_ID);
    }

    // --- act ---
    private HttpResponse<String> consultar(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build();
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void cargarPiloto() throws SQLException {
        try (Connection connection = jdbc.getDataSource().getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(PILOTO_SCRIPT));
        }
    }

    private void cargarPilotoRoto(String sql) throws SQLException {
        ByteArrayResource script = new ByteArrayResource(
                sql.replace("COMMIT;", "SELECT 1 / 0; COMMIT;").getBytes(StandardCharsets.UTF_8));
        try (Connection connection = jdbc.getDataSource().getConnection()) {
            assertThatThrownBy(() -> ScriptUtils.executeSqlScript(connection, script))
                    .isInstanceOf(ScriptStatementFailedException.class);
            connection.createStatement().execute("ROLLBACK");
        }
    }

    private OffsetDateTime fechaCreacionPiloto() {
        return jdbc.queryForObject("SELECT creado_en FROM ruta WHERE slug = '" + PILOTO_SLUG + "'",
                OffsetDateTime.class);
    }

    // --- assert ---
    private void thenListIsSecondPageOfPublishedGlobalRutas(HttpResponse<String> response) {
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<Number>read(response.body(), "$.totalElements").longValue()).isEqualTo(4);
        assertThat(JsonPath.<Number>read(response.body(), "$.totalPages").intValue()).isEqualTo(4);
        assertThat(JsonPath.<String>read(response.body(), "$.content[0].id")).isEqualTo(RUTA_SIN_CONTENIDO_ID);
        assertThat(JsonPath.<Boolean>read(response.body(), "$.first")).isFalse();
        assertThat(JsonPath.<Boolean>read(response.body(), "$.last")).isFalse();
    }

    private void thenDetailHasSchemaValues(HttpResponse<String> response) {
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<String>read(response.body(), "$.estado")).isEqualTo("PUBLICADA");
        assertThat(JsonPath.<String>read(response.body(), "$.tipo")).isEqualTo("TECNICA");
        assertThat(JsonPath.<Number>read(response.body(), "$.horasEstimadas").doubleValue()).isEqualTo(10.5);
        assertThat(JsonPath.<Number>read(response.body(), "$.ritmoRecomendadoMin").intValue()).isEqualTo(30);
        assertThat(response.body()).doesNotContain("organizacionId", "revisadaPor", "confirmadaPor");
    }

    private void thenHitosAreOrderedWithoutEditorialData(HttpResponse<String> response) {
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<String>>read(response.body(), "$[*].titulo"))
                .containsExactly("Primer hito", "Segundo hito", "Hito sin nodos");
        assertThat(JsonPath.<Object>read(response.body(), "$[0].horasEstimadas")).isNull();
        assertThat(JsonPath.<Boolean>read(response.body(), "$[1].esFinal")).isTrue();
        assertThat(JsonPath.<String>read(response.body(), "$[1].evidenciaEsperada")).isEqualTo("Respuesta evaluada");
        assertThat(response.body()).doesNotContain("estadoPropuesta", "comentarioAjuste", "Hito privado");
    }

    private void thenNodosAreOrderedWithArraysAndTemaWithoutHito(HttpResponse<String> response) {
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<String>>read(response.body(), "$[*].codigo"))
                .containsExactly("A1", "A2", "B1", "T1");
        assertThat(JsonPath.<List<String>>read(response.body(), "$[0].palabrasClave"))
                .containsExactly("comunicación", "cliente");
        assertThat(JsonPath.<List<String>>read(response.body(), "$[1].palabrasClave")).isEmpty();
        assertThat(JsonPath.<List<String>>read(response.body(), "$[0].prerrequisitoIds")).isEmpty();
        assertThat(JsonPath.<Object>read(response.body(), "$[1].minutosEstimados")).isNull();
        assertThat(JsonPath.<Object>read(response.body(), "$[3].hitoId")).isNull();
        assertThat(JsonPath.<String>read(response.body(), "$[3].tipo")).isEqualTo("TEMA");
    }

    private void thenNodosOmitForeignReferences(HttpResponse<String> response) {
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<String>read(response.body(), "$[0].ramaId")).isEqualTo(RAMA_GLOBAL_ID);
        assertThat(JsonPath.<String>read(response.body(), "$[0].habilidadId")).isEqualTo(HABILIDAD_GLOBAL_ID);
        assertThat(JsonPath.<Object>read(response.body(), "$[1].ramaId")).isNull();
        assertThat(JsonPath.<Object>read(response.body(), "$[1].habilidadId")).isNull();
        assertThat(response.body()).doesNotContain(ID_PREFIX + "505", ID_PREFIX + "506", ID_PREFIX + "507",
                ID_PREFIX + "602", ID_PREFIX + "702");
    }

    private void thenNodosWereLoadedInFixedNumberOfQueries(HttpResponse<String> response, Statistics statistics) {
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<Object>>read(response.body(), "$")).hasSize(NODOS_TOTALES_CON_EXTRAS);
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(CONSULTAS_ESPERADAS_EN_LOTE);
    }

    private void thenPilotoIsNotDuplicated(OffsetDateTime creadoEn) {
        assertThat(jdbc.queryForObject("SELECT count(*) FROM ruta", Integer.class)).isEqualTo(9);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM hito WHERE ruta_id = '" + PILOTO_RUTA_ID + "'",
                Integer.class)).isEqualTo(3);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM nodo WHERE ruta_id = '" + PILOTO_RUTA_ID + "'",
                Integer.class)).isEqualTo(6);
        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM nodo_prerrequisito WHERE nodo_id::text LIKE 'b4000000-%'", Integer.class))
                .isEqualTo(5);
        assertThat(fechaCreacionPiloto()).isEqualTo(creadoEn);
    }

    private void thenPilotoIsWalkable(HttpResponse<String> detalle, HttpResponse<String> hitos,
                                      HttpResponse<String> nodos) {
        assertThat(detalle.statusCode()).isEqualTo(200);
        assertThat(hitos.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<Object>>read(hitos.body(), "$")).hasSize(3);
        assertThat(nodos.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<String>>read(nodos.body(), "$[*].codigo")).containsExactly("DEMO-03", "DEMO-04");
        assertThat(JsonPath.<List<String>>read(nodos.body(), "$[0].prerrequisitoIds"))
                .containsExactly(PILOTO_NODO_2_ID);
    }

    // --- helpers ---
    private List<String> slugs(HttpResponse<String> response) {
        return JsonPath.read(response.body(), "$.content[*].slug");
    }
}
