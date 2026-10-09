package com.xpedia.backend.infrastructure.config;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SwaggerConfigTest {
    @Test void documentacionIdentificaElBackendXpedia() {
        var info = new SwaggerConfig().openAPI().getInfo();
        assertThat(info.getTitle()).isEqualTo("Xpedia API"); assertThat(info.getVersion()).isEqualTo("0.0.1");
        assertThat(info.getDescription()).contains("Xpedia");
    }
}
