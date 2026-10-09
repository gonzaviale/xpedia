package com.xpedia.backend.infrastructure.config.useCase;

import com.xpedia.backend.domain.mapper.microleccion.ListarMicroleccionesMapper;
import com.xpedia.backend.domain.service.microleccion.MicroleccionService;
import com.xpedia.backend.domain.useCase.microleccion.ListarMicroleccionesUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MicroleccionUseCaseConfig {

    @Bean
    public ListarMicroleccionesMapper listarMicroleccionesMapper() {
        return new ListarMicroleccionesMapper();
    }

    @Bean
    public ListarMicroleccionesUseCase listarMicroleccionesUseCase(
            MicroleccionService microleccionService,
            ListarMicroleccionesMapper listarMicroleccionesMapper) {
        return new ListarMicroleccionesUseCase(microleccionService, listarMicroleccionesMapper);
    }
}
