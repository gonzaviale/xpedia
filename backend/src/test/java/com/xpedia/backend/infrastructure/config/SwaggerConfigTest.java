package com.xpedia.backend.infrastructure.config;

import io.swagger.v3.oas.models.info.Info;
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
    private Info documentationInfo() {
        return swaggerConfig.openAPI().getInfo();
    }

    // --- assert ---
    private void thenInfoIdentifiesXpedia(Info info) {
        assertThat(info.getTitle()).isEqualTo("Xpedia API");
        assertThat(info.getVersion()).isEqualTo("0.0.1");
        assertThat(info.getDescription()).contains("Xpedia");
    }
}
