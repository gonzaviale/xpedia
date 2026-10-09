package com.xpedia.backend.infrastructure.config.useCase;

import com.xpedia.backend.domain.mapper.cuestionario.ListarCuestionariosMapper;
import com.xpedia.backend.domain.service.cuestionario.CuestionarioService;
import com.xpedia.backend.domain.useCase.cuestionario.ListarCuestionariosUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CuestionarioUseCaseConfig {

    @Bean
    public ListarCuestionariosMapper listarCuestionariosMapper() {
        return new ListarCuestionariosMapper();
    }

    @Bean
    public ListarCuestionariosUseCase listarCuestionariosUseCase(
            CuestionarioService cuestionarioService,
            ListarCuestionariosMapper listarCuestionariosMapper) {
        return new ListarCuestionariosUseCase(cuestionarioService, listarCuestionariosMapper);
    }
}
