package com.xpedia.backend.infrastructure.config.useCase;

import com.xpedia.backend.domain.mapper.hito.ListarHitosMapper;
import com.xpedia.backend.domain.mapper.nodo.ListarNodosMapper;
import com.xpedia.backend.domain.service.hito.HitoService;
import com.xpedia.backend.domain.service.nodo.NodoService;
import com.xpedia.backend.domain.useCase.hito.ListarHitosUseCase;
import com.xpedia.backend.domain.useCase.nodo.ListarNodosUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContenidoRutaUseCaseConfig {

    @Bean
    public ListarHitosMapper listarHitosMapper() {
        return new ListarHitosMapper();
    }

    @Bean
    public ListarNodosMapper listarNodosMapper() {
        return new ListarNodosMapper();
    }

    @Bean
    public ListarHitosUseCase listarHitosUseCase(HitoService hitoService,
                                                 ListarHitosMapper listarHitosMapper) {
        return new ListarHitosUseCase(hitoService, listarHitosMapper);
    }

    @Bean
    public ListarNodosUseCase listarNodosUseCase(NodoService nodoService,
                                                 ListarNodosMapper listarNodosMapper) {
        return new ListarNodosUseCase(nodoService, listarNodosMapper);
    }
}
