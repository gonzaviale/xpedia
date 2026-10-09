package com.xpedia.backend.infrastructure.config.useCase;

import com.xpedia.backend.domain.mapper.inscripcion.CrearInscripcionMapper;
import com.xpedia.backend.domain.mapper.inscripcion.ObtenerInscripcionActualMapper;
import com.xpedia.backend.domain.service.inscripcion.InscripcionService;
import com.xpedia.backend.domain.useCase.inscripcion.CrearInscripcionUseCase;
import com.xpedia.backend.domain.useCase.inscripcion.ObtenerInscripcionActualUseCase;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.List;

@Configuration
public class InscripcionUseCaseConfig {

    @Bean
    public OpenApiCustomizer inscripcionOpenApiCustomizer() {
        return api -> {
            PathItem path = api.getPaths().get("/api/inscripciones");
            if (path != null && path.getPost() != null) {
                path.getPost().setSecurity(List.of(new SecurityRequirement()
                        .addList("sesion")
                        .addList("csrf")));
            }
        };
    }

    @Bean
    public CrearInscripcionMapper crearInscripcionMapper() {
        return new CrearInscripcionMapper();
    }

    @Bean
    public ObtenerInscripcionActualMapper obtenerInscripcionActualMapper() {
        return new ObtenerInscripcionActualMapper();
    }

    @Bean
    public CrearInscripcionUseCase crearInscripcionUseCase(
            InscripcionService inscripcionService, CrearInscripcionMapper crearInscripcionMapper, Clock clock) {
        return new CrearInscripcionUseCase(inscripcionService, crearInscripcionMapper, clock);
    }

    @Bean
    public ObtenerInscripcionActualUseCase obtenerInscripcionActualUseCase(
            InscripcionService inscripcionService, ObtenerInscripcionActualMapper obtenerInscripcionActualMapper) {
        return new ObtenerInscripcionActualUseCase(inscripcionService, obtenerInscripcionActualMapper);
    }
}

