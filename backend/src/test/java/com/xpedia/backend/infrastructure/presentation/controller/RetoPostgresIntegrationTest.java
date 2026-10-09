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
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/db/rutas-test.sql", "/db/retos-test.sql"})
class RetoPostgresIntegrationTest extends PostgresRepositoryTestSupport {

    private static final String ID_PREFIX = "00000000-0000-0000-0000-000000000";
    private static final String RUTA_ID = ID_PREFIX + "201";
    private static final String NODO_ID = ID_PREFIX + "501";
    private static final String NODO_SIN_RETOS_ID = ID_PREFIX + "502";
    private static final String NODO_TEMA_ID = ID_PREFIX + "504";
    private static final String RETO_ID = "d3000000-0000-4000-8000-000000000001";
    private static final String RETO_ID_PREFIX = "d3000000-0000-4000-8000-";
    private static final String RUBRICA_GLOBAL_ID = "d1000000-0000-4000-8000-000000000001";
    private static final String PILOTO_RUTA_ID = "b1000000-0000-4000-8000-000000000001";
    private static final String PILOTO_NODO_ID = "b4000000-0000-4000-8000-000000000003";
    private static final String PILOTO_RETO_ID = "b5000000-0000-4000-8000-000000000002";
    private static final String PILOTO_RUBRICA_ID = "b7000000-0000-4000-8000-000000000001";
    private static final String PILOTO_CRITERIO_ID = "b7100000-0000-4000-8000-000000000001";
    private static final String PILOTO_SCRIPT = "db/dev/ruta-piloto.sql";
    private static final int EXTRA_RETOS = 20;
    private static final int TOTAL_RETOS = 23;
    private static final int EXPECTED_STATEMENTS = 5;

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private final HttpClient http = HttpClient.newHttpClient();

    @Test
    @DisplayName("Lista los retos aprobados del nodo en orden estable")
    void listarShouldReturnApprovedRetosInStableOrder() throws Exception {
        HttpResponse<String> response = get(retosPath(RUTA_ID, NODO_ID));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(this.<List<String>>read(response, "$[*].tipo"))
                .containsExactly("ENSAYO", "RETO_PROYECTO", "DESAFIO_REAL");
    }

    @Test
    @DisplayName("Lista los criterios de la rúbrica ordenados por posición")
    void listarShouldReturnRubricaCriteriosOrdered() throws Exception {
        HttpResponse<String> response = get(retosPath(RUTA_ID, NODO_ID));

        assertThat(this.<List<String>>read(response, "$[0].rubrica.criterios[*].nombre"))
                .containsExactly("Claridad", "Empatía", "Próximo paso", "Política");
    }

    @Test
    @DisplayName("Lista la rúbrica con puntaje de aprobación, máximo, criterio eliminatorio y descripción nula")
    void listarShouldReturnRubricaScoresAndFlags() throws Exception {
        HttpResponse<String> response = get(retosPath(RUTA_ID, NODO_ID));

        thenFirstRubricaHasScoresAndFlags(response);
    }

    @Test
    @DisplayName("Lista el máximo ponderado decimal y solo la consigna pública cuando el contenido tiene otros tipos")
    void listarShouldReturnWeightedMaximumAndOnlyPublicContent() throws Exception {
        HttpResponse<String> response = get(retosPath(RUTA_ID, NODO_ID));

        thenSecondRetoHasWeightedMaximumAndPublicContent(response);
    }

    @Test
    @DisplayName("No expone datos internos ni de organizaciones privadas en la lista")
    void listarShouldNotExposeInternalData() throws Exception {
        HttpResponse<String> response = get(retosPath(RUTA_ID, NODO_ID));

        assertThat(response.body()).doesNotContain(
                "SECRETO", "respuestaEsperada", "evaluador", "organizacionId", "revisadoPor", "Privada secreta");
    }

    @Test
    @DisplayName("El detalle de un reto coincide con su elemento de la lista")
    void obtenerShouldMatchListItem() throws Exception {
        HttpResponse<String> detail = get(retoPath(RUTA_ID, NODO_ID, RETO_ID));
        HttpResponse<String> list = get(retosPath(RUTA_ID, NODO_ID));

        assertThat(detail.statusCode()).isEqualTo(200);
        assertThat(this.<Map<String, Object>>read(detail, "$")).isEqualTo(this.<Map<String, Object>>read(list, "$[0]"));
    }

    @Test
    @DisplayName("Devuelve 404 al pedir el detalle de un reto desde otro nodo")
    void obtenerShouldReturn404WhenRetoBelongsToAnotherNodo() throws Exception {
        HttpResponse<String> response = get(retoPath(RUTA_ID, NODO_SIN_RETOS_ID, RETO_ID));

        assertThat(response.statusCode()).isEqualTo(404);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 32, 999})
    @DisplayName("Devuelve 404 cuando el reto está oculto, es inconsistente o está mal formado")
    void obtenerShouldReturn404WhenRetoIsHiddenOrInconsistent(int suffix) throws Exception {
        HttpResponse<String> response = get(retoPath(RUTA_ID, NODO_ID, RETO_ID_PREFIX + String.format("%012d", suffix)));

        assertThat(response.statusCode()).isEqualTo(404);
    }

    @ParameterizedTest
    @ValueSource(strings = {"205", "206", "207", "208", "999"})
    @DisplayName("Devuelve 404 al listar los retos de una ruta oculta o ausente")
    void listarShouldReturn404WhenRutaIsHiddenOrAbsent(String rutaSuffix) throws Exception {
        HttpResponse<String> response = get(retosPath(ID_PREFIX + rutaSuffix, NODO_ID));

        assertThat(response.statusCode()).isEqualTo(404);
    }

    @ParameterizedTest
    @ValueSource(strings = {"205", "206", "207", "208", "999"})
    @DisplayName("Devuelve 404 al pedir el detalle de un reto de una ruta oculta o ausente")
    void obtenerShouldReturn404WhenRutaIsHiddenOrAbsent(String rutaSuffix) throws Exception {
        HttpResponse<String> response = get(retoPath(ID_PREFIX + rutaSuffix, NODO_ID, RETO_ID));

        assertThat(response.statusCode()).isEqualTo(404);
    }

    @ParameterizedTest
    @ValueSource(strings = {"505", "506", "507", "999"})
    @DisplayName("Devuelve 404 al listar los retos de un nodo ajeno, incompatible o ausente")
    void listarShouldReturn404WhenNodoIsForeignOrAbsent(String nodoSuffix) throws Exception {
        HttpResponse<String> response = get(retosPath(RUTA_ID, ID_PREFIX + nodoSuffix));

        assertThat(response.statusCode()).isEqualTo(404);
    }

    @Test
    @DisplayName("Devuelve un array vacío cuando el nodo no tiene retos")
    void listarShouldReturnEmptyArrayWhenNodoHasNoRetos() throws Exception {
        HttpResponse<String> response = get(retosPath(RUTA_ID, NODO_SIN_RETOS_ID));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(this.<List<Object>>read(response, "$")).isEmpty();
    }

    @Test
    @DisplayName("Admite un reto sin hito en un nodo de tipo tema")
    void listarShouldAcceptRetoWithoutHitoInTemaNodo() throws Exception {
        givenRetoMovedToTemaNodoWithoutHito();

        HttpResponse<String> response = get(retosPath(RUTA_ID, NODO_TEMA_ID));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(this.<List<Object>>read(response, "$")).hasSize(1);
    }

    @Test
    @DisplayName("Lista 23 retos con la misma rúbrica usando solo cinco consultas")
    void listarShouldLoadRubricasInBatchWithFiveStatements() throws Exception {
        givenExtraRetosWithSameRubrica();

        ListadoMedido listado = listarContandoSentencias();

        thenAllRetosListedWithFiveStatements(listado);
    }

    @Test
    @DisplayName("OpenAPI documenta la lista, el detalle y los campos de la rúbrica")
    void openApiShouldDocumentListDetailAndRubricaFields() throws Exception {
        HttpResponse<String> response = get("/v3/api-docs");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains(
                "/api/rutas/{rutaId}/nodos/{nodoId}/retos", "retos/{retoId}",
                "puntajeMaximo", "eliminatorio", "DESAFIO_REAL");
    }

    @Test
    @DisplayName("El piloto repetible permite recorrer la lista de retos del nodo")
    void listarShouldReturnPilotoRetoAfterRepeatedLoad() throws Exception {
        givenPilotoLoadedTwiceWithLocalEdit();

        HttpResponse<String> response = get(retosPath(PILOTO_RUTA_ID, PILOTO_NODO_ID));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(this.<List<Object>>read(response, "$")).hasSize(1);
    }

    @Test
    @DisplayName("El piloto repetible conserva la edición local de un criterio de la rúbrica")
    void obtenerShouldKeepLocalCriterioEditAfterRepeatedLoad() throws Exception {
        givenPilotoLoadedTwiceWithLocalEdit();

        HttpResponse<String> response = get(retoPath(PILOTO_RUTA_ID, PILOTO_NODO_ID, PILOTO_RETO_ID));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(this.<String>read(response, "$.rubrica.criterios[0].descripcion")).isEqualTo("Edición local");
    }

    @Test
    @DisplayName("El piloto repetible no duplica la rúbrica ni sus criterios")
    void obtenerShouldNotDuplicateRubricaAfterRepeatedLoad() throws Exception {
        givenPilotoLoadedTwiceWithLocalEdit();

        HttpResponse<String> response = get(retoPath(PILOTO_RUTA_ID, PILOTO_NODO_ID, PILOTO_RETO_ID));

        assertThat(this.<List<Object>>read(response, "$.rubrica.criterios")).hasSize(4);
        assertThat(countPilotoRubricas()).isEqualTo(1);
    }

    // --- arrange ---
    private void givenRetoMovedToTemaNodoWithoutHito() {
        jdbc.update("UPDATE actividad SET nodo_id='" + NODO_TEMA_ID + "', hito_id=NULL WHERE id='" + RETO_ID + "'");
    }

    private void givenExtraRetosWithSameRubrica() {
        for (int i = 0; i < EXTRA_RETOS; i++) {
            jdbc.update("""
                    INSERT INTO actividad(id,ruta_id,nodo_id,rubrica_id,tipo,titulo,contenido,origen,estado_revision)
                    VALUES (?,?::uuid,?::uuid,?::uuid,'ENSAYO','Extra','{"consigna":"Texto"}'::jsonb,'IA','APROBADA')
                    """, UUID.randomUUID(), RUTA_ID, NODO_ID, RUBRICA_GLOBAL_ID);
        }
    }

    private void givenPilotoLoadedTwiceWithLocalEdit() throws Exception {
        runPilotoScript();
        jdbc.update("UPDATE rubrica_criterio SET descripcion='Edición local' WHERE id='" + PILOTO_CRITERIO_ID + "'");
        runPilotoScript();
    }

    // --- act ---
    private HttpResponse<String> get(String uri) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + uri)).GET().build();
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private ListadoMedido listarContandoSentencias() throws Exception {
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        boolean enabled = statistics.isStatisticsEnabled();
        statistics.setStatisticsEnabled(true);
        statistics.clear();
        try {
            HttpResponse<String> response = get(retosPath(RUTA_ID, NODO_ID));
            return new ListadoMedido(response, statistics.getPrepareStatementCount());
        } finally {
            statistics.setStatisticsEnabled(enabled);
        }
    }

    private void runPilotoScript() throws Exception {
        try (Connection connection = jdbc.getDataSource().getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(PILOTO_SCRIPT));
        }
    }

    // --- helpers ---
    private String retosPath(String rutaId, String nodoId) {
        return "/api/rutas/" + rutaId + "/nodos/" + nodoId + "/retos";
    }

    private String retoPath(String rutaId, String nodoId, String retoId) {
        return retosPath(rutaId, nodoId) + "/" + retoId;
    }

    private <T> T read(HttpResponse<String> response, String jsonPath) {
        return JsonPath.read(response.body(), jsonPath);
    }

    private Integer countPilotoRubricas() {
        return jdbc.queryForObject(
                "SELECT count(*) FROM rubrica WHERE id='" + PILOTO_RUBRICA_ID + "'", Integer.class);
    }

    private record ListadoMedido(HttpResponse<String> response, long sentencias) {
    }

    // --- assert ---
    private void thenFirstRubricaHasScoresAndFlags(HttpResponse<String> response) {
        assertThat(this.<Number>read(response, "$[0].rubrica.puntajeAprobacion").doubleValue()).isEqualTo(8);
        assertThat(this.<Number>read(response, "$[0].rubrica.puntajeMaximo").doubleValue()).isEqualTo(12);
        assertThat(this.<Boolean>read(response, "$[0].rubrica.criterios[3].eliminatorio")).isTrue();
        assertThat(this.<Object>read(response, "$[0].rubrica.descripcion")).isNull();
    }

    private void thenSecondRetoHasWeightedMaximumAndPublicContent(HttpResponse<String> response) {
        assertThat(this.<Number>read(response, "$[1].rubrica.puntajeMaximo").doubleValue()).isEqualTo(2.25);
        assertThat(this.<Map<String, Object>>read(response, "$[1].contenido")).containsOnlyKeys("consigna");
    }

    private void thenAllRetosListedWithFiveStatements(ListadoMedido listado) {
        assertThat(listado.response().statusCode()).isEqualTo(200);
        assertThat(this.<List<Object>>read(listado.response(), "$")).hasSize(TOTAL_RETOS);
        assertThat(listado.sentencias()).isEqualTo(EXPECTED_STATEMENTS);
    }
}
