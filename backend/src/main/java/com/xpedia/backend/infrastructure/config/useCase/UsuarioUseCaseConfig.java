package com.xpedia.backend.infrastructure.config.useCase;

import com.xpedia.backend.domain.mapper.usuario.RegistrarUsuarioMapper;
import com.xpedia.backend.domain.service.usuario.UsuarioService;
import com.xpedia.backend.domain.useCase.usuario.RegistrarUsuarioUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class UsuarioUseCaseConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public RegistrarUsuarioMapper registrarUsuarioMapper() {
        return new RegistrarUsuarioMapper();
    }

    @Bean
    public RegistrarUsuarioUseCase registrarUsuarioUseCase(
            UsuarioService usuarioService, RegistrarUsuarioMapper registrarUsuarioMapper, Clock clock) {
        return new RegistrarUsuarioUseCase(usuarioService, registrarUsuarioMapper, clock);
    }
}
