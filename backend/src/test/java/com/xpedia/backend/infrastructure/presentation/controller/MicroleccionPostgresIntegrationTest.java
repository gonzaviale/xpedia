package com.xpedia.backend.infrastructure.presentation.controller;

import com.jayway.jsonpath.JsonPath;
import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
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
import java.net.http.*;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/db/rutas-test.sql", "/db/microlecciones-test.sql"})
class MicroleccionPostgresIntegrationTest extends PostgresRepositoryTestSupport {
    @LocalServerPort private int port;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private EntityManagerFactory entityManagerFactory;
    private final HttpClient http = HttpClient.newHttpClient();
    private final String root = "/api/rutas/00000000-0000-0000-0000-000000000201/nodos/";
    private final String path = root + "00000000-0000-0000-0000-000000000501/microlecciones";

    private HttpResponse<String> get(String uri) throws Exception {
        return http.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + uri)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test void entregaMaterialAprobadoConJsonAnidadoYCitasCompletas() throws Exception {
        var result = get(path);
        assertThat(result.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<String>>read(result.body(), "$[*].titulo")).containsExactly("Lección inicial", "Lección posterior");
        assertThat(JsonPath.<String>read(result.body(), "$[0].contenido.texto")).isEqualTo("Comunicación ñ");
        assertThat(JsonPath.<List<String>>read(result.body(), "$[0].contenido.pasos")).containsExactly("saludo", "acción");
        assertThat(JsonPath.<List<String>>read(result.body(), "$[0].fuentes[*].titulo")).containsExactly("A Fuente global", "B Enlace global");
        assertThat(JsonPath.<String>read(result.body(), "$[0].fuentes[0].licencia")).isEqualTo("Licencia de prueba");
        assertThat(JsonPath.<String>read(result.body(), "$[0].fuentes[0].url")).isEqualTo("https://example.org/material");
        assertThat(JsonPath.<Boolean>read(result.body(), "$[0].fuentes[0].permiteUsoComercial")).isTrue();
        assertThat(JsonPath.<Object>read(result.body(), "$[0].fuentes[1].url")).isNull();
        assertThat(JsonPath.<Object>read(result.body(), "$[0].fuentes[1].ubicacion")).isNull();
        assertThat(JsonPath.<Boolean>read(result.body(), "$[0].fuentes[1].permiteUsoComercial")).isFalse();
        assertThat(result.body()).doesNotContain("secreta", "secreto", "Confidencial", "revisadoPor", "organizacionId", "correcta");
    }

    @ParameterizedTest @ValueSource(strings = {"505", "506", "507", "999"})
    void nodoAjenoOcultoIncompatibleOAusenteDevuelve404(String suffix) throws Exception {
        assertThat(get(root + "00000000-0000-0000-0000-000000000" + suffix + "/microlecciones").statusCode()).isEqualTo(404);
    }

    @ParameterizedTest @ValueSource(strings = {"205", "206", "207", "208", "999"})
    void rutaOcultaOAusenteDevuelve404(String suffix) throws Exception {
        assertThat(get(path.replace("000000000201", "000000000" + suffix)).statusCode()).isEqualTo(404);
    }

    @Test void nodoValidoSinMaterialDevuelve200ConArrayVacio() throws Exception {
        var result = get(path.replace("000000000501", "000000000502"));
        assertThat(result.statusCode()).isEqualTo(200); assertThat(JsonPath.<List<Object>>read(result.body(), "$")).isEmpty();
    }

    @Test void temaSinHitoAdmiteMicroleccionSinHito() throws Exception {
        jdbc.update("UPDATE actividad SET nodo_id='00000000-0000-0000-0000-000000000504', hito_id=NULL WHERE id='00000000-0000-0000-0000-000000000911'");
        var result = get(path.replace("000000000501", "000000000504"));
        assertThat(result.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<String>>read(result.body(), "$[*].titulo")).containsExactly("Lección inicial");
    }

    @Test void fuentesEnLoteMantienenCuatroConsultasConMuchasActividades() throws Exception {
        for (int i = 0; i < 20; i++) {
            var id = UUID.randomUUID();
            jdbc.update("""
                    INSERT INTO actividad (id,ruta_id,nodo_id,tipo,titulo,contenido,origen,estado_revision)
                    VALUES (?,'00000000-0000-0000-0000-000000000201','00000000-0000-0000-0000-000000000501',
                            'MICROLECCION','Extra','{}'::jsonb,'IA','APROBADA')
                    """, id);
            jdbc.update("INSERT INTO actividad_fuente (actividad_id,fuente_id) VALUES (?,'00000000-0000-0000-0000-000000000901')", id);
        }
        var statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        boolean enabled = statistics.isStatisticsEnabled(); statistics.setStatisticsEnabled(true); statistics.clear();
        try {
            var result = get(path); assertThat(result.statusCode()).isEqualTo(200);
            assertThat(JsonPath.<List<Object>>read(result.body(), "$")).hasSize(22);
            assertThat(statistics.getPrepareStatementCount()).isEqualTo(4);
        } finally { statistics.setStatisticsEnabled(enabled); }
    }

    @Test void openApiDocumentaLosDosIdsYElContenido() throws Exception {
        var result = get("/v3/api-docs"); assertThat(result.statusCode()).isEqualTo(200);
        String key = "/api/rutas/{rutaId}/nodos/{nodoId}/microlecciones";
        assertThat(JsonPath.<List<String>>read(result.body(), "$.paths['" + key + "'].get.parameters[*].name")).containsExactlyInAnyOrder("rutaId", "nodoId");
        assertThat(result.body()).contains("FuenteMicroleccionResponse", "permiteUsoComercial", "MicroleccionResponse");
    }

    @Test void pilotoConMicroleccionPuedeCargarseDosVecesYConsultarse() throws Exception {
        for (int i = 0; i < 2; i++) {
            try (var connection = jdbc.getDataSource().getConnection()) {
                ScriptUtils.executeSqlScript(connection, new ClassPathResource("db/dev/ruta-piloto.sql"));
            }
        }
        var result = get("/api/rutas/b1000000-0000-4000-8000-000000000001/nodos/b4000000-0000-4000-8000-000000000003/microlecciones");
        assertThat(result.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<Object>>read(result.body(), "$")).hasSize(1);
        assertThat(JsonPath.<Boolean>read(result.body(), "$[0].contenido.demo")).isTrue();
        assertThat(JsonPath.<String>read(result.body(), "$[0].fuentes[0].licencia")).isEqualTo("Material de prueba; sin licencia editorial asignada");
    }
}
