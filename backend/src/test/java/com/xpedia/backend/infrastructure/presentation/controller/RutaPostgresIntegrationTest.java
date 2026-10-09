package com.xpedia.backend.infrastructure.presentation.controller;

import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("postgres-test")
@Testcontainers
@Sql("/db/rutas-test.sql")
class RutaPostgresIntegrationTest {
    @Container
    static final PostgreSQLContainer postgres = new PostgreSQLContainer(
            DockerImageName.parse("pgvector/pgvector:pg17").asCompatibleSubstituteFor("postgres"));

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private final HttpClient http = HttpClient.newHttpClient();

    private HttpResponse<String> consultar(String path) throws Exception {
        return http.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void ejecutaV1YValidaElMapeoContraPostgres() {
        assertThat(jdbc.queryForObject("SELECT success FROM flyway_schema_history WHERE version = '1'", Boolean.class))
                .isTrue();
        assertThat(jdbc.queryForObject("SELECT extname FROM pg_extension WHERE extname = 'vector'", String.class))
                .isEqualTo("vector");
    }

    @Test
    void listaSoloGlobalesPublicadasConOrdenEstableYPaginacion() throws Exception {
        var response = consultar("/api/rutas?size=1&page=1");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<Number>read(response.body(), "$.totalElements").longValue()).isEqualTo(4);
        assertThat(JsonPath.<Number>read(response.body(), "$.totalPages").intValue()).isEqualTo(4);
        assertThat(JsonPath.<String>read(response.body(), "$.content[0].id"))
                .isEqualTo("00000000-0000-0000-0000-000000000202");
        assertThat(JsonPath.<Boolean>read(response.body(), "$.first")).isFalse();
        assertThat(JsonPath.<Boolean>read(response.body(), "$.last")).isFalse();
    }

    @Test
    void filtraPorTipoYObjetivoCombinados() throws Exception {
        var response = consultar("/api/rutas?tipo=CAMBIO_RUBRO&objetivo=CAMBIAR");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<String>>read(response.body(), "$.content[*].slug")).containsExactly("atencion-test");
    }

    @Test
    void filtraPorTipoSinObjetivo() throws Exception {
        var response = consultar("/api/rutas?tipo=TECNICA");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<Number>read(response.body(), "$.totalElements").intValue()).isEqualTo(2);
    }

    @Test
    void filtraPorObjetivoSinTipo() throws Exception {
        var response = consultar("/api/rutas?objetivo=MEJORAR");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<String>>read(response.body(), "$.content[*].slug")).containsExactly("oratoria-test");
    }

    @ParameterizedTest
    @ValueSource(strings = {"?tipo=TECNICA&objetivo=CAMBIAR", "?page=10&size=1"})
    void devuelvePaginaVaciaCuandoNoHayResultados(String query) throws Exception {
        var response = consultar("/api/rutas" + query);
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<Object>>read(response.body(), "$.content")).isEmpty();
    }

    @Test
    void detalleIncluyeDuracionDecimalYValoresDelEsquema() throws Exception {
        var response = consultar("/api/rutas/00000000-0000-0000-0000-000000000201");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<String>read(response.body(), "$.estado")).isEqualTo("PUBLICADA");
        assertThat(JsonPath.<String>read(response.body(), "$.tipo")).isEqualTo("TECNICA");
        assertThat(JsonPath.<Number>read(response.body(), "$.horasEstimadas").doubleValue()).isEqualTo(10.5);
        assertThat(JsonPath.<Number>read(response.body(), "$.ritmoRecomendadoMin").intValue()).isEqualTo(30);
        assertThat(response.body()).doesNotContain("organizacionId", "revisadaPor", "confirmadaPor");
    }

    @Test
    void detalleConservaNulosYValidacionHumana() throws Exception {
        var response = consultar("/api/rutas/00000000-0000-0000-0000-000000000204");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<String>read(response.body(), "$.validacion")).isEqualTo("REVISADA");
        assertThat(JsonPath.<Object>read(response.body(), "$.horasEstimadas")).isNull();
        assertThat(JsonPath.<String>read(response.body(), "$.revisadaEn")).isNotBlank();
    }

    @ParameterizedTest
    @ValueSource(strings = {"205", "206", "207", "208", "999"})
    void noExponeRutasPrivadasNoPublicadasONoExistentes(String suffix) throws Exception {
        var response = consultar("/api/rutas/00000000-0000-0000-0000-000000000" + suffix);
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(JsonPath.<Number>read(response.body(), "$.status").intValue()).isEqualTo(404);
    }

    @Test
    void openApiDocumentaEndpointsYFiltros() throws Exception {
        var response = consultar("/v3/api-docs");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("/api/rutas", "/api/rutas/{id}");
        assertThat(JsonPath.<List<String>>read(response.body(), "$.paths['/api/rutas'].get.parameters[*].name"))
                .contains("tipo", "objetivo", "page", "size");
    }

    @Test
    void listaHitosEnOrdenConEvidenciaYNulosSinDatosEditoriales() throws Exception {
        var response = consultar("/api/rutas/00000000-0000-0000-0000-000000000201/hitos");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<String>>read(response.body(), "$[*].titulo"))
                .containsExactly("Primer hito", "Segundo hito", "Hito sin nodos");
        assertThat(JsonPath.<Object>read(response.body(), "$[0].horasEstimadas")).isNull();
        assertThat(JsonPath.<Boolean>read(response.body(), "$[1].esFinal")).isTrue();
        assertThat(JsonPath.<String>read(response.body(), "$[1].evidenciaEsperada")).isEqualTo("Respuesta evaluada");
        assertThat(response.body()).doesNotContain("estadoPropuesta", "comentarioAjuste", "Hito privado");
    }

    @Test
    void listaNodosConOrdenTemasSinHitoYArraysDePostgres() throws Exception {
        var response = consultar("/api/rutas/00000000-0000-0000-0000-000000000201/nodos");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<String>>read(response.body(), "$[*].codigo")).containsExactly("A1", "A2", "B1", "T1");
        assertThat(JsonPath.<List<String>>read(response.body(), "$[0].palabrasClave"))
                .containsExactly("comunicación", "cliente");
        assertThat(JsonPath.<List<String>>read(response.body(), "$[1].palabrasClave")).isEmpty();
        assertThat(JsonPath.<List<String>>read(response.body(), "$[0].prerrequisitoIds")).isEmpty();
        assertThat(JsonPath.<Object>read(response.body(), "$[1].minutosEstimados")).isNull();
        assertThat(JsonPath.<Object>read(response.body(), "$[3].hitoId")).isNull();
        assertThat(JsonPath.<String>read(response.body(), "$[3].tipo")).isEqualTo("TEMA");
    }

    @Test
    void filtraHitoYConservaPrerrequisitosDeHitosAnteriores() throws Exception {
        var response = consultar("/api/rutas/00000000-0000-0000-0000-000000000201/nodos?hitoId=00000000-0000-0000-0000-000000000402");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<String>>read(response.body(), "$[*].codigo")).containsExactly("B1");
        assertThat(JsonPath.<List<String>>read(response.body(), "$[0].prerrequisitoIds"))
                .containsExactly("00000000-0000-0000-0000-000000000501", "00000000-0000-0000-0000-000000000502");
    }

    @Test
    void noExponeReferenciasDeOtraRutaNiHabilidadesPrivadas() throws Exception {
        var response = consultar("/api/rutas/00000000-0000-0000-0000-000000000201/nodos");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<String>read(response.body(), "$[0].ramaId"))
                .isEqualTo("00000000-0000-0000-0000-000000000601");
        assertThat(JsonPath.<String>read(response.body(), "$[0].habilidadId"))
                .isEqualTo("00000000-0000-0000-0000-000000000701");
        assertThat(JsonPath.<Object>read(response.body(), "$[1].ramaId")).isNull();
        assertThat(JsonPath.<Object>read(response.body(), "$[1].habilidadId")).isNull();
        assertThat(response.body()).doesNotContain("00000000-0000-0000-0000-000000000505",
                "00000000-0000-0000-0000-000000000506", "00000000-0000-0000-0000-000000000507",
                "00000000-0000-0000-0000-000000000602", "00000000-0000-0000-0000-000000000702");
    }

    @ParameterizedTest
    @ValueSource(strings = {"403", "404", "999"})
    void rechazaHitoAjenoONoExistente(String suffix) throws Exception {
        var response = consultar("/api/rutas/00000000-0000-0000-0000-000000000201/nodos?hitoId=00000000-0000-0000-0000-000000000" + suffix);
        assertThat(response.statusCode()).isEqualTo(404);
    }

    @ParameterizedTest
    @ValueSource(strings = {"hitos", "nodos"})
    void rutaPublicadaSinContenidoDevuelveArrayVacio(String resource) throws Exception {
        var response = consultar("/api/rutas/00000000-0000-0000-0000-000000000202/" + resource);
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<Object>>read(response.body(), "$")).isEmpty();
    }

    @Test
    void hitoSinNodosDevuelveArrayVacio() throws Exception {
        var response = consultar("/api/rutas/00000000-0000-0000-0000-000000000201/nodos?hitoId=00000000-0000-0000-0000-000000000405");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<Object>>read(response.body(), "$")).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"205", "206", "207", "208", "999"})
    void contenidosDeRutasOcultasDevuelven404(String suffix) throws Exception {
        for (String resource : List.of("hitos", "nodos")) {
            var response = consultar("/api/rutas/00000000-0000-0000-0000-000000000" + suffix + "/" + resource);
            assertThat(response.statusCode()).isEqualTo(404);
        }
    }

    @Test
    void cargaPrerrequisitosYReferenciasEnLoteSinConsultaPorNodo() throws Exception {
        for (int i = 800; i < 820; i++) {
            jdbc.update("""
                    INSERT INTO nodo (id, ruta_id, hito_id, codigo, titulo, posicion)
                    VALUES (?::uuid, '00000000-0000-0000-0000-000000000201',
                            '00000000-0000-0000-0000-000000000401', ?, 'Nodo extra', 2)
                    """, "00000000-0000-0000-0000-000000000" + i, "EXTRA-" + i);
        }
        var statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        boolean enabled = statistics.isStatisticsEnabled();
        statistics.setStatisticsEnabled(true);
        statistics.clear();
        try {
            var response = consultar("/api/rutas/00000000-0000-0000-0000-000000000201/nodos");
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(JsonPath.<List<Object>>read(response.body(), "$")).hasSize(24);
            assertThat(statistics.getPrepareStatementCount()).isEqualTo(4);
        } finally {
            statistics.setStatisticsEnabled(enabled);
        }
    }

    @Test
    void openApiDocumentaConsultasDeContenidoYFiltroHito() throws Exception {
        var response = consultar("/v3/api-docs");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("/api/rutas/{rutaId}/hitos", "/api/rutas/{rutaId}/nodos");
        assertThat(JsonPath.<List<String>>read(response.body(), "$.paths['/api/rutas/{rutaId}/nodos'].get.parameters[*].name"))
                .contains("rutaId", "hitoId");
    }

    private void cargarPiloto() throws Exception {
        try (var connection = jdbc.getDataSource().getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("db/dev/ruta-piloto.sql"));
        }
    }

    @Test
    void pilotoPuedeCargarseDosVecesSinDuplicarNiBorrarDatosExistentes() throws Exception {
        cargarPiloto();
        var fecha = jdbc.queryForObject("SELECT creado_en FROM ruta WHERE slug = 'demo-atencion-cliente-remota'", java.time.OffsetDateTime.class);
        cargarPiloto();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM ruta", Integer.class)).isEqualTo(9);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM hito WHERE ruta_id = 'b1000000-0000-4000-8000-000000000001'", Integer.class)).isEqualTo(3);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM nodo WHERE ruta_id = 'b1000000-0000-4000-8000-000000000001'", Integer.class)).isEqualTo(6);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM nodo_prerrequisito WHERE nodo_id::text LIKE 'b4000000-%'", Integer.class)).isEqualTo(5);
        assertThat(jdbc.queryForObject("SELECT creado_en FROM ruta WHERE slug = 'demo-atencion-cliente-remota'", java.time.OffsetDateTime.class)).isEqualTo(fecha);
    }

    @Test
    void repetirPilotoConservaEdicionesLocales() throws Exception {
        cargarPiloto();
        jdbc.update("UPDATE hito SET titulo = 'Título editado' WHERE id = 'b2000000-0000-4000-8000-000000000001'");
        cargarPiloto();
        assertThat(jdbc.queryForObject("SELECT titulo FROM hito WHERE id = 'b2000000-0000-4000-8000-000000000001'", String.class)).isEqualTo("Título editado");
    }

    @Test
    void errorEnPilotoRevierteTodaLaCarga() throws Exception {
        String sql = new ClassPathResource("db/dev/ruta-piloto.sql").getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        try (var connection = jdbc.getDataSource().getConnection()) {
            assertThatThrownBy(() -> ScriptUtils.executeSqlScript(connection,
                    new ByteArrayResource(sql.replace("COMMIT;", "SELECT 1 / 0; COMMIT;").getBytes(java.nio.charset.StandardCharsets.UTF_8))))
                    .isInstanceOf(org.springframework.jdbc.datasource.init.ScriptStatementFailedException.class);
            connection.createStatement().execute("ROLLBACK");
        }
        assertThat(jdbc.queryForObject("SELECT count(*) FROM ruta", Integer.class)).isEqualTo(8);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM nodo WHERE id::text LIKE 'b4000000-%'", Integer.class)).isZero();
    }

    @Test
    void swaggerPuedeRecorrerPilotoConIdsDocumentados() throws Exception {
        cargarPiloto();
        String ruta = "/api/rutas/b1000000-0000-4000-8000-000000000001";
        assertThat(consultar(ruta).statusCode()).isEqualTo(200);
        var hitos = consultar(ruta + "/hitos");
        assertThat(hitos.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<Object>>read(hitos.body(), "$")).hasSize(3);
        var nodos = consultar(ruta + "/nodos?hitoId=b2000000-0000-4000-8000-000000000002");
        assertThat(nodos.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<String>>read(nodos.body(), "$[*].codigo")).containsExactly("DEMO-03", "DEMO-04");
        assertThat(JsonPath.<List<String>>read(nodos.body(), "$[0].prerrequisitoIds"))
                .containsExactly("b4000000-0000-4000-8000-000000000002");
    }
}
