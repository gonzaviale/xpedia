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
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/db/rutas-test.sql","/db/retos-test.sql"})
class RetoPostgresIntegrationTest extends PostgresRepositoryTestSupport {
    @LocalServerPort private int port;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private EntityManagerFactory entityManagerFactory;
    private final HttpClient http = HttpClient.newHttpClient();
    private final String path = "/api/rutas/00000000-0000-0000-0000-000000000201/nodos/00000000-0000-0000-0000-000000000501/retos";
    private HttpResponse<String> get(String uri) throws Exception {
        return http.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + uri)).GET().build(),HttpResponse.BodyHandlers.ofString());
    }
    @Test void listaConsignasPublicasRubricasYCriteriosOrdenadosSinDatosInternos() throws Exception {
        var response = get(path); assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<String>>read(response.body(),"$[*].tipo")).containsExactly("ENSAYO","RETO_PROYECTO","DESAFIO_REAL");
        assertThat(JsonPath.<List<String>>read(response.body(),"$[0].rubrica.criterios[*].nombre")).containsExactly("Claridad","Empatía","Próximo paso","Política");
        assertThat(JsonPath.<Number>read(response.body(),"$[0].rubrica.puntajeAprobacion").doubleValue()).isEqualTo(8);
        assertThat(JsonPath.<Number>read(response.body(),"$[0].rubrica.puntajeMaximo").doubleValue()).isEqualTo(12);
        assertThat(JsonPath.<Boolean>read(response.body(),"$[0].rubrica.criterios[3].eliminatorio")).isTrue();
        assertThat(JsonPath.<Object>read(response.body(),"$[0].rubrica.descripcion")).isNull();
        assertThat(JsonPath.<Number>read(response.body(),"$[1].rubrica.puntajeMaximo").doubleValue()).isEqualTo(2.25);
        assertThat(JsonPath.<Map<String,Object>>read(response.body(),"$[1].contenido")).containsOnlyKeys("consigna");
        assertThat(response.body()).doesNotContain("SECRETO","respuestaEsperada","evaluador","organizacionId","revisadoPor","Privada secreta");
    }
    @Test void detalleCoincideConLaListaYRespetaElNodo() throws Exception {
        var response = get(path + "/d3000000-0000-4000-8000-000000000001"); assertThat(response.statusCode()).isEqualTo(200);
        var list = get(path);
        assertThat(JsonPath.<Map<String,Object>>read(response.body(),"$")).isEqualTo(JsonPath.<Map<String,Object>>read(list.body(),"$[0]"));
        assertThat(get(path.replace("000000000501","000000000502") + "/d3000000-0000-4000-8000-000000000001").statusCode()).isEqualTo(404);
    }
    @ParameterizedTest @ValueSource(ints={10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,32,999})
    void detalleOcultoInconsistenteOMalFormadoDevuelve404(int suffix) throws Exception {
        assertThat(get(path + "/d3000000-0000-4000-8000-" + String.format("%012d",suffix)).statusCode()).isEqualTo(404);
    }
    @ParameterizedTest @ValueSource(strings={"205","206","207","208","999"})
    void rutaOcultaOAusenteNoPermiteListaNiDetalle(String suffix) throws Exception {
        String uri=path.replace("000000000201","000000000"+suffix);
        assertThat(get(uri).statusCode()).isEqualTo(404);
        assertThat(get(uri+"/d3000000-0000-4000-8000-000000000001").statusCode()).isEqualTo(404);
    }
    @ParameterizedTest @ValueSource(strings={"505","506","507","999"})
    void nodoAjenoIncompatibleOAUSenteDevuelve404(String suffix) throws Exception {
        assertThat(get(path.replace("000000000501","000000000"+suffix)).statusCode()).isEqualTo(404);
    }
    @Test void nodoSinRetosDevuelveArrayVacioYTemaSinHitoAdmiteConsigna() throws Exception {
        var empty=get(path.replace("000000000501","000000000502")); assertThat(empty.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<Object>>read(empty.body(),"$")).isEmpty();
        jdbc.update("UPDATE actividad SET nodo_id='00000000-0000-0000-0000-000000000504',hito_id=NULL WHERE id='d3000000-0000-4000-8000-000000000001'");
        var tema=get(path.replace("000000000501","000000000504")); assertThat(tema.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<List<Object>>read(tema.body(),"$")).hasSize(1);
    }
    @Test void rubricasYCriteriosSeCarganEnLoteConCincoConsultasPara23Retos() throws Exception {
        for(int i=0;i<20;i++) {
            jdbc.update("""
                INSERT INTO actividad(id,ruta_id,nodo_id,rubrica_id,tipo,titulo,contenido,origen,estado_revision)
                VALUES (?,'00000000-0000-0000-0000-000000000201','00000000-0000-0000-0000-000000000501',
                        'd1000000-0000-4000-8000-000000000001','ENSAYO','Extra','{"consigna":"Texto"}'::jsonb,'IA','APROBADA')
                """,UUID.randomUUID());
        }
        var stats=entityManagerFactory.unwrap(SessionFactory.class).getStatistics(); boolean enabled=stats.isStatisticsEnabled();
        stats.setStatisticsEnabled(true); stats.clear();
        try {
            var result=get(path); assertThat(result.statusCode()).isEqualTo(200);
            assertThat(JsonPath.<List<Object>>read(result.body(),"$")).hasSize(23); assertThat(stats.getPrepareStatementCount()).isEqualTo(5);
        } finally { stats.setStatisticsEnabled(enabled); }
    }
    @Test void openApiDocumentaListaDetalleYCamposDeRubrica() throws Exception {
        var result=get("/v3/api-docs"); assertThat(result.statusCode()).isEqualTo(200);
        assertThat(result.body()).contains("/api/rutas/{rutaId}/nodos/{nodoId}/retos", "retos/{retoId}","puntajeMaximo","eliminatorio","DESAFIO_REAL");
    }
    @Test void pilotoRepetibleConservaEdicionDeRubricaYPermiteRecorrerReto() throws Exception {
        try(var connection=jdbc.getDataSource().getConnection()) { ScriptUtils.executeSqlScript(connection,new ClassPathResource("db/dev/ruta-piloto.sql")); }
        jdbc.update("UPDATE rubrica_criterio SET descripcion='Edición local' WHERE id='b7100000-0000-4000-8000-000000000001'");
        try(var connection=jdbc.getDataSource().getConnection()) { ScriptUtils.executeSqlScript(connection,new ClassPathResource("db/dev/ruta-piloto.sql")); }
        String uri="/api/rutas/b1000000-0000-4000-8000-000000000001/nodos/b4000000-0000-4000-8000-000000000003/retos";
        var list=get(uri); assertThat(list.statusCode()).isEqualTo(200); assertThat(JsonPath.<List<Object>>read(list.body(),"$")).hasSize(1);
        var result=get(uri+"/b5000000-0000-4000-8000-000000000002"); assertThat(result.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<String>read(result.body(),"$.rubrica.criterios[0].descripcion")).isEqualTo("Edición local");
        assertThat(JsonPath.<List<Object>>read(result.body(),"$.rubrica.criterios")).hasSize(4);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM rubrica WHERE id='b7000000-0000-4000-8000-000000000001'",Integer.class)).isEqualTo(1);
    }
}
