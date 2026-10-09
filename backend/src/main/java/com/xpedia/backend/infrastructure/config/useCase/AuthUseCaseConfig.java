package com.xpedia.backend.infrastructure.config.useCase;

import com.xpedia.backend.domain.mapper.auth.IniciarSesionMapper;
import com.xpedia.backend.domain.mapper.auth.ObtenerUsuarioActualMapper;
import com.xpedia.backend.domain.service.auth.AuthService;
import com.xpedia.backend.domain.service.usuario.UsuarioService;
import com.xpedia.backend.domain.useCase.auth.IniciarSesionUseCase;
import com.xpedia.backend.domain.useCase.auth.ObtenerUsuarioActualUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthUseCaseConfig {

    @Bean
    public IniciarSesionMapper iniciarSesionMapper() {
        return new IniciarSesionMapper();
    }

    @Bean
    public ObtenerUsuarioActualMapper obtenerUsuarioActualMapper() {
        return new ObtenerUsuarioActualMapper();
    }

    @Bean
    public IniciarSesionUseCase iniciarSesionUseCase(
            AuthService authService, IniciarSesionMapper iniciarSesionMapper) {
        return new IniciarSesionUseCase(authService, iniciarSesionMapper);
    }

    @Bean
    public ObtenerUsuarioActualUseCase obtenerUsuarioActualUseCase(
            UsuarioService usuarioService, ObtenerUsuarioActualMapper obtenerUsuarioActualMapper) {
        return new ObtenerUsuarioActualUseCase(usuarioService, obtenerUsuarioActualMapper);
    }
}
