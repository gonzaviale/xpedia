package com.xpedia.backend.infrastructure.config.useCase;

import com.xpedia.backend.domain.mapper.intento.RegistrarIntentoMapper;
import com.xpedia.backend.domain.service.intento.IntentoService;
import com.xpedia.backend.domain.useCase.intento.RegistrarIntentoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class IntentoUseCaseConfig {

    @Bean
    public RegistrarIntentoMapper registrarIntentoMapper() {
        return new RegistrarIntentoMapper();
    }

    @Bean
    public RegistrarIntentoUseCase registrarIntentoUseCase(IntentoService intentoService,
                                                           RegistrarIntentoMapper registrarIntentoMapper,
                                                           Clock clock) {
        return new RegistrarIntentoUseCase(intentoService, registrarIntentoMapper, clock);
    }
}
