package com.xpedia.backend;

import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import static org.assertj.core.api.Assertions.assertThat;

class ArchitectureTest {
    @Test void clasesDeDominioNoDependenDeInfraestructura() throws Exception {
        var root = Path.of("target/classes/com/xpedia/backend/domain");
        assertThat(root).exists();
        try (var paths = Files.walk(root)) {
            var classes = paths.filter(p -> p.toString().endsWith(".class")).toList();
            assertThat(classes).isNotEmpty();
            for (var type : classes) {
                // La tabla de constantes de una clase conserva las referencias de tipos JVM.
                String bytecode = new String(Files.readAllBytes(type), StandardCharsets.ISO_8859_1);
                assertThat(bytecode).as("dependencias de %s", type)
                        .doesNotContain("com/xpedia/backend/infrastructure");
            }
        }
    }
}
