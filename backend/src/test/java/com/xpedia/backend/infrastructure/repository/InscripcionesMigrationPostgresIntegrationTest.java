package com.xpedia.backend.infrastructure.repository;

import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InscripcionesMigrationPostgresIntegrationTest extends PostgresRepositoryTestSupport {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private Environment environment;

    @Test
    @DisplayName("V3 agrega objetivo sin inventarlo ni alterar inscripciones, progreso o migraciones previas")
    void migrationShouldPreserveExistingEnrollmentAndProgress() {
        DriverManagerDataSource dataSource = givenDatabaseAtV2();
        JdbcTemplate database = new JdbcTemplate(dataSource);
        givenExistingEnrollment(database);
        List<Map<String, Object>> checksums = database.queryForList(
                "SELECT version, checksum FROM flyway_schema_history WHERE version IN ('1','2') ORDER BY version");
        Map<String, Object> inscriptionBefore = database.queryForMap("SELECT * FROM inscripcion");
        Map<String, Object> progressBefore = database.queryForMap("SELECT * FROM progreso_nodo");

        migrate(dataSource, "3");

        thenDataAndChecksumsWerePreserved(database, inscriptionBefore, progressBefore, checksums);
    }

    // --- arrange ---
    private DriverManagerDataSource givenDatabaseAtV2() {
        String name = "inscripcion_migration_" + UUID.randomUUID().toString().replace("-", "");
        jdbcTemplate.execute("CREATE DATABASE " + name);
        String current = environment.getRequiredProperty("spring.datasource.url");
        String url = current.substring(0, current.lastIndexOf('/') + 1) + name;
        DriverManagerDataSource dataSource = new DriverManagerDataSource(url,
                environment.getRequiredProperty("spring.datasource.username"),
                environment.getRequiredProperty("spring.datasource.password"));
        migrate(dataSource, "2");
        return dataSource;
    }

    private void givenExistingEnrollment(JdbcTemplate database) {
        database.execute("""
                INSERT INTO usuario (id, email, nombre)
                VALUES ('d9000000-0000-4000-8000-000000000001', 'anterior@example.com', 'Anterior')
                """);
        database.execute("""
                INSERT INTO ruta (id, slug, titulo, tipo, objetivo, meta, estado)
                VALUES ('d1000000-0000-4000-8000-000000000001', 'anterior', 'Anterior',
                        'TECNICA', 'ARRANCAR', 'Meta de la ruta', 'PUBLICADA')
                """);
        database.execute("""
                INSERT INTO hito (id, ruta_id, posicion, titulo)
                VALUES ('d2000000-0000-4000-8000-000000000001',
                        'd1000000-0000-4000-8000-000000000001', 1, 'Inicio')
                """);
        database.execute("""
                INSERT INTO nodo (id, ruta_id, hito_id, codigo, titulo, posicion)
                VALUES ('d4000000-0000-4000-8000-000000000001', 'd1000000-0000-4000-8000-000000000001',
                        'd2000000-0000-4000-8000-000000000001', 'A1', 'Nodo anterior', 1)
                """);
        database.execute("""
                INSERT INTO inscripcion (id, usuario_id, ruta_id, meta_personal, ritmo_min, estado,
                                         hito_actual_id, diagnostico, fecha_llegada_estimada)
                VALUES ('d8000000-0000-4000-8000-000000000001', 'd9000000-0000-4000-8000-000000000001',
                        'd1000000-0000-4000-8000-000000000001', 'Mi meta anterior', 20, 'ACTIVA',
                        'd2000000-0000-4000-8000-000000000001', '{"respuestas":[1,2]}', '2027-01-01')
                """);
        database.execute("""
                INSERT INTO progreso_nodo (inscripcion_id, nodo_id, estado, dominio, nivel, cantidad_fallos,
                                           proximo_repaso_en, intervalo_repaso_dias, motivo_repaso)
                VALUES ('d8000000-0000-4000-8000-000000000001', 'd4000000-0000-4000-8000-000000000001',
                        'EN_CURSO', 0.4, 2, 3, '2026-10-12T10:00:00Z', 3, 'Repaso anterior')
                """);
    }

    // --- act ---
    private void migrate(DriverManagerDataSource dataSource, String version) {
        Flyway.configure().dataSource(dataSource).locations("classpath:db/migration").target(version).load().migrate();
    }

    // --- assert ---
    private void thenDataAndChecksumsWerePreserved(
            JdbcTemplate database, Map<String, Object> before, Map<String, Object> progress,
            List<Map<String, Object>> checksums) {
        Map<String, Object> after = database.queryForMap("SELECT * FROM inscripcion");
        assertThat(after.get("objetivo")).isNull();
        after.remove("objetivo");
        assertThat(after).containsExactlyInAnyOrderEntriesOf(before);
        assertThat(database.queryForMap("SELECT * FROM progreso_nodo")).containsExactlyInAnyOrderEntriesOf(progress);
        assertThat(database.queryForList(
                "SELECT version, checksum FROM flyway_schema_history WHERE version IN ('1','2') ORDER BY version"))
                .isEqualTo(checksums);
        assertThat(database.queryForObject(
                "SELECT count(*) FROM pg_indexes WHERE indexname = 'ix_inscripcion_actual'", Integer.class))
                .isEqualTo(1);
    }
}

