package com.xpedia.backend.infrastructure.config.useCase;

import com.xpedia.backend.domain.mapper.ruta.ListarRutasMapper;
import com.xpedia.backend.domain.mapper.ruta.ObtenerRutaMapper;
import com.xpedia.backend.domain.service.ruta.RutaService;
import com.xpedia.backend.domain.useCase.ruta.ListarRutasUseCase;
import com.xpedia.backend.domain.useCase.ruta.ObtenerRutaUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RutaUseCaseConfig {

    @Bean
    public ListarRutasMapper listarRutasMapper() {
        return new ListarRutasMapper();
    }

    @Bean
    public ObtenerRutaMapper obtenerRutaMapper() {
        return new ObtenerRutaMapper();
    }

    @Bean
    public ListarRutasUseCase listarRutasUseCase(RutaService rutaService,
                                                 ListarRutasMapper listarRutasMapper) {
        return new ListarRutasUseCase(rutaService, listarRutasMapper);
    }

    @Bean
    public ObtenerRutaUseCase obtenerRutaUseCase(RutaService rutaService,
                                                 ObtenerRutaMapper obtenerRutaMapper) {
        return new ObtenerRutaUseCase(rutaService, obtenerRutaMapper);
    }
}
