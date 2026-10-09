package com.xpedia.backend.infrastructure.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SwaggerConfigTest {

    private SwaggerConfig swaggerConfig;

    @BeforeEach
    void setUp() {
        swaggerConfig = new SwaggerConfig();
    }

    @Test
    @DisplayName("La documentación identifica el backend de Xpedia con título, versión y descripción")
    void openApiShouldIdentifyXpediaBackend() {
        Info info = documentationInfo();

        thenInfoIdentifiesXpedia(info);
    }

    // --- act ---
    @Test
    @DisplayName("Swagger documenta el header CSRF y la cookie de sesión")
    void openApiShouldDescribeCsrfAndSessionSchemes() {
        Components components = swaggerConfig.openAPI().getComponents();

        thenSecuritySchemesAreDocumented(components);
    }

    // --- act ---
    private Info documentationInfo() {
        return swaggerConfig.openAPI().getInfo();
    }

    // --- assert ---
    private void thenSecuritySchemesAreDocumented(Components components) {
        SecurityScheme csrf = components.getSecuritySchemes().get("csrf");
        assertThat(csrf.getType()).isEqualTo(SecurityScheme.Type.APIKEY);
        assertThat(csrf.getIn()).isEqualTo(SecurityScheme.In.HEADER);
        assertThat(csrf.getName()).isEqualTo("X-CSRF-TOKEN");
        SecurityScheme session = components.getSecuritySchemes().get("sesion");
        assertThat(session.getType()).isEqualTo(SecurityScheme.Type.APIKEY);
        assertThat(session.getIn()).isEqualTo(SecurityScheme.In.COOKIE);
        assertThat(session.getName()).isEqualTo("SESSION");
    }

    private void thenInfoIdentifiesXpedia(Info info) {
        assertThat(info.getTitle()).isEqualTo("Xpedia API");
        assertThat(info.getVersion()).isEqualTo("0.0.1");
        assertThat(info.getDescription()).contains("Xpedia");
    }
}
