package com.xpedia.backend.infrastructure.config.useCase;

import com.xpedia.backend.domain.mapper.reto.ListarRetosMapper;
import com.xpedia.backend.domain.mapper.reto.ObtenerRetoMapper;
import com.xpedia.backend.domain.service.reto.RetoService;
import com.xpedia.backend.domain.useCase.reto.ListarRetosUseCase;
import com.xpedia.backend.domain.useCase.reto.ObtenerRetoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RetoUseCaseConfig {

    @Bean
    public ListarRetosMapper listarRetosMapper() {
        return new ListarRetosMapper();
    }

    @Bean
    public ObtenerRetoMapper obtenerRetoMapper() {
        return new ObtenerRetoMapper();
    }

    @Bean
    public ListarRetosUseCase listarRetosUseCase(RetoService retoService,
                                                 ListarRetosMapper listarRetosMapper) {
        return new ListarRetosUseCase(retoService, listarRetosMapper);
    }

    @Bean
    public ObtenerRetoUseCase obtenerRetoUseCase(RetoService retoService,
                                                 ObtenerRetoMapper obtenerRetoMapper) {
        return new ObtenerRetoUseCase(retoService, obtenerRetoMapper);
    }
}
