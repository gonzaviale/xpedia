package com.xpedia.backend.infrastructure.config.useCase;

import com.xpedia.backend.domain.service.inscripcion.InscripcionService;
import com.xpedia.backend.domain.useCase.inscripcion.CrearInscripcionUseCase;
import com.xpedia.backend.domain.useCase.inscripcion.ObtenerInscripcionActualUseCase;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class InscripcionUseCaseConfigTest {

    private final InscripcionUseCaseConfig config = new InscripcionUseCaseConfig();

    @Test
    @DisplayName("OpenAPI exige sesión y CSRF conjuntamente para crear una inscripción")
    void inscripcionOpenApiCustomizerShouldRequireBothSecuritySchemes() {
        OpenAPI api = new OpenAPI().paths(new Paths().addPathItem(
                "/api/inscripciones", new PathItem().post(new Operation())));

        config.inscripcionOpenApiCustomizer().customise(api);

        thenBothSchemesAreRequired(api);
    }

    @Test
    @DisplayName("No modifica documentos que excluyen la ruta de inscripciones")
    void inscripcionOpenApiCustomizerShouldAcceptExcludedPath() {
        OpenAPI api = new OpenAPI().paths(new Paths());

        config.inscripcionOpenApiCustomizer().customise(api);

        thenPathIsStillAbsent(api);
    }

    @Test
    @DisplayName("No modifica documentos que excluyen la operación POST")
    void inscripcionOpenApiCustomizerShouldAcceptExcludedPost() {
        OpenAPI api = new OpenAPI().paths(new Paths().addPathItem("/api/inscripciones", new PathItem()));

        config.inscripcionOpenApiCustomizer().customise(api);

        thenPostIsStillAbsent(api);
    }

    @Test
    @DisplayName("Construye el caso de creación con servicio, mapper y reloj")
    void crearInscripcionUseCaseShouldWireDependencies() {
        var useCase = config.crearInscripcionUseCase(
                mock(InscripcionService.class), config.crearInscripcionMapper(), Clock.systemUTC());

        thenCrearInscripcionUseCaseShouldWireDependencies(useCase);
    }

    @Test
    @DisplayName("Construye el caso de consulta actual con servicio y mapper")
    void obtenerInscripcionActualUseCaseShouldWireDependencies() {
        var useCase = config.obtenerInscripcionActualUseCase(
                mock(InscripcionService.class), config.obtenerInscripcionActualMapper());

        thenObtenerInscripcionActualUseCaseShouldWireDependencies(useCase);
    }

    // --- assert ---
    private void thenBothSchemesAreRequired(OpenAPI api) {
        var security = api.getPaths().get("/api/inscripciones").getPost().getSecurity();
        assertThat(security).hasSize(1);
        assertThat(security.getFirst()).containsOnlyKeys("sesion", "csrf");
    }

    private void thenPathIsStillAbsent(OpenAPI api) {
        assertThat(api.getPaths()).isEmpty();
    }

    private void thenPostIsStillAbsent(OpenAPI api) {
        assertThat(api.getPaths().get("/api/inscripciones").getPost()).isNull();
    }

    private void thenCrearInscripcionUseCaseShouldWireDependencies(CrearInscripcionUseCase useCase) {
        assertThat(useCase).isNotNull();
    }

    private void thenObtenerInscripcionActualUseCaseShouldWireDependencies(ObtenerInscripcionActualUseCase useCase) {
        assertThat(useCase).isNotNull();
    }
}

