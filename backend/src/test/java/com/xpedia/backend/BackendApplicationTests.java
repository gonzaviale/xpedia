package com.xpedia.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
@ActiveProfiles("h2-test")
class BackendApplicationTests {

    @Test
    void contextLoads() {
        // Ejecuta el main real y verifica el arranque completo con la base aislada H2.
    }
}
