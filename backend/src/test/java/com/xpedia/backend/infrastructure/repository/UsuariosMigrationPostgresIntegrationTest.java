package com.xpedia.backend.infrastructure.repository;

import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuariosMigrationPostgresIntegrationTest extends PostgresRepositoryTestSupport {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Environment environment;

    @Test
    @DisplayName("V2 conserva usuarios anteriores y el checksum de V1 al agregar sesiones")
    void migrationShouldPreserveExistingUsuarioAndV1Checksum() {
        DriverManagerDataSource dataSource = givenDatabaseAtV1();
        JdbcTemplate database = new JdbcTemplate(dataSource);
        database.update("INSERT INTO usuario (email, nombre) VALUES (?, ?)", " Anterior@Example.COM ", "Anterior");
        Integer previousChecksum = database.queryForObject(
                "SELECT checksum FROM flyway_schema_history WHERE version = '1'", Integer.class);
        migrate(dataSource, "2");

        thenMigrationShouldPreserveExistingUsuarioAndV1Checksum(database, previousChecksum);
    }

    @Test
    @DisplayName("Si hay emails que colisionan V2 falla y conserva todos los datos")
    void migrationShouldFailWithoutDeletingCollidingEmails() {
        DriverManagerDataSource dataSource = givenDatabaseAtV1();
        JdbcTemplate database = new JdbcTemplate(dataSource);
        database.update("INSERT INTO usuario (email, nombre) VALUES (?, ?)", "persona@example.com", "Primera");
        database.update("INSERT INTO usuario (email, nombre) VALUES (?, ?)", " PERSONA@Example.COM ", "Segunda");

        thenMigrationShouldFailWithoutDeletingCollidingEmails(dataSource, database);
    }

    // --- arrange ---
    private DriverManagerDataSource givenDatabaseAtV1() {
        String name = "auth_migration_" + UUID.randomUUID().toString().replace("-", "");
        jdbcTemplate.execute("CREATE DATABASE " + name);
        String current = environment.getRequiredProperty("spring.datasource.url");
        String url = current.substring(0, current.lastIndexOf('/') + 1) + name;
        DriverManagerDataSource dataSource = new DriverManagerDataSource(url,
                environment.getRequiredProperty("spring.datasource.username"),
                environment.getRequiredProperty("spring.datasource.password"));
        migrate(dataSource, "1");
        return dataSource;
    }

    // --- act ---
    private void migrate(DriverManagerDataSource dataSource, String version) {
        Flyway.configure().dataSource(dataSource).locations("classpath:db/migration").target(version).load().migrate();
    }

    // --- assert ---
    private void thenMigrationShouldPreserveExistingUsuarioAndV1Checksum(
            JdbcTemplate database, Integer previousChecksum) {
        assertThat(database.queryForObject("SELECT email FROM usuario",
                String.class)).isEqualTo(" Anterior@Example.COM ");
        assertThat(database.queryForObject("SELECT nombre FROM usuario", String.class)).isEqualTo("Anterior");
        assertThat(database.queryForObject(
                "SELECT checksum FROM flyway_schema_history WHERE version = '1'", Integer.class))
                .isEqualTo(previousChecksum);
        assertThat(database.queryForObject("SELECT count(*) FROM spring_session", Integer.class)).isZero();
        assertThat(database.queryForObject("SELECT count(*) FROM spring_session_attributes", Integer.class)).isZero();
    }

    private void thenMigrationShouldFailWithoutDeletingCollidingEmails(
            DriverManagerDataSource dataSource, JdbcTemplate database) {
        assertThatThrownBy(() -> migrate(dataSource, "2")).isInstanceOf(FlywayException.class);
        assertThat(database.queryForList("SELECT nombre FROM usuario ORDER BY nombre", String.class))
                .containsExactly("Primera", "Segunda");
        assertThat(database.queryForList("SELECT email FROM usuario ORDER BY nombre", String.class))
                .containsExactly("persona@example.com", " PERSONA@Example.COM ");
        assertThat(database.queryForObject(
                "SELECT count(*) FROM flyway_schema_history WHERE version = '2'", Integer.class)).isZero();
    }
}
