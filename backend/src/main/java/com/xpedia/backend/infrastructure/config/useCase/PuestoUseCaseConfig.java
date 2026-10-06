package com.xpedia.backend.infrastructure.config.useCase;

import com.xpedia.backend.domain.mapper.puesto.ActualizarPuestoMapper;
import com.xpedia.backend.domain.mapper.puesto.CrearPuestoMapper;
import com.xpedia.backend.domain.mapper.puesto.ListarPuestosMapper;
import com.xpedia.backend.domain.mapper.puesto.ObtenerPuestoMapper;
import com.xpedia.backend.domain.service.puesto.PuestoService;
import com.xpedia.backend.domain.useCase.puesto.ActualizarPuestoUseCase;
import com.xpedia.backend.domain.useCase.puesto.CrearPuestoUseCase;
import com.xpedia.backend.domain.useCase.puesto.EliminarPuestoUseCase;
import com.xpedia.backend.domain.useCase.puesto.ListarPuestosUseCase;
import com.xpedia.backend.domain.useCase.puesto.ObtenerPuestoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PuestoUseCaseConfig {

    @Bean
    public CrearPuestoMapper crearPuestoMapper() {
        return new CrearPuestoMapper();
    }

    @Bean
    public ActualizarPuestoMapper actualizarPuestoMapper() {
        return new ActualizarPuestoMapper();
    }

    @Bean
    public ObtenerPuestoMapper obtenerPuestoMapper() {
        return new ObtenerPuestoMapper();
    }

    @Bean
    public ListarPuestosMapper listarPuestosMapper() {
        return new ListarPuestosMapper();
    }

    @Bean
    public CrearPuestoUseCase crearPuestoUseCase(PuestoService puestoService,
                                                 CrearPuestoMapper crearPuestoMapper) {
        return new CrearPuestoUseCase(puestoService, crearPuestoMapper);
    }

    @Bean
    public ActualizarPuestoUseCase actualizarPuestoUseCase(PuestoService puestoService,
                                                           ActualizarPuestoMapper actualizarPuestoMapper) {
        return new ActualizarPuestoUseCase(puestoService, actualizarPuestoMapper);
    }

    @Bean
    public EliminarPuestoUseCase eliminarPuestoUseCase(PuestoService puestoService) {
        return new EliminarPuestoUseCase(puestoService);
    }

    @Bean
    public ObtenerPuestoUseCase obtenerPuestoUseCase(PuestoService puestoService,
                                                     ObtenerPuestoMapper obtenerPuestoMapper) {
        return new ObtenerPuestoUseCase(puestoService, obtenerPuestoMapper);
    }

    @Bean
    public ListarPuestosUseCase listarPuestosUseCase(PuestoService puestoService,
                                                     ListarPuestosMapper listarPuestosMapper) {
        return new ListarPuestosUseCase(puestoService, listarPuestosMapper);
    }
}
